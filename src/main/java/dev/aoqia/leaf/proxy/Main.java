package dev.aoqia.leaf.proxy;

import java.lang.instrument.Instrumentation;

import dev.aoqia.leaf.proxy.transformers.EntrypointTransformer;

public class Main {
    public static Arguments args;

    public static void premain(String args, Instrumentation inst) {
        final var arguments = new Arguments();
        arguments.parse(args.split(";"));
        Main.args = arguments;

        inst.addTransformer(new EntrypointTransformer(inst), false);
    }
}
