import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Checkers extends JFrame {
    public static void main (String [] args) {
        JFrame game = new JFrame();

        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.getContentPane();
        game.pack();
        game.setSize(336,454);
        game.setResizable(false);
        game.setLayout(null);
        game.setVisible(true);
        game.setBackground(new Color(225, 225, 225));

        Board board = new Board();
        game.add(board);
        game.add(board.title);
        game.add(board.newGame);
        game.add(board.howToPlay);
        game.add(board.credits);
        game.add(board.message);

        board.setBounds(0,80,324,324);
        board.title.setBounds(0,0,324,50);
        board.newGame.setBounds(6, 50, 100, 30);
        board.howToPlay.setBounds(112, 50, 100, 30);
        board.credits.setBounds(218, 50, 100, 30);
        board.message.setBounds(0, 404, 324, 30);

    }

}




class Board extends JPanel implements ActionListener, MouseListener {

    Data board;
    boolean gameInProgress;
    int currentPlayer;
    int selectedRow, selectedCol;
    movesMade[] legalMoves;
    JLabel title;
    JButton newGame;
    JButton howToPlay;
    JButton credits;
    JLabel message;
    String Player1;
    String Player2;

    public Board() {
        addMouseListener(this);

        title = new JLabel("Checkers!");
        title.setFont(new Font("Serif", Font.CENTER_BASELINE, 40));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(Color.darkGray);
        howToPlay = new JButton("Checkers?");
        howToPlay.addActionListener(this);
        newGame = new JButton("New Game");
        newGame.addActionListener(this);
        credits = new JButton("Credits");
        credits.addActionListener(this);
        message = new JLabel("",JLabel.CENTER);
        message.setFont(new  Font("Serif", Font.BOLD, 14));
        message.setHorizontalAlignment(SwingConstants.CENTER);
        message.setForeground(Color.darkGray);

        board = new Data();
        getPlayersNames();
        NewGame();

    }

    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        if (src == newGame)
            NewGame();
        else if (src == howToPlay)
            instructions();
        else if (src == credits)
            showCredits();
    }

    void NewGame() {
        board.setUpBoard();
        currentPlayer = Data.player1;
        legalMoves = board.getLegalMoves(Data.player1);
        selectedRow = -1;
        message.setText("It's " + Player1 + "'s turn.");
        gameInProgress = true;
        newGame.setEnabled(true);
        howToPlay.setEnabled(true);
        credits.setEnabled(true);
        repaint();

    }

    public void getPlayersNames(){
        JTextField player1Name = new JTextField("Player 1");
        JTextField player2Name = new JTextField("Player 2");

        JPanel getNames = new JPanel();
        getNames.setLayout(new BoxLayout(getNames, BoxLayout.PAGE_AXIS));
        getNames.add(player1Name);
        getNames.add(player2Name);

        int result = JOptionPane.showConfirmDialog(null, getNames, "Enter Your Names!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Player1 = player1Name.getText();
            Player2 = player2Name.getText();
        } else {
            Player1 = "Player 1";
            Player2 = "Player 2";
        }

    }

    void instructions() {
        String intro = "Checkers, called Draughts in most countries,\n" +
                "has been traced back to the 1300s, though it\n" +
                "may indeed stretch further into history than that.\n" +
                "These are the standard U.S. rules for Checkers.\n\n"+
                "Read how to play: http://abt.cm/1d0fHKE";

        JOptionPane.showMessageDialog(null, intro, "What is Checkers", JOptionPane.PLAIN_MESSAGE);

    }

    void showCredits() {
        String credits = "Checkers\n" + "By Andrii Voinarovskyi\n" + "12.01.2020";
        JOptionPane.showMessageDialog(null, credits, "Credits", JOptionPane.PLAIN_MESSAGE);

    }

    void gameOver(String str) {
        message.setText(str);
        newGame.setEnabled(true);
        howToPlay.setEnabled(true);
        credits.setEnabled(true);
        gameInProgress = false;

    }

    public void mousePressed(MouseEvent evt) {
        if (!gameInProgress){
            message.setText("Start a new game.");
        }else {
            int col = (evt.getX() - 2) / 40;
            int row = (evt.getY() - 2) / 40;
            if (col >= 0 && col < 8 && row >= 0 && row < 8)
                ClickedSquare(row,col);
        }
    }

    void ClickedSquare(int row, int col) {
        for (int i = 0; i < legalMoves.length; i++){
            if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
                selectedRow = row;
                selectedCol = col;
                if (currentPlayer == Data.player1)
                    message.setText("It's " + Player1 + "'s turn.");
                else
                    message.setText("It's " + Player2 + "'s turn.");
                repaint();
                return;
            }
        }

        if (selectedRow < 0) {
            message.setText("Select a piece to move.");
            return;
        }

        for (int i = 0; i < legalMoves.length; i++){
            if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
                    && legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
                MakeMove(legalMoves[i]);
                return;
            }
        }

        message.setText("Where do you want to move it?");
    }
    void MakeMove(movesMade move) {
        board.makeMove(move);

        if (move.isJump()) {
            legalMoves = board.getLegalJumpsFrom(currentPlayer, move.toRow, move.toCol);
            if (legalMoves != null) {
                if (currentPlayer == Data.player1)
                    message.setText(Player1 + ", you must jump.");
                else
                    message.setText(Player2 + ", you must jump.");
                selectedRow = move.toRow;
                selectedCol = move.toCol;
                repaint();
                return;
            }
        }

        if (currentPlayer == Data.player1) {
            currentPlayer = Data.player2;
            legalMoves = board.getLegalMoves(currentPlayer);
            if (legalMoves == null)
                gameOver(Player1 + " wins!");
            else if (legalMoves[0].isJump())
                message.setText(Player2 + ", you must jump.");
            else
                message.setText("It's " + Player2 + "'s turn.");
        } else {
            currentPlayer = Data.player1;
            legalMoves = board.getLegalMoves(currentPlayer);
            if (legalMoves == null)
                gameOver(Player2 + " wins!");
            else if (legalMoves[0].isJump())
                message.setText(Player1 + ", you must jump.");
            else
                message.setText("It's " + Player1 + "'s turn.");
        }

        selectedRow = -1;

        if (legalMoves != null) {
            boolean sameFromSquare = true;
            for (int i = 1; i < legalMoves.length; i++)
                if (legalMoves[i].fromRow != legalMoves[0].fromRow
                        || legalMoves[i].fromCol != legalMoves[0].fromCol) {
                    sameFromSquare = false;
                    break;
                }
            if (sameFromSquare) {
                selectedRow = legalMoves[0].fromRow;
                selectedCol = legalMoves[0].fromCol;
            }
        }

        repaint();

    }

    public void paintComponent(Graphics g) {

        g.setColor(new Color(139,119,101));
        g.fillRect(0, 0, 324, 324);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                if ( row % 2 == col % 2 )
                    g.setColor(new Color(139,119,101));
                else
                    g.setColor(new Color(238,203,173));
                g.fillRect(2 + col*40, 2 + row*40, 40, 40);

                switch (board.pieceAt(row,col)) {
                    case Data.player1:
                        g.setColor(Color.lightGray);
                        g.fillOval(4 + col*40, 4 + row*40, 36, 36);
                        break;
                    case Data.player2:
                        g.setColor(Color.darkGray);
                        g.fillOval(4 + col*40, 4 + row*40, 36, 36);
                        break;
                    case Data.playerKing1:
                        g.setColor(Color.lightGray);
                        g.fillOval(4 + col*40, 4 + row*40, 36, 36);
                        g.setColor(Color.white);
                        g.drawString("K", 27 + col*40, 36 + row*40);
                        break;
                    case Data.playerKing2:
                        g.setColor(Color.darkGray);
                        g.fillOval(4 + col*40, 4 + row*40, 36, 36);
                        g.setColor(Color.white);
                        g.drawString("K", 27 + col*40, 36 + row*40);
                        break;
                }
            }
        }

        if (gameInProgress) {

            g.setColor(new Color(0, 255,0));
            for (int i = 0; i < legalMoves.length; i++) {

                g.drawRect(2 + legalMoves[i].fromCol*40, 2 + legalMoves[i].fromRow*40, 39, 39);
            }
            if (selectedRow >= 0){
                g.setColor(Color.white);
                g.drawRect(2 + selectedCol*40, 2 + selectedRow*40, 39, 39);
                g.drawRect(3 + selectedCol*40, 3 + selectedRow*40, 37, 37);
                g.setColor(Color.green);
                for (int i = 0; i < legalMoves.length; i++) {
                    if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow)
                        g.drawRect(2 + legalMoves[i].toCol*40, 2 + legalMoves[i].toRow*40, 39, 39);
                }
            }
        }
    }
    public void mouseEntered(MouseEvent evt) { }
    public void mouseClicked(MouseEvent evt) { }
    public void mouseReleased(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }

}

