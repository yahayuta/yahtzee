@echo off
REM Run YahtzeeFX with JavaFX
set JAVAFX_HOME=C:\javafx-sdk-24.0.1
java --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls,javafx.graphics YahtzeeFX
