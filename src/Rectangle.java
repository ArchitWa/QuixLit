import java.util.ArrayList;

public class Rectangle {
    private final Point topLeft, topRight, bottomLeft, bottomRight;
    private int topLeftX, topLeftY;
    private final ArrayList<Rectangle> otherRects;

    public Rectangle(int topLeftX, int topLeftY, int width, int height, ArrayList<Rectangle> otherRects) {
        topLeft = new Point(topLeftX, topLeftY);
        topRight = new Point(topLeftX + width, topLeftY);
        bottomLeft = new Point(topLeftX, topLeftY + height);
        bottomRight = new Point(topLeftX + width, topLeftY + height);

        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.otherRects = otherRects;
    }

    public int getTopLeftX() {return topLeftX;}
    public int getTopLeftY() {return topLeftY;}

    public boolean isClear() {
        for (Rectangle otherRect : otherRects) {
            if (isOverlapping(otherRect)) return false;
        }
        return true;
    }

    public boolean isOverlapping(Rectangle other) {
        if (this.topRight.getX() < other.bottomLeft.getX()
                || this.bottomLeft.getX() > other.topRight.getX()) {
            return false;
        }
        if (this.topRight.getY() > other.bottomLeft.getY()
                || this.bottomLeft.getY() < other.topRight.getY()) {
            return false;
        }
        return true;
    }
}
