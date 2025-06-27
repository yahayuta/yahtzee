
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
    String[] scoredisp = {"1", "2", "3", "4", "5", "6", "3card", "4card", "Full", "SStght", "LStght", "Chance", "Yahtzee"};

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
        int CANVAS_WIDTH = 400;
        int CANVAS_HEIGHT = 400;
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 16));
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, CANVAS_WIDTH, CANVAS_HEIGHT);
        primaryStage.setTitle("YahtzeeFX");
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
     * Handles keyboard input for all game states (title, open, end).
     * @param code The KeyCode pressed
     */
    private void handleKey(KeyCode code) {
        switch (gameStatus) {
            case GAME_TITLE:
                if (code == KeyCode.ENTER) {
                    gameStatus = GAME_OPEN;
                }
                break;
            case GAME_END:
                if (code == KeyCode.ENTER) {
                    for (int i1 = 0; i1 < 13; i1++) scoreused[i1] = false;
                    gameStatus = GAME_OPEN;
                    total = 0;
                    selectedscr1 = 0;
                    selectedscr2 = 0;
                    chkscore1();
                    chkscore2();
                }
                break;
            case GAME_OPEN:
                if (code == KeyCode.DIGIT1 && !goflush) hold[0] = !hold[0];
                if (code == KeyCode.DIGIT2 && !goflush) hold[1] = !hold[1];
                if (code == KeyCode.DIGIT3 && !goflush) hold[2] = !hold[2];
                if (code == KeyCode.DIGIT4 && !goflush) hold[3] = !hold[3];
                if (code == KeyCode.DIGIT5 && !goflush) hold[4] = !hold[4];
                if (code == KeyCode.SPACE) {
                    if (chance > 0 && !flushout) {
                        if (!goflush) {
                            goflush = true;
                            if (!initflag) chance--;
                            else initflag = false;
                        } else if (goflush) {
                            goflush = false;
                            chkscore1();
                            chkscore2();
                        }
                    } else if (goflush) {
                        goflush = false;
                        chkscore1();
                        chkscore2();
                    } else {
                        flushout = true;
                    }
                }
                if (code == KeyCode.UP) {
                    currentscorechkd--;
                    if (currentscorechkd < SCR_1) currentscorechkd = SCR_YAHTZEE;
                    while (scoreused[currentscorechkd]) {
                        currentscorechkd--;
                        if (currentscorechkd < SCR_1) currentscorechkd = SCR_YAHTZEE;
                    }
                    chkscore1();
                    chkscore2();
                }
                if (code == KeyCode.DOWN) {
                    currentscorechkd++;
                    if (currentscorechkd > SCR_YAHTZEE) currentscorechkd = SCR_1;
                    while (scoreused[currentscorechkd]) {
                        currentscorechkd++;
                        if (currentscorechkd > SCR_YAHTZEE) currentscorechkd = SCR_1;
                    }
                    chkscore1();
                    chkscore2();
                }
                if (code == KeyCode.ENTER) {
                    enterscr();
                }
                break;
            default:
                break;
        }
        if (code == KeyCode.ESCAPE) {
            System.exit(0);
        }
        draw();
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
            flushcards();
            draw();
        }
    }

    /**
     * Rolls all non-held dice if rolling is active.
     */
    public void flushcards() {
        if (goflush) {
            for (int i = 0; i < 5; i++) {
                if (!hold[i]) {
                    cardnum[i] = Math.abs(rnd.nextInt()) % 6;
                }
            }
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
        pairChkdStatus = rtnchpair(subscr);
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
        if (!scoreused[currentscorechkd] && !initflag) {
            total += selectedscr1;
            total += selectedscr2;
            bonuscount += selectedscr1;
            if (bonuscount > 62 && !bonusflag) {
                total = total + 35;
                bonusflag = true;
            }
            gameleft--;
            scoreused[currentscorechkd] = true;
            flushout = false;
            initflag = true;
            chance = 3;
            currentscorechkd = SCR_1;
            while (scoreused[currentscorechkd] && currentscorechkd < SCR_YAHTZEE) {
                currentscorechkd++;
            }
            for (int i = 0; i < 5; i++) hold[i] = false;
        }
        if (gameleft == 0) {
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
        gc.clearRect(0, 0, 400, 400);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 16));
        switch (gameStatus) {
            case GAME_TITLE:
                gc.setFill(Color.GRAY);
                gc.fillRect(0, 0, 400, 400);
                gc.setFill(Color.YELLOW);
                gc.fillText("JYahtzee", 140, 60);
                gc.setFill(Color.RED);
                gc.fillText("PRESS ENTER", 110, 100);
                gc.setFill(Color.BLUE);
                gc.fillText("(c)2025", 150, 140);
                gc.fillText("YAHAYUTA", 120, 170);
                break;
            case GAME_END:
                gc.setFill(Color.GRAY);
                gc.fillRect(0, 0, 400, 400);
                gc.setFill(Color.BLUE);
                gc.fillText("GAME OVER", 130, 100);
                gc.setFill(Color.YELLOW);
                gc.fillText("YOUR SCORE IS " + total + ".", 80, 140);
                break;
            case GAME_OPEN:
                gc.setFill(Color.GRAY);
                gc.fillRect(0, 0, 400, 400);
                int x = 30, x2 = 30, y2 = 60;
                // Dice row
                for (int c = 0; c < 5; c++) {
                    if (card[cardnum[c]] != null) {
                        gc.drawImage(card[cardnum[c]], x, 220, 48, 48);
                    } else {
                        gc.setFill(Color.WHITE);
                        gc.fillRect(x, 220, 48, 48);
                        gc.setFill(Color.BLACK);
                        gc.strokeRect(x, 220, 48, 48);
                        gc.fillText(String.valueOf(cardnum[c] + 1), x + 16, 250);
                    }
                    x += 66;
                }
                // Score categories left
                for (int c1 = 0; c1 < 6; c1++) {
                    gc.setFill(scoreused[c1] ? Color.WHITE : Color.BLACK);
                    if (currentscorechkd == c1) gc.setFill(Color.RED);
                    gc.fillText(scoredisp[c1], x2, y2);
                    y2 += 26;
                }
                y2 -= 156;
                x2 += 120;
                // Score categories right
                for (int c2 = 6; c2 < 13; c2++) {
                    gc.setFill(scoreused[c2] ? Color.WHITE : Color.BLACK);
                    if (currentscorechkd == c2) gc.setFill(Color.RED);
                    gc.fillText(scoredisp[c2], x2, y2);
                    y2 += 26;
                }
                y2 -= 182;
                x2 -= 120;
                gc.setFill(Color.BLUE);
                gc.fillText("Total:" + total, 30, 35);
                if (bonusflag) gc.fillText("BONUS35", 250, 35);
                if (goflush) {
                    gc.setFill(Color.YELLOW);
                    gc.fillText("Rolling...", 250, 70);
                    gc.fillText("PRESS STOP", 250, 100);
                    gc.setFill(Color.BLUE);
                }
                if (initflag) {
                    gc.setFill(Color.YELLOW);
                    gc.fillText("PRESS ROLL", 250, 100);
                    gc.setFill(Color.BLUE);
                } else {
                    gc.fillText("GAME START", 250, 130);
                    gc.fillText("Rolls:" + chance, 30, 340);
                    gc.fillText("Score:" + (selectedscr1 + selectedscr2), 250, 340);
                }
                x = 30;
                for (int i = 0; i < 5; i++) {
                    if (hold[i]) gc.fillText("HLD", x, 210);
                    x += 66;
                }
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
