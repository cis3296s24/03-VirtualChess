<a name="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Issues][issues-shield]][issues-url]



<br />
<div align="center">

<h2 align="center">Virtual Chess</h2>

  <p align="center">
    <br />
    <a href="https://github.com/cis3296s24/03-VirtualChess/tree/main/documentation"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    ·
    <a href="https://github.com/cis3296s24/03-VirtualChess/issues">Report Bug/Request Features</a>
    ·
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#abstract">Abstract</a>
    </li>
    <li>
      <a href="#team-members">Team Members</a>
    </li>
    <li>
      <a href="#downloads">Downloads</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#installation">Installation</a></li>
        <li><a href="#running-the-app">Running the App</a></li>
        <li><a href="#running-tests">Running Tests</a></li>
        <li><a href="#playing-against-stockfish">Playing Against Stockfish</a></li>
        <li><a href="#contributing">Contributing</a></li>
        <li><a href="#documentation">Documentation</a></li>
      </ul>
    </li>

  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## Abstract
Virtual Chess is a Java based application that allows two players to play against each other and also allows a single user to play a game against a computer opponent. Users have the options to select customizable settings such as a turn timer of adjustable length, the option to undo moves, and option to select their favourite theme for the chess board. Users can also turn on hints for an ideal move while playing the game. After finishing the game, users can also see their results from the leaderboard. For users, who are new to chess have the option to learn chess from this application. Our application provides a good user experiece, and is ideal for players from all skill levels.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Team Members

Aidan Cuff

Kenneth Fonseca

Matt Gibson

Nick Rucinski

Toker Ahmed

## Downloads

1. Make sure Java 21 or newer is installed on your machine
   (any distribution works, e.g. [Eclipse Temurin](https://adoptium.net/) or your OS package manager)
2. Go to the releases and download `VirtualChess.zip` from the latest release
   https://github.com/cis3296s24/03-VirtualChess/releases
3. Unzip it and run `VirtualChess.jar` (double-click, or `java -jar VirtualChess.jar` from the unzipped folder)
4. The zip includes Stockfish for macOS (Intel), Windows, and Ubuntu in a `stockfish/` folder next to the jar.
   For another platform, download Stockfish from https://stockfishchess.org/download/,
   put it in that `stockfish/` folder, and give it the same name as one of the existing files
   (see [Playing Against Stockfish](#playing-against-stockfish))

<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple example steps.


### Installation

1. Install a JDK, version 21 or newer (e.g. `sudo apt install openjdk-25-jdk` on Ubuntu,
   or [Eclipse Temurin](https://adoptium.net/) on Windows/macOS). Maven does **not** need to be installed:
   the repo includes the Maven Wrapper (`mvnw` / `mvnw.cmd`), which downloads the right version on first use.
2. Clone the repo
   ```sh
   git clone https://github.com/cis3296s24/03-VirtualChess.git
   cd 03-VirtualChess
   ```
3. Build it and run the tests (this also downloads all dependencies)
   ```sh
   ./mvnw verify          # macOS / Linux
   mvnw.cmd verify        # Windows
   ```

### Running the App

```sh
./mvnw javafx:run
```

Your settings (`config.xml`) and game history (`leaderboard.db`) are saved in a `.virtualchess` folder in your
home directory (`~/.virtualchess` on macOS/Linux, `C:\Users\<you>\.virtualchess` on Windows), not in the repo.
Delete that folder to reset both.

#### Logs

The app logs what it's doing to the console: startup, where your data is stored, games starting and ending,
settings changes, Stockfish starting (or why it couldn't), and any errors with full details. For more detail,
including every move and every command sent to Stockfish, turn on debug logging:

```sh
./mvnw javafx:run -Dlog.level=debug
```

Log levels and format are set in `src/main/resources/simplelogger.properties`. In code, get a logger with
`LoggerFactory.getLogger(YourClass.class)` (SLF4J).

### Running Tests

```sh
./mvnw test
```

Tests live in `src/test/java` and use JUnit. In VS Code or IntelliJ you can also run them from the editor's test panel.

### Playing Against Stockfish

Player-vs-computer mode uses the [Stockfish](https://stockfishchess.org/) engine, which is **not** included in the
repository. The app looks for it in a `stockfish/` folder in the directory you launch from (the repo root when using
`./mvnw javafx:run`), under one of these exact names:

| OS      | Expected path                                 |
|---------|-----------------------------------------------|
| Linux   | `stockfish/stockfish-ubuntu-x86-64-avx2`      |
| Windows | `stockfish/stockfish-windows-x86-64-avx2.exe` |
| macOS   | `stockfish/stockfish-macos-x86-64-avx2`       |

The easiest way to get them is to copy the `stockfish/` folder out of the latest release's `VirtualChess.zip`.
On Linux and macOS, make sure the file is executable (`chmod +x stockfish/*`).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Contributing

How to contribute to the project

0. See Installation
1. Create your feature branch from `main` (`git switch -c YourName/feature-name`)
2. Commit your changes (`git commit -m "Add some feature"`)
3. Make sure the build and tests pass (`./mvnw verify`)
4. Push the branch (`git push -u origin YourName/feature-name`)
5. Open a pull request. `main` is protected, so all changes go through a pull request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Documentation

Project documentation (UML diagrams and user personas): https://github.com/cis3296s24/03-VirtualChess/tree/main/documentation

API documentation (Javadoc) is generated from the source rather than kept in the repo, so it's always up to date:

```sh
./mvnw javadoc:javadoc
```

Then open `target/reports/apidocs/index.html` in a browser.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

[contributors-shield]: https://img.shields.io/github/contributors/cis3296s24/03-VirtualChess.svg?style=for-the-badge
[contributors-url]: https://github.com/cis3296s24/03-VirtualChess/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/cis3296s24/03-VirtualChess.svg?style=for-the-badge
[forks-url]: https://github.com/cis3296s24/03-VirtualChess/network/members
[issues-shield]: https://img.shields.io/github/issues/cis3296s24/03-VirtualChess.svg?style=for-the-badge
[issues-url]: https://github.com/cis3296s24/03-VirtualChess/issues
