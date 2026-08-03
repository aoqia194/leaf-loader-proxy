package zombie.gameStates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;

import dev.aoqia.leaf.proxy.InstallerJson;

public class MainScreenState {
    private static BufferedWriter LOG;
    private static final DslJson<Object> JSON = new DslJson<>(Settings.basicSetup());

    static void main(final String[] args) throws Exception {
        final File proxyJar = getProxyJarPath();
        if ("1".equals(System.getProperty("leaf.proxy.disabled", ""))) {
            loadVanillaJar(args, proxyJar);
            return;
        }

        initLog(proxyJar.getParentFile());
        log("Initialised logging for leaf-loader-proxy.");

        // If there's a better way to not hardcode this ID...
        final File loaderJar = getLoaderJar(proxyJar, "3776625738");

        if (loaderJar == null) {
            closeLog();
            return;
        }

        final URL[] urls = new URL[] { loaderJar.toURI().toURL() };
        try (final URLClassLoader cl = new URLClassLoader(urls, null)) {
            log("Classloader was created successfully.");

            final Class<?> entrypoint = Class.forName(getLoaderEntrypoint(cl), true, cl);
            final Method entryMethod = entrypoint.getMethod("main", String[].class);

            log("Invoking loader entrypoint, have fun!");
            Thread.currentThread().setContextClassLoader(cl);
            entryMethod.invoke(null, (Object) args);
        }
    }

    private static void initLog(final File gameDir) throws IOException {
        File leafDir = new File(gameDir, ".leaf/");
        if (!leafDir.exists()) {
            Files.createDirectory(leafDir.toPath());
        }

        File logFile = new File(leafDir, "proxy.log");
        LOG = new BufferedWriter(new FileWriter(logFile, false));
    }

    private static void closeLog() {
        if (LOG == null) {
            return;
        }

        try {
            LOG.close();
            LOG = null;
        } catch (IOException ignored) {
        }
    }

    private static File getProxyJarPath() throws URISyntaxException {
        return new File(MainScreenState.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    }

    private static File getLoaderJar(final File proxyJar, final String workshopId) {
        log("Attempting to get loader jar...");

        final Path workshopPath = getWorkshopPath(proxyJar);
        final Path modPath = workshopPath.resolve(workshopId, "mods", "LeafLoader");

        final File loaderJar = modPath.resolve("common", "media", "java", "loader.jar").toFile().getAbsoluteFile();
        if (!loaderJar.exists()) {
            log("Loader JAR doesn't exist in the workshop mod, exiting game.");
            return null;
        }

        return loaderJar;
    }

    private static Path getWorkshopPath(final File proxyJar) {
        Path gamePath = proxyJar.toPath().getParent();
        if ("projectzomboid".equals(gamePath.getFileName().toString())) {
            gamePath = gamePath.getParent();
        }

        return gamePath.getParent().getParent().resolve("workshop", "content", "108600");
    }

    private static String getLoaderEntrypoint(URLClassLoader cl) throws IOException {
        byte[] jsonBytes;
        try (InputStream is = cl.getResourceAsStream("leaf-installer.json")) {
            if (is == null) {
                log("leaf-installer.json doesn't exist in the loader JAR!");
                return null;
            }

            jsonBytes = is.readAllBytes();
        }

        InstallerJson installerJson = JSON.deserialize(InstallerJson.class, jsonBytes, jsonBytes.length);
        return Objects.requireNonNull(installerJson).mainClass().client();
    }

    private static void loadVanillaJar(final String[] args, final File proxyJar) throws IOException,
        ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        final File loaderJar = proxyJar.toPath().getParent().resolve("projectzomboid.jar").toFile();
        final URL[] urls = new URL[] { loaderJar.toURI().toURL() };
        try (final URLClassLoader cl = new URLClassLoader(urls, null)) {
            final Class<?> entrypoint = Class.forName(MainScreenState.class.getTypeName(), true, cl);
            final Method entryMethod = entrypoint.getMethod("main", String[].class);
            Thread.currentThread().setContextClassLoader(cl);
            entryMethod.invoke(null, (Object) args);
        } catch (InvocationTargetException e) {
            Throwable actual = e.getCause();
            throw new RuntimeException(actual);
        }
    }

    private static void log(final String s) {
        System.out.println(s);
        try {
            LOG.write(s);
            LOG.newLine();
            // Flush every time just in case of crashes
            LOG.flush();
        } catch (IOException ignored) {
        }
    }
}
