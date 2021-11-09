# PokeRando Classic

A work in progress Android(R) Pokemon Randomizer based on the
original [Dabomstew Universal Pokemon Randomizer.](https://github.com/Dabomstew/universal-pokemon-randomizer)
Why base it on the now archived UPR project? Because it is more simple and (hopefully) easier to
port for a beginner programmer. It combines a drive to create an Android app and my enjoyment of
pokemon. This one is for learning and will act as a foundation for a version based
on [UPRzx.](https://github.com/Ajarmar/universal-pokemon-randomizer-zx)

## Status

It Does NOTHING!
Development in progress

## Limitations

- Loading Roms over 64ish MB fails
    - seems to be an issue in Gen5Romhandler
    - related to getting/parsing strings
    - logcat shows issue with PPTxtHandler
    - tested roms Black and Black 2

## Current Version

### skunkwerks1:

- [*] Very, Very, Very basic UI
- [*] Requests Storage Permissions
- [*] Has some images

### skunkwerks2:

- [*] open file
- [] save non-random file with new name
- [*] verify file is rom
- [*] Pokemon names array
- [*] Dynamic Box Art Loading
- [] full gen 1 support

### skunkwerks3

- [] add select pokemon fragment (only lists pokemon names)
- [] Fix Gen 5 rom loading
- [] Full gen 2 support
- [] New Tabbed UI?

###  

## FUTURE:

- Parity with Dabomstew UPR

## Copyright & Licence

Large parts of code copyright to Dabomstew and others and will be preserved if CR exists GPL-3.0
