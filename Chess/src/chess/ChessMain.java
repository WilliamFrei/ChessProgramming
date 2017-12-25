package chess;

import agents.*;
import java.util.Date;
import java.util.Random;

public class ChessMain {

    private static final Random RAND = new Random(new Date().getTime());

    public static void main(String[] args) {
        Board board = new Board();

        Player whitePlayer = new HumanPlayer();
        Player blackPlayer = new MyPlayer();
        //Player whitePlayer = new MyPlayer();

        boolean whiteMat = false;
        boolean blackMat = false;
        boolean draw = false;
        int round = 1;
        int MAX_ROUNDS = 200;
        int MAX_TIME = 1200;
        int color = 0;
        System.out.println(board.toString());
        while (!board.isGameover()) {// && round < MAX_ROUNDS) {
            Move move = (color == 0 ? whitePlayer : blackPlayer).chooseMove(board, color, MAX_TIME, RAND);
            System.out.println("Chosen move:\t" + move);
            board.executeMove(move);
            color = (color + 1) % 2;
            System.out.println(board.toString());
            round++;
            draw = board.isDraw(color);
            if (color == 0) {
                whiteMat = board.isMat(color);
            } else {
                blackMat = board.isMat(color);
            }
        }
        if (whiteMat) {
            System.out.println("Result: BLACK wins");
        }
        if (blackMat) {
            System.out.println("Result: WHITE wins");
        }
        if (draw || round == MAX_ROUNDS) {
            System.out.println("Result: REMIS");
        }
        System.out.println(board);
    }

}
