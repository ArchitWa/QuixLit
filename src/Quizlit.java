import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/* To Do List:
 - picture match game (take image from google, resize and
 - resolve issue with duplicate keys
 - remove punctuation from answers
 - speed round with timer (?)
 - crossword 

 */

public class Quizlit implements ActionListener {
    private final String filePath;
    private final JFrame frame;
    private final JButton learnButton, matchButton, crosswordButton, exitButton;

    // Learn Variables
    private JTextField learnAnsField;
    private JLabel questionLabel, responseLabel, amtLabel, distanceLabel;
    private JButton checkBtn, showAnsButton, skipBtn;
    private HashMap<String, String> vocabMap;
    private String currentQ, currentA, totalAmt;

    // Match Variables
    private JButton[] matchButtons;
    private final int NUM_MATCH_BUTTONS = 10;
    private int firstClick = -1;
    private HashMap<String, String> tempVocabMap;

    // Crossword Variables
    private CrosswordBoard grid;
    private JPanel crosswordBoard;
    private JButton[][] crosswordBtns;
    private JTextField cwAnsField;
    private JLabel acrossLabel, downLabel;
    private ArrayList<String> acrossHints, downHints;
    private JButton[] acrossHintBtns, downHintBtns;
    private final JButton restartButton;
    private int correctAns = 0;

    // Final Vars
    private final int BUTTON_HEIGHT = 50;
    private final int MATCH_SCREEN_HEIGHT = 500, MATCH_SCREEN_WIDTH = 600;
    private final int DEFAULT_SCREEN_HEIGHT = 300, DEFAULT_SCREEN_WIDTH = 400;

    // Fonts
    private final Font fontBig = new Font(Font.DIALOG, Font.PLAIN, 20);
    private final Font fontSmall = new Font(Font.DIALOG, Font.PLAIN, 15);

    /* ----- Initialize components for all games ----- */

    public Quizlit(String path) {
        this.filePath = path;

        frame = new JFrame("Quixlit \uD83D\uDD25"); // fire name, right?
        frame.getContentPane().setBackground(new Color(10, 9, 45));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
        frame.setLayout(null);

        learnButton = new JButton("Learn");
        learnButton.addActionListener(this);
        learnButton.setFont(fontBig);
        learnButton.setFocusable(false);
        learnButton.setBounds(50, 100, 140, BUTTON_HEIGHT);

        matchButton = new JButton("Match");
        matchButton.addActionListener(this);
        matchButton.setFont(fontBig);
        matchButton.setFocusable(false);
        matchButton.setBounds(200, 100, 140, BUTTON_HEIGHT);

        crosswordButton = new JButton("Crossword");
        crosswordButton.addActionListener(this);
        crosswordButton.setFont(fontBig);
        crosswordButton.setFocusable(false);
        crosswordButton.setBounds(115, 160, 140, BUTTON_HEIGHT);

        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        exitButton.setFont(fontSmall);
        exitButton.setFocusable(false);
        exitButton.setBounds(5, 5, 50, 40);
        exitButton.setVisible(false);

        restartButton = new JButton("Restart");
        restartButton.addActionListener(this);
        restartButton.setFont(fontSmall);
        restartButton.setFocusable(false);
        restartButton.setBounds(65, 5, 70, 40);
        restartButton.setVisible(false);

        initVocabMap(); // <-- create word map for learn and match game modes
        initLearnVar(); // <-- create learn variables for learn game mode
        initMatchVar(); // <-- create match variables for match game mode

        frame.add(learnButton);
        frame.add(matchButton);
        frame.add(crosswordButton);
        frame.add(exitButton);
        frame.add(restartButton);
        frame.setVisible(true);
    }

