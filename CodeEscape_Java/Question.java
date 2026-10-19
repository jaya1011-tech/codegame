/**
 * Question model representing a programming or logic challenge.
 */
public class Question {
    private final int id;
    private final String category;
    private final int difficulty; // 1 to 5
    private final String prompt;
    private final String codeSnippet; // null or formatted code
    private final String[] options;
    private final int correctIndex;
    private final String explanation;
    private final String hint;

    public Question(int id, String category, int difficulty, String prompt,
                    String codeSnippet, String[] options, int correctIndex,
                    String explanation, String hint) {
        if (options == null || options.length < 2) {
            throw new IllegalArgumentException("Question must have at least 2 options");
        }
        if (correctIndex < 0 || correctIndex >= options.length) {
            throw new IllegalArgumentException("Invalid correctIndex: " + correctIndex);
        }
        this.id = id;
        this.category = category;
        this.difficulty = difficulty;
        this.prompt = prompt;
        this.codeSnippet = codeSnippet;
        this.options = options;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
        this.hint = hint;
    }

    public int getId() { return id; }
    public String getCategory() { return category; }
    public int getDifficulty() { return difficulty; }
    public String getPrompt() { return prompt; }
    public String getCodeSnippet() { return codeSnippet; }
    public String[] getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
    public String getExplanation() { return explanation; }
    public String getHint() { return hint; }

    public boolean isCorrect(int choice) {
        return choice == correctIndex;
    }
}
