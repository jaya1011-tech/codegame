#!/usr/bin/env bash
echo "========================================================"
echo " CodeEscape: Advanced Professional Java Puzzle Adventure"
echo "========================================================"
echo "Compiling project classes..."
javac *.java
if [ $? -ne 0 ]; then
  echo ""
  echo "[ERROR] Compilation failed. Ensure JDK 17+ is installed."
  exit 1
fi
echo "Launching CodeEscape..."
java CodeEscape
