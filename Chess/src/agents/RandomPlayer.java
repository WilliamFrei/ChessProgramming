package agents;

import chess.Board;
import chess.Move;
import java.util.List;
import java.util.Random;

public class RandomPlayer implements Player {

    @Override
    public Move chooseMove(Board b, int color, int milliseconds, Random random) {
        List<Move> moves = b.getValidMoves();
        return moves.get(random.nextInt(moves.size()));
    }

}
