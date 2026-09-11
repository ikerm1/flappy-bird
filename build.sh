#!/usr/bin/env bash
# ============================================================
#  build.sh — compile and run FlappyBird
# ============================================================
set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"
SRC="$ROOT/src"
OUT="$ROOT/out"

echo "==> Cleaning output directory..."
rm -rf "$OUT"
mkdir -p "$OUT"

echo "==> Compiling Java sources..."
find "$SRC" -name "*.java" | xargs javac -d "$OUT" -sourcepath "$SRC"

echo "==> Copying assets into classpath..."
cp -r "$SRC/flappybird/assets" "$OUT/flappybird/"

echo "==> Build successful! Launching game..."
cd "$ROOT"
java -cp "$OUT" flappybird.Main
