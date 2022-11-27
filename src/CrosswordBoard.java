import java.util.ArrayList;

public class CrosswordBoard {
    private char[][] answerGrid;
    private String[][] fillInGrid;
    private ArrayList<Placement> placements;
    private ArrayList<String> hints;
    private double score;

    /**
     * Creates a new CrosswordBoard
     * @param answerGrid grid of answers
     * @param score the score of the board
     * @param placements locations of each word
     */
    public CrosswordBoard(char[][] answerGrid, double score, ArrayList<Placement> placements) {
        this.answerGrid = answerGrid;
        this.fillInGrid = new String[answerGrid.length][answerGrid[0].length];
        this.score = score;
        this.placements = trimPlacements(placements);
        hints = new ArrayList<>();

        createHints();
        createFillInGrid();
    }

    /**
     * Trims the placement list so that the coordinates match the grid
     * @param placements list of placements of the words
     * @return trimmed placement list
     */
    private ArrayList<Placement> trimPlacements(ArrayList<Placement> placements) {
        Placement p1 = placements.get(0); // using first word to offset the locations
        int rowDiff = 0, colDiff = 0;

        L:
        for (int r = 0; r < answerGrid.length; r++) {
            for (int c = 0; c < answerGrid[0].length; c++) {
                if (wordIsAt(p1.word, r, c)) { // checks if the word is at each spot in the grid
                    rowDiff = p1.row - r; // finds the difference between untrimmed and actual indexes
                    colDiff = p1.col - c;
                    break L;
                }
            }
        }

        for (Placement p : placements) { // for each placement, update the row and col indexes to match the new array
            p.row = p.row - rowDiff;
            p.col = p.col - colDiff;
        }

        return placements;
    }

    /**
     * Checks if a word is located at the starting location
     * @param word word to check if it exists there
     * @param row row index of start of word
     * @param col col index of start of word
     * @return boolean if the word is there
     */
    private boolean wordIsAt(String word, int row, int col) {
        int c = 0;
        if (Crossword.inBound(answerGrid, row, col + word.length() - 1)) { // first word is always horizontal; no need to check vertically
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == answerGrid[row][col + i]) c++; // if the letter matches, a counter is incremented
            }
        }
        return c == word.length(); // if counter equals the word length, it exists there
    }

    /**
     * Creates the blank grid using placements and answer grid
     */
    private void createFillInGrid() {
        for (int i = 0; i < fillInGrid.length; i++) { // fills fillInGrid with all "-"'s
            for (int j = 0; j < fillInGrid[0].length; j++) {
                fillInGrid[i][j] = "-";
            }
        }

        for (int i = 0; i < placements.size(); i++) { // initial numbering
            Placement p = placements.get(i);
            String v = fillInGrid[p.row][p.col]; // the location of v is where a new word beings
            if (v.equals("-") || v.equals("?")) { // if the location isn't already taken, it sets it to be the index
                fillInGrid[p.row][p.col] = "" + (i + 1);
            } else {
                fillInGrid[p.row][p.col] += "/" + (i + 1); // if it is taken, it initially shows both indexes in the same spot
            }

            for (int j = 1; j < p.word.length(); j++) { // for every location after first letter, the string is replaced with a "?": signifying a word exists there
                if (p.dir == 'h') {
                    if (fillInGrid[p.row][p.col + j].equals("-")) fillInGrid[p.row][p.col + j] = "?"; // if it's not occupied, it will replace the dash; if it has a number there already, nothing is changed
                } else {
                    if (fillInGrid[p.row + j][p.col].equals("-")) fillInGrid[p.row + j][p.col] = "?";
                }
            }
        }

        for (int r = 0; r < fillInGrid.length; r++) { // now checks if two numbers exist at the same location (i.e: 1/2)
            for (int c = 0; c < fillInGrid[0].length; c++) {
                String v = fillInGrid[r][c];
                if (v.contains("/")) {
                    int oldIndex = Integer.parseInt(v.substring(v.indexOf("/") + 1)) - 1;
                    hints.set(oldIndex, v.charAt(0) + " " + hints.get(oldIndex).substring(2)); // updates the hint of that index to have the same number (still tells direction obv)
                    fillInGrid[r][c] = v.substring(0, 1); // replaces text at the location to only be one number: 1/2 -> 1
                }
            }
        }
    }

    /**
     * Creates the list of hints for each word
     */
    private void createHints() {
        for (int i = 0; i < placements.size(); i++) { // For each placement sets the hint to be the index + 1, direction and hint
            hints.add((i + 1) + " " + (placements.get(i).dir == 'h' ? "across" : "down") + ": " + placements.get(i).hint);
        }
    }

    // ---- Getters for all the field variables ----

    public char[][] getAnswerGrid() {return answerGrid;}

    public String[][] getFillInGrid() {
        return fillInGrid;
    }

    public ArrayList<String> getHints() {
        return hints;
    }

    public double getScore() {
        return score;
    }

    public ArrayList<Placement> getPlacements() {
        return placements;
    }
}
