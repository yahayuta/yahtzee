import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Random;
import java.io.File;

public class YahtzeeFX extends Application implements Runnable {
    // Constants
    final int MSEC_WAIT = 50;
    final int GAME_TITLE = 0;
    final int GAME_OPEN = 1;
    final int GAME_END = 2;
    final int GAME_PAUSED = 3;
    final int SCR_1 = 0;
    final int SCR_2 = 1;
    final int SCR_3 = 2;
    final int SCR_4 = 3;
    final int SCR_5 = 4;
    final int SCR_6 = 5;
    final int SCR_3CARD = 6;
    final int SCR_4CARD = 7;
    final int SCR_FULL = 8;
    final int SCR_SSTGHT = 9;
    final int SCR_LSTGHT = 10;
    final int SCR_CHANCE = 11;
    final int SCR_YAHTZEE = 12;
    final int PCK_3CARD = 1;
    final int PCK_4CARD = 2;
    final int PCK_FULL = 3;
    final int PCK_YAHTZEE = 4;

    // Object variables
    Image[] card = new Image[6];
    Random rnd = new Random();
    Thread thIyahtzee = null;
    String[] scoredisp = {"Aces", "Twos", "Threes", "Fours", "Fives", "Sixes", "3 of a Kind", "4 of a Kind", "Full House", "Small Straight", "Large Straight", "Chance", "Yahtzee"};
    String[] categoryDescriptions = {
        "Sum of all 1s", "Sum of all 2s", "Sum of all 3s", "Sum of all 4s", "Sum of all 5s", "Sum of all 6s",
        "Sum of all dice if 3+ same", "Sum of all dice if 4+ same", "25 points for 3+2", "30 points for 4 in sequence", "40 points for 5 in sequence", "Sum of all dice", "50 points for 5 same"
    };

    // Game state variables
    int gameStatus = GAME_TITLE;
    int chance = 3;
    int[] cardnum = new int[5];
    int currentscorechkd = 0;
    int selectedscr1 = 0;
    int selectedscr2 = 0;
    int gameleft = 13;
    int bonuscount = 0;
    int total = 0;
    int X = 5;
    int X2 = 5;
    int Y2 = 20;
    
    // Score tracking for each category
    int[] categoryScores = new int[13];
    
    // New game features
    boolean showTooltips = false;
    boolean showDiceValues = true;
    int tooltipTimer = 0;
    String currentTooltip = "";

    // Flags
    boolean goflush = false;
    boolean flushout = false;
    boolean[] hold = new boolean[5];
    boolean[] scoreused = new boolean[13];
    boolean initflag = false;
    boolean bonusflag = false;

    // JavaFX
    Canvas canvas;
    GraphicsContext gc;

