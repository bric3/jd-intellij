# JD-IntelliJ #

JD-IntelliJ is a plug-in for IntelliJ IDEA, initiated by Brice Dutheil. It allows you to display all the Java sources during your debugging process, even if you do not have them all. Currently, the project is under development. The Java sources are hosted on [Bitbucket](java.decompiler.free.fr/?q=jdintellij). Your contributions are welcome.

## Notes ##

It is the official [Java Decompiler](http://java.decompiler.free.fr/) plugin for IntelliJ IDEA. The plugin should work from IntelliJ IDEA 10.5.x to the latest stable version (12 as of this writing).

Last but not the least, all the credit for decompilation stuff must go to Emmanuel Dupuy, who is the author of Java Decompiler.

## Screenshots ##

![JD-IntelliJ in action](http://java.decompiler.free.fr/sites/default/screenshots/screenshot16.png)

## Supported Platforms ##

- Windows 32/64-bit
- Linux 32/64-bit
- Mac OSX 32/64-bit on x86 hardware

## Installation ##

##### Windows Platform Prerequisites #####

The native library, included into JD-Eclipse for Windows, has been built with Microsoft Visual C++ 2008 Express Edition. Some runtime components of Visual C++ Libraries are required to run the decompiler. You can download and install them from the Microsoft Web site :

[Microsoft Visual C++ 2008 SP1 Redistributable Package (x86)](http://www.microsoft.com/downloads/details.aspx?familyid=A5C84275-3B97-4AB7-A40D-3802B2AF5FC2&displaylang=en)

[Microsoft Visual C++ 2008 SP1 Redistributable Package (x64)](http://www.microsoft.com/downloads/details.aspx?familyid=BA9257CA-337F-4B40-8C14-157CFDFFEE4E&displaylang=en)

## Development ##

##### First steps #####

1. Download the project from Bitbucket.
2. Import it on IntelliJ IDEA.
3. Create a new configuration with the type "plugin".
4. Run the new configuration.
5. Copy native library on the sandbox folder if needed.

##### Building it #####

To create the plugin zip archive with the custom repository file in the `deploy` folder it by running :

On Mac OSX

    ant -f jd-intellij-osx.xml make


On Windows

    ant -f jd-intellij-win32.xml make



Take a look at the properties file `jd-intellij-osx.prperties` or `jd-intellij-win32.properties`, you'll find interesting properties :


    # IntelliJ 11 path
    idea.home=/Applications/IntelliJ IDEA 11.app
    jdk.home=${idea.home}/../../System/Library/Java/JavaVirtualMachines/1.6.0.jdk


    # Version of the plugin
    current.version=0.1


    # Base URL where the plugin ZIP file will be deployed
    plugin.deploy.url=http://arkey.fr/jd-intellij


## Disclaimer ##

Copyright © 2008-2012 Emmanuel Dupuy.

THIS SOFTWARE IS PROVIDED “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.