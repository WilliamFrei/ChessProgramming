package agents;

import chess.Board;
import chess.Move;

public interface Player {

    public Move chooseMove(Board b, int color, int milliseconds, java.util.Random random);

    // I moved the "getFitness" out of the interface since only one of the players ("MyPlayer") uses it while the other two do not
}
