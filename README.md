# YahtzeeFX - Enhanced Edition

A modern, feature-rich Yahtzee game built with JavaFX, featuring an enhanced UI, comprehensive game features, and detailed debug tracing.

![YahtzeeFX](https://img.shields.io/badge/Java-11+-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-24.0.1-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

## 🎮 Features

### Core Gameplay
- **Classic Yahtzee Rules**: Complete implementation of standard Yahtzee scoring
- **13 Scoring Categories**: Aces through Sixes, 3/4 of a Kind, Full House, Straights, Chance, and Yahtzee
- **Upper Section Bonus**: Automatic 35-point bonus when upper section exceeds 63 points
- **Real-time Scoring**: Instant calculation of potential scores for each category

### Enhanced User Interface
- **Modern Dark Theme**: Professional color scheme with excellent contrast
- **Visual Feedback**: 
  - Red borders and "HELD" indicators for held dice
  - Highlighted selected categories
  - Clear score display with actual vs. potential scores
- **Responsive Layout**: 600x600 window with organized sections
- **Status Indicators**: Rolls remaining, potential scores, and game state

### Advanced Game Features
- **Pause/Resume**: Pause the game at any time (P key)
- **New Game**: Start fresh anytime (N key)
- **Tooltips System**: Toggle detailed category descriptions (T key)
- **Dice Value Display**: Toggle numerical dice values (D key)
- **Enhanced Controls**: Comprehensive keyboard shortcuts

### Debug & Development
- **Comprehensive Logging**: Detailed console output for game logic tracing
- **Error Handling**: Robust fallback systems for rendering issues
- **State Validation**: Complete game state tracking and validation

## 🎯 Game Controls

| Key | Action |
|-----|--------|
| **1-5** | Toggle hold for corresponding die |
| **SPACE** | Roll dice / Stop rolling |
| **UP/DOWN** | Navigate between score categories |
| **ENTER** | Confirm score for selected category |
| **P** | Pause/Resume game |
| **N** | Start new game |
| **T** | Toggle tooltips |
| **D** | Toggle dice value display |
| **ESC** | Exit game |

## 🚀 Quick Start

### Prerequisites
- **Java 11** or later
- **JavaFX SDK 24.0.1** (or compatible version)

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/yahtzee.git
   cd yahtzee
   ```

2. **Download JavaFX SDK**
   - Visit [OpenJFX](https://openjfx.io/)
   - Download JavaFX SDK for your platform
   - Extract to a known location (e.g., `C:/javafx-sdk-24.0.1/`)

3. **Prepare dice images** (optional)
   - Place dice images in `resources/` folder:
     - `ace.gif`, `two.gif`, `three.gif`, `four.gif`, `five.gif`, `six.gif`
   - If images are missing, the game will display numbers instead

### Compilation & Execution

**Windows:**
```bash
# Compile
javac --module-path "C:/javafx-sdk-24.0.1/lib" --add-modules javafx.controls,javafx.graphics YahtzeeFX.java

# Run
java --module-path "C:/javafx-sdk-24.0.1/lib" --add-modules javafx.controls,javafx.graphics YahtzeeFX
```

**macOS/Linux:**
```bash
# Compile
javac --module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.graphics YahtzeeFX.java

# Run
java --module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.graphics YahtzeeFX
```

## 🎲 Game Rules

### Upper Section (Aces - Sixes)
- Score the sum of dice showing the specified number
- **Bonus**: 35 points if upper section total exceeds 63

### Lower Section
- **3 of a Kind**: Sum of all dice if 3+ same numbers
- **4 of a Kind**: Sum of all dice if 4+ same numbers  
- **Full House**: 25 points for 3+2 of the same numbers
- **Small Straight**: 30 points for 4 consecutive numbers
- **Large Straight**: 40 points for 5 consecutive numbers
- **Chance**: Sum of all dice
- **Yahtzee**: 50 points for 5 of the same number

## 🔧 Development Features

### Debug Tracing
The game includes comprehensive console logging for development and debugging:

```bash
=== KEY PRESSED: SPACE (Game Status: 1) ===
SPACE pressed - Rolls left: 3, goflush: false, flushout: false
Starting roll, chance reduced to: 2
Rolling dice - Held: [false,false,false,false,false]
Dice 1 rolled: 3 -> 6
Score2 calculation - Original dice: [6,4,2,1,5]
Pair check status: 0 (0=none, 1=3kind, 2=4kind, 3=full, 4=yahtzee)
=== ENTERING SCORE ===
Category: Aces (used: false, initflag: false)
Category score: 1, Total: 1, Bonus count: 1
```

### Error Handling
- **Rendering Fallbacks**: Automatic fallback to basic rendering if advanced features fail
- **Thread Safety**: Robust error handling in game loop
- **State Validation**: Comprehensive validation of game state transitions

## 📁 Project Structure

```
yahtzee/
├── YahtzeeFX.java          # Main application file
├── yahtzee.java            # Original applet version
├── compile.bat             # Windows compilation script
├── run.bat                 # Windows execution script
├── resources/              # Dice images directory
│   ├── ace.gif
│   ├── two.gif
│   ├── three.gif
│   ├── four.gif
│   ├── five.gif
│   └── six.gif
├── README.md               # This file
└── LICENSE                 # MIT License
```

## 🛠️ Technical Details

### Architecture
- **JavaFX Canvas**: Custom rendering for optimal performance
- **Threaded Game Loop**: 50ms refresh rate for smooth animations
- **State Machine**: Clean separation of game states (Title, Game, Pause, End)
- **Modular Design**: Separate methods for UI components and game logic

### Compatibility
- **JavaFX 24.0.1**: Optimized for latest JavaFX version
- **Cross-Platform**: Windows, macOS, and Linux support
- **Backward Compatible**: Works with JavaFX 11+ versions

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow existing code style and naming conventions
- Add debug tracing for new features
- Test on multiple JavaFX versions
- Update documentation for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Original Applet**: Created by Yakkun
- **JavaFX Port**: Community contribution
- **Enhanced Features**: Modern UI/UX improvements and game features
- **JavaFX Team**: For the excellent JavaFX framework

## 📞 Support

- **Issues**: Report bugs and feature requests via GitHub Issues
- **Discussions**: Join community discussions for tips and help
- **Documentation**: Check this README and inline code comments

---

**Enjoy playing YahtzeeFX! 🎲✨**
