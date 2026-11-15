#!/usr/bin/env bash
# Builds a self-contained VirtualChess app for the operating system this runs on: its own Java runtime,
# JavaFX, and the Stockfish engine, so the user needs to install nothing. Output: target/dist/VirtualChess
# (a VirtualChess.app folder on macOS). Needs a JDK 21+ with jpackage; Maven comes from ./mvnw.
#
#   scripts/package-app.sh
#
# Why jpackage is fed a plain classpath rather than modules: sqlite-jdbc is an "automatic module", which
# jlink refuses to link. Main.java exists so JavaFX can start from the classpath.
set -euo pipefail
cd "$(dirname "$0")/.."

NAME="VirtualChess"
input="target/jpackage/input"
rm -rf "$input" target/dist
mkdir -p "$input"

echo "== Building the jar and collecting its dependencies"
./mvnw -q package -DskipTests
./mvnw -q org.apache.maven.plugins:maven-dependency-plugin:3.8.1:copy-dependencies \
  -DincludeScope=runtime -DoutputDirectory="$input"
cp target/VirtualChess-*-SNAPSHOT.jar "$input/$NAME.jar" 2>/dev/null || cp target/VirtualChess-*.jar "$input/$NAME.jar"

echo "== Fetching Stockfish"
scripts/fetch-stockfish.sh "" target/jpackage/stockfish  # "" = this machine's platform

echo "== Packaging"
jpackage --type app-image --name "$NAME" \
  --input "$input" --main-jar "$NAME.jar" --main-class com.cis3296.virtualchess.Main \
  --add-modules java.base,java.logging,java.xml,java.desktop,java.sql,java.scripting,java.prefs,jdk.unsupported \
  --java-options '--enable-native-access=ALL-UNNAMED' \
  --jlink-options '--no-man-pages --no-header-files' \
  --app-content target/jpackage/stockfish \
  --dest target/dist

# The app finds its engine in a "stockfish" folder up to three levels above its jar (see
# Stockfish.defaultExecutable); each OS lays the package out differently, so check it landed somewhere findable
jar="$(find target/dist -name "$NAME.jar" | head -n1)"
engine="$(find target/dist -path '*/stockfish/stockfish*' -type f \( -name stockfish -o -name stockfish.exe \) | head -n1)"
[[ -n "$jar" && -n "$engine" ]] || { echo "The package is missing the jar or the engine" >&2; exit 1; }
folder="$(dirname "$jar")"; found=no
for _ in 1 2 3; do
  [[ "$(cd "$(dirname "$engine")/.." && pwd)" == "$(cd "$folder" && pwd)" ]] && found=yes
  folder="$folder/.."
done
[[ "$found" == yes ]] || { echo "Stockfish was packaged at $engine, where the app won't look for it" >&2; exit 1; }
echo "== Done: $(du -sh target/dist | cut -f1) in target/dist (engine at $engine)"
