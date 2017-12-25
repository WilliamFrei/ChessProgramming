package chess;

import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import static chess.Globals.*;
import java.util.Iterator;

public class Board {

    private final byte[][] figures;
    private boolean[] canColorCastleQueensside;
    private boolean[] canColorCastleKingsside;
    private final LinkedList<Move> history;
    private int moveCount;
    private int colorToMove;

    public byte[][] getFigures() {
        byte[][] clone = new byte[figures.length][];
        for (int i = 0; i < figures.length; i++) {
            clone[i] = figures[i].clone();
        }
        return clone;
    }

    public byte getFigure(int row, int column) {
        return figures[column][row];
    }

    public void setFigure(int row, int column, byte figure) {
        figures[column][row] = figure;
    }

    public boolean[] getCanColorCastleQueensside() {
        return canColorCastleQueensside.clone();
    }

    public boolean[] getCanColorCastleKingsside() {
        return canColorCastleKingsside.clone();
    }

    public int getMoveCount() {
        return moveCount;
    }

    public int getColorToMove() {
        return colorToMove;
    }

    public Board() {
        this(new byte[8][8], new boolean[]{true, true}, new boolean[]{true, true}, 0, new LinkedList<>(), WHITE);
        reset();
    }

    public Board(byte[][] figures, boolean[] canColorCastleQueensside, boolean[] canColorCastleKingsside, int moveCount, LinkedList<Move> history, int colorToMove) {
        this.figures = figures;
        this.canColorCastleQueensside = canColorCastleQueensside;
        this.canColorCastleKingsside = canColorCastleKingsside;
        this.history = history;
        this.moveCount = moveCount;
        this.colorToMove = colorToMove;
    }

    public final void reset() {
        for (int i = 0; i <= MAX_IDX; i++) {
            for (int j = 2; j <= 5; j++) {
                figures[i][j] = EMPTY_FIELD;
            }
        }
        for (int color : COLORS) {
            figures[0][COLOR_HOME_ROW[color]] = (byte) (color | ROOK);
            figures[1][COLOR_HOME_ROW[color]] = (byte) (color | KNIGHT);
            figures[2][COLOR_HOME_ROW[color]] = (byte) (color | BISHOP);
            figures[3][COLOR_HOME_ROW[color]] = (byte) (color | QUEEN);
            figures[4][COLOR_HOME_ROW[color]] = (byte) (color | KING);
            figures[5][COLOR_HOME_ROW[color]] = (byte) (color | BISHOP);
            figures[6][COLOR_HOME_ROW[color]] = (byte) (color | KNIGHT);
            figures[7][COLOR_HOME_ROW[color]] = (byte) (color | ROOK);
            for (int i = 0; i < 8; i++) {
                figures[i][COLOR_PAWN_ROW[color]] = (byte) (color | PAWN);
            }
            canColorCastleQueensside[color] = true;
            canColorCastleKingsside[color] = true;
        }
        this.history.clear();
        this.moveCount = 0;
        this.colorToMove = WHITE;
    }

    public Board cloneIncompletely() {
        LinkedList<Move> lastMove = new LinkedList<>();
        if (!history.isEmpty()) {
            lastMove.add(history.getLast());
        }
        return new Board(getFigures(),
                canColorCastleQueensside.clone(),
                canColorCastleKingsside.clone(),
                moveCount, lastMove, colorToMove);
    }

    public Board cloneCompletely() {
        LinkedList<Move> copiedHistory = new LinkedList<>();
        Collections.copy(history, copiedHistory);
        return new Board(getFigures(),
                canColorCastleQueensside.clone(),
                canColorCastleKingsside.clone(),
                moveCount, copiedHistory, colorToMove);
    }

    public List<Move> getValidMoves() {
        return getValidMoves(colorToMove);
    }

