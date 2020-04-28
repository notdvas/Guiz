package guiz;

public  class OpenQuestion extends Question {


    public OpenQuestion (String category, String difficulty, String type, String question, String correctAnswer)
    {
        super(category, difficulty, type, question, correctAnswer);
    }

    public String toString() {
        return super.toString();

    }

    public boolean equals(Question question)
    {
        if (question instanceof MultiQuestion || question instanceof FillQuestion)
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


