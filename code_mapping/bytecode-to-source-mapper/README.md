# bytecode-to-source-mapper
Mapping JVM-bytecode files (.class files) to corresponding Kotlin source code files

## Program use

The program extracts meta information from .class files.

Based on the received source file name and the package name, the program searches required file in the specified directory with source code files.

Package names are extracted from the source code files using the following regular expression:
```regexp
(?:^|\\s|;)package (?<package>(?:\\.?\\w+)+)(?:\\s|;|$)
```

Before package name extracting from the file, all comments and strings are deleted.

## Output

At the output, the program creates a file `bytecode_to_source_map.json` in the repo folder (or parent directory of `classesDirectory` or `sourcesDirectory` if you use single mode).

Also the program appends `lineNumbers` (map of methods to line numbers in source files) to map file.

**Mapping will be performed correctly only if the directory with bytecode files (.class files) and source code files are fully consistent.**

Map file example:
```json
{
  "myname/myrepo/classes/net/headlezz/resdiff/CLOptions.class": {
    "file": "myname/myrepo/sources/danijoo-resdiff-0eda9db/src/main/kotlin/net/headlezz/resdiff/CLOptions.kt",
    "lineNumbers": {
      "printHelp": {
        "first": 23,
        "second": 24
      },
      "hasFlag": {
        "first": 26,
        "second": 26
      },
      "getOptionValue": {
        "first": 28,
        "second": 28
      },
      "<init>": {
        "first": 10,
        "second": 19
      }
    }
  },
  "myname/myrepo/classes/net/headlezz/resdiff/ResDiffKt.class": {
    "file": "myname/myrepo/sources/danijoo-resdiff-0eda9db/src/main/kotlin/net/headlezz/resdiff/ResDiff.kt",
    "lineNumbers": {
      "main": {
        "first": 14,
        "second": 178
      },
      "printDiffNormal": {
        "first": 67,
        "second": 175
      },
      "printDiffInTable": {
        "first": 83,
        "second": 184
      },
      "getDifferencesForClass": {
        "first": 93,
        "second": 187
      },
      "getDifferences": {
        "first": 100,
        "second": 175
      },
      "genResourcePairs": {
        "first": 116,
        "second": 175
      },
      "getFiles": {
        "first": 146,
        "second": 190
      },
      "getResourcesFromFile": {
        "first": 157,
        "second": 182
      }
    }
  },
  "myname/myrepo/classes/net/headlezz/resdiff/StringResource.class": {
    "file": "myname/myrepo/sources/danijoo-resdiff-0eda9db/src/main/kotlin/net/headlezz/resdiff/Resource.kt",
    "lineNumbers": {
      "<init>": {
        "first": 57,
        "second": 57
      }
    }
  }
}
```

### Program arguments

* `-d` or `--repos_directory`: path to folder with repositories grouped by Github username.

Also you can edit main.kt and change input parameters to `classesDirectory` and `sourcesDirectory` (single mode).
Then you will have to use [RunnerByDirs](https://github.com/PetukhovVictor/bytecode-to-source-mapper/blob/master/src/main/kotlin/org/jetbrains/bytecodetosourcemapper/RunnerByDirs.kt).

### How to run

To run program you must run `main` function in `main.kt`, not forgetting to set the program arguments.

Also you can run jar file (you can download from the [release assets](https://github.com/PetukhovVictor/bytecode-to-source-mapper/releases)):
```bash
java -jar ./bytecode-to-source-mapper-0.1.jar -d ./repos
```
