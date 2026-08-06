package dev.aoqia.leaf.proxy;

import java.lang.instrument.Instrumentation;

import dev.aoqia.leaf.proxy.transformers.EntrypointTransformer;

public class Main {
    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new EntrypointTransformer(inst), false);
    }
}
