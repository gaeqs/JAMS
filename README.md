# JAMS
Just Another MIPS Simulator.

![Image JAMS](https://i.imgur.com/kwzl9Ta.png)

## Introduction

Jams is a modern and extensible project-based MIPS assembler and simulator written in Java.
It supports multiple themes and languages.

> Currently on an alpha state, JAMS is aimed to support multiple architectures and instruction sets, allowing other developers to expand JAMS through plugins.

## Dependencies
- JavaFX 8. (JavaFX 11 is used for standalone builds.)
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

```bash
git clone https://github.com/gaeqs/JAMS JAMS
mvn clean install
```
The built jar should be found at target/JAMS-X.X-X.jar.
