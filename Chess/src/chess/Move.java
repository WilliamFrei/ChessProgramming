package chess;

import java.util.List;
import static chess.Globals.*;

public class Move {

    public final int color;
    public final byte type; // Colorless type
    public final int sourceCol;
    public final int sourceRow;
    public final int destCol;
    public final int destRow;
    public final boolean isHit;
    public final byte newType; // type with color

    public Move(int color, byte type, int sourceCol, int sourceRow, int destCol, int destRow, boolean isHit, byte newType) {
        this.color = color;
        this.type = type;
        this.sourceCol = sourceCol;
        this.sourceRow = sourceRow;
        this.destCol = destCol;
        this.destRow = destRow;
        this.isHit = isHit;
        this.newType = newType;
    }

    public Move(int color, byte type, int sourceCol, int sourceRow, int destCol, int destRow, boolean isHit, int newType) {
        this(color, type, sourceCol, sourceRow, destCol, destRow, isHit, (byte) newType);
    }

    public Move(Board board, int color, int type, int sourceCol,
            int sourceRow, int destCol, int destRow) {
        this(color, (byte) (type & NO_COLOR_MASK), sourceCol, sourceRow, destCol, destRow, board.getFigures()[destCol][destRow] != EMPTY_FIELD, type);
    }

    public int getColor() {
        return color;
    }

    public int getType() {
        return type;
    }

    public int getSourceCol() {
        return sourceCol;
    }

    public int getSourceRow() {
        return sourceRow;
    }

    public int getDestCol() {
        return destCol;
    }

    public int getDestRow() {
        return destRow;
    }

    public void setColor(int color) {
        // This is done in the constructor
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit() {
        // This is done in the constructor
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(6);
        char c = (char) ((type & NO_COLOR_MASK) >>> 1);
        if (c != 'P') {
            sb.append(c);
        } else {
            sb.append(' ');
        }
        sb.append((char) ('a' + sourceCol)).append(1 + sourceRow);
        sb.append(isHit ? 'x' : '-');
        sb.append((char) ('a' + destCol)).append(1 + destRow);
        if (newType != type) {
            sb.append((char) ((newType & NO_COLOR_MASK) >>> 1));
        }
        return sb.toString();
    }

    public static Move importMove(String str, Board board, int color) {
        byte type;
        String nstr;
        if (str.charAt(0) >= 'A' && str.charAt(0) <= 'Z') {
            type = (byte) str.charAt(0);
            nstr = str.substring(1);
        } else {
            type = 'P';
            nstr = str;
        }
        int sourceCol = nstr.charAt(0) - 'a';
        int sourceRow = nstr.charAt(1) - '1';
        boolean isHit = nstr.charAt(2) == 'x';
        int destCol = nstr.charAt(3) - 'a';
        int destRow = nstr.charAt(4) - '1';
        int ntype = (nstr.length() > 5 ? (byte) nstr.charAt(5) : type) | color;
        return new Move(color, type, sourceCol, sourceRow, destCol, destRow, isHit, ntype);
    }

    public static Boolean movelistIncludesMove(List<Move> moves, Move move) {
        return moves.stream().anyMatch((m) -> (m.equals(move)));
    }
}