    @Override
    /**
     * JavaFX entry point. Sets up the main window, canvas, event handlers, and game state.
     * @param primaryStage The main application window
     */
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        int CANVAS_WIDTH = 600;
        int CANVAS_HEIGHT = 600;
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        
        // Set a basic font that should work with JavaFX 24.0.1
        try {
            gc.setFont(javafx.scene.text.Font.font(16));
        } catch (Exception e) {
            // If that fails, use default font
        }
        
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, CANVAS_WIDTH, CANVAS_HEIGHT);
        primaryStage.setTitle("YahtzeeFX - Enhanced Edition");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Standard: Load dice images from resources folder using file: URI
        for (int i = 0; i < 6; i++) {
            String path = "resources/" + getDieName(i) + ".gif";
            File f = new File(path);
            if (f.exists()) {
                card[i] = new Image("file:" + f.getAbsolutePath().replace("\\", "/"));
            } else {
                card[i] = null;
            }
        }
        


        for (int i = 0; i < 5; i++) {
            cardnum[i] = Math.abs(rnd.nextInt()) % 6;
        }

        scene.setOnKeyPressed(e -> handleKey(e.getCode()));

        thIyahtzee = new Thread(this);
        thIyahtzee.setDaemon(true);
        thIyahtzee.start();
        chkscore1();
        chkscore2();
        draw();
    }

    /**
     * Returns the filename base for a die face index.
     * @param i The die index (0-5)
     * @return The string name for the die image
     */
    private String getDieName(int i) {
        switch (i) {
            case 0: return "ace";
            case 1: return "two";
            case 2: return "three";
            case 3: return "four";
            case 4: return "five";
            case 5: return "six";
            default: return "ace";
        }
    }

    /**
     * Handles keyboard input for all game states (title, open, end, paused).
     * @param code The KeyCode pressed
     */
    private void handleKey(KeyCode code) {
        System.out.println("=== KEY PRESSED: " + code + " (Game Status: " + gameStatus + ") ===");
        
        switch (gameStatus) {
            case GAME_TITLE:
                if (code == KeyCode.ENTER) {
                    System.out.println("Starting game from title screen");
                    gameStatus = GAME_OPEN;
                }
                break;
            case GAME_END:
                if (code == KeyCode.ENTER) {
                    System.out.println("Starting new game from end screen");
                    startNewGame();
                }
                break;
            case GAME_PAUSED:
                if (code == KeyCode.P) {
                    System.out.println("Resuming game from pause");
                    gameStatus = GAME_OPEN;
                } else if (code == KeyCode.N) {
                    System.out.println("Starting new game from pause menu");
                    startNewGame();
                }
                break;
            case GAME_OPEN:
                if (code == KeyCode.DIGIT1 && !goflush) {
                    hold[0] = !hold[0];
                    System.out.println("Dice 1 hold toggled: " + hold[0]);
                }
                if (code == KeyCode.DIGIT2 && !goflush) {
                    hold[1] = !hold[1];
                    System.out.println("Dice 2 hold toggled: " + hold[1]);
                }
                if (code == KeyCode.DIGIT3 && !goflush) {
                    hold[2] = !hold[2];
                    System.out.println("Dice 3 hold toggled: " + hold[2]);
                }
                if (code == KeyCode.DIGIT4 && !goflush) {
                    hold[3] = !hold[3];
                    System.out.println("Dice 4 hold toggled: " + hold[3]);
                }
                if (code == KeyCode.DIGIT5 && !goflush) {
                    hold[4] = !hold[4];
                    System.out.println("Dice 5 hold toggled: " + hold[4]);
                }
                if (code == KeyCode.SPACE) {
                    System.out.println("SPACE pressed - Rolls left: " + chance + ", goflush: " + goflush + ", flushout: " + flushout);
                    if (chance > 0 && !flushout) {
                        if (!goflush) {
                            goflush = true;
                            if (!initflag) {
                                chance--;
                                System.out.println("Starting roll, chance reduced to: " + chance);
                            } else {
                                initflag = false;
                                System.out.println("Starting roll, initflag set to false");
                            }
                        } else if (goflush) {
                            goflush = false;
                            System.out.println("Stopping roll, calculating scores");
                            chkscore1();
                            chkscore2();
                        }
                    } else if (goflush) {
                        goflush = false;
                        System.out.println("Stopping roll (no more chances), calculating scores");
                        chkscore1();
                        chkscore2();
                    } else {
                        flushout = true;
                        System.out.println("No more rolls available, flushout set to true");
                    }
                }
                if (code == KeyCode.UP) {
                    System.out.println("Moving UP from category " + currentscorechkd + " (" + scoredisp[currentscorechkd] + ")");
                    currentscorechkd--;
                    if (currentscorechkd < SCR_1) currentscorechkd = SCR_YAHTZEE;
                    while (scoreused[currentscorechkd]) {
                        currentscorechkd--;
                        if (currentscorechkd < SCR_1) currentscorechkd = SCR_YAHTZEE;
                    }
                    System.out.println("Selected category: " + currentscorechkd + " (" + scoredisp[currentscorechkd] + ")");
                    chkscore1();
                    chkscore2();
                    showTooltip();
                }
                if (code == KeyCode.DOWN) {
                    System.out.println("Moving DOWN from category " + currentscorechkd + " (" + scoredisp[currentscorechkd] + ")");
                    currentscorechkd++;
                    if (currentscorechkd > SCR_YAHTZEE) currentscorechkd = SCR_1;
                    while (scoreused[currentscorechkd]) {
                        currentscorechkd++;
                        if (currentscorechkd > SCR_YAHTZEE) currentscorechkd = SCR_1;
                    }
                    System.out.println("Selected category: " + currentscorechkd + " (" + scoredisp[currentscorechkd] + ")");
                    chkscore1();
                    chkscore2();
                    showTooltip();
                }
                if (code == KeyCode.ENTER) {
                    System.out.println("ENTER pressed - attempting to score category " + currentscorechkd);
                    enterscr();
                }
                if (code == KeyCode.P) {
                    System.out.println("Pausing game");
                    gameStatus = GAME_PAUSED;
                }
                if (code == KeyCode.N) {
                    System.out.println("Starting new game from game screen");
                    startNewGame();
                }
                if (code == KeyCode.T) {
                    showTooltips = !showTooltips;
                    System.out.println("Tooltips toggled: " + showTooltips);
                }
                if (code == KeyCode.D) {
                    showDiceValues = !showDiceValues;
                    System.out.println("Dice values toggled: " + showDiceValues);
                }
                break;
            default:
                System.out.println("Unknown game status: " + gameStatus);
                break;
        }
        if (code == KeyCode.ESCAPE) {
            System.out.println("ESC pressed - exiting game");
            System.exit(0);
        }
        draw();
    }
    
    /**
     * Starts a new game, resetting all game state
     */
    private void startNewGame() {
        System.out.println("=== STARTING NEW GAME ===");
        for (int i = 0; i < 13; i++) {
            scoreused[i] = false;
            categoryScores[i] = 0;
        }
        gameStatus = GAME_OPEN;
        total = 0;
        selectedscr1 = 0;
        selectedscr2 = 0;
        bonuscount = 0;
        bonusflag = false;
        chance = 3;
        gameleft = 13;
        initflag = false;
        goflush = false;
        flushout = false;
        for (int i = 0; i < 5; i++) {
            hold[i] = false;
            cardnum[i] = Math.abs(rnd.nextInt()) % 6;
        }
        currentscorechkd = SCR_1;
        System.out.println("New game initialized - Dice: [" + (cardnum[0]+1) + "," + (cardnum[1]+1) + "," + (cardnum[2]+1) + "," + (cardnum[3]+1) + "," + (cardnum[4]+1) + "]");
        chkscore1();
        chkscore2();
        showTooltip();
    }
    
    /**
     * Shows tooltip for current category
     */
    private void showTooltip() {
        if (showTooltips) {
            currentTooltip = categoryDescriptions[currentscorechkd];
            tooltipTimer = 60; // Show for 3 seconds (60 * 50ms)
        }
    }

    @Override
    /**
     * Main game loop thread. Periodically updates the game state and redraws the UI.
     */
    public void run() {
        while (true) {
            try {
                Thread.sleep(MSEC_WAIT);
            } catch (Throwable th) {
                break;
            }
            try {
                flushcards();
                updateTooltips();
                draw();
            } catch (Exception e) {
                // Continue running even if there's an error
            }
        }
    }
    
    /**
     * Updates tooltip timing
     */
    private void updateTooltips() {
        if (tooltipTimer > 0) {
            tooltipTimer--;
            if (tooltipTimer == 0) {
                currentTooltip = "";
            }
        }
    }

    /**
     * Rolls all non-held dice if rolling is active.
     */
    public void flushcards() {
        if (goflush) {
            System.out.println("Rolling dice - Held: [" + hold[0] + "," + hold[1] + "," + hold[2] + "," + hold[3] + "," + hold[4] + "]");
            for (int i = 0; i < 5; i++) {
                if (!hold[i]) {
                    int oldValue = cardnum[i];
                    cardnum[i] = Math.abs(rnd.nextInt()) % 6;
                    System.out.println("Dice " + (i+1) + " rolled: " + (oldValue+1) + " -> " + (cardnum[i]+1));
                }
            }
            System.out.println("New dice values: [" + (cardnum[0]+1) + "," + (cardnum[1]+1) + "," + (cardnum[2]+1) + "," + (cardnum[3]+1) + "," + (cardnum[4]+1) + "]");
        }
    }

    /**
     * Calculates the primary score for the currently selected upper section category.
     */
    public void chkscore1() {
        if (currentscorechkd == SCR_1) selectedscr1 = chknums(1, SCR_1, cardnum);
        else if (currentscorechkd == SCR_2) selectedscr1 = chknums(2, SCR_2, cardnum);
        else if (currentscorechkd == SCR_3) selectedscr1 = chknums(3, SCR_3, cardnum);
        else if (currentscorechkd == SCR_4) selectedscr1 = chknums(4, SCR_4, cardnum);
        else if (currentscorechkd == SCR_5) selectedscr1 = chknums(5, SCR_5, cardnum);
        else if (currentscorechkd == SCR_6) selectedscr1 = chknums(6, SCR_6, cardnum);
        else selectedscr1 = 0;
        
        System.out.println("Score1 calculated for " + scoredisp[currentscorechkd] + ": " + selectedscr1);
    }

    /**
     * Calculates the score for the upper section (aces through sixes).
     * @param actscr The face value to check (1-6)
     * @param cdnum The score category index
     * @param inscr The dice array
     * @return The score for this category
     */
    public int chknums(int actscr, int cdnum, int[] inscr) {
        int scre = 0;
        for (int i = 0; i < 5; i++) {
            if (inscr[i] == actscr - 1) scre += actscr;
        }
        System.out.println("chknums: Looking for " + actscr + "s, found " + (scre/actscr) + " dice, score = " + scre);
        return scre;
    }

    /**
     * Calculates the secondary score (e.g., 3/4 of a kind, full house, straights, chance, Yahtzee)
     * for the currently selected score category.
     */
    public void chkscore2() {
        int[] subscr = new int[8];
        int hldscr = 0;
        int pairChkdStatus = 0;
        for (int c = 0; c < 5; c++) subscr[c] = cardnum[c];
        
        System.out.println("Score2 calculation - Original dice: [" + (subscr[0]+1) + "," + (subscr[1]+1) + "," + (subscr[2]+1) + "," + (subscr[3]+1) + "," + (subscr[4]+1) + "]");
        
        for (int i = 0; i < 4; i++) {
            for (int ii = 0; ii < 4; ii++) {
                int first = subscr[ii];
                int second = subscr[ii + 1];
                if (first > second) {
                    subscr[ii] = second;
                    subscr[ii + 1] = first;
                }
            }
        }
        
        System.out.println("Score2 calculation - Sorted dice: [" + (subscr[0]+1) + "," + (subscr[1]+1) + "," + (subscr[2]+1) + "," + (subscr[3]+1) + "," + (subscr[4]+1) + "]");
        
        pairChkdStatus = rtnchpair(subscr);
        System.out.println("Pair check status: " + pairChkdStatus + " (0=none, 1=3kind, 2=4kind, 3=full, 4=yahtzee)");
        
        if (currentscorechkd == SCR_3CARD) {
            if (pairChkdStatus == PCK_3CARD || pairChkdStatus == PCK_4CARD || pairChkdStatus == PCK_FULL || pairChkdStatus == PCK_YAHTZEE) {
                for (int i12 = 0; i12 < 5; i12++) {
                    hldscr += subscr[i12];
                    hldscr++;
                }
                selectedscr2 = hldscr;
            } else selectedscr2 = 0;
        } else if (currentscorechkd == SCR_4CARD) {
            if (pairChkdStatus == PCK_4CARD || pairChkdStatus == PCK_YAHTZEE) {
                for (int i22 = 0; i22 < 5; i22++) {
                    hldscr += subscr[i22];
                    hldscr++;
                }
                selectedscr2 = hldscr;
            } else selectedscr2 = 0;
        } else if (currentscorechkd == SCR_FULL) {
            if (pairChkdStatus == PCK_FULL) selectedscr2 = 25;
            else selectedscr2 = 0;
        } else if (currentscorechkd == SCR_SSTGHT) {
            int smstraightflag = 0;
            for (int i4 = 0; i4 < 4; i4++) {
                if (subscr[i4] == (subscr[i4 + 1] - 1)) smstraightflag++;
            }
            if (smstraightflag > 2) selectedscr2 = 30;
            else selectedscr2 = 0;
        } else if (currentscorechkd == SCR_LSTGHT) {
            int lgstraightflag = 0;
            for (int i5 = 0; i5 < 4; i5++) {
                if (subscr[i5] == (subscr[i5 + 1] - 1)) lgstraightflag++;
            }
            if (lgstraightflag == 4) selectedscr2 = 40;
            else selectedscr2 = 0;
        } else if (currentscorechkd == SCR_CHANCE) {
            for (int i6 = 0; i6 < 5; i6++) {
                hldscr += subscr[i6];
                hldscr++;
            }
            selectedscr2 = hldscr;
        } else if (currentscorechkd == SCR_YAHTZEE) {
            if (pairChkdStatus == PCK_YAHTZEE) selectedscr2 = 50;
            else selectedscr2 = 0;
        } else selectedscr2 = 0;
        
        System.out.println("Score2 calculated for " + scoredisp[currentscorechkd] + ": " + selectedscr2);
    }

    /**
     * Checks the dice array for pairs, three/four of a kind, full house, or Yahtzee.
     * @param incd The sorted dice array
     * @return The type of combination found (PCK_3CARD, PCK_4CARD, PCK_FULL, PCK_YAHTZEE, or 0)
     */
    public int rtnchpair(int[] incd) {
        int pair = 0;
        boolean threecardflag = false;
        boolean fourcardflag = false;
        boolean yahtflag = false;
        for (int i = 0; i < 4; i++) {
            if (incd[i] == incd[i + 1]) {
                pair++;
                if ((pair == 1) && (incd[i] == incd[i + 2])) {
                    threecardflag = true;
                    if (threecardflag && (incd[i] == incd[i + 3])) {
                        fourcardflag = true;
                        if (fourcardflag && (incd[i] == incd[i + 4])) {
                            yahtflag = true;
                        }
                    }
                }
            }
        }
        System.out.println("rtnchpair: pairs=" + pair + ", three=" + threecardflag + ", four=" + fourcardflag + ", yahtzee=" + yahtflag);
        if (yahtflag) return PCK_YAHTZEE;
        if (fourcardflag) return PCK_4CARD;
        else if (!fourcardflag && pair == 3) return PCK_FULL;
        else if (threecardflag) return PCK_3CARD;
        else return 0;
    }

    /**
     * Handles the logic for entering a score for the currently selected category.
     * Updates total, bonus, and resets state for the next round or ends the game.
     */
    public void enterscr() {
        System.out.println("=== ENTERING SCORE ===");
        System.out.println("Category: " + scoredisp[currentscorechkd] + " (used: " + scoreused[currentscorechkd] + ", initflag: " + initflag + ")");
        System.out.println("Scores: selectedscr1=" + selectedscr1 + ", selectedscr2=" + selectedscr2);
        
        if (!scoreused[currentscorechkd] && !initflag) {
            int categoryScore = selectedscr1 + selectedscr2;
            categoryScores[currentscorechkd] = categoryScore;
            total += categoryScore;
            bonuscount += selectedscr1;
            
            System.out.println("Category score: " + categoryScore + ", Total: " + total + ", Bonus count: " + bonuscount);
            
            if (bonuscount > 62 && !bonusflag) {
                total = total + 35;
                bonusflag = true;
                System.out.println("BONUS 35 ADDED! New total: " + total);
            }
            
            gameleft--;
            scoreused[currentscorechkd] = true;
            flushout = false;
            initflag = true;
            chance = 3;
            
            System.out.println("Game state updated - Game left: " + gameleft + ", Chance: " + chance + ", Initflag: " + initflag);
            
            currentscorechkd = SCR_1;
            while (scoreused[currentscorechkd] && currentscorechkd < SCR_YAHTZEE) {
                currentscorechkd++;
            }
            System.out.println("Next available category: " + currentscorechkd + " (" + scoredisp[currentscorechkd] + ")");
            
            for (int i = 0; i < 5; i++) hold[i] = false;
            System.out.println("All dice holds reset");
        } else {
            System.out.println("Score entry blocked - Category used: " + scoreused[currentscorechkd] + ", Initflag: " + initflag);
        }
        
        if (gameleft == 0) {
            System.out.println("=== GAME OVER ===");
            bonusflag = false;
            gameStatus = GAME_END;
            gameleft = 13;
        }
    }

    /**
     * Draws the entire game UI based on the current game state.
     * Handles drawing the title, end, and main game screens, including dice, scores, and messages.
     */
    public void draw() {
        try {
            gc.clearRect(0, 0, 600, 600);
            
            // Add a simple background to make sure rendering is working
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, 600, 600);
            
            switch (gameStatus) {
                case GAME_TITLE:
                    drawSimpleTitleScreen();
                    break;
                case GAME_END:
                    drawSimpleEndScreen();
                    break;
                case GAME_PAUSED:
                    drawSimplePausedScreen();
                    break;
                case GAME_OPEN:
                    drawSimpleGameScreen();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // If anything fails, draw a basic fallback
            drawFallbackScreen();
        }
    }
    
    /**
     * Fallback drawing method that uses only the most basic operations
     */
    private void drawFallbackScreen() {
        gc.clearRect(0, 0, 600, 600);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 600, 600);
        gc.setFill(Color.BLACK);
        gc.fillText("YAHTZEE GAME", 250, 300);
        gc.fillText("Press ENTER to start", 250, 350);
    }
    
    /**
     * Draws a simple title screen without complex font operations
     */
    private void drawSimpleTitleScreen() {
        // Background
        gc.setFill(Color.DARKBLUE);
        gc.fillRect(0, 0, 600, 600);
        
        // Title - make it much larger and more visible
        gc.setFill(Color.YELLOW);
        gc.fillText("YAHTZEE", 250, 150);
        
        // Subtitle
        gc.setFill(Color.WHITE);
        gc.fillText("Enhanced Edition", 220, 180);
        
        // Instructions
        gc.setFill(Color.RED);
        gc.fillText("PRESS ENTER TO START", 200, 250);
        
        // Controls info
        gc.setFill(Color.LIGHTGRAY);
        gc.fillText("Controls: 1-5 Hold | SPACE Roll | UP/DOWN Select | ENTER Score", 100, 300);
        gc.fillText("P:Pause N:New T:Tooltips D:DiceValues ESC:Quit", 150, 320);
        
        // Credits
        gc.setFill(Color.GRAY);
        gc.fillText("(c) 2025 YAHAYUTA", 220, 380);
        

    }
    
    /**
     * Draws a simple end game screen
     */
    private void drawSimpleEndScreen() {
        // Background
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, 600, 600);
        
        // Game Over title
        gc.setFill(Color.RED);
        gc.fillText("GAME OVER", 200, 150);
        
        // Final score
        gc.setFill(Color.YELLOW);
        gc.fillText("FINAL SCORE: " + total, 180, 220);
        
        // Score breakdown
        gc.setFill(Color.LIGHTGRAY);
        int upperTotal = 0;
        for (int i = 0; i < 6; i++) {
            upperTotal += categoryScores[i];
        }
        gc.fillText("Upper Section: " + upperTotal, 200, 260);
        gc.fillText("Lower Section: " + (total - upperTotal - (bonusflag ? 35 : 0)), 200, 280);
        if (bonusflag) {
            gc.fillText("Bonus: 35", 200, 300);
        }
        
        // Restart instruction
        gc.setFill(Color.GREEN);
        gc.fillText("PRESS ENTER TO PLAY AGAIN", 150, 350);
    }
    
    /**
     * Draws a simple paused game screen
     */
    private void drawSimplePausedScreen() {
        // Semi-transparent overlay
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 600, 600);
        
        // Pause menu background
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(150, 200, 300, 200);
        
        // Pause title
        gc.setFill(Color.WHITE);
        gc.fillText("GAME PAUSED", 220, 250);
        
        // Menu options
        gc.setFill(Color.GREEN);
        gc.fillText("P: Resume Game", 200, 290);
        gc.setFill(Color.YELLOW);
        gc.fillText("N: New Game", 200, 320);
        gc.setFill(Color.RED);
        gc.fillText("ESC: Quit Game", 200, 350);
    }
    
    /**
     * Draws the main game screen with simplified rendering
     */
    private void drawSimpleGameScreen() {
        // Background
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, 600, 600);
        
        // Header section
        drawSimpleHeader();
        
        // Score sections
        drawSimpleScoreSections();
        
        // Dice section
        drawSimpleDiceSection();
        
        // Status bar
        drawSimpleStatusBar();
    }
    
    /**
     * Draws the header with total score and game status
     */
    private void drawSimpleHeader() {
        // Header background
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, 600, 50);
        
        // Total score
        gc.setFill(Color.YELLOW);
        gc.fillText("Total: " + total, 20, 35);
        
        // Bonus indicator
        if (bonusflag) {
            gc.setFill(Color.GREEN);
            gc.fillText("BONUS +35", 150, 35);
        }
        
        // Game status
        if (goflush) {
            gc.setFill(Color.YELLOW);
            gc.fillText("ROLLING... PRESS SPACE TO STOP", 300, 35);
        } else if (initflag) {
            gc.setFill(Color.GREEN);
            gc.fillText("PRESS SPACE TO ROLL", 300, 35);
        } else {
            gc.setFill(Color.WHITE);
            gc.fillText("GAME START - PRESS SPACE TO ROLL", 300, 35);
        }
    }
    
    /**
     * Draws the score sections with simplified rendering
     */
    private void drawSimpleScoreSections() {
        // Left column (Upper section)
        drawSimpleScoreColumn(50, 70, 0, 6, "UPPER SECTION");
        
        // Right column (Lower section)
        drawSimpleScoreColumn(350, 70, 6, 13, "LOWER SECTION");
    }
    
    /**
     * Draws a column of score categories with simplified rendering
     */
    private void drawSimpleScoreColumn(int x, int y, int startIdx, int endIdx, String title) {
        // Column title
        gc.setFill(Color.WHITE);
        gc.fillText(title, x, y);
        
        int currentY = y + 30;
        
        for (int i = startIdx; i < endIdx; i++) {
            // Background for selected item
            if (currentscorechkd == i) {
                gc.setFill(Color.RED);
                gc.fillRect(x - 5, currentY - 20, 200, 25);
            }
            
            // Category name
            if (scoreused[i]) {
                gc.setFill(Color.GRAY);
            } else if (currentscorechkd == i) {
                gc.setFill(Color.WHITE);
            } else {
                gc.setFill(Color.LIGHTGRAY);
            }
            gc.fillText(scoredisp[i], x, currentY);
            
            // Score value
            if (scoreused[i]) {
                gc.setFill(Color.YELLOW);
                gc.fillText(": " + categoryScores[i], x + 80, currentY);
            } else if (currentscorechkd == i) {
                int potentialScore = selectedscr1 + selectedscr2;
                gc.setFill(Color.GREEN);
                gc.fillText(": " + potentialScore, x + 80, currentY);
            }
            
            currentY += 30;
        }
        
        // Draw tooltip for selected category
        if (showTooltips && tooltipTimer > 0 && currentscorechkd >= startIdx && currentscorechkd < endIdx) {
            drawSimpleTooltip(x, y + 200, currentTooltip);
        }
    }
    
    /**
     * Draws a simple tooltip
     */
    private void drawSimpleTooltip(int x, int y, String text) {
        // Tooltip background
        gc.setFill(Color.BLACK);
        gc.fillRect(x - 5, y - 5, 250, 40);
        gc.setFill(Color.YELLOW);
        gc.fillRect(x - 3, y - 3, 246, 36);
        
        // Tooltip text
        gc.setFill(Color.BLACK);
        gc.fillText(text, x, y + 15);
    }
    
    /**
     * Draws the dice section with simplified rendering
     */
    private void drawSimpleDiceSection() {
        int diceWidth = 70;
        int diceGap = 20;
        int diceCount = 5;
        int totalDiceWidth = diceCount * diceWidth + (diceCount - 1) * diceGap;
        int diceStartX = (600 - totalDiceWidth) / 2;
        int diceY = 400;
        
        // Dice section title
        gc.setFill(Color.WHITE);
        gc.fillText("DICE", 280, diceY - 20);
        
        int x = diceStartX;
        for (int i = 0; i < 5; i++) {
            // Dice background with border
            if (hold[i]) {
                // Held dice have a red border
                gc.setFill(Color.RED);
                gc.fillRect(x - 3, diceY - 3, diceWidth + 6, diceWidth + 6);
                gc.setFill(Color.PINK);
                gc.fillRect(x, diceY, diceWidth, diceWidth);
            } else {
                // Normal dice have a white border
                gc.setFill(Color.WHITE);
                gc.fillRect(x - 2, diceY - 2, diceWidth + 4, diceWidth + 4);
                gc.setFill(Color.LIGHTGRAY);
                gc.fillRect(x, diceY, diceWidth, diceWidth);
            }
            
            // Draw die face
            if (card[cardnum[i]] != null) {
                gc.drawImage(card[cardnum[i]], x + 5, diceY + 5, diceWidth - 10, diceWidth - 10);
            } else {
                // Fallback to number display
                gc.setFill(Color.BLACK);
                gc.fillText(String.valueOf(cardnum[i] + 1), x + 25, diceY + 45);
            }
            
            // Show dice value if enabled
            if (showDiceValues) {
                gc.setFill(Color.BLUE);
                gc.fillText("Value: " + (cardnum[i] + 1), x + 5, diceY + diceWidth + 30);
            }
            
            // Hold indicator
            if (hold[i]) {
                gc.setFill(Color.RED);
                gc.fillText("HELD", x + 15, diceY - 10);
            }
            
            // Dice number indicator
            gc.setFill(Color.GRAY);
            gc.fillText("(" + (i + 1) + ")", x + 25, diceY + diceWidth + 15);
            
            x += diceWidth + diceGap;
        }
    }
    
    /**
     * Draws the status bar at the bottom with simplified rendering
     */
    private void drawSimpleStatusBar() {
        // Status bar background
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 550, 600, 50);
        
        // Rolls remaining
        gc.setFill(Color.WHITE);
        gc.fillText("Rolls: " + chance, 20, 575);
        
        // Current potential score
        int potentialScore = selectedscr1 + selectedscr2;
        gc.setFill(Color.GREEN);
        gc.fillText("Potential Score: " + potentialScore, 150, 575);
        
        // Feature indicators
        if (showTooltips) {
            gc.setFill(Color.YELLOW);
            gc.fillText("Tooltips: ON", 300, 575);
        }
        if (showDiceValues) {
            gc.setFill(Color.CYAN);
            gc.fillText("Dice Values: ON", 400, 575);
        }
        
        // Controls reminder
        gc.setFill(Color.LIGHTGRAY);
        gc.fillText("P:Pause N:New T:Tooltips D:DiceValues", 20, 590);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