    public List<Move> getValidMoves(int color, boolean checkPossible) {
        List<Move> moves = new LinkedList<>();
        int otherColor = color ^ BLACK;
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                byte oldType = figures[col][row];
                if (oldType != EMPTY_FIELD
                        && (oldType & BLACK) == color) {
                    byte colorlessType = (byte) (oldType & NO_COLOR_MASK);
                    switch (colorlessType) {
                        case PAWN: {
                            int newRow = row + COLOR_FORWARD[color];
                            if (newRow >= MIN_IDX && newRow <= MAX_IDX) {
                                if (!history.isEmpty()) {// En passant
                                    Move lastMove = history.getLast();
                                    if (lastMove.type == PAWN && Math.abs(lastMove.destRow - lastMove.sourceRow) == 2
                                            && row == COLOR_PAWN_ADVANCE_ROW[otherColor] && Math.abs(col - lastMove.destCol) == 1) {
                                        moves.add(new Move(color, oldType, col, row, lastMove.destCol, newRow, true, oldType));
                                    }
                                }
                                for (byte b : PAWN_ATTACKING_MOVES) {
                                    int newColumn = col + b;
                                    if (newColumn < MIN_IDX || newColumn > MAX_IDX) {
                                        continue;
                                    }
                                    if (figures[newColumn][newRow] != 0 && (figures[newColumn][newRow] & BLACK) != color) {
                                        if (newRow == COLOR_HOME_ROW[color]) {
                                            moves.add(new Move(color, oldType, col, row, newColumn, newRow, true, QUEEN | color));
                                            moves.add(new Move(color, oldType, col, row, newColumn, newRow, true, ROOK | color));
                                            moves.add(new Move(color, oldType, col, row, newColumn, newRow, true, KNIGHT | color));
                                            moves.add(new Move(color, oldType, col, row, newColumn, newRow, true, BISHOP | color));
                                        } else {
                                            moves.add(new Move(color, oldType, col, row, newColumn, newRow, true, oldType));
                                        }
                                    }
                                }
                                if (figures[col][newRow] == EMPTY_FIELD) {
                                    if (newRow == COLOR_HOME_ROW[otherColor]) {
                                        moves.add(new Move(color, oldType, col, row, col, newRow, false, QUEEN | color));
                                        moves.add(new Move(color, oldType, col, row, col, newRow, false, ROOK | color));
                                        moves.add(new Move(color, oldType, col, row, col, newRow, false, KNIGHT | color));
                                        moves.add(new Move(color, oldType, col, row, col, newRow, false, BISHOP | color));
                                    } else {
                                        moves.add(new Move(color, oldType, col, row, col, newRow, false, oldType));
                                        if (row == COLOR_PAWN_ROW[color] && figures[col][COLOR_PAWN_ADVANCE_ROW[color]] == EMPTY_FIELD) {
                                            moves.add(new Move(color, oldType, col, row, col, COLOR_PAWN_ADVANCE_ROW[color], false, oldType));
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
                            for (byte[] moveDirs : getMoveDirections(colorlessType)) {
                                int curColumn = col + moveDirs[0];
                                int curRow = row + moveDirs[1];
                                while (curColumn >= MIN_IDX && curColumn <= MAX_IDX && curRow >= MIN_IDX && curRow <= MAX_IDX) {
                                    if (figures[curColumn][curRow] == EMPTY_FIELD) {
                                        moves.add(new Move(color, oldType, col, row, curColumn, curRow, false, oldType));
                                    } else {
                                        if ((figures[curColumn][curRow] & BLACK) != color) {
                                            moves.add(new Move(color, oldType, col, row, curColumn, curRow, true, oldType));
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
                            for (byte[] moveDirs : getMoveDirections(colorlessType)) {
                                int curColumn = col + moveDirs[0];
                                int curRow = row + moveDirs[1];
                                if (curColumn >= MIN_IDX && curColumn <= MAX_IDX && curRow >= MIN_IDX && curRow <= MAX_IDX) {
                                    if (figures[curColumn][curRow] == EMPTY_FIELD) {
                                        moves.add(new Move(color, oldType, col, row, curColumn, curRow, false, oldType));
                                    } else if ((figures[curColumn][curRow] & BLACK) != color) {
                                        moves.add(new Move(color, oldType, col, row, curColumn, curRow, true, oldType));
                                    }
                                }
                            }
                            // Castling
                            if (checkPossible && (canColorCastleQueensside[color] || canColorCastleKingsside[color]) && colorlessType == KING && !isCheck(color)) {
                                List<Move> checkMoves = getValidMoves(otherColor, false);
                                check:
                                if (canColorCastleQueensside[color]) {
                                    if (figures[KING_QUEENSSIDE_CASTLING][row] == EMPTY_FIELD
                                            && figures[ROOK_KINGSSIDE_CASTLING][row] == EMPTY_FIELD) {
                                        for (Move oppMove : checkMoves) {
                                            if (oppMove.destCol == ROOK_QUEENSSIDE_CASTLING && oppMove.destRow == row) {
                                                break check;
                                            }
                                        }
                                        moves.add(new Move(color, oldType, col, row, KING_QUEENSSIDE_CASTLING, row, false, oldType));
                                    }
                                }
                                check:
                                if (canColorCastleKingsside[color]) {
                                    if (figures[KING_KINGSSIDE_CASTLING][row] == EMPTY_FIELD
                                            && figures[ROOK_KINGSSIDE_CASTLING][row] == EMPTY_FIELD) {
                                        for (Move oppMove : checkMoves) {
                                            if (oppMove.destCol == ROOK_KINGSSIDE_CASTLING && oppMove.destRow == row) {
                                                break check;
                                            }
                                        }
                                        moves.add(new Move(color, oldType, col, row, KING_KINGSSIDE_CASTLING, row, false, oldType));
                                    }
                                }
                            }
                            break;
                        default:
                            throw new IllegalStateException("illegalFigureException");
                    }
                }
            }
        }
        if (checkPossible) {
            Iterator<Move> it = moves.iterator();
            while (it.hasNext()) {
                Move move = it.next();
                Board clone = cloneIncompletely();
                clone.executeMove(move);
                if (clone.isCheck(color)) {
                    it.remove();
                }
            }
        }
        moves.sort((m1, m2) -> (m2.isHit ? 10_000 : 0) - (m1.isHit ? 10_000 : 0) +
                Double.compare(PIECE_VALUE[m2.newType & NO_COLOR_MASK], PIECE_VALUE[m1.newType & NO_COLOR_MASK]));
        return moves;
    }

    public List<Move> getValidMoves(int color) {
        return getValidMoves(color, true);
    }

    private byte[][] getMoveDirections(byte type) {
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

    public List<Move> getHistory() {
        return history;
    }

    /**
     * Fast internal method to execute moves, moves are not checked beforehand
     *
     * @param move the move to execute
     */
    public void executeMove(Move move) {
        if (move.isHit && figures[move.destCol][move.destRow] == EMPTY_FIELD) {
            // en passant
            figures[move.destCol][move.destRow - (COLOR_FORWARD[move.color])] = EMPTY_FIELD;
        }
        figures[move.sourceCol][move.sourceRow] = EMPTY_FIELD;
        figures[move.destCol][move.destRow] = move.newType;
        byte type = (byte) (move.type & NO_COLOR_MASK);
        if (type == KING) {
            if (Math.abs(move.destCol - move.sourceCol) > 1) {
                // castling
                if (move.destCol == KING_QUEENSSIDE_CASTLING) {
                    figures[ROOK_QUEENSSIDE_CASTLING][move.destRow] = figures[MIN_IDX][move.destRow];
                    figures[MIN_IDX][move.destRow] = EMPTY_FIELD;
                } else {
                    figures[ROOK_KINGSSIDE_CASTLING][move.destRow] = figures[MAX_IDX][move.destRow];
                    figures[MAX_IDX][move.destRow] = EMPTY_FIELD;
                }
            }
            // Moving the king takes away the possibility to castle
            canColorCastleKingsside[move.color] = false;
            canColorCastleQueensside[move.color] = false;
        }
        // If there is another move to or from an initial rook position, then castling is disabled there too
        if ((move.sourceRow % MAX_IDX == 0 && move.sourceCol % MAX_IDX == 0)
                || (move.destRow % MAX_IDX == 0 && move.destCol % MAX_IDX == 0)) {
            // check which rook position was affected
            // might be multiple since one rook can hit another
            if ((move.sourceRow == MIN_IDX && move.sourceCol == MIN_IDX)
                    || (move.destRow == MIN_IDX && move.destCol == MIN_IDX)) {
                canColorCastleQueensside[WHITE] = false;
            }
            if ((move.sourceRow == MAX_IDX && move.sourceCol == MIN_IDX)
                    || (move.destRow == MAX_IDX && move.destCol == MIN_IDX)) {
                canColorCastleQueensside[BLACK] = false;
            }
            if ((move.sourceRow == MIN_IDX && move.sourceCol == MAX_IDX)
                    || (move.destRow == MIN_IDX && move.destCol == MAX_IDX)) {
                canColorCastleKingsside[WHITE] = false;
            }
            if ((move.sourceRow == MAX_IDX && move.sourceCol == MAX_IDX)
                    || (move.destRow == MAX_IDX && move.destCol == MAX_IDX)) {
                canColorCastleKingsside[BLACK] = false;
            }
        }

        if (move.isHit || type == PAWN) {
            moveCount = 0;
        } else {
            moveCount++;
        }
        history.add(move);
        colorToMove ^= BLACK;
    }

    /*
    /**
     * Slow method to execute moves which includes a check whether the move is
     * legal, and only executes the move if it is
     *
     * @param move the move to check, and if it is legal, execute
     * @return true if the move was executed successfully, false if it was an
     * illegal move which was not executed
     */
 /*public boolean checkAndExecuteMove(Move move) {
        // Is the field already used by one of the figures of the player
        if ((figures[move.destCol][move.destRow] & BLACK) == move.color) {
            return false;
        }
        // Check whether source field contains the correct figure
        // Indirectly also checks whether the move does not actually move the figure together with the check above
        if ((figures[move.sourceCol][move.sourceRow] & NO_COLOR_MASK) != move.type) {
            return false;
        }
        // If newType is different from type and type is not Pawn
        if ((move.newType & NO_COLOR_MASK) != move.type && move.type != PAWN) {
            return false;
        }
        int cMove = move.destCol - move.sourceCol;
        int rMove = move.destRow - move.sourceRow;
        check:
        switch (move.type) {
            case PAWN:

                break;
            case QUEEN:
            case ROOK:
            case BISHOP:
            case KNIGHT:
            case KING:
        }
        if (true) {
            throw new IllegalStateException("todoexception");
        }
        executeMove(move);
        return true;
    }*/
    public Boolean canWhiteCastle() {
        return canColorCastleQueensside[WHITE] || canColorCastleKingsside[WHITE];
    }

    public Boolean canBlackCastle() {
        return canColorCastleQueensside[BLACK] || canColorCastleKingsside[BLACK];
    }

    public String getCastlingState() {
        StringBuilder sb = new StringBuilder();
        sb.append("castling: ")
                .append("wQ=").append(canColorCastleQueensside[WHITE])
                .append(" wK=").append(canColorCastleKingsside[WHITE])
                .append("\tbQ=").append(canColorCastleQueensside[BLACK])
                .append(" bK=").append(canColorCastleKingsside[BLACK]);
        return sb.toString();
    }

    public boolean isGameover() {
        return isDraw(colorToMove) || isMat(colorToMove);
    }

    public boolean isCheck(int color) {
        return getValidMoves(color ^ BLACK, false).stream()
                .anyMatch((validMove) -> (validMove.isHit() && figures[validMove.destCol][validMove.destRow] == (KING | color)));
    }

    public boolean isDraw(int color) {
        return moveCount >= 100 || (!isCheck(color) && getValidMoves(color).isEmpty());
    }

    public boolean isMat(int color) {
        if (!isCheck(color)) {
            return false;
        }
        for (Move move : getValidMoves(color)) {
            Board clone = cloneIncompletely();
            clone.executeMove(move);
            if (!clone.isCheck(color)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        LinkedList<StringBuilder> als = new LinkedList<>();
        als.add(new StringBuilder("     a   b   c   d   e   f   g   h").append(System.getProperty("line.separator")));
        als.add(new StringBuilder("  ##################################").append(System.getProperty("line.separator")));
        for (int i = 0; i < 8; i++) {
            if (i > 0) {
                als.add(new StringBuilder("  #                                #").append(System.getProperty("line.separator")));
            }
            StringBuilder sb = new StringBuilder();
            sb.append(i + 1).append(" #");
            for (int j = 0; j < 8; j++) {
                sb.append(' ');
                byte fig = figures[j][i];
                if (fig == EMPTY_FIELD) {
                    sb.append(EMPTY);
                } else {
                    sb.append((fig & BLACK) == WHITE ? WHITE_STRING : BLACK_STRING);
                    sb.append((char) ((fig & NO_COLOR_MASK) >>> 1));
                }
                sb.append(' ');
            }
            sb.append('#').append(System.getProperty("line.separator"));
            als.add(sb);
        }
        als.add(new StringBuilder("  ##################################").append(System.getProperty("line.separator")));
        StringBuilder complete = new StringBuilder();
        while (!als.isEmpty()) {
            complete.append(als.removeLast());
        }
        complete.append("\t").append(colorToMove == WHITE ? "White" : "Black").append(" to move").append(System.getProperty("line.separator"));
        return complete.toString();
    }

    public static int flipColor(int color) {
        return color ^ BLACK;
    }

    public static String colorToString(int color) {
        return color == WHITE ? WHITE_STRING : BLACK_STRING;
    }

    public static String columnName(int index) {
        return new Character((char) (index + 'a')).toString();
    }
}
