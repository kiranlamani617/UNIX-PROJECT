@echo off
echo =================================================================
echo   Compiling & Starting Restaurant Management System Terminal App
echo =================================================================
if not exist bin mkdir bin
javac -d bin src/ds/*.java src/model/*.java src/service/*.java src/Main.java src/TestRunner.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
java -cp bin Main
