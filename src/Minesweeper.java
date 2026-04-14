import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.swing.*;

public class Minesweeper {
    private class MineTile extends JButton{
        int row, column;

        public MineTile(int row, int column){
            this.row = row;
            this.column = column;
        }
    }

    int tileSize = 70;
    int numRows = 8;
    int numColumns = numRows;
    int boardWidth = numColumns * tileSize;
    int boardHeight = numRows * tileSize;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
    JLabel mineCountLabel = new JLabel("Mines:");

    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();

    JPanel startGamePanel = new JPanel();
    JButton startButton = new JButton();

    Scanner scanner = new Scanner(System.in);
    int mineNum = scanner.nextInt();

    JTextField mineNumsField = new JTextField(String.valueOf(mineNum), 2);

    int mineCount = Integer.parseInt(mineNumsField.getText());
    MineTile[][] board = new MineTile[numRows][numColumns];
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0; //goal is to click all tiles except the ones containing mines
    boolean gameOver = false;

    Minesweeper(){
//        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + Integer.toString(mineCount));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.NORTH);
        controlPanel.add(mineCountLabel);
        controlPanel.add(mineNumsField);
        textPanel.add(controlPanel, BorderLayout.SOUTH);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numColumns)); //8 x 8
        frame.add(boardPanel);

        for (int row = 0; row < numRows; row++){
            for (int column = 0; column < numColumns; column++){
                MineTile tile = new MineTile(row, column);
                board[row][column] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0,0,0,0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
//                tile.setText("💣");
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver){
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();

                        //left click
                        if (e.getButton() == MouseEvent.BUTTON1){
                            if (tile.getText().isEmpty()){
                                if (mineList.contains(tile)){
                                    revealMines();
                                }
                                else {
                                    checkMine(tile.row, tile.column);
                                }
                            }
                        }
                        //right click
                        else if (e.getButton() == MouseEvent.BUTTON3){
                            if (tile.getText().isEmpty() && tile.isEnabled()){
                                tile.setText("🚩");
                            }
                            else if ("🚩".equals(tile.getText())){
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }

        //restart game button
        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                if (!gameOver) {
                                                    return;
                                                }

                                                gameOver = false;
                                                restartButton.setEnabled(false);

                                            }
                                        });

        //start game button
        startButton.setFont(new Font("Arial", Font.PLAIN, 16));
        startButton.setText("Restart Game");
        startButton.setPreferredSize(new Dimension(boardWidth, 30));
        startButton.setFocusable(false);
        startButton.setEnabled(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver && mineCount != 0) {
                    startButton.setEnabled(false);
                }
            }
        });

        frame.setVisible(true);

        setMines();

    }

    void setMines(){
        mineList = new ArrayList<MineTile>();

//        mineList.add(board[2][2]);
//        mineList.add(board[2][3]);
//        mineList.add(board[5][6]);
//        mineList.add(board[3][4]);
//        mineList.add(board[1][1]);
        int mineLeft = mineCount;
        while (mineLeft > 0){
            int row = random.nextInt(numRows); // 0-7
            int column = random.nextInt(numColumns); // 0-7

            MineTile tile = board[row][column];
            if (!mineList.contains(tile)){
                mineList.add(tile);
                mineLeft--;
            }
        }
    }

    void revealMines(){
        for (int i = 0; i < mineList.size(); i++){
            MineTile tile = mineList.get(i);
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over!");
    }

    void checkMine(int row, int column){
        if (row < 0 || row >= numRows || column < 0 || column >= numColumns){
            return;
        }

        MineTile tile = board[row][column];
        if(!tile.isEnabled()){
            return;
        }
        tile.setEnabled(false);
        tilesClicked++;

        int minesFound = 0;

        //top 3
        minesFound += countMine(row - 1, column - 1); //top left
        minesFound += countMine(row - 1, column); //top
        minesFound += countMine(row - 1, column + 1); //top right

        //left and right
        minesFound += countMine(row, column - 1); //left
        minesFound += countMine(row, column + 1); //right

        //bottom 3
        minesFound += countMine(row + 1, column - 1); //bottom left
        minesFound += countMine(row + 1, column); //bottom
        minesFound += countMine(row + 1, column + 1); //bottom right

        if (minesFound > 0){
            tile.setText(Integer.toString(minesFound));
        }
        else{
            tile.setText("");

            //top 3
            checkMine(row - 1, column - 1); //top left
            checkMine(row - 1, column ); //top
            checkMine(row - 1, column + 1); //top right

            //left and right
            checkMine(row, column - 1); //left
            checkMine(row, column + 1); //right

            //bottom 3
            checkMine(row + 1, column - 1); //bottom left
            checkMine(row + 1, column ); //bottom
            checkMine(row + 1, column + 1); //bottom right
        }

        if (tilesClicked == numRows * numColumns - mineList.size()){
            gameOver = true;
            textLabel.setText("Mines Cleared!");
        }
    }

    int countMine(int row, int column){
        if (row < 0 || row >= numRows || column < 0 || column >= numColumns){
            return 0;
        }
        if (mineList.contains(board[row][column])){
            return 1;
        }
        return 0;
    }
}
