package agents;

import chess.Board;
import chess.Move;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

public class HumanPlayer implements Player {

    private final BufferedReader br;

    public HumanPlayer() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public Move chooseMove(Board b, int color, int milliseconds, Random random) {
        List<Move> moves = b.getValidMoves(color);
        int size = moves.size();
        System.out.println(b);
        int chosen = -1;
        do {
            int idx = 0;
            
            for (Move move : moves) {
                System.out.println("#" + idx++ + "\t" + move);
            }
            System.out.println("Enter any number between 0 and " + (idx - 1) + " to choose that move");
            try {
                String line = br.readLine();
                chosen = Integer.parseInt(line, 10);
                if (chosen < 0 || chosen >= size) {
                    System.out.println("Number outside of range");
                }
            } catch (IOException ex) {
            } catch (NumberFormatException ex) {
                System.out.println("Not a valid number");
            }
        } while (chosen < 0 || chosen >= size);
        return moves.get(chosen);
    }
}
