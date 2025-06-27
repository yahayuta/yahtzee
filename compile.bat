@echo off
REM Compile YahtzeeFX.java with JavaFX
set JAVAFX_HOME=C:\javafx-sdk-24.0.1
javac --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls,javafx.graphics YahtzeeFX.java
if %ERRORLEVEL% neq 0 (
    echo Compile failed.
    exit /b %ERRORLEVEL%
)
echo Compile successful.