class movesMade {
    int fromRow, fromCol;
    int toRow, toCol;

    movesMade(int r1, int c1, int r2, int c2) {
        fromRow = r1;
        fromCol = c1;
        toRow = r2;
        toCol = c2;

    }

    boolean isJump() {
        return (fromRow - toRow == 2 || fromRow - toRow == -2);

    }

}


class Data {
    public static final int
            blank = 0,
            player1 = 1,
            playerKing1 = 2,
            player2 = 3,
            playerKing2 = 4;
    private int[][] board;
    public Data() {
        board = new int[8][8];
        setUpBoard();

    }

    public void setUpBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ( row % 2 == col % 2 ) {
                    if (row < 3)
                        board[row][col] = player2;
                    else if (row > 4)
                        board[row][col] = player1;
                    else
                        board[row][col] = blank;
                } else
                    board[row][col] = blank;
            }

        }

    }

    public int pieceAt(int row, int col) {
        return board[row][col];

    }

    public void makeMove(movesMade move) {
        makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);

    }

    public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = blank;

        if (fromRow - toRow == 2 || fromRow - toRow == -2){

            int jumpRow = (fromRow + toRow) / 2;
            int jumpCol = (fromCol + toCol) / 2;
            board[jumpRow][jumpCol] = blank;

        }

        if (toRow == 0 && board[toRow][toCol] == player1){
            board[toRow][toCol] = playerKing1;
        }

        if (toRow == 7 && board[toRow][toCol] == player2){
            board[toRow][toCol] = playerKing2;
        }
    }

    public movesMade[] getLegalMoves(int player) {

        if (player != player1 && player != player2)
            return null;

        int playerKing;

        if (player == player1){
            playerKing = playerKing1;
        } else {
            playerKing = playerKing2;
        }

        ArrayList moves = new ArrayList();

        for (int row = 0; row < 8; row++){

            for (int col = 0; col < 8; col++){

                if (board[row][col] == player || board[row][col] == playerKing){
                    if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                        moves.add(new movesMade(row, col, row+2, col+2));
                    if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                        moves.add(new movesMade(row, col, row-2, col+2));
                    if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                        moves.add(new movesMade(row, col, row+2, col-2));
                    if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                        moves.add(new movesMade(row, col, row-2, col-2));

                }

            }

        }

        if (moves.size() == 0){
            for (int row = 0; row < 8; row++){
                for (int col = 0; col < 8; col++){
                    if (board[row][col] == player || board[row][col] == playerKing){

                        if (canMove(player,row,col,row+1,col+1))
                            moves.add(new movesMade(row,col,row+1,col+1));
                        if (canMove(player,row,col,row-1,col+1))
                            moves.add(new movesMade(row,col,row-1,col+1));
                        if (canMove(player,row,col,row+1,col-1))
                            moves.add(new movesMade(row,col,row+1,col-1));
                        if (canMove(player,row,col,row-1,col-1))
                            moves.add(new movesMade(row,col,row-1,col-1));

                    }

                }

            }

        }

        if (moves.size() == 0){
            return null;
        }else {
            movesMade[] moveArray = new movesMade[moves.size()];
            for (int i = 0; i < moves.size(); i++){
                moveArray[i] = (movesMade)moves.get(i);
            }
            return moveArray;
        }

    }

    public movesMade[] getLegalJumpsFrom(int player, int row, int col){
        if (player != player1 && player != player2)
            return null;

        int playerKing;

        if (player == player1){
            playerKing = playerKing1;
        }else {
            playerKing = playerKing2;
        }

        ArrayList moves = new ArrayList();

        if (board[row][col] == player || board[row][col] == playerKing){
            if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                moves.add(new movesMade(row, col, row+2, col+2));
            if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                moves.add(new movesMade(row, col, row-2, col+2));
            if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                moves.add(new movesMade(row, col, row+2, col-2));
            if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                moves.add(new movesMade(row, col, row-2, col-2));
        }

        if (moves.size() == 0){
            return null;
        }else {
            movesMade[] moveArray = new movesMade[moves.size()];
            for (int i = 0; i < moves.size(); i++){
                moveArray[i] = (movesMade)moves.get(i);
            }
            return moveArray;
        }
    }

    private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3){

        if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
            return false;

        if (board[r3][c3] != blank)
            return false;

        if (player == player1) {
            if (board[r1][c1] == player1 && r3 > r1)
                return false;
            if (board[r2][c2] != player2 && board[r2][c2] != playerKing2)
                return false;
            return true;
        }else {
            if (board[r1][c1] == player2 && r3 < r1)
                return false;
            if (board[r2][c2] != player1 && board[r2][c2] != playerKing1)
                return false;
            return true;
        }
    }

    private boolean canMove(int player, int r1, int c1, int r2, int c2){
        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
            return false;

        if (board[r2][c2] != blank)
            return false;

        if (player == player1) {
            if (board[r1][c1] == player1 && r2 > r1)
                return false;
            return true;
        }else {
            if (board[r1][c1] == player2 && r2 < r1)
                return false;
            return true;
        }
    }
}
