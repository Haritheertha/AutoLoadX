@echo off
echo === AutoLoadX - GUI Version ===
echo Building and running GUI...
echo.

cd /d "C:\Users\harit\source\AutoLoadX"

if not exist "build" mkdir build
if not exist "build\classes" mkdir build\classes

echo Compiling all classes...
javac -d build\classes src\main\java\com\loadtesting\phase1\model\*.java
javac -cp build\classes -d build\classes src\main\java\com\loadtesting\phase1\service\*.java
javac -cp build\classes -d build\classes src\main\java\com\loadtesting\phase1\gui\LoadTestGUI.java

if %errorlevel% equ 0 (
    echo.
    echo GUI compiled successfully!
    echo Starting Load Testing GUI...
    echo.
    java -cp build\classes com.loadtesting.phase1.gui.LoadTestGUI
) else (
    echo.
    echo Compilation failed. Check errors above.
)

pause