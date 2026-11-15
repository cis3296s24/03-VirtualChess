#!/usr/bin/env bash
# Downloads the pinned Stockfish release for a platform, verifies its SHA-256, and installs just the engine
# (plus its license) as <dest>/stockfish (<dest>/stockfish.exe on Windows).
#
#   scripts/fetch-stockfish.sh [platform] [dest]
#
#   platform  linux-x86-64 | linux-arm64 | macos | windows-x86-64 | windows-arm64
#             (default: detected from this machine)
#   dest      default: stockfish
#
# To upgrade Stockfish, change VERSION and the hashes below (the release page lists a SHA-256 per file:
# https://github.com/official-stockfish/Stockfish/releases). The "universal" builds pick the right CPU
# instructions at run time, so one build per platform works on old and new CPUs alike.
set -euo pipefail

VERSION="sf_19"
BASE_URL="https://github.com/official-stockfish/Stockfish/releases/download/${VERSION}"

# platform -> "archive name|sha256"
declare -A ASSETS=(
  [linux-x86-64]="stockfish-linux-x86-64-universal.tar.gz|9defc0d4e55d49c65a6d042f3e571a39fcea499ade6dbe741b53b8c65e03611f"
  [linux-arm64]="stockfish-linux-arm64-universal.tar.gz|fe26cfd1d9db4c8af3d21e24d9ff34cacb31c1f940085a7583da11796f2bac01"
  [macos]="stockfish-macos-universal.tar.gz|a1f0e3bcc5a6927a11fe6fc8e54a779754645f3c2bae2cf13420fd1957adaa77"
  [windows-x86-64]="stockfish-windows-x86-64-universal.zip|3c8bf1f9ea66a09350a40df4f632288285ac206d99f33ab5842c408fc30b48a7"
  [windows-arm64]="stockfish-windows-arm64-universal.zip|8372ad3f0d7276deb2c70f801f541ec7db463219fc6d9c7592864e542aa4f401"
)

detect_platform() {
  local os arch
  os="$(uname -s)"; arch="$(uname -m)"
  case "$os" in
    Linux)  [[ "$arch" == aarch64 || "$arch" == arm64 ]] && echo linux-arm64 || echo linux-x86-64 ;;
    Darwin) echo macos ;;
    MINGW*|MSYS*|CYGWIN*) [[ "$arch" == aarch64 || "$arch" == arm64 ]] && echo windows-arm64 || echo windows-x86-64 ;;
    *) echo "Unsupported OS: $os" >&2; return 1 ;;
  esac
}

sha256_of() {
  if command -v sha256sum >/dev/null; then sha256sum "$1" | cut -d' ' -f1; else shasum -a 256 "$1" | cut -d' ' -f1; fi
}

platform="${1:-$(detect_platform)}"
dest="${2:-stockfish}"
[[ -n "${ASSETS[$platform]:-}" ]] || { echo "Unknown platform: $platform (try: ${!ASSETS[*]})" >&2; exit 1; }
IFS='|' read -r archive expected <<<"${ASSETS[$platform]}"
exe="stockfish"; [[ "$platform" == windows-* ]] && exe="stockfish.exe"

if [[ -x "$dest/$exe" && -f "$dest/.version" && "$(cat "$dest/.version")" == "$VERSION $platform" ]]; then
  echo "Stockfish $VERSION ($platform) is already in $dest/"
  exit 0
fi

work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT

echo "Downloading $archive ($VERSION)..."
curl -fSL --retry 3 --progress-bar -o "$work/$archive" "$BASE_URL/$archive"

actual="$(sha256_of "$work/$archive")"
if [[ "$actual" != "$expected" ]]; then
  echo "Checksum mismatch for $archive" >&2
  echo "  expected $expected" >&2
  echo "  actual   $actual" >&2
  exit 1
fi

mkdir "$work/x"
case "$archive" in
  *.tar.gz) tar -xzf "$work/$archive" -C "$work/x" ;;
  *.zip)    if command -v unzip >/dev/null; then unzip -q "$work/$archive" -d "$work/x"; else 7z x -y -o"$work/x" "$work/$archive" >/dev/null; fi ;;
esac

# The archive holds a stockfish/ folder with the engine (named like stockfish-linux-x86-64-universal), the
# license, and the full source; keep only the engine and the license
binary="$(find "$work/x" -type f \( -name 'stockfish-*' -o -name 'stockfish' \) ! -path '*/src/*' ! -path '*/wiki/*' ! -name '*.md' | head -n1)"
[[ -n "$binary" ]] || { echo "Couldn't find the engine inside $archive" >&2; exit 1; }
license="$(find "$work/x" -type f -name 'Copying.txt' | head -n1)"

mkdir -p "$dest"
cp "$binary" "$dest/$exe"
chmod +x "$dest/$exe"
[[ -n "$license" ]] && cp "$license" "$dest/Copying.txt"
echo "$VERSION $platform" > "$dest/.version"
echo "Installed Stockfish $VERSION to $dest/$exe"
