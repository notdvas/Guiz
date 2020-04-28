package guiz;

public abstract class Question {

    String category;
    String difficulty;
    String type;
    String question;
    String correctAnswer;

    public Question (String category, String difficulty, String type, String question, String correctAnswer)
    {
        this.category = category;
        this.difficulty = difficulty;
        this.type = type;
        this.question = question;
        this.correctAnswer = correctAnswer;
    }
    public String getQuestion()
    {
        return question;
    }
    public String getCorrectAnswer()
    {
        return correctAnswer;
    }
    public String getDifficulty()
    {
        return difficulty;
    }
    public String getType()
    {
        return type;
    }
    public String getCategory(){return category;}

    public String toString()
    {
        return category+"~"+difficulty+"~"+type+"~"+question+"~"+correctAnswer+"//";

    }

    abstract boolean equals(Question question);
    public double setDiffScore(String s){
        double scoreNum=0.0;
        switch(s){
            case "Facile":
                scoreNum=5000.0;
                break;
            case "Media":
                scoreNum=8000.0;
                break;
            case "Difficile":
                scoreNum=10000.0;
                break;
        }
        return scoreNum;
    }
}


