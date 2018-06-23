package snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import javafx.scene.shape.Circle;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Snake extends JApplet {

    //Used to know whe to spawn the snake and the food, and what text to display
    boolean start = false;

    Color foodColor = new Color(0, 102, 255);
    int foodAmmount = 0;
    int requiredFood = 1;
    ArrayList<Food> foods = new ArrayList();

    boolean alive;
    //The time between updating every frame in milliseconds
    int frameRate = 10;
    //When the snake eats a foods thingy, it shouldn't delete the hinderest snake-piece
    boolean growing = false;
    char direction = ' ';
    //Used to prevent being able to move from left to right or from up to down or the reverse.
    char prevMove = ' ';
    int snakeSpeed = 100;
    Color snakeColor = new Color(204, 0, 0);
    //Array of the snakes coordinates. te first index in the int[] is the x-coordinate, and the second is the y-coordinate
    ArrayList<int[]> snakeCoordinates = new ArrayList();

    @Override
    public void init() {
        Canvas canvas = new Canvas();
        super.add(canvas);

        super.setFocusable(true);

        super.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                System.out.println("Hey");
                //Evaluate wether the food is out of the window, when it is resized
                ArrayList<Food> removeFoods = new ArrayList();
                int newFoods = 0;
                for (Food food : foods) {
                    if (food.getX() + 10 > getContentPane().getWidth() || food.getY() + 10 > getContentPane().getHeight()) {
                        removeFoods.add(food);
                        newFoods++;
                    }
                }
                for (Food food : removeFoods) {
                    foods.remove(food);
                    foodAmmount--;
                }
                for (int i = 0; i < newFoods; i++) {
                    addFood();
                }
            }
        });

        super.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        if (direction != 'u' && prevMove != 'u') {
                            direction = 'd';
                        }
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        if (direction != 'd' && prevMove != 'd') {
                            direction = 'u';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        if (direction != 'l' && prevMove != 'l') {
                            direction = 'r';
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        if (direction != 'r' && prevMove != 'r') {
                            direction = 'l';
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        if (!start) {
                            start = true;
                            alive = true;
                            initializeSnake();
                        }
                        break;
                }
            }
        });
    }

    private void evaluateFood() {
        //Add the foods to be removed in this list, and remove the list from the food list. This is to prevent ConcurrentModifictaionException
        ArrayList<Food> removeFoods = new ArrayList();
        for (Food food : foods) {
            if (food.getX() == snakeCoordinates.get(0)[0] && food.getY() == snakeCoordinates.get(0)[1]) {
                removeFoods.add(food);
            }
        }
        for (Food food : removeFoods) {
            foods.remove(food);
            foodAmmount--;
            snakeSpeed -= (int) snakeSpeed * 0.01;
            growing = true;
        }
        addFood();
    }

    private void addFood() {
        if (foodAmmount < requiredFood) {
            foods.add(new Food((int) (Math.random() * (int) (getContentPane().getWidth() / 20) - 1) * 20,
                    (int) (Math.random() * (int) (getContentPane().getHeight() / 20) - 1) * 20, 10));
            foodAmmount++;
            for (int[] coordinate : snakeCoordinates) {
                if (foods.get(foods.size() - 1).getX() == coordinate[0] && foods.get(foods.size() - 1).getY() == coordinate[1]) {
                    foods.remove(foods.size() - 1);
                    foodAmmount--;
                    addFood();
                }
            }
        }
    }

    //This method is needed, as the Thread needs to be reset every time the snake dies.
    private void initializeSnake() {
        foods = new ArrayList();
        snakeCoordinates = new ArrayList();
        snakeSpeed = 100;
        foodAmmount = 0;
        direction = ' ';

        Thread moveSnake = new Thread() {
            @Override
            public void run() {
                while (alive) {
                    if (direction == ' ') {
                        snakeCoordinates.clear();
                        snakeCoordinates.add(new int[]{getContentPane().getWidth() / 20 / 2 * 20, getContentPane().getHeight() / 20 / 2 * 20});
                    }
                    try {
                        sleep(snakeSpeed);
                    } catch (InterruptedException ex) {
                        System.err.println("Error in movingSnake Thread");
                    }
                    int[] newCoordinates = new int[2];
                    switch (direction) {
                        case 'u':
                            //The x-coordinate should stay the same when the direction is up.
                            newCoordinates[0] = snakeCoordinates.get(0)[0];
                            newCoordinates[1] = snakeCoordinates.get(0)[1] - 20;
                            prevMove = 'u';
                            break;
                        case 'd':
                            newCoordinates[0] = snakeCoordinates.get(0)[0];
                            newCoordinates[1] = snakeCoordinates.get(0)[1] + 20;
                            prevMove = 'd';
                            break;
                        case 'l':
                            newCoordinates[0] = snakeCoordinates.get(0)[0] - 20;
                            newCoordinates[1] = snakeCoordinates.get(0)[1];
                            prevMove = 'l';
                            break;
                        case 'r':
                            newCoordinates[0] = snakeCoordinates.get(0)[0] + 20;
                            newCoordinates[1] = snakeCoordinates.get(0)[1];
                            prevMove = 'r';
                            break;
                        default:
                            newCoordinates[0] = snakeCoordinates.get(0)[0];
                            newCoordinates[1] = snakeCoordinates.get(0)[1];
                            break;
                    }

                    snakeCoordinates.add(0, newCoordinates);

                    if (!growing) {
                        snakeCoordinates.remove(snakeCoordinates.size() - 1);
                    }
                    growing = false;

                    //Check if the snake is eating itself.
                    for (int i = 1; i < snakeCoordinates.size() - 1; i++) {
                        if (snakeCoordinates.get(i)[0] == snakeCoordinates.get(0)[0]
                                && snakeCoordinates.get(i)[1] == snakeCoordinates.get(0)[1]) {
                            System.out.println("DED");
                            alive = false;
                            start = false;
                        }
                    }

                    for (int[] coordinate : snakeCoordinates) {
                        if (coordinate[0] < 0) {
                            coordinate[0] = (int) ((getContentPane().getWidth() / 20) - 1) * 20;
                        } else if (coordinate[0] > (int) ((getContentPane().getWidth() / 20) - 1) * 20) {
                            coordinate[0] = 0;
                        } else if (coordinate[1] < 0) {
                            coordinate[1] = (int) ((getContentPane().getHeight() / 20) - 1) * 20;
                        } else if (coordinate[1] > (int) ((getContentPane().getHeight() / 20) - 1) * 20) {
                            coordinate[1] = 0;
                        }
                    }

                    evaluateFood();
                }
            }
        };

        moveSnake.start();
    }

    class Canvas extends JPanel {

        Thread update = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(snakeSpeed);
                    } catch (InterruptedException ex) {
                        System.err.println("Error in update Thread");
                    }
                    repaint();
                }
            }
        };

        Canvas() {
            update.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (direction == ' ' && start && alive) {
                g.setFont(new Font("Arial", Font.BOLD, 21));
                FontMetrics fm = g.getFontMetrics();
                String up = "W/↑";
                g.drawString(up, getContentPane().getWidth() / 2 - fm.stringWidth(up) / 2, getContentPane().getHeight() / 2 - 10 - fm.getAscent() / 2);
                String down = "S/↓";
                g.drawString(down, getContentPane().getWidth() / 2 - fm.stringWidth(down) / 2, getContentPane().getHeight() / 2 + 10 + fm.getAscent());
                String left = "A/←";
                g.drawString(left, getContentPane().getWidth() / 2 - 15 - fm.stringWidth(left), getContentPane().getHeight() / 2 + fm.getAscent() / 2 - 5);
                String right = "D/→";
                g.drawString(right, getContentPane().getWidth() / 2 + fm.stringWidth(right) / 2, getContentPane().getHeight() / 2 + fm.getAscent() / 2 - 5);
            }

            g.setFont(new Font("Arial", Font.BOLD, 25));
            FontMetrics fm = g.getFontMetrics();

            if (start && alive) {
                String score = "Score: " + (snakeCoordinates.size() - 1);
                g.drawString(score, getContentPane().getWidth() / 2 - fm.stringWidth(score) / 2,
                        0 + fm.getAscent());
                for (int[] coordinates : snakeCoordinates) {
                    //TODO: fix Exception in thread "AWT-EventQueue-0" java.util.ConcurrentModificationException!!!!!!!!!!
                    g.setColor(snakeColor);
                    g.fillOval(coordinates[0] + 1, coordinates[1] + 1, 18, 18);
                }
                for (Food food : foods) {
                    g.setColor(foodColor);
                    g.fillOval(food.getX() + 5, food.getY() + 5, food.getSize(), food.getSize());
                }
            } else if (!start) {
                String start = "Press 'SPACE' to start!";
                g.drawString(start, getContentPane().getWidth() / 2 - fm.stringWidth(start) / 2, getContentPane().getHeight() / 2 - fm.getAscent() / 2);
            } else if (!alive) {
                String score = "Final score: " + (snakeCoordinates.size() - 1);
                g.drawString(score, getContentPane().getWidth() / 2 - fm.stringWidth(score) / 2, getContentPane().getHeight() - fm.getAscent());
                String dead = "You died!";
                g.drawString(dead, getContentPane().getWidth() / 2 - fm.stringWidth(dead) / 2, getContentPane().getHeight() / 2 - fm.getAscent() / 2);
                String playAgagin = "Press 'SPACE' to try again!";
                g.drawString(playAgagin, getContentPane().getWidth() / 2 - fm.stringWidth(playAgagin) / 2, getContentPane().getHeight() / 2 + fm.getAscent() + fm.getAscent());
            }
        }
    }

    //Used if the class needs to be in a single file
    class Food extends Circle {

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

    public static void main(String[] args) {
        JFrame main = new JFrame("Snake");
        main.setSize(800, 500);
        Snake app = new Snake();

        main.add(app);
        app.init();
        app.start();

        main.setLocationRelativeTo(null);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setVisible(true);
    }
}
