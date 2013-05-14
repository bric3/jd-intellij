# JD-IntelliJ #

**JD-IntelliJ** is a plug-in for **IntelliJ IDEA**, initiated by **Brice Dutheil**. It uses the **Java Decompiler** binaries It allows you to display all the Java sources during your debugging process, even if you do not have them all. Currently, the project is under development. The Java sources are hosted on [Bitbucket](java.decompiler.free.fr/?q=jdintellij). Your contributions are welcome.

## Notes ##

It is the official [**Java Decompiler**](http://java.decompiler.free.fr/) plugin for IntelliJ IDEA. The plugin should work from IntelliJ IDEA 10.5.x to the latest stable version (12 as of this writing).

Last but not the least, all the credit for decompilation stuff must go to **Emmanuel Dupuy**, who is the author of Java Decompiler.

## Screenshots ##

![JD-IntelliJ in action](http://java.decompiler.free.fr/sites/default/screenshots/screenshot16.png)

## Supported Platforms ##

- Windows 32/64-bit
- Linux 32/64-bit
- Mac OSX 32/64-bit on x86 hardware

## Installation ##

##### From the JetBrains repository #####

The plugin is deployed on the publin JetBrains IntelliJ repository, to install it go to the _Settings_ dialog window
 > _Plugins_ pane > _Browse Repositories_ dialog window, then search for _Java Decompiler_.

##### From the zip archive #####

It is also possible to install JD-IntelliJ from a file, to the _Settings_ dialog window > _Plugins_ pane >
_Install Plugin from disk..._


##### Windows Platform Prerequisites #####

The native library, included into JD-Eclipse for Windows, has been built with Microsoft Visual C++ 2008 Express Edition.
Some runtime components of Visual C++ Libraries are required to run the decompiler. You can download and install them
from the Microsoft Web site :

[Microsoft Visual C++ 2008 SP1 Redistributable Package (x86)](http://www.microsoft.com/downloads/details.aspx?familyid=A5C84275-3B97-4AB7-A40D-3802B2AF5FC2&displaylang=en)

[Microsoft Visual C++ 2008 SP1 Redistributable Package (x64)](http://www.microsoft.com/downloads/details.aspx?familyid=BA9257CA-337F-4B40-8C14-157CFDFFEE4E&displaylang=en)

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
5. Copy native library on the sandbox folder if needed. _(It shouldn't be necessary anymore, as the loading code and
   the IntelliJ project file `jd-intellij.ipr` has been tweaked to copy over the native binaries, see Resource
   Patterns in the Compiler settings)_

#### Building it ####

Don't use the IntelliJ internal plugin deployment feature _Prepare plugin module 'jd-intellij' for deployment_, it
doesn't deal well we have native binaries. in order to deal properly with this matter an ant build script has
been created.

So, to create the plugin zip archive (with the custom repository file) in the `deploy` folder, enter the following at
root of the project :

    ant make

> _Note that Linux packaging is not yet available. Contributions are welcome._



To package the archive you should also setup your the properties in your platform related file
`jd-intellij-osx.prperties` or `jd-intellij-win32.properties`, you'll find interesting properties :


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


## Java Decompiler Disclaimer ##

Copyright © 2008-2012 Emmanuel Dupuy.

THIS SOFTWARE IS PROVIDED “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.