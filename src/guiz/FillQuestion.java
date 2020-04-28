package guiz;

public  class FillQuestion extends Question {


    public FillQuestion (String category, String difficulty, String type, String question, String correctAnswer)
    {
        super(category, difficulty, type, question, correctAnswer);
    }

    public String toString()
    {
        return super.toString();

    }

    public boolean equals(Question question)
    {
        if (question instanceof MultiQuestion || question instanceof OpenQuestion)
        {
            return false;
        }
        boolean category = this.category.equalsIgnoreCase(question.getCategory());
        boolean difficulty = this.difficulty.equalsIgnoreCase(question.getDifficulty());
        boolean type = this.type.equalsIgnoreCase(question.getType());
        boolean quest = this.question.equalsIgnoreCase(question.getQuestion());
        boolean correctAnswer = this.correctAnswer.equalsIgnoreCase(question.getCorrectAnswer());
        if (category && difficulty && type && quest && correctAnswer)
        {
            return true;
        }

        return  false;
    }
}


