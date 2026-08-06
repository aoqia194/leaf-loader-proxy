package dev.aoqia.leaf.proxy;

import org.objectweb.asm.Opcodes;

public class Constants {
    public static final int ASM_VERSION = Opcodes.ASM9;

    public static final String CLIENT_ENTRYPOINT = "zombie.gameStates.MainScreenState";

    public static final class Properties {
        public static final String CLASS_PATH = "java.class.path";

        public static final String LEAF_DISABLED = "leaf.disabled";

        public static final String DISABLE_CLASSPATH_ISOLATION = "leaf.debug.disableClassPathIsolation";
    }
}
