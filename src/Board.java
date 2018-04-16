
import java.awt.*;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author alu20908719v
 */
public class Board extends JPanel implements ActionListener {

    class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (canMoveTo(currentShape, currentRow, currentCol - 1)) {
                        currentCol--;

                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (canMoveTo(currentShape, currentRow, currentCol + 1)) {
                        currentCol++;
                    }
                    break;
                case KeyEvent.VK_UP:
                    Shape rotShape = currentShape.rotateRight();
                    if (canMoveTo(currentShape, currentRow, currentCol)) {
                        currentShape = rotShape;
                    }
                    break;
                case KeyEvent.VK_DOWN:

                    if (canMoveTo(currentShape, currentRow + 1, currentCol)) {
                        currentRow++;
                    }

                    break;
                case KeyEvent.VK_P:
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    if(! timer.isRunning()){
                        initGame();
                    }
                    break;

                default:
                    break;
            }
            repaint();
        }
    }

    public static final int NUM_ROWS = 22;
    public static final int NUM_COLS = 10;

    private Tetrominoes[][] matrix;
    private int deltaTime;
    private Shape currentShape;
    MyKeyAdapter keyAdapter;
    private int currentRow;
    private int currentCol;

    public IncrementScore scoreDelegate;

    private Timer timer;
    public static final int INIT_ROW = -2;

    public Board() {
        super();
        matrix = new Tetrominoes[NUM_ROWS][NUM_COLS];
        initValues();
        currentShape = new Shape(Tetrominoes.NoShape);
        timer = new Timer(deltaTime, this);
        MyKeyAdapter keyAdapter = new MyKeyAdapter();
        addKeyListener(keyAdapter);

    }

    public void setScore(IncrementScore score) {
        this.scoreDelegate = score;
    }

    public void initGame() {
        initValues();
        currentShape = new Shape();
        addKeyListener(keyAdapter);
        timer.start();

    }

    public void gameOver() {
        timer.stop();
        scoreDelegate.getScore();
        timer = new Timer(50, new ActionListener() {
            int row = 0;
            int col =0;

            @Override
            public void actionPerformed(ActionEvent ae) {
                currentShape=null;
                if (row != NUM_ROWS && col != NUM_COLS) {

                    matrix[row][col] = Tetrominoes.LShape;
                    if (col < NUM_COLS) {
                        col++;

                    }
                    if (col == NUM_COLS) {
                        row++;
                        col = 0;
                    }
                    repaint();
                }
            }
        });
        timer.start();
    } 
                

    /*for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                matrix[row][col] = Tetrominoes.LShape;
                repaint();

            }*/

    public void initValues() {
        setFocusable(true);
        cleanBoard();
        deltaTime = 500;
        currentShape = null;
        currentRow = INIT_ROW;
        currentCol = NUM_COLS / 2;

    }

    public void cleanBoard() {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                matrix[row][col] = Tetrominoes.NoShape;
            }
        }
    }

    private boolean canMoveTo(Shape shape, int newRow, int newCol) {

        if (newCol + shape.getXmin() < 0
                || newCol + shape.getXmax() >= NUM_COLS
                || newRow + shape.getYmax() >= NUM_ROWS
                || hitWithMatrix(shape, newRow, newCol)) {
            return false;

        }
        return true;

    }

    private boolean hitWithMatrix(Shape shape, int newRow, int newCol) {

        int[][] squaresArray = shape.getCoordinates();
        int row;
        int col;
        for (int point = 0; point <= 3; point++) {
            row = newRow + squaresArray[point][1];
            col = newCol + squaresArray[point][0];
            if (row >= 0 && row < NUM_ROWS) {
                if (matrix[row][col] != Tetrominoes.NoShape) {
                    return true;

                }
            }
        }
        return false;

    }

    //Game Main loop
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (canMoveTo(currentShape, currentRow + 1, currentCol)) {
            currentRow++;
            repaint();
        } else {
            checkGameOver();
            moveCurrentShapeToMatrix();
            currentShape = new Shape();
            currentRow = INIT_ROW;
            currentCol = NUM_COLS / 2;
            checkRows();
        }

    }
        

    public boolean checkGameOver() {
        int[][] squaresArray = currentShape.getCoordinates();

        for (int point = 0; point <= 3; point++) {
            if (currentRow + squaresArray[point][1] < 0) {
                timer.stop();
                gameOver();
                return true;
            }

        }
        return false;

    }

    public void checkRows() {
        boolean clean = true;
        for (int row = 0; row < NUM_ROWS; row++) {
            clean = true;

            for (int col = 0; col < NUM_COLS; col++) {
                if (matrix[row][col] == Tetrominoes.NoShape) {
                    clean = false;
                }
            }
            if (clean) {
                cleanRow(row);
            }

        }
    }

    public void cleanRow(int rowCompleted) {

        for (int row = rowCompleted; row >= 1; row--) {
            for (int col = 0; col < NUM_COLS; col++) {
                matrix[row][col] = matrix[row - 1][col];

            }
        }
        for (int col = 0; col < NUM_COLS; col++) {
            matrix[0][col] = Tetrominoes.NoShape;
        }
        scoreDelegate.increment(100);
        decrementDelay();
        repaint();
        

    }
    public void decrementDelay(){
        deltaTime*=0.9;
        timer.setDelay(deltaTime);
    }

    private void moveCurrentShapeToMatrix() {
        int[][] squaresArray = currentShape.getCoordinates();

        for (int point = 0; point <= 3; point++) {
            matrix[currentRow + squaresArray[point][1]][currentCol + squaresArray[point][0]] = currentShape.getShape();
        }
    }

    public void drawBorder(Graphics g) {
        g.setColor(Color.red);
        g.draw3DRect(0, 0, NUM_COLS * squareWidth(), NUM_ROWS * squareHeight(), true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        if (currentShape != null) {
            drawCurrentShape(g);
        }
        drawBorder(g);
    }

    public void drawBoard(Graphics g) {

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                drawSquare(g, row, col, matrix[row][col]);

            }
        }

    }

    private void drawSquare(Graphics g, int row, int col, Tetrominoes shape) {
        Color colors[] = {new Color(0, 0, 0),
            new Color(204, 102, 102),
            new Color(102, 204, 102), new Color(102, 102, 204),
            new Color(204, 204, 102), new Color(204, 102, 204),
            new Color(102, 204, 204), new Color(218, 170, 0)
        };
        int x = col * squareWidth();
        int y = row * squareHeight();
        Color color = colors[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2,
                squareHeight() - 2);
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1,
                y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

    private int squareWidth() {
        return getWidth() / NUM_COLS;

    }

    private int squareHeight() {
        return getHeight() / NUM_ROWS;
    }

    private void drawCurrentShape(Graphics g) {

        int[][] squaresArray = currentShape.getCoordinates();

        for (int point = 0; point <= 3; point++) {
            drawSquare(g, currentRow + squaresArray[point][1], currentCol + squaresArray[point][0], currentShape.getShape());
        }
    }

}
