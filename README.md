# TuxGuitar2
A multitrack tablature editor and player for stringed instruments. This is a downstream fork
of [TuxGuitar](http://tuxguitar.com.ar/) with several changes merged in from the community.

TuxGuitar was originally developed by Julian Casadesus and many others. See [AUTHORS](AUTHORS)
for more information.

[![License: LGPLv2.1](https://img.shields.io/badge/License-LGPL%20v2.1-blue.svg?logo=gnu)](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html)

## System Requirements
- Java 7 or higher

## What's Changed
- Added tuning presets, grouped by instrument and number of strings
- Removed VST2 instrument support due to the SDK being discontinued
- Removed the Gervill synth due to the interface being hidden in JDK9 (see [JDK-8170518](https://bugs.openjdk.java.net/browse/JDK-8170518))
- Various bugfixes in importers/exporters by [b4dc0d3r](https://sourceforge.net/p/tuxguitar-fork)
- UI cleanup, MIDI track name support by [Alex Abdugafarov](https://github.com/frozenspider/tuxguitar)
- Mouse selection support by [Bartek Poleszak](https://github.com/bart-poleszak/TuxGuitar-workspace)

## Build Instructions
See [INSTALL.md](INSTALL.md)
