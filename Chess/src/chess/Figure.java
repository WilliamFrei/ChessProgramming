package chess;

import java.util.List;
import static chess.Globals.*;
import java.util.LinkedList;

public class Figure {

    public static final String PAWN_STRING = "P";
    public static final String ROOK_STRING = "R";
    public static final String KNIGHT_STRING = "N";
    public static final String BISHOP_STRING = "B";
    public static final String QUEEN_STRING = "Q";
    public static final String KING_STRING = "K";

    public static int getType(int figureIndex) {
        return figureIndex & NO_COLOR_MASK;
    }

    public static int getColor(int figureIndex) {
        return figureIndex & BLACK;
    }

    public static String toString(int figureIndex) {
        return "" + (char) figureIndex;
    }

    public static String toString(int color, int type) {
        return (color == WHITE ? "w" : "b") + ((type & NO_COLOR_MASK)>>>1);
    }

    public static byte fromString(String str) {
        return (byte) str.charAt(0);
    }

    static public List<Move> getValidMoves(Board board, int col, int row) {
        // Implemented in Board.getValidMoves()
        List<Move> moves = new LinkedList<>();
        byte[][] figures = board.getFigures();
        boolean[] canColorCastleQueensside = board.getCanColorCastleQueensside();
        boolean[] canColorCastleKingsside = board.getCanColorCastleKingsside();
        List<Move> history = board.getHistory();
        byte newType = figures[col][row];
        int color = newType & BLACK;
        int otherColor = color ^ BLACK;
        if (newType != EMPTY_FIELD
                && (newType & BLACK) == color) {
            byte type = (byte) (newType & NO_COLOR_MASK);
            switch (type) {
                case PAWN: {
                    int newRow = row + COLOR_FORWARD[color];
                    if (newRow >= MIN_IDX && newRow <= MAX_IDX) {
                        if (!history.isEmpty()) {// En passant
                            Move lastMove = history.get(history.size() - 1);
                            if (lastMove.type == PAWN && Math.abs(lastMove.destRow - lastMove.sourceRow) == 2
                                    && row == COLOR_PAWN_ADVANCE_ROW[otherColor] && Math.abs(col - lastMove.destCol) == 1) {
                                moves.add(new Move(color, type, col, row, lastMove.destCol, newRow, true, newType));
                            }
                        }
                        for (byte b : PAWN_ATTACKING_MOVES) {
                            int newColumn = col + b;
                            if (newColumn < MIN_IDX || newColumn > MAX_IDX) {
                                continue;
                            }
                            if (figures[newColumn][newRow] != 0 && (figures[newColumn][newRow] & BLACK) != color) {
                                if (newRow == COLOR_HOME_ROW[color]) {
                                    moves.add(new Move(color, type, col, row, newColumn, newRow, true, QUEEN | color));
                                    moves.add(new Move(color, type, col, row, newColumn, newRow, true, ROOK | color));
                                    moves.add(new Move(color, type, col, row, newColumn, newRow, true, KNIGHT | color));
                                    moves.add(new Move(color, type, col, row, newColumn, newRow, true, BISHOP | color));
                                } else {
                                    moves.add(new Move(color, type, col, row, newColumn, newRow, true, newType));
                                }
                            }
                        }
                        if (figures[col][newRow] == EMPTY_FIELD) {
                            if (newRow == COLOR_HOME_ROW[otherColor]) {
                                moves.add(new Move(color, type, col, row, col, newRow, false, QUEEN | color));
                                moves.add(new Move(color, type, col, row, col, newRow, false, ROOK | color));
                                moves.add(new Move(color, type, col, row, col, newRow, false, KNIGHT | color));
                                moves.add(new Move(color, type, col, row, col, newRow, false, BISHOP | color));
                            } else {
                                moves.add(new Move(color, type, col, row, col, newRow, false, newType));
                                if (row == COLOR_PAWN_ROW[color] && figures[col][COLOR_PAWN_ADVANCE_ROW[color]] == EMPTY_FIELD) {
                                    moves.add(new Move(color, type, col, row, col, COLOR_PAWN_ADVANCE_ROW[color], false, newType));
                                }
                            }
                        }
                    }
                }
                break;
                case QUEEN:
                case ROOK:
                case BISHOP:
                    // Movement of the above units is actually equal, only directions differ
                    dirLoop:
                    for (byte[] moveDirs : getMoveDirections(type)) {
                        int curColumn = col + moveDirs[0];
                        int curRow = row + moveDirs[1];
                        while (curColumn >= MIN_IDX && curColumn <= MAX_IDX && curRow >= MIN_IDX && curRow <= MAX_IDX) {
                            if (figures[curColumn][curRow] == EMPTY_FIELD) {
                                moves.add(new Move(color, type, col, row, curColumn, curRow, false, newType));
                            } else {
                                if ((figures[curColumn][curRow] & BLACK) != color) {
                                    moves.add(new Move(color, type, col, row, curColumn, curRow, true, newType));
                                }
                                continue dirLoop;
                            }
                            curColumn = curColumn + moveDirs[0];
                            curRow = curRow + moveDirs[1];
                        }
                    }
                    break;
                case KNIGHT:
                case KING:
                    // Again, the movement is equal, only how far they go in a step differs
                    for (byte[] moveDirs : getMoveDirections(type)) {
                        int curColumn = col + moveDirs[0];
                        int curRow = row + moveDirs[1];
                        if (curColumn >= MIN_IDX && curColumn <= MAX_IDX && curRow >= MIN_IDX && curRow <= MAX_IDX) {
                            if (figures[curColumn][curRow] == EMPTY_FIELD) {
                                moves.add(new Move(color, type, col, row, curColumn, curRow, false, newType));
                            } else if ((figures[curColumn][curRow] & BLACK) != color) {
                                moves.add(new Move(color, type, col, row, curColumn, curRow, true, newType));
                            }
                        }
                    }
                    if ((canColorCastleQueensside[color] || canColorCastleKingsside[color]) && type == KING) {
                        if (canColorCastleQueensside[color]) {
                            // TODO: Missing:
                            // Should also check whether the king would move through a check because then castling is not possible
                            if (figures[KING_QUEENSSIDE_CASTLING][row] == EMPTY_FIELD
                                    && figures[ROOK_KINGSSIDE_CASTLING][row] == EMPTY_FIELD) {
                                moves.add(new Move(color, type, col, row, KING_QUEENSSIDE_CASTLING, row, false, newType));
                            }
                        }
                        if (canColorCastleKingsside[color]) {
                            // TODO: Missing:
                            // Should also check whether the king would move through a check because then castling is not possible
                            if (figures[KING_KINGSSIDE_CASTLING][row] == EMPTY_FIELD
                                    && figures[ROOK_KINGSSIDE_CASTLING][row] == EMPTY_FIELD) {
                                moves.add(new Move(color, type, col, row, KING_KINGSSIDE_CASTLING, row, false, newType));
                            }
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("illegalFigureException");
            }
        }
        return moves;
    }

    private static byte[][] getMoveDirections(byte type) {
        switch (type) {
            // We do not consider pawns here since the movement rules of pawns are vastly different anyways
            case QUEEN:
            case KING:
                return QUEEN_MOVE_DIRECTIONS; // Since Queen and King move in the same directions
            case ROOK:
                return ROOK_MOVE_DIRECTIONS;
            case BISHOP:
                return BISHOP_MOVE_DIRECTIONS;
            case KNIGHT:
                return KNIGHT_MOVES;
            default:
                throw new IllegalStateException("illegalFigureException");
        }
    }

    static private boolean isValidDestination(Board board, int color, int col, int row) {
        return (board.getFigures()[col][row] & BLACK) != color;
    }

    static private boolean isFree(Board board, int col, int row) {
        return board.getFigures()[col][row] == EMPTY_FIELD;
    }
}
