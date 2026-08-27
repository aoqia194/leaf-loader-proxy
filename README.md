<div align="center">

**A loader that loads [leaf-loader](https://github.com/aoqia194/leaf-loader) from the Workshop. Ironic?!**

![Gradle Version](https://img.shields.io/badge/Gradle-9.7.1-teal?logo=gradle)
![License](https://img.shields.io/badge/License-MIT-orange)
![Build Status](https://github.com/aoqia194/leaf-loader-proxy/actions/workflows/build.yml/badge.svg?branch=main)

</div>

### Requirements

- Java 25

### Installation

To install the proxy, you should be using [leaf-installer](https://github.com/aoqia194/leaf-installer).
If you want to install it purely for testing in-dev, you can run the `copyToGame` Gradle task.

### Usage

The installer will help you with the following, but if you wish, you can do it manually:

The proxy jar should be placed alongside `projectzomboid.jar` in the game folder.
To actually get the game to use the proxy, you need to add `-javaagent:loader-proxy:0.1.0.jar` to the game's launch options.

If you need help with this, you should read [Startup Parameters](https://pzwiki.net/wiki/Startup_parameters).

### Configuration

If you use the `copyToGame` task, you will need to set the `LEAF_CLIENT_GAME_PATH` environment variable to the game root.
Otherwise, you don't need any external configuration.

### Development

You can build the project like so:

```shell
./gradlew build
```
