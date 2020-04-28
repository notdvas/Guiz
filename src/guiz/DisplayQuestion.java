package guiz;

public class DisplayQuestion {
    private String category, difficulty, type, question, correctAnswer, wrongAnswer1, wrongAnswer2, wrongAnswer3;
    public DisplayQuestion(Question question)
    {
        this.category = question.getCategory();
        this.difficulty = question.getDifficulty();
        this.type = question.getType();
        this.question = question.getQuestion();
        this.correctAnswer = question.getCorrectAnswer();
        if(question instanceof MultiQuestion)
        {
            this.wrongAnswer1 = ((MultiQuestion) question).getWrongAnswer1();
            this.wrongAnswer2 = ((MultiQuestion) question).getWrongAnswer2();
            this.wrongAnswer3 = ((MultiQuestion) question).getWrongAnswer3();
        }
        else
        {
            this.wrongAnswer1 = null;
            this.wrongAnswer2 = null;
            this.wrongAnswer3 = null;
        }
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
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
    public MultiQuestion toMultiQuestion()
    {
        MultiQuestion multi = new MultiQuestion(this.category, this.difficulty, this.type, this.question, this.correctAnswer, this.wrongAnswer1, this.wrongAnswer2, this.wrongAnswer3);
        return multi;
    }
    public OpenQuestion toOpenQuestion()
    {
        OpenQuestion open = new OpenQuestion(this.category, this.difficulty, this.type, this.question, this.correctAnswer);
        return open;
    }
    public FillQuestion toFillQuestion()
    {
        FillQuestion fill = new FillQuestion(this.category, this.difficulty, this.type, this.question, this.correctAnswer);
        return fill;
    }
}
