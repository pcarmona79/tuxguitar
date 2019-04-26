# Build Instructions
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
sudo apt install build-essential default-jdk maven libfluidsynth-dev libjack-jackd2-dev libasound2-dev
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
