# YahtzeeFX

A desktop Yahtzee game implemented in Java using JavaFX.

## Features
- Classic Yahtzee gameplay
- Keyboard controls for rolling, holding dice, and selecting scores
- Simple graphical interface (JavaFX Canvas)
- Dice images (or fallback to numbers if images are missing)

## Controls
- **1-5**: Toggle hold for each die
- **Space**: Roll/Stop dice
- **Up/Down**: Select score category
- **Enter**: Confirm score
- **Escape**: Exit game

## Requirements
- Java 11 or later
- JavaFX SDK (tested with JavaFX 24.0.1)

## How to Run
1. Download and install the [JavaFX SDK](https://openjfx.io/).
2. Place your dice images (`ace.gif`, `two.gif`, `three.gif`, `four.gif`, `five.gif`, `six.gif`) in the resources directory or adjust the code to use your own images.
3. Compile:
   ```sh
   javac --module-path "C:/javafx-sdk-24.0.1/lib" --add-modules javafx.controls,javafx.graphics YahtzeeFX.java
   ```
4. Run:
   ```sh
   java --module-path "C:/javafx-sdk-24.0.1/lib" --add-modules javafx.controls,javafx.graphics YahtzeeFX
   ```

## Notes
- If you do not have dice images, the game will display numbers instead.
- The UI is scaled for a 400x400 window.

## License
MIT License

---

*Original applet version by Yakkun. JavaFX port and enhancements by the community.*
