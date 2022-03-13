# Build Instructions

### Known Errors
In some cases SWT Libs need to be added manually because maven fails ... check out 
https://archive.eclipse.org/eclipse/downloads/drops4/R-4.13-201909161045/

ohli's solution from https://github.com/pterodactylus42/tuxguitar/issues/2
:
#### download SWT
wget -qO- https://archive.eclipse.org/eclipse/downloads/drops4/R-4.13-201909161045/download.php?dropFile=swt-4.13-gtk-linux-x86_64.zip | unzip

cd <where tuxguitar is>/build-scripts/tuxguitar-linux-x86_64

mvn install:install-file -Dfile=<absolute path to your swt download>/swt-4.13-gtk-linux-x86_64/swt.jar -DartifactId=org.eclipse.swt.gtk.linux.x86_64 -Dpackaging=jar -DgroupId=org.eclipse.swt -Dversion=4.13

# now you can build tuxguitar without errors
mvn -P native-modules package

## Prerequisites
- JDK 7 or higher
- Maven 3.3 or higher
- Fluidsynth (optional)
- JACK (optional)
## Setup
```sh
git clone https://github.com/cyclopsian/tuxguitar
cd tuxguitar
```
## GNU/Linux
### Debian/Ubuntu
```sh
sudo apt install build-essential default-jdk maven libfluidsynth-dev libjack-jackd2-dev libasound2-dev libgtk-3-dev
cd build-scripts/tuxguitar-linux-x86_64-deb
mvn -P native-modules package
sudo dpkg -i target/tuxguitar-*.deb
```
## Generic GNU/Linux
```sh
cd build-scripts/tuxguitar-linux-x86_64
mvn -P native-modules package
# To run the program:
cd target/tuxguitar-*
./tuxguitar.sh
```
## Windows
### Cross compiling from Ubuntu/Debian with [mingw-w64](https://mingw-w64.org/)
```sh
sudo apt install default-jdk maven gcc-mingw-w64-i686
cd build-scripts/tuxguitar-windows-x86
mvn -P native-modules -D tuxguitar.jni.cc=i686-w64-mingw32-gcc package
# Application will now be in be in the build-scripts/tuxguitar-windows-x86/target folder
```
## macOS with [Homebrew](https://brew.sh)
```sh
brew install oracle-jdk maven
cd build-scripts/tuxguitar-macosx-cocoa-64
mvn package
# Application will now be in be in the build-scripts/tuxguitar-macosx-cocoa-64/target folder
```
## FreeBSD
```sh
cd build-scripts/tuxguitar-freebsd-x86_64
pkg install openjdk8 alsa-plugins maven swt gcc gmake fluidsynth
mvn -P native-modules package
```
