public class Main {
    public static void main(String[] args) {
        String filePath = "vocabLists/unit4.txt";

        CrosswordBoard grid = Crossword.createBestBoard(filePath , 30, 200);
        Crossword.printCharBoard(grid.getAnswerGrid()); // print out the boards
        Crossword.printStringBoard(grid.getFillInGrid());
        System.out.println(grid.getHints());
    }



    /* private static void createBoard(ArrayList<String> words, char[][] grid) {
        int NUM_WORDS = 3;
        for (int i = 0; i < NUM_WORDS; i++) {
            int p = 0;
            while (true) {
                int randRow = (int) (Math.random() * 8);
                int randCol = (int) (Math.random() * 8);
                String word = words.get((int) (Math.random() * words.size()));

                if (Math.random() < 0.5) {
                    if (wordFits(word, grid, randRow, randCol, "across")) {
                        for (int j = 0; j < word.length(); j++) {
                            grid[randRow][randCol + j] = word.charAt(j);
                        }
                        words.remove(word.replaceAll("-", ""));
                        break;
                    }
                } else {
                    if (wordFits(word, grid, randRow, randCol, "down")) {
                        for (int j = 0; j < word.length(); j++) {
                            grid[randRow + j][randCol] = word.charAt(j);
                        }
                        words.remove(word.replaceAll("-", ""));
                        break;
                    }
                }
                System.out.println("On possibility " + p);
                p++;
            }
        }
    }

    private static boolean wordFits(String word, char[][] grid, int randRow, int randCol, String dir) {
        if ((dir.equals("across") && word.length() + randCol > grid[0].length) ||
                (dir.equals("down") && word.length() + randRow > grid.length) ||
                hasNeighboringWord(grid, randRow, randCol)) return false;

        if (dir.equals("across")) {
            if (hasNeighboringWord(grid, randRow, randCol + word.length())) return false;
        }
        if (dir.equals("down")) {
            if (hasNeighboringWord(grid, randRow + word.length(), randCol)) return false;
        }

        int horizontalA = dir.equals("across") ? 1 : 0;
        int verticalA = dir.equals("across") ? 0 : 1;


        for (int i = 0; i < word.length(); i++) {
            char gridVal = grid[randRow + (i * verticalA)][randCol + (i * horizontalA)];
            if (gridVal != '\u0000' && word.charAt(i) != gridVal) return false;
        }


        return true;
    }

    private static boolean hasNeighboringWord(char[][] grid, int randRow, int randCol) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (inBound(grid, randRow + i, randCol + j) && grid[randRow + i][randCol + j] != '\u0000') return true;
            }
        }
        return false;

    } */
}
