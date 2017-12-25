package agents;

import static chess.Globals.*;
import chess.Board;
import chess.Move;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

public class MyPlayer implements Player {

    @Override
    public Move chooseMove(Board b, int color, int milliseconds, Random random) {
        Thinker thinker = new Thinker(b, color, random);
        Thread t = new Thread(thinker);
        t.start();
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
        }
        t.stop();
        return thinker.optimalMove;
    }

    private static class Thinker implements Runnable {

        private final Board board;
        private final int color;
        private final Random random;

        private Move optimalMove = null;

        private Thinker(Board board, int color, Random random) {
            this.board = board;
            this.color = color;
            this.random = random;
        }

        public Move getMove() {
            return optimalMove;
        }

        @Override
        public void run() {
            int level = 1;
            LinkedList<ValuedMove> possibleMoves = new LinkedList<>();
            possibleMoves.addAll(board.getValidMoves(color).stream().map(move -> new ValuedMove(0, move)).collect(Collectors.toList()));
            LinkedList<ValuedMove> executedMoves = new LinkedList<>();
            for (;;) {
                while (!possibleMoves.isEmpty()) {
                    Move move = possibleMoves.remove().getMove();
                    Board clone = board.cloneIncompletely();
                    clone.executeMove(move);
                    double v = evaluate(clone, level - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, color);
                    executedMoves.add(new ValuedMove(v, move));
                }
                Collections.sort(executedMoves);
                int maxCount = 0;
                double maxVal = executedMoves.getFirst().value;
                for (ValuedMove valuedMove : executedMoves) {
                    if (Math.abs(maxVal - valuedMove.value) < 1e-15) {
                        maxCount++;
                    } else {
                        break;
                    }
                }
                optimalMove = maxCount == 0 ? executedMoves.getFirst().move : executedMoves.get(random.nextInt(maxCount)).move;
                possibleMoves.addAll(executedMoves);
                executedMoves.clear();
                level += 2;
            }
        }

        public double getFitness(Board b, int color) {
            double fitness = 0;
            byte[][] figures = b.getFigures();
            for (byte[] row : figures) {
                for (int j = 0; j < row.length; j++) {
                    byte fig = (byte) (row[j] & NO_COLOR_MASK);
                    if (fig == EMPTY_FIELD) {
                        continue;
                    }
                    double value = PIECE_VALUE[fig & NO_COLOR_MASK];
                    switch (fig) {
                        case PAWN:
                            value = 1;
                            break;
                        case ROOK:
                            value = 5;
                            break;
                        case BISHOP:
                        case KNIGHT:
                            value = 3.3;
                            break;
                        case QUEEN:
                            value = 9;
                            break;
                        case KING:
                            value = 1000_000;
                    }
                    fitness += (color == (row[j] & BLACK) ? value : -value);
                }
            }
            return fitness;
        }

        private double evaluate(Board board, int level, double alpha, double beta, boolean max, int color) {
            if (board.isGameover()) {
                if (board.isMat(color)) {
                    return Double.NEGATIVE_INFINITY;
                } else if (board.isMat(color ^ BLACK)) {
                    return Double.POSITIVE_INFINITY;
                } else {
                    return 0;
                }
            }
            if (level == 0) {
                return getFitness(board, color);
            }
            List<Move> moves = board.getValidMoves();
            if (max) {
                double v = Double.NEGATIVE_INFINITY;
                for (Move move : moves) {
                    Board clone = board.cloneIncompletely();
                    clone.executeMove(move);
                    v = Double.max(v, evaluate(clone, level - 1, alpha, beta, false, color));
                    alpha = Double.max(alpha, v);
                    if (beta <= alpha) {
                        break;
                    }
                }
                return v;
            } else {
                double v = Double.POSITIVE_INFINITY;
                for (Move move : moves) {
                    Board clone = board.cloneIncompletely();
                    clone.executeMove(move);
                    v = Double.min(v, evaluate(clone, level - 1, alpha, beta, true, color));
                    beta = Double.min(beta, v);
                    if (beta <= alpha) {
                        break;
                    }
                }
                return v;
            }
        }

        private static class ValuedMove implements Comparable<ValuedMove> {

            private final double value;
            private final Move move;

            public ValuedMove(double value, Move move) {
                this.value = value;
                this.move = move;
            }

            public double getValue() {
                return value;
            }

            public Move getMove() {
                return move;
            }

            @Override
            public int compareTo(ValuedMove o) {
                return Double.compare(o.value, value);
            }

        }
    }

}
