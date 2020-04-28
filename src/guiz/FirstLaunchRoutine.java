package guiz;

import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class FirstLaunchRoutine {
    QuestionsDatabase db = QuestionsDatabase.getInstance();
    String path = getGuizPath();

    public void start() throws QuestionsDatabase.BadFileException, IOException
    {
        File dir = new File(path + "/lib/");
        dir.mkdir();
        dir = new File(path + "/lib/Pass/");
        dir.mkdir();
        dir = new File(path + "/lib/Domande/");
        dir.mkdir();
        Settings.resetPassword();
        ArrayList<InputStreamReader> standardQuestions = new ArrayList<>();
        standardQuestions.add(new InputStreamReader(getClass().getResourceAsStream("/lib/Geografia.txt")));
        standardQuestions.add(new InputStreamReader(getClass().getResourceAsStream("/lib/Storia.txt")));
        standardQuestions.add(new InputStreamReader(getClass().getResourceAsStream("/lib/Informatica.txt")));
        for (int i = 0; i < standardQuestions.size(); i++)
        {
            writeStandardQuestions(standardQuestions.get(i));
        }
    }
    public static String getGuizPath() {
        String currentdir = System.getProperty("user.dir");
        currentdir = currentdir.replace( "\\", "/" );
        return currentdir;

    }
    public void writeStandardQuestions(InputStreamReader standardQuestions) throws IOException, QuestionsDatabase.BadFileException
    {
        BufferedReader br = new BufferedReader(standardQuestions);
        ArrayList<String> categoryArray = new ArrayList<>();
        File file;

        String line = br.readLine();
        while ((line) != null)
        {
            if (!line.isEmpty())
            {
                categoryArray.add(line);
            }
            line = br.readLine();
        }
        br.close();
        ArrayList<Question> questions = db.generateQuestions(categoryArray);
        file = new File(path + "/lib/Domande/" + questions.get(0).getCategory() + ".txt");
        Settings.writeHeader(file);
        for (int i = 0; i < questions.size(); i ++)
        {
            Settings.writeQuestion(questions.get(i), file);
        }
    }
}
