@echo off
title CodeEscape — Advanced Professional Java Puzzle Adventure
echo ========================================================
echo  CodeEscape: Advanced Professional Java Puzzle Adventure
echo ========================================================
echo Compiling project classes...
javac *.java
if errorlevel 1 (
  echo.
  echo [ERROR] Compilation failed. Ensure JDK 17+ is installed and javac is on PATH.
  pause
  exit /b 1
)
echo Launching CodeEscape...
java CodeEscape
if errorlevel 1 (
  echo.
  echo [ERROR] Application exited with an error.
  pause
  exit /b 1
)
pause
