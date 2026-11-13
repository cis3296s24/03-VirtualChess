package com.cis3296.virtualchess.engine;

/**
 * How a finished game ended.
 *
 * @param winner the side that won, or null for a draw
 * @param reason why the game ended
 */
public record GameResult(Color winner, Reason reason) {

    /**
     * Why a game ended. Each reason is either always a win or always a draw.
     */
    public enum Reason {
        CHECKMATE(true, "checkmate"),
        RESIGNATION(true, "resignation"),
        TIMEOUT(true, "timeout"),
        STALEMATE(false, "stalemate"),
        INSUFFICIENT_MATERIAL(false, "insufficient material"),
        FIFTY_MOVE_RULE(false, "the fifty-move rule"),
        THREEFOLD_REPETITION(false, "threefold repetition"),
        TIMEOUT_VS_INSUFFICIENT_MATERIAL(false, "timeout vs insufficient material"),
        AGREEMENT(false, "agreement");

        private final boolean decisive;
        private final String description;

        Reason(boolean decisive, String description) {
            this.decisive = decisive;
            this.description = description;
        }

        /**
         * @return true if this reason produces a winner, false if it's a draw
         */
        public boolean isDecisive() {
            return decisive;
        }

        /**
         * @return a lowercase phrase for messages, e.g. "threefold repetition"
         */
        public String description() {
            return description;
        }
    }

    public GameResult {
        if (reason == null) {
            throw new IllegalArgumentException("A result needs a reason");
        }
        if (reason.isDecisive() != (winner != null)) {
            throw new IllegalArgumentException(reason + (reason.isDecisive() ? " needs a winner" : " is a draw"));
        }
    }

    public static GameResult win(Color winner, Reason reason) {
        return new GameResult(winner, reason);
    }

    public static GameResult draw(Reason reason) {
        return new GameResult(null, reason);
    }

    public boolean isDraw() {
        return winner == null;
    }

    /**
     * @return the result in PGN notation: "1-0", "0-1", or "1/2-1/2"
     */
    public String score() {
        if (winner == null) {
            return "1/2-1/2";
        }
        return winner == Color.WHITE ? "1-0" : "0-1";
    }

    /**
     * @return a sentence for showing to players, e.g. "White wins by checkmate" or "Draw by stalemate"
     */
    public String describe() {
        if (winner == null) {
            return "Draw by " + reason.description();
        }
        String side = winner == Color.WHITE ? "White" : "Black";
        return side + " wins by " + reason.description();
    }
}
