# crafty-interpreter

Code for Crafting Interpreters.

### Requirements
Generally each folder containing code will have a Makefile that will build (and perhaps run) the project. Any needed tools to run these files is explained here, for Mac anyway.
#### Java/Kotlin
I chose to use Kotlin for the Java parts of the book. You'll need the kotlin compiler (kotlinc) and a jre installed to run the jars it generates. Using brew to install the kotlin compiler should be enough to get both of those.
```brew install kotlin```

#### C
Just needs gcc, which you probably already have.

#### Kotlin/Native
I decided to write some of the C parts in Kotlin as well and compile them with the Kotlin/Native compiler. Mostly I'm just curious where the annoyances show up but also in how it performs compared to the C implementation. Currently it's a bit of a headache to get it installed next to the regular Kotlin compiler, here's what I had to do.
1. Install Xcode. We need the command line tools and it seems those just come bundled with Xcode now?
2. If you have `kotlinc` installed, delete it. For now, the kotlin native installer installs a version which conflicts.
    ```
    brew uninstall kotlin
    ```
3. Install kotlin-native.
    ```
    brew install --cask kotlin-native
    ```
4. The version of `kotlinc` that cask installs spits out a bunch of warnings and stuff, so I deleted the symlink to it and re-installed the main version of `kotlinc`.
    ```
    rm /usr/local/bin/kotlinc
    brew install kotlin
    ```
