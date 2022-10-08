import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Quizlit implements ActionListener {
    private JFrame frame;
    private JButton learnButton, matchButton, exitButton;

    // Learn Variables
    private JTextField ansField;
    private JLabel questionLabel, responseLabel, amtLabel;
    private JButton checkBtn, showAnsButton, skipBtn;
    private HashMap<String, String> vocabMap;
    private String currentQ, currentA, totalAmt;
    private String gameMode;

    // Match Variables
    private JButton[] matchButtons;
    private final int NUM_MATCH_BUTTONS = 10;
    private int firstClick = -1;
    private HashMap<String, String> tempVocabMap;

    // Final Vars
    private final int BUTTON_HEIGHT = 50;
    private final int MATCH_SCREEN_HEIGHT = 500, MATCH_SCREEN_WIDTH = 600;
    private final int DEFAULT_SCREEN_HEIGHT = 300, DEFAULT_SCREEN_WIDTH = 400;

    // Fonts
    private final Font fontBig = new Font("Dialog", Font.PLAIN, 20);
    private final Font fontSmall = new Font(Font.DIALOG, Font.PLAIN, 15);

    public Quizlit(String path) {
        frame = new JFrame("Quixlit \uD83D\uDD25");
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

        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        exitButton.setFont(fontSmall);
        exitButton.setFocusable(false);
        exitButton.setBounds(5, 5, 50, 40);
        exitButton.setVisible(false);

        initVocabMap(path);
        initLearnVar();
        initMatchVar();

        frame.add(learnButton);
        frame.add(matchButton);
        frame.add(exitButton);
        frame.setVisible(true);
    }

    private void initLearnVar() {
        amtLabel = new JLabel(0 + "/" + totalAmt);
        amtLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        amtLabel.setBounds(360, 10, 100, BUTTON_HEIGHT);
        amtLabel.setVisible(false);
        amtLabel.setForeground(Color.WHITE);

        changeQnA();

        questionLabel = new JLabel(currentQ);
        questionLabel.setFont(fontSmall);
        questionLabel.setBounds(50, 30, 300, BUTTON_HEIGHT);
        questionLabel.setForeground(Color.WHITE);
        questionLabel.setVisible(false);

        ansField = new JTextField();
        ansField.setBounds(50, 90, 300, BUTTON_HEIGHT);
        ansField.setFont(fontBig);
        ansField.setVisible(false);

        responseLabel = new JLabel("");
        responseLabel.setFont(fontSmall);
        responseLabel.setBounds(50, 150, 300, 30);
        responseLabel.setForeground(Color.WHITE);
        responseLabel.setVisible(false);

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

        frame.add(ansField);
        frame.add(questionLabel);
        frame.add(responseLabel);
        frame.add(amtLabel);
        frame.add(checkBtn);
        frame.add(showAnsButton);
        frame.add(skipBtn);
    }

    private void initMatchVar() {
        matchButtons = new JButton[NUM_MATCH_BUTTONS];
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

    private void setRandomLocations() {
        int width = 190;
        ArrayList<Rectangle> tempRects = new ArrayList<>();
        tempRects.add(new Rectangle(5, 5, 50, 40, tempRects));

        for (int i = 0; i < NUM_MATCH_BUTTONS; i++) {
            int randX = (int) (Math.random() * (MATCH_SCREEN_WIDTH - width)),
                    randY = (int) (Math.random() * (MATCH_SCREEN_HEIGHT - BUTTON_HEIGHT - 20));

            Rectangle tempRect = new Rectangle(randX, randY, width + 10, BUTTON_HEIGHT + 10, tempRects);

            while (!tempRect.isClear()) {
                randX = (int) (Math.random() * (MATCH_SCREEN_WIDTH - width));
                randY = (int) (Math.random() * (MATCH_SCREEN_HEIGHT - BUTTON_HEIGHT - 20));
                tempRect = new Rectangle(randX, randY, width + 10, BUTTON_HEIGHT + 10, tempRects);
            }

            matchButtons[i].setBounds(tempRect.getTopLeftX(), tempRect.getTopLeftY(), width, BUTTON_HEIGHT);
            tempRects.add(tempRect);
        }
    }

    private void setRandomQuestions() {
        tempVocabMap = new HashMap<>();
        for (int i = 0; i < NUM_MATCH_BUTTONS / 2; i++) {
            int randIdx = (int) (Math.random() * vocabMap.size());

            currentQ = (String) vocabMap.keySet().toArray()[randIdx];
            if (tempVocabMap.containsKey(currentQ)) {
                i--;
                continue;
            }
            currentA = vocabMap.get(currentQ);
            tempVocabMap.put(currentQ, currentA);

            matchButtons[i].setText(currentQ);
            matchButtons[NUM_MATCH_BUTTONS - 1 - i].setText(currentA);
        }

        for (int i = 0; i < NUM_MATCH_BUTTONS; i++) {
            matchButtons[i].setVisible(true);
        }
    }
    
    private void initVocabMap(String filePath) {
        vocabMap = new HashMap<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line = br.readLine();
            while (line != null) {
                vocabMap.put(line.split(":")[0], line.split(":")[1]);
                line = br.readLine();
            }
        } catch (Exception err) {
            System.err.println("There was a problem reading the " + filePath);
        }

        totalAmt = "" + vocabMap.size();
    }

    private void changeQuestion() {
        if (vocabMap.size() >= 1) {
            changeQnA();
            questionLabel.setText(currentQ);
        } else {
            questionLabel.setText("No more words.");
            ansField.setEditable(false);
            checkBtn.setVisible(false);
            showAnsButton.setVisible(false);
            skipBtn.setVisible(false);
        }
    }

    private void changeQnA() {
        double randIdx1 = Math.random();
        int randIdx2 = (int) (Math.random() * vocabMap.size());

        if (randIdx1 < 0.5) {
            currentQ = (String) vocabMap.keySet().toArray()[randIdx2];
            currentA = vocabMap.get(currentQ);
            vocabMap.remove(currentQ);
        } else {
            currentA = (String) vocabMap.keySet().toArray()[randIdx2];
            currentQ = vocabMap.get(currentA);
            vocabMap.remove(currentA);
        }

        amtLabel.setText(Integer.parseInt(totalAmt) - vocabMap.size() + "/" + totalAmt);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == learnButton) {
            gameMode = "learn";
            learnButton.setVisible(false);
            matchButton.setVisible(false);

            ansField.setVisible(true);
            amtLabel.setVisible(true);
            questionLabel.setVisible(true);
            responseLabel.setVisible(true);
            checkBtn.setVisible(true);
            skipBtn.setVisible(true);
            showAnsButton.setVisible(true);

            exitButton.setVisible(true);
            frame.setSize(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
        }
        if (e.getSource() == matchButton) {
            gameMode = "match";
            learnButton.setVisible(false);
            matchButton.setVisible(false);

            setRandomLocations();
            setRandomQuestions();

            for (JButton button : matchButtons) {
                button.setVisible(true);
            }

            exitButton.setVisible(true);
            frame.setSize(MATCH_SCREEN_WIDTH, MATCH_SCREEN_HEIGHT);
        }

        if (gameMode.equals("learn")) {
            if (e.getSource() == checkBtn) {
                if (ansField.getText().trim().equalsIgnoreCase(currentA.trim())) {
                    ansField.setText("");
                    changeQuestion();
                    responseLabel.setText("");
                } else {
                    responseLabel.setText("Try again!");
                }
            }
            if (e.getSource() == showAnsButton) {
                responseLabel.setText(currentA);
            }
            if (e.getSource() == skipBtn) {
                ansField.setText("");
                changeQuestion();
                responseLabel.setText("");
            }
        }

        if (gameMode.equals("match")) {
            for (int i = 0; i < NUM_MATCH_BUTTONS; i++) {
                if (e.getSource() == matchButtons[i]) {
                    if (firstClick == -1) {
                        firstClick = i;
                    } else {
                        if (firstClick + i == NUM_MATCH_BUTTONS - 1) {
                            matchButtons[firstClick].setVisible(false);
                            matchButtons[i].setVisible(false);
                            tempVocabMap.remove(matchButtons[Math.min(firstClick, i)].getText());
                        }
                        firstClick = -1;
                    }
                    if (tempVocabMap.size() == 0) {
                        setRandomLocations();
                        setRandomQuestions();
                    }
                }
            }
        }

        if (e.getSource() == exitButton) {
            gameMode = "";
            learnButton.setVisible(true);
            matchButton.setVisible(true);

            ansField.setVisible(false);
            amtLabel.setVisible(false);
            questionLabel.setVisible(false);
            responseLabel.setVisible(false);
            checkBtn.setVisible(false);
            skipBtn.setVisible(false);
            showAnsButton.setVisible(false);
            exitButton.setVisible(false);

            for (JButton button : matchButtons) {
                button.setVisible(false);
            }

            frame.setSize(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
        }
    }

    public static void main(String[] args) {
        String input = JOptionPane.showInputDialog("What vocab unit?");
        Quizlit ql = new Quizlit("vocabLists/" + input + ".txt");
    }
}
