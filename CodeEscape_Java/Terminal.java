/**
 * Interactive coding terminal containing programming challenges.
 */
public class Terminal {
    private final int x;
    private final int y;
    private final int terminalId;
    private final int questionId;
    private final String label;
    private boolean solved;

    public Terminal(int x, int y, int terminalId, int questionId, String label) {
        this.x = x;
        this.y = y;
        this.terminalId = terminalId;
        this.questionId = questionId;
        this.label = label;
        this.solved = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getTerminalId() { return terminalId; }
    public int getQuestionId() { return questionId; }
    public String getLabel() { return label; }
    public boolean isSolved() { return solved; }
    public void setSolved(boolean solved) { this.solved = solved; }

    public void reset() {
        this.solved = false;
    }
}
