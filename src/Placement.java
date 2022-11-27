public class Placement {
    int row, col;
    char dir;
    boolean cP;
    String word, hint;

    public Placement(int row, int col, char direction, boolean canPlace, String word, String hint) {
        this.row = row;
        this.col = col;
        this.dir = direction;
        this.cP = canPlace;
        this.word = word;
        this.hint = hint;
    }

    @Override
    public String toString() {
        return "Placement{" +
                "row=" + row +
                ", col=" + col +
                ", dir=" + dir +
                ", cP=" + cP +
                ", word='" + word + '\'' +
                ", hint='" + hint + '\'' +
                '}';
    }
}
