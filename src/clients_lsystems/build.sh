#!/usr/bin/env bash

# ============================================================
#   Build + run a Java file (Linux / macOS / BSD)
#   Usage:  ./build.sh MyProgram.java
# ============================================================

if [ -z "$1" ]; then
    echo "Usage: $0 filename.java"
    exit 1
fi

SRC="$1"
BASE="${SRC%.java}"

echo
echo "=== Compiling $SRC ==="

javac -g -Xlint -Xdiags:verbose -cp .:.. "$SRC"
STATUS=$?

if [ $STATUS -ne 0 ]; then
    echo
    echo "*** Compilation failed ***"
    exit $STATUS
fi

echo
echo "=== Running $BASE ==="
java -Dsun.java2d.uiScale=1.0 -cp .:.. "$BASE"
