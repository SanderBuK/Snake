package snake;

import javafx.scene.shape.Circle;

public class Food extends Circle {
    private int x;
    private int y;
    private int size;

    public Food(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }
}
