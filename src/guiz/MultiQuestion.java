package guiz;

public class MultiQuestion extends Question {
    private String wrongAnswer1, wrongAnswer2, wrongAnswer3;

    public MultiQuestion(String category, String difficulty, String type, String question, String correctAnswer, String wrongAnswer1, String wrongAnswer2, String wrongAnswer3)
    {
        super(category, difficulty, type, question, correctAnswer);
        this.wrongAnswer1 = wrongAnswer1;
        this.wrongAnswer2 = wrongAnswer2;
        this.wrongAnswer3 = wrongAnswer3;
    }
    public String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public String getWrongAnswer2() {
        return wrongAnswer2;
    }
    public String getWrongAnswer3() {
        return wrongAnswer3;
    }

    public String toString() {

        return super.toString()+"~"+wrongAnswer1+"~"+wrongAnswer2+"~"+wrongAnswer3;
    }

    public boolean equals(Question question)
    {
        if (question instanceof OpenQuestion || question instanceof FillQuestion)
        {
            return false;
        }
        boolean category = this.category.equalsIgnoreCase(question.getCategory());
        boolean difficulty = this.difficulty.equalsIgnoreCase(question.getDifficulty());
        boolean type = this.type.equalsIgnoreCase(question.getType());
        boolean quest = this.question.equalsIgnoreCase(question.getQuestion());
        boolean correctAnswer = this.correctAnswer.equalsIgnoreCase(question.getCorrectAnswer());
        MultiQuestion multi = (MultiQuestion) question;
        boolean wrongsAnswers = this.wrongAnswer1.equalsIgnoreCase(multi.getWrongAnswer1()) && this.wrongAnswer2.equalsIgnoreCase(multi.getWrongAnswer2()) && this.wrongAnswer3.equalsIgnoreCase(multi.getWrongAnswer3());
        if (category && difficulty && type && quest && correctAnswer && wrongsAnswers)
        {
            return true;
        }
        return false;
    }
}
