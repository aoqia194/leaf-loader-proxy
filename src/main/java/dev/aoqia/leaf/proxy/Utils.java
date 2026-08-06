package dev.aoqia.leaf.proxy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;

public class Utils {
    private static BufferedWriter LOG;

    public static void initLog(final File gameDir) throws IOException {
        File leafDir = new File(gameDir, ".leaf/");
        if (!leafDir.exists()) {
            Files.createDirectory(leafDir.toPath());
        }

        File logFile = new File(leafDir, "proxy.log");
        LOG = new BufferedWriter(new FileWriter(logFile, false));
    }

    public static void closeLog() {
        if (LOG == null) {
            return;
        }

        try {
            LOG.close();
            LOG = null;
        } catch (IOException ignored) {
        }
    }

    public static void log(final String s) {
        System.out.println(s);
        try {
            LOG.write(s);
            LOG.newLine();
            // Flush every time just in case of crashes
            LOG.flush();
        } catch (IOException ignored) {
        }
    }

    public static byte[] remapClass(final byte[] originalBytes, final String newInternalName) {
        ClassReader reader = new ClassReader(originalBytes);
        ClassWriter writer = new ClassWriter(0);
        ClassRemapper remapper = new ClassRemapper(writer,
            new SimpleRemapper(Constants.ASM_VERSION, reader.getClassName(), newInternalName));
        reader.accept(remapper, 0);
        return writer.toByteArray();
    }

    public static String internalName(final String s) {
        return s.replace('.', '/');
    }

    public static void addToClasspath(final String... entries) {
        final List<String> origClassPath = new ArrayList<>(
            Arrays.asList(System.getProperty(Constants.Properties.CLASS_PATH).split(File.pathSeparator)));

        String classPath = String.join(File.pathSeparator, origClassPath);
        if (!classPath.isEmpty()) {
            classPath += File.pathSeparator;
        }
        classPath += String.join(File.pathSeparator, entries);

        System.setProperty(Constants.Properties.CLASS_PATH, classPath);
    }

    public static void removeFromClasspath(final Predicate<String> predicate) {
        List<String> origClassPath = new ArrayList<>(
            Arrays.asList(System.getProperty(Constants.Properties.CLASS_PATH).split(File.pathSeparator)));
        origClassPath.removeIf(predicate);

        final String classPath = String.join(File.pathSeparator, origClassPath);
        System.setProperty(Constants.Properties.CLASS_PATH, classPath);
    }
}
