# TuxGuitar

[![Screenshot](./TuxGuitar/share/skins/Symbolic-Dark/skin-preview.png)](./TuxGuitar/share/skins/Symbolic-Dark/skin-preview.png)

A multitrack tablature editor and player for stringed instruments. This is a downstream fork
of [TuxGuitar](http://tuxguitar.com.ar/) with several changes merged in from the community.

TuxGuitar was originally developed by Julian Casadesus and many others. See [AUTHORS](AUTHORS)
for more information.

[![License: LGPLv2.1](https://img.shields.io/badge/License-LGPL%20v2.1-blue.svg?logo=gnu)](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html)

## System Requirements
- Java 9 or higher

## What's Changed
- Global style update with a new skin
- Main toolbar redesigned
- Native window header and toolbar for the SWT GTK backend
- High DPI display support
- Hiding/showing of individual tracks
- Click-and-drag selection support, started by [Bartek Poleszak](https://github.com/bart-poleszak/TuxGuitar-workspace)
- Cut/Copy/Paste of individual notes
- More tuning presets, grouped by instrument and number of strings
- Default program/clef support for tunings
- Scale finder
- Customizable scale intervals
- Percussion dialog and percussion notation editor
- Support for mix table change commands from GP3/GP4/GP5 files
- UI cleanup, MIDI track name support by [Alex Abdugafarov](https://github.com/frozenspider/tuxguitar)
- Various bug fixes in importers/exporters by [b4dc0d3r](https://sourceforge.net/p/tuxguitar-fork)
- And lots of other small improvements and bug fixes

VST2 instrument support has been disabled, due to the SDK being discontinued.

## Build Instructions
See [INSTALL.md](INSTALL.md)
