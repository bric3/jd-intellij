# JD-IntelliJ #

**JD-IntelliJ** is a plug-in for **IntelliJ IDEA**, initiated by **Brice Dutheil**. It uses the **Java Decompiler** binaries It allows you to display all the Java sources during your debugging process, even if you do not have them all. Currently, the project is under development. The Java sources are hosted on [Bitbucket](java.decompiler.free.fr/?q=jdintellij). Your contributions are welcome.
Also note that it is possible to decompile jars/classes with **JD-GUI** (can be found on the official site).

## Warning ##

This plugin is currently partly maintained, due to lack of time and the availablity of a decompiler within [IntelliJ IDEA itself](https://www.jetbrains.com/idea/features/#built-in-tools) (even the [community version](https://www.jetbrains.com/idea/features/editions_comparison_matrix.html)).


## Notes ##

It is the official [**Java Decompiler**](http://jd.benow.ca/) plugin for IntelliJ IDEA. The plugin should work from IntelliJ IDEA 2018.3.x to the latest stable version .

Last but not the least, all the credit for decompilation stuff must go to **Emmanuel Dupuy**, who is the author of Java Decompiler.


## Screenshots ##

![JD-IntelliJ in action](http://jd.benow.ca/img/screenshot16.png)

## Installation ##

##### From the JetBrains repository #####

The plugin is deployed on the publin JetBrains IntelliJ repository, to install it go to the _Settings_ dialog window > _Plugins_ pane > _Browse Repositories_ dialog window, then search for _Java Decompiler_.

##### From the zip archive #####

It is also possible to install JD-IntelliJ from a file (if compiled from the source), to the _Settings_ dialog window > _Plugins_ pane > _Install Plugin from disk..._

**Don't download from Bitbucket!** The Bitbucket link is the zipped mercurial repository, not the actual plugin. You can find the latest version on the [IntelliJ plugin site](http://plugins.jetbrains.com/plugin/7100).

## Development ##

#### First steps ####

> _Note that the source code is compiled against the IntelliJ 11 API, the JetBrains introduced a few backward incompatible changes in later
versions that would require changes and incompatibilities for earlier verison of IntelliJ. In order to maintain this compitibility for
the time being the plugin will still be compiled against IntelliJ 11, compilation should work against a community edition of IntelliJ 11
(which can be found [here](http://devnet.jetbrains.com/docs/DOC-1228))._


1. Download the project from Bitbucket.
2. Import it on IntelliJ IDEA.
3. Create a new configuration with the type "plugin". Don't forget to set up an _IntelliJ IDEA Plugin SDK_.
4. Run the new configuration.

#### Building it ####

Don't use the IntelliJ internal plugin deployment feature _Prepare plugin module 'jd-intellij' for deployment_, it
doesn't deal well we have native binaries. in order to deal properly with this matter an ant build script has
been created.

So, to create the plugin zip archive (with the custom repository file) in the `deploy` folder, enter the following at
root of the project :

    ant make

> _note for Linux/Unix: change jd-intellij-unix.properties to reflect the IntelliJ Installation path (default to /usr/local/intelliJ ) 



To package the archive you should also setup your the properties in your platform related file
`jd-intellij-osx.properties` or `jd-intellij-win32.properties`, you'll find interesting properties :


    # IntelliJ 11 path
    idea.home=/Applications/IntelliJ IDEA 11.app
    jdk.home=${idea.home}/../../System/Library/Java/JavaVirtualMachines/1.6.0.jdk


    # Version of the plugin
    current.version=0.1


    # Base URL where the plugin ZIP file will be deployed
    plugin.deploy.url=http://arkey.fr/jd-intellij

#### Interesting IntelliJ Plugin development links ####

For development purpose, you can take a look here :

- [http://tomaszdziurko.pl/2011/09/developing-plugin-intellij-idea-some-tips-and-links/](http://tomaszdziurko.pl/2011/09/developing-plugin-intellij-idea-some-tips-and-links/)
- [http://confluence.jetbrains.net/display/IDEADEV/PluginDevelopment](http://confluence.jetbrains.net/display/IDEADEV/PluginDevelopment)