    /**
     * Creates all the components for the learn game mode
     */
    private void initLearnVar() {
        amtLabel = new JLabel(0 + "/" + totalAmt);
        amtLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        amtLabel.setBounds(360, 10, 100, BUTTON_HEIGHT);
        amtLabel.setVisible(false);
        amtLabel.setForeground(Color.WHITE);

        questionLabel = new JLabel(currentQ);
        questionLabel.setFont(fontSmall);
        questionLabel.setBounds(50, 30, 300, BUTTON_HEIGHT);
        questionLabel.setForeground(Color.WHITE);
        questionLabel.setVisible(false);

        learnAnsField = new JTextField();
        learnAnsField.setBounds(50, 90, 300, BUTTON_HEIGHT);
        learnAnsField.setFont(fontBig);
        learnAnsField.setVisible(false);

        responseLabel = new JLabel("");
        responseLabel.setFont(fontSmall);
        responseLabel.setBounds(50, 150, 300, 30);
        responseLabel.setForeground(Color.WHITE);
        responseLabel.setVisible(false);

        distanceLabel = new JLabel("");
        distanceLabel.setFont(fontSmall);
        distanceLabel.setBounds(250, 150, 300, 30);
        distanceLabel.setForeground(Color.WHITE);
        distanceLabel.setVisible(false);

        checkBtn = new JButton("Check");
        checkBtn.addActionListener(this);
        checkBtn.setFont(fontBig);
        checkBtn.setFocusable(false);
        checkBtn.setBounds(50, 190, 90, BUTTON_HEIGHT);
        checkBtn.setVisible(false);

        showAnsButton = new JButton("Show");
        showAnsButton.addActionListener(this);
        showAnsButton.setFont(fontBig);
        showAnsButton.setFocusable(false);
        showAnsButton.setBounds(155, 190, 90, BUTTON_HEIGHT);
        showAnsButton.setVisible(false);

        skipBtn = new JButton("Skip");
        skipBtn.addActionListener(this);
        skipBtn.setFont(fontBig);
        skipBtn.setFocusable(false);
        skipBtn.setBounds(255, 190, 90, BUTTON_HEIGHT);
        skipBtn.setVisible(false);

        frame.add(learnAnsField);
        frame.add(questionLabel);
        frame.add(responseLabel);
        frame.add(distanceLabel);
        frame.add(amtLabel);
        frame.add(checkBtn);
        frame.add(showAnsButton);
        frame.add(skipBtn);
    }

    /**
     * Creates all the components for the match game mode
     */
    private void initMatchVar() {
        matchButtons = new JButton[NUM_MATCH_BUTTONS]; // number of buttons on the game mode is set to 10
        for (int i = 0; i < NUM_MATCH_BUTTONS; i++) {
            JButton temp = new JButton("");
            temp.addActionListener(this);
            temp.setFocusable(false);
            temp.setFont(fontSmall);
            temp.setVisible(false);

            matchButtons[i] = temp;
            frame.add(matchButtons[i]);
        }
    }

    /**
     * Creates all the components for the crossword game (isn't initialized in the beginning)
     */
    private void createCrossword() {
        grid = Crossword.createBestBoard(filePath, 15, 200); // creates a random crossword board using the unit vocab file path

        createBoard(); // <-- creates the panel and initializes the values for each button
        createHints(); // creates all the hint and text field components

        restartButton.setVisible(true);
        frame.setSize(crosswordBoard.getSize().width + 550, crosswordBoard.getSize().height + 200); // dynamically changes screen size according to crossword size
    }

    /**
     * Reads the txt file and updates the vocab HashMap
     */
    private void initVocabMap() {
        vocabMap = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line = br.readLine();
            while (line != null) {
                vocabMap.put(formatText(line.split(":")[0]), formatText(line.split(":")[1])); // splits the line into spanish and english parts and formats the text
                line = br.readLine();
            }
        } catch (Exception err) {
            System.err.println("There was a problem reading the " + filePath);
        }

