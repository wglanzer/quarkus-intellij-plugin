# Quarkus Integration for JetBrains IntelliJ IDEA

This plugin adds a run configuration for [quarkus](http://quarkus.io) to make debugging a lot more easier.

What the newly added runconfig does:
1. Starting the "quarkus:dev" goal from your desired quarkus project
2. If you started the runconfig in debug mode, the plugin will attach a remote debugging session on a random port

## Minimum Requirements
- IntelliJ IDEA Community > 173.0
- Installed and enabled "Maven" plugin