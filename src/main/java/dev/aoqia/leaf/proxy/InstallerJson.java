package dev.aoqia.leaf.proxy;

import com.dslplatform.json.CompiledJson;

@CompiledJson
public record InstallerJson(MainClass mainClass) {
    public record MainClass(String client, String server) {}
}