        totalAmt = "" + vocabMap.size(); // variable to see how many words they've answered so far (learn game mode)
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == learnButton) {
            learnButton.setVisible(false);
            matchButton.setVisible(false);
            crosswordButton.setVisible(false);

            initVocabMap(); // recreates the vocab map
            changeQuestion(); // chooses the first question for the user
            showLearnComponents(); // shows all the components for the learn game

            exitButton.setVisible(true);
            frame.setSize(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
        }

        if (e.getSource() == matchButton) {
            learnButton.setVisible(false);
            matchButton.setVisible(false);
            crosswordButton.setVisible(false);

            initVocabMap(); // recreates the vocab map
            setRandomLocations(); // chooses random questions and sets them on the screen
            setRandomQuestions();

            for (JButton button : matchButtons) {
                button.setVisible(true);
            }

            exitButton.setVisible(true);
            frame.setSize(MATCH_SCREEN_WIDTH, MATCH_SCREEN_HEIGHT);
        }

        if (e.getSource() == crosswordButton) {
            learnButton.setVisible(false);
            matchButton.setVisible(false);
            crosswordButton.setVisible(false);

            createCrossword();

            exitButton.setVisible(true);
        }

        /* --- Learn Buttons --- */
        if (e.getSource() == checkBtn) { // checks if the answer is correct
            if (learnAnsField.getText().equalsIgnoreCase(currentA.trim())) {
                learnAnsField.setText("");
                changeQuestion();
                responseLabel.setText("");
                distanceLabel.setText("");
            } else {
                distanceLabel.setText("Error: " + lev(formatText(currentA), formatText(learnAnsField.getText())));
                responseLabel.setText("Try again!");
            }
        }

        if (e.getSource() == showAnsButton) { // shows the answer to the question
            responseLabel.setText(currentA);
        }

        if (e.getSource() == skipBtn) { // skips the learn game mode question
            // hides all the fields
            learnAnsField.setText("");
            responseLabel.setText("");
            distanceLabel.setText("");
            changeQuestion();
        }

        /* --- Match Buttons --- */
        for (int i = 0; i < NUM_MATCH_BUTTONS; i++) { // checks if a match game button has been clicked
            if (e.getSource() == matchButtons[i]) {
                if (firstClick == -1) { // sets the first click to be the index of the button
                    firstClick = i;
                } else {
                    if (firstClick + i == NUM_MATCH_BUTTONS - 1) { // if the two click indexes add up to 10 then a correct pair has been clicked
                        matchButtons[firstClick].setVisible(false);
                        matchButtons[i].setVisible(false);
                        tempVocabMap.remove(matchButtons[Math.min(firstClick, i)].getText()); // min index is the key in the map
                    }
                    firstClick = -1; // resets first click
                }
                if (tempVocabMap.size() == 0) { // if all buttons have been clicked, it creates a new game
                    setRandomLocations();
                    setRandomQuestions();
                }
            }
        }

        /* --- Crossword Buttons --- */
        if (grid != null) { // Only if a crossword is created can a button from that game be clicked
            for (JButton acrossHintBtn : acrossHintBtns) {
                if (e.getSource() == acrossHintBtn) {
                    checkCrosswordAnswer(acrossHintBtn, 'h');
                }
            }

            for (JButton downHintBtn : downHintBtns) {
                if (e.getSource() == downHintBtn) {
                    checkCrosswordAnswer(downHintBtn, 'd');
                }
            }

            if (e.getSource() == restartButton) {
                hideCrosswordComponents(); // hides existing components
                createCrossword(); // creates new ones
            }
        }

        if (e.getSource() == exitButton) {
            learnButton.setVisible(true); // resets to home screen
            matchButton.setVisible(true);
            crosswordButton.setVisible(true);
            exitButton.setVisible(false);

            // Hides each games components (checks if grid is null for crossword because it isn't initialized at the start)
            hideLearnComponents();
            hideMatchComponents();
            if (grid != null) hideCrosswordComponents();

            frame.setSize(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
        }

    }

    public static void main(String[] args) {
        String input = JOptionPane.showInputDialog("What vocab unit?");
        new Quizlit("vocabLists/unit" + input + ".txt");
    }


    /* ---- Learn Game Mode Methods ---- */

    /**
     * Changes the question if there are more words in the list
     */
    private void changeQuestion() {
        if (vocabMap.size() >= 1) {
            changeQnA(); // changes question and answer if words are available
            questionLabel.setText(currentQ);
        } else {
            questionLabel.setText("No more words."); // hides all the components if no words are available
            learnAnsField.setEditable(false);
            checkBtn.setVisible(false);
            showAnsButton.setVisible(false);
            skipBtn.setVisible(false);
        }

    }

    /**
     * Updates the labels with a new question and label
     */
    private void changeQnA() {
        double randIdx1 = Math.random(); // deciding between spanish or english question
        int randIdx2 = (int) (Math.random() * vocabMap.size()); // randomly choosing question

        if (randIdx1 < 0.5) {
            currentQ = (String) vocabMap.keySet().toArray()[randIdx2]; // spanish question
            currentA = vocabMap.get(currentQ); // english answer
            vocabMap.remove(currentQ);
        } else {
            currentA = (String) vocabMap.keySet().toArray()[randIdx2]; // spanish answer
            currentQ = vocabMap.get(currentA); // english question
            vocabMap.remove(currentA);
        }

        amtLabel.setText(Integer.parseInt(totalAmt) - vocabMap.size() + "/" + totalAmt); // updates number of questions viewed
    }

    /**
     * Shows all components for learn mode
     */
    private void showLearnComponents() {
        learnAnsField.setVisible(true);
        amtLabel.setVisible(true);
        questionLabel.setVisible(true);
        responseLabel.setVisible(true);
        responseLabel.setText("");
        distanceLabel.setVisible(true);
        distanceLabel.setText("");
        checkBtn.setVisible(true);
        skipBtn.setVisible(true);
        showAnsButton.setVisible(true);
    }

    /**
     * Hides all components for the learn game
     */
    private void hideLearnComponents() {
        learnAnsField.setVisible(false);
        amtLabel.setVisible(false);
        questionLabel.setVisible(false);
        responseLabel.setVisible(false);
        distanceLabel.setVisible(false);
        checkBtn.setVisible(false);
        skipBtn.setVisible(false);
        showAnsButton.setVisible(false);
    }

    /* ---- Match Game Mode Methods ---- */

    /**
     * Sets random locations for each button and makes sure they don't overlap
     */
    private void setRandomLocations() {
        int width = 190;
        ArrayList<Rectangle> tempRects = new ArrayList<>(); // list of rectangles where each button is
        tempRects.add(new Rectangle(5, 5, 50, 40, tempRects)); // exit button rect

        for (int i = 0; i < NUM_MATCH_BUTTONS; i++) { // number of buttons on screen
            int randX = (int) (Math.random() * (MATCH_SCREEN_WIDTH - width)),
                    randY = (int) (Math.random() * (MATCH_SCREEN_HEIGHT - BUTTON_HEIGHT - 20)); // makes two random x and y locations

            Rectangle tempRect = new Rectangle(randX, randY, width + 10, BUTTON_HEIGHT + 10, tempRects); // makes a rectangle at that location

            while (!tempRect.isClear()) { // loops until the rectangle doesn't intersect with any other rectangle in tempRects
                randX = (int) (Math.random() * (MATCH_SCREEN_WIDTH - width));
                randY = (int) (Math.random() * (MATCH_SCREEN_HEIGHT - BUTTON_HEIGHT - 20));
                tempRect = new Rectangle(randX, randY, width + 10, BUTTON_HEIGHT + 10, tempRects); // keeps making a new rectangle
            }

            matchButtons[i].setBounds(tempRect.getTopLeftX(), tempRect.getTopLeftY(), width, BUTTON_HEIGHT); // updates the location of that button
            tempRects.add(tempRect);
        }
    }

    /**
     * Sets each button to have a new spanish-english pair
     */
    private void setRandomQuestions() {
        tempVocabMap = new HashMap<>();
        for (int i = 0; i < NUM_MATCH_BUTTONS / 2; i++) { // only half since each button has an answer button
            int randIdx = (int) (Math.random() * vocabMap.size());

            currentQ = (String) vocabMap.keySet().toArray()[randIdx]; // randomly chooses question
            if (tempVocabMap.containsKey(currentQ)) { // checks if question is already been used
                i--;
                continue;
            }
            currentA = vocabMap.get(currentQ);
            tempVocabMap.put(currentQ, currentA); // updates the temporary HashMap

            matchButtons[i].setText(currentQ); // sets the text of the button with the question
            matchButtons[NUM_MATCH_BUTTONS - 1 - i].setText(currentA); // sets the text of the button with the answer (from the back) (i.e button 0 will have its answer in button 9)
        }

        for (int i = 0; i < NUM_MATCH_BUTTONS; i++) {
            matchButtons[i].setVisible(true); // makes all buttons visible
        }
    }

    /**
     * Hides all components for the match game
     */
    private void hideMatchComponents() {
        for (JButton button : matchButtons) {
            button.setVisible(false);
        }
    }

    /* ---- Crossword Game Mode Methods ---- */

    /**
     * Creates the labels and buttons for all the hints
     */
    private void createHints() {
        int STARTING_X = crosswordBoard.getSize().width + 150, STARTING_Y = 170; // coordinates that change all  the components

        acrossLabel = new JLabel("Across");
        acrossLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 25));
        acrossLabel.setBounds(STARTING_X, STARTING_Y, 100, BUTTON_HEIGHT);
        acrossLabel.setVisible(true);
        acrossLabel.setForeground(Color.WHITE);

        downLabel = new JLabel("Down");
        downLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 25));
        downLabel.setBounds(STARTING_X + 225, STARTING_Y, 100, BUTTON_HEIGHT);
        downLabel.setVisible(true);
        downLabel.setForeground(Color.WHITE);

        cwAnsField = new JTextField();
        cwAnsField.setBounds(STARTING_X + 5, STARTING_Y - 70, 300, BUTTON_HEIGHT);
        cwAnsField.setFont(fontBig);
        cwAnsField.setVisible(true);

        makeHintLists(); // <-- separates the hints into across and down hints

        acrossHintBtns = new JButton[acrossHints.size()];
        placeHints(acrossHintBtns, acrossHints, STARTING_X-35, STARTING_Y + 50); // <-- puts the hints on the frame

        downHintBtns = new JButton[downHints.size()];
        placeHints(downHintBtns, downHints, STARTING_X + 190, STARTING_Y + 50);

        frame.add(cwAnsField);
        frame.add(acrossLabel);
        frame.add(downLabel);
    }

    /**
     * Creates the hint buttons and puts them on the screen
     * @param hintBtns array of buttons to create
     * @param hintsList list of hints to use for the text
     * @param startX starting x location of the button
     * @param startY starting y location of the button
     */
    private void placeHints(JButton[] hintBtns, ArrayList<String> hintsList, int startX, int startY) {
        for (int i = 0; i < hintsList.size(); i++) {
            JButton btn = new JButton(hintsList.get(i));
            btn.addActionListener(this);
            btn.setFont(fontSmall);
            btn.setFocusable(false);
            btn.setVisible(true);
            btn.setBounds(startX, startY + (i * 60), 150, BUTTON_HEIGHT);

            hintBtns[i] = btn;
            frame.add(btn);
        }
    }

    /**
     * Separates the grid's hint list into across and down hints and changes the text
     */
    private void makeHintLists() {
        acrossHints = new ArrayList<>();
        downHints = new ArrayList<>();
        for (int i = 0; i < grid.getHints().size(); i++) {
            if (grid.getHints().get(i).contains("across")) {
                acrossHints.add(grid.getHints().get(i).replace(" across:", ".")); // since the hint is put into a separate list, the text doesn't need to say the direction
            } else {
                downHints.add(grid.getHints().get(i).replace(" down:", "."));
            }
        }
    }

    /**
     * Initializes the crossword board and adds every character to the panel (displayed on frame)
     */
    private void createBoard() {
        char[][] answerGrid = grid.getAnswerGrid();
        String[][] blankGrid = grid.getFillInGrid();

        crosswordBoard = new JPanel();
        crosswordBoard.setBounds(50, 100, 50 * answerGrid[0].length, 50 * answerGrid.length);
        crosswordBoard.setLayout(new GridLayout(answerGrid.length, answerGrid[0].length, 3, 3));
        crosswordBoard.setBackground(Color.BLACK);

        crosswordBtns = new JButton[answerGrid.length][answerGrid[0].length];
        for (int r = 0; r < answerGrid.length; r++) {
            for (int c = 0; c < answerGrid[0].length; c++) {
                JButton btn = new JButton(blankGrid[r][c].equals("-") ? "" : blankGrid[r][c].equals("?") ? " " : blankGrid[r][c]); // if a location has a -, the button need not have any information
                btn.addActionListener(this);
                btn.setFont(fontSmall);
                btn.setFocusable(false);

                if (btn.getText().equals("")) {
                    btn.setEnabled(false); // disables buttons not on the crossword
                }

                crosswordBtns[r][c] = btn; // sets the button in the grid of buttons
                crosswordBoard.add(btn);
            }
        }

        crosswordBoard.setVisible(true);
        frame.add(crosswordBoard);
    }

    /**
     * Checks if the user has guessed the word correctly
     * @param btn the button/hint being guessed
     * @param dir the direction of the word
     */
    private void checkCrosswordAnswer(JButton btn, char dir) {
        String text = btn.getText().replace(".", dir == 'h' ? " across:" : " down:"); // changes the hint to be the original hint

        int index = grid.getHints().indexOf(text); // the index of the hint
        ArrayList<Placement> tempPms = grid.getPlacements();

        btn.setOpaque(true);
        btn.setBorderPainted(true);

        if (formatText(cwAnsField.getText()).equals(tempPms.get(index).word)) { // if the guess is equal to the word in that placement index
            btn.setEnabled(false); // disables the button since user got it correct
            btn.setBackground(Color.GREEN);
            revealWord(tempPms.get(index));
            correctAns++; // counter to keep track of game stage
        } else btn.setBackground(Color.RED); // changes color to red to show it's an incorrect answer

        if (correctAns == grid.getHints().size()) { // if all the words are guessed
            cwAnsField.setEditable(false);
            cwAnsField.setText("     Good job! You're done.");
        }
    }

    /**
     * Updates the crossword panel to show the answer on the panel
     * @param p placement object to supply the coordinates
     */
    private void revealWord(Placement p) {
        for (int i = 0; i < p.word.length(); i++) {
            if (p.dir == 'h') {
                crosswordBtns[p.row][p.col + i].setText(String.valueOf(p.word.charAt(i)));
            } else {
                crosswordBtns[p.row + i][p.col].setText(String.valueOf(p.word.charAt(i)));
            }
        }
    }

    /**
     * Hides all components for the crossword game
     */
    private void hideCrosswordComponents() {
        crosswordBoard.setVisible(false);
        acrossLabel.setVisible(false);
        downLabel.setVisible(false);
        cwAnsField.setVisible(false);
        for (JButton button : downHintBtns) {
            button.setVisible(false);
        }
        for (JButton button : acrossHintBtns) {
            button.setVisible(false);
        }
        restartButton.setVisible(false);

        correctAns = 0;
    }

    /* ---- Helper Methods ---- */

    /**
     * Calculates the levenstein distance between 2 words
     * @param a first word
     * @param b second word
     * @return the distance between the two words
     */
    public double lev(String a, String b) {
        if (a.length() == 0) return b.length();
        if (b.length() == 0) return a.length();
        a = a.toLowerCase();
        b = b.toLowerCase();
        if (a.charAt(0) == b.charAt(0)) return lev(tail(a), tail(b));

        return 1 + Math.min(lev(tail(a), b), Math.min(lev(a, tail(b)), lev(tail(a), tail(b))));
    }

    /**
     * Returns the string after the first character
     * @param a string to tail
     * @return the tailed string
     */
    public static String tail(String a) {
        return a.substring(1);
    }

    /**
     * Formats a string
     * @param s string to be formatted
     * @return trimmed and lowercase string
     */
    public static String formatText(String s) {
        return s.trim().toLowerCase();
    }
}
