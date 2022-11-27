import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Crossword {
    private static final int BASE_GRID_SIZE = 100;
    /**
     * Creates the optimal crossword using a random subset of words
     * @param words map of words to be in the crossword
     * @param grid the template grid for the crossword
     * @return final crossword grid (answer grid)
     */
    public static CrosswordBoard createBoard(HashMap<String, String> words, char[][] grid, int max_words) {
        ArrayList<Placement> finalPms = new ArrayList<>();

        int randIdx = (int) (Math.random() * words.size());
        String firstWord = (String) words.keySet().toArray()[randIdx]; // randomly picks a word to place on the board first

        grid = placeWord(firstWord, grid, grid.length/2, (grid[0].length - firstWord.length())/2, 'h'); // places word in relative middle horizontally
        finalPms.add(new Placement(grid.length/2, (grid[0].length - firstWord.length())/2, 'h', true, Quizlit.formatText(firstWord), words.get(firstWord)));
        words.remove(firstWord);

        int count = 1;
        while (count < max_words && words.size() > 0) {
            int tempRandIdx = (int) (Math.random() * words.size());
            String word = (String) words.keySet().toArray()[tempRandIdx]; // randomly choose a word to put on board
            ArrayList<Placement> possiblePms = new ArrayList<>(); // array of possible placements it can go

            for (int i = 0; i < word.length(); i++) { // loops through each character in word
                for (int r = 0; r < grid.length; r++) {
                    for (int c = 0; c < grid[0].length; c++) {
                        if (grid[r][c] == word.charAt(i)) { // if the character exists in the grid, it's a possible intersection point
                            Placement temp = canPlace(word, words.get(word), grid, r, c, i); // "Placement" object to store important placement data - checks if a location is valid placement or not
                            if (temp.cP) {
                                possiblePms.add(temp); // if it's a valid placement, adds it to the list
                            }
                        }
                    }
                }
            }

            double maxScore = 0;
            char[][] bestBoard = new char[BASE_GRID_SIZE][BASE_GRID_SIZE]; // best board placement for the current word
            Placement bestPlacement = null;

            for (Placement p : possiblePms) {
                char[][] tempBoard = placeWord(word, grid, p.row, p.col, p.dir); // creates a new board for each placement
                double tempScore = genScore(trimGrid(tempBoard)); // scores and finds the max score
                if (tempScore > maxScore) {
                    maxScore = tempScore;
                    bestBoard = copyArr(tempBoard);
                    bestPlacement = new Placement(p.row, p.col, p.dir, p.cP, Quizlit.formatText(p.word), p.hint);
                }
            }

            if (maxScore > 0){ // sometimes scores can be negative/infinity, so we need to check if it's > 0
                grid = copyArr(bestBoard);
                finalPms.add(bestPlacement);
                count++;
            }
            words.remove(word); // remove word so it can't show up again
        }
        grid = trimGrid(grid);
        return new CrosswordBoard(grid, genScore(grid), finalPms); // trim down the 50x50 grid since a lot of it is white space
    }

    /**
     * Creates and copies the contents of the grid to a new grid
     * @param array original array to be copied
     * @return copied array
     */
    private static char[][] copyArr(char[][] array) {
        char[][] tempGrid = new char[array.length][array[0].length]; // creates array
        for (int i = 0; i < array.length; i++) { // loops over original array and copies values over 1:1
            System.arraycopy(array[i], 0, tempGrid[i], 0, array[0].length);
        }
        return tempGrid;
    }

    /**
     * Generates a score for a given crossword depending on # of unfilled boxes and relative size
     * @param grid crossword grid to be scored
     * @return score of the crossword
     */
    private static double genScore(char[][] grid) {
        if (grid.length <=  1 || grid[0].length <= 1) return 0;

        double empty = 0, filled = 0;
        for (char[] chars : grid) { // finds number of filled and empty boxes
            for (int c = 0; c < grid[0].length; c++) {
                if (chars[c] == '-' || chars[c] == '\u0000') empty++;
                else filled++;
            }
        }
        return 20 * (filled/empty) + 15 * ((double)Math.min(grid.length, grid[0].length) / Math.max(grid.length, grid[0].length)); // random math to give score
    }

    /**
     * Finds top left and bottom right coordinates of crossword to "trim" down the array
     * @param grid original grid to be trimmed
     * @return trimmed array with less whitespace
     */
    private static char[][] trimGrid(char[][] grid) {
        Point top = null, bottom = null, left = null, right = null;
        for (int r = 0; r < grid.length; r++) { // loops over original grid
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] != '\u0000') {
                    if (top == null) top = new Point(r, c); // first character it finds is the top point
                    bottom = new Point(r, c); // every character up until last one is the bottom point
                }
            }
        }
        for (int c = 0; c < grid[0].length; c++) { // loops over grid "sideways"
            for (int r = 0; r < grid.length; r++) {
                if (grid[r][c] != '\u0000') {
                    if (left == null) left = new Point(r, c); // see previous for loop
                    right = new Point(r, c);
                }
            }
        }

        assert bottom != null;
        assert right != null;
        char[][] tempGrid = new char[bottom.getX() - top.getX() + 1][right.getY() - left.getY() + 1]; // creates a temp grid that is bounding box for the words

        for (int r = top.getX(); r <= bottom.getX(); r++) { // copies over values
            if (right.getY() + 1 - left.getY() >= 0)
                System.arraycopy(grid[r], left.getY(), tempGrid[r - top.getX()], 0, right.getY() + 1 - left.getY());
        }
        return tempGrid;
    }

    /**
     * Checks if a word can be placed and returns the location where it can be placed
     * @param word word to be placed
     * @param grid current crossword grid
     * @param row intersection row index
     * @param col intersection column index
     * @param index index of the letter in the word where it will intersect
     * @return placement object with orientation information
     */
    private static Placement canPlace(String word, String hint, char[][] grid, int row, int col, int index) {
        String beg = word.substring(0, index); // split the original word into before intersection and after intersection
        String end = word.substring(index + 1);
        Placement defaultO = new Placement(-1, -1, 'n', false, "", ""); // default placement (if it can't be put anywhere)

        // checking if it fits horizontally
        if (col - beg.length() >= 0 && col + end.length() < grid[0].length) { // checking if a word horizontally fits in the array without going out of bounds
            int c = 0;
            for (int i = col - beg.length(); i <= col + end.length(); i++) {
                if (i != col) { // if there's a word directly above or below, the loop will exit - returns default object
                    if ((inBound(grid, row - 1, col) && grid[row - 1][i] != '\u0000') ||
                            inBound(grid, row + 1, col) && grid[row + 1][i] != '\u0000') break;
                }

                if (grid[row][i] == '\u0000') c++;
            }
            if (c + 1 == word.length()) { // if there are enough empty spaces for the word to put there
                if ((inBound(grid, row, col - beg.length() - 1) && grid[row][col - beg.length() - 1] != '\u0000') ||
                        (inBound(grid, row, col + end.length() + 1) && grid[row][col + end.length() + 1] != '\u0000'))
                    return defaultO; // checks if a word is already at the left and right ends - search up "how do i check to make sure banana doesnt run onto another word" in discord

                return new Placement(row, col - beg.length(), 'h', true, word, hint); // if all the checks pass, the final placement object is returned
            }
        }

        // checking if it fits vertically
        if (row - beg.length() >= 0 && row + end.length() < grid.length) { // same logic as checking horizontally - only now with rows
            int c = 0;
            for (int i = row - beg.length(); i <= row + end.length(); i++) {
                if (i != row) {
                    if ((inBound(grid, row, col - 1) && grid[i][col - 1] != '\u0000') ||
                            (inBound(grid, row, col + 1) && grid[i][col + 1] != '\u0000')) break;
                }
                if (grid[i][col] == '\u0000') c++;
            }

            if (c + 1 == word.length()) {
                if ((inBound(grid, row - beg.length() - 1, col) && grid[row - beg.length() - 1][col] != '\u0000') ||
                        (inBound(grid, row + end.length() + 1, col) && grid[row + end.length() + 1][col] != '\u0000'))
                    return defaultO;

                return new Placement(row - beg.length(), col, 'd', true, word, hint);
            }
        }


        return defaultO;
    }

    /**
     * Places the given word in the location in the crossword
     * @param word word to be placed
     * @param grid current crossword grid
     * @param r row index to start placing at
     * @param c column index to start placing at
     * @param dir direction word is placed in
     * @return new crossword grid with the word placed in it
     */
    private static char[][] placeWord(String word, char[][] grid, int r, int c, char dir) {
        char[][] tempGrid = copyArr(grid); // creates a temp grid

        for (int i = 0; i < word.length(); i++) { // loops through letters
            if (dir == 'h') tempGrid[r][c + i] = word.charAt(i); // hor
            else tempGrid[r + i][c] = word.charAt(i); // ver
        }

        return tempGrid;
    }

    /**
     * Creates the word list of the crossword puzzle
     * @return the list of words
     */
    public static HashMap<String, String> createWordsList(String filePath) {
        HashMap<String, String> wordsMap = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line = br.readLine();
            while (line != null) {
                String[] pair = line.split(":"); // splits line into answer and hint
                pair[0] = replacePunctuation(pair[0]); // removes punctuation from both
                pair[1] = replacePunctuation(pair[1]);
                if (pair[0].split(" ").length <= 2) { // if the answer only has 1 word excluding articles, it's added
                    if (!pair[0].contains(" ")) wordsMap.put(pair[0], pair[1]);
                    if (pair[0].startsWith("el ") || pair[0].startsWith("la ") || pair[0].startsWith("un ")) wordsMap.put(pair[0].substring(3).trim(), pair[1]);
                    if (pair[0].startsWith("una ")) wordsMap.put(pair[0].substring(4).trim(), pair[1]);
                }
                // wordsMap.put(line, ""); // <-- uncomment if using crosswords map

                line = br.readLine();
            }
        } catch (Exception err) {
            System.err.println("There was a problem reading the " + filePath);
        }

        return wordsMap;
    } // needs to be reworked to factor in hints

    /**
     * Replaces all punctuation in a string
     * @param s string to be cleaned
     * @return cleaned string
     */
    private static String replacePunctuation(String s) {
        return s.toLowerCase().replaceAll("\\p{Punct}", "").replaceAll("¿", "").replaceAll("¡", "").trim();
    }

    /**
     * Prints out the answer crossword board
     * @param grid grid to be printed
     */
    public static void printCharBoard(char[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '\u0000') grid[i][j] = '-'; // replaces null chars with a -
                System.out.print(grid[i][j] + "  ");
            }
            System.out.println();
        }

        System.out.println("This board has a score of: " + genScore(grid));
    }

    /**
     * Prints out the blank crossword board
     * @param grid grid to be printed
     */
    public static void printStringBoard(String[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == null) grid[i][j] = "-"; // replaces null strings with a -
                if (grid[i][j].length() > 1) System.out.print(grid[i][j] + " ");
                else System.out.print(grid[i][j] + "  ");
            }
            System.out.println();
        }
    }

    /**
     * Checks if a location is inside the array
     * @param arr array that is being checked
     * @param r row index
     * @param c column index
     * @return boolean if it's in bounds
     */
    public static boolean inBound(char[][] arr, int r, int c) {
        return (arr.length - 1 >= r && arr[0].length - 1 >= c && r >= 0 && c >= 0);
    }

    /**
     * Creates the best crossword board through many iterations
     * @param filePath file path of the word list
     * @param maxWords maximum number of words to be put on crossword
     * @param iterations number of boards to construct
     * @return the board with the highest score
     */
    public static CrosswordBoard createBestBoard(String filePath, int maxWords, int iterations) {
        int index = 0;
        ArrayList<CrosswordBoard> boards = new ArrayList<>(); // creates lists of boards

        double start = System.currentTimeMillis(); // run time calculator
        for (int i = 0; i < iterations; i++) {
            CrosswordBoard tempBoard = createBoard(createWordsList(filePath), new char[BASE_GRID_SIZE][BASE_GRID_SIZE], maxWords); // makes a new board
            boards.add(tempBoard);
            if (tempBoard.getScore() > boards.get(index).getScore() && tempBoard.getScore() > 0) { // if the new board has the highest score, index is set to that index
                index = i;
            }
        }
        double end = System.currentTimeMillis();

        System.out.println("Generated " + iterations + " crossword puzzles in " + (end - start)/1000 + "s!");
        return boards.get(index);
    }
}
