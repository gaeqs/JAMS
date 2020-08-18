# JAMS
Just Another MIPS Simulator.
![Image JAMS](https://i.imgur.com/0UojZr4.png)
![Image JAMS_2](https://i.imgur.com/qXSNvO3.png)

## Introduction

Jams is a modern and extensible project-based MIPS assembler and simulator written in Java.
It supports multiple themes and languages.

> Currently on an alpha state, JAMS is aimed to support multiple architectures and instruction sets, allowing other developers to expand JAMS through plugins.

## Dependencies
- Java 14.
- JavaFX 14.
- [RichTextFX](https://github.com/FXMisc/RichTextFX)
- [FX-BorderlessScene](https://www.google.com/search?client=firefox-b-d&q=FX-BorderlessScene)
- [JSON](https://mvnrepository.com/artifact/org.json/json)

## Main packages
- **Collection**: custom collections.
- **Configuration**: configuration classes.
- **Event**: main event classes. This package doesn't contain specific events. Those events are found among their users.
- **File**: files-related classes, such as FileType. 
- **Gui**: contains **all** gui-related classes.
- **Language**: language classes.
- **Mips**: contains the MIPS32 assembler and simulator. This package doesn't contain project-related or gui-related classes.
- **Project**: project-related classes.
- **Util**: util classes that dont't match any other package.

## Download and installation

JAMS has been succesfully built and tested on Ubuntu 18.04/20.04 and Windows 10 using Intellij IDEA.

To run the app inside the IDE use:
```bash
git clone https://github.com/gaeqs/JAMS JAMS
gradle clean run
```

To make a fat-jar build use:
```bash
//Clean is not required.
gradle bundle
```

The built jar should be found at build/libs/JAMS-X.X-X.jar.

To package the app use:
```bash
gradle clean jpackage
```

The packet app should be found at build/jpackage/JAMS-X.jar.