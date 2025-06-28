# YahtzeeFX

A desktop Yahtzee game implemented in Java using JavaFX.

## Features
- Classic Yahtzee gameplay
- Keyboard controls for rolling, holding dice, and selecting scores
- Simple, modern graphical interface (JavaFX Canvas)
- Dice images (or fallback to numbers if images are missing)
- **Improved UI layout:** All components are centered and non-overlapping, with clear spacing for a professional look
- **Hold indicators (HLD) are now centered above each die and never overlap**
- **Score, status, and total positions are visually balanced**

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
- The UI is scaled for a 500x500 window and is visually balanced for clarity.
- **HLD indicators** are always centered above each die and never overlap, even if all dice are held.
- **Total score** is shown at the top left, **status messages** (e.g., GAME START) at the top right, and **Rolls/Score** at the bottom left.

## License
MIT License

---

*Original applet version by Yakkun. JavaFX port and enhancements by the community.*
