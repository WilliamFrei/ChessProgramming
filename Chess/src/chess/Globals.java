package chess;

public class Globals {

    public static final int[] COLORS = {0, 1};

    public static final int WHITE = 0;
    public static final int BLACK = 1;

    public static final int NO_COLOR_MASK = (~BLACK) & 255;

    public static final byte EMPTY_FIELD = 0;
    public static final byte PAWN = (byte) ('P' << 1);
    public static final byte BISHOP = (byte) ('B' << 1);
    public static final byte KNIGHT = (byte) ('N' << 1);
    public static final byte ROOK = (byte) ('R' << 1);
    public static final byte QUEEN = (byte) ('Q' << 1);
    public static final byte KING = (byte) ('K' << 1);

    public static final double[] PIECE_VALUE = new double[255];

    static {
        PIECE_VALUE[EMPTY_FIELD] = 0;
        PIECE_VALUE[PAWN & NO_COLOR_MASK] = 1.0;
        PIECE_VALUE[BISHOP & NO_COLOR_MASK] = 3.3;
        PIECE_VALUE[KNIGHT & NO_COLOR_MASK] = 3.3;
        PIECE_VALUE[ROOK & NO_COLOR_MASK] = 5.0;
        PIECE_VALUE[QUEEN & NO_COLOR_MASK] = 9.0;
        PIECE_VALUE[KING & NO_COLOR_MASK] = 1_000.0;
    }

    public static final String EMPTY = "  ";
    public static final String WHITE_STRING = "w";
    public static final String BLACK_STRING = "b";

    public static final byte[] COLOR_FORWARD = {1, -1};
    public static final byte[] PAWN_ATTACKING_MOVES = {-1, 1};
    public static final byte[][] QUEEN_MOVE_DIRECTIONS = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    public static final byte[][] ROOK_MOVE_DIRECTIONS = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
    public static final byte[][] BISHOP_MOVE_DIRECTIONS = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    public static final byte[][] KNIGHT_MOVES = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};

    public static final int MIN_IDX = 0;
    public static final int MAX_IDX = 7;

    public static final int[] COLOR_HOME_ROW = {0, 7};
    public static final int[] COLOR_PAWN_ROW = {1, 6};
    public static final int[] COLOR_PAWN_ADVANCE_ROW = {3, 4};

    public static final int KING_POSITION = 4;

    public static final int KING_QUEENSSIDE_CASTLING = 2;
    public static final int ROOK_QUEENSSIDE_CASTLING = 3;
    public static final int KING_KINGSSIDE_CASTLING = 6;
    public static final int ROOK_KINGSSIDE_CASTLING = 5;
}
