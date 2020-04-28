package guiz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionsDatabase {

    //La classe question database si occupa esclusivamente di gestire, filtrare e leggere i file categoria e le domande.
    //Ogni operazione di scrittura è delegata ai metodi presenti nella classe Settings.
//    private File dir = new File(getClass().getResource("/lib/Domande)").toString());
    private static QuestionsDatabase db;
    private static String path = FirstLaunchRoutine.getGuizPath();
    private static File dir = new File(path + "/lib/Domande/");
    private static ArrayList<Category> categories = new ArrayList<>(); //obj category
    private static ObservableList<String> categoriesList = FXCollections.observableArrayList(); //nomi delle categorie

    private QuestionsDatabase(){}

    public static QuestionsDatabase getInstance()
    {
        if (db == null)
        {
            db = new QuestionsDatabase();
        }
        return db;
    }

    public static void generateCategories() // questo metodo genera un'arrayList di obj Category (tutte le categorie) e uan ObservableList di stringhe contenenti i nomi delle categorie
    {
        categories.clear();
        categoriesList.clear();
        ArrayList<File> fileList = new ArrayList<File>(Arrays.asList(dir.listFiles()));
        for (int i = 0; i < fileList.size(); i++)
        {

            File file = fileList.get(i);
            String categoryName = fileList.get(i).getName().replace(".txt", "");
            Category category = new Category(categoryName, file);
            categories.add(category);
            categoriesList.add(categories.get(i).getName());
        }
    } //questo metodo viene chiamato all'avvio dell'applicazione e va richiamato ogni volta che avviene una modifica sui file categoria

    public static ArrayList<Category> getCategories()
    {
        return categories; //ritorna un'array di Category, getName per ottenere il nome Es"Geografia", getFile per ottenere il File
    }


    public static ObservableList<String> getCategoriesNames()
    {
        return categoriesList; //ritorna i nomi delle categorie
    }


    public static ArrayList<String> readCategory (File category) throws IOException //questo metodo legge un File categoria e riempie un'array con le domande(stringhe) in esso contenute
            //questo metoodo è stupido, non ha modo di determinare se ci siano errori di formattazione, si limita a leggere e andare a capo
            //è generateQuestions, che invece, controlla che il file sia formattato bene
    {
        ArrayList<String> categoryArray = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new FileReader(category));

            String line = reader.readLine();

            while ((line) != null)
            {
                if (!line.isEmpty())
                {
                    categoryArray.add(line);
                }
                line = reader.readLine();
            }
            reader.close();
        return categoryArray; //questa ArrayList contiene tutte le domande del file categoria che passo al metodo
    }
    public static ArrayList<Question> generateQuestions (ArrayList<String> fileContentArr) throws BadFileException // questo metodo genera un'array di QUESTION8(obj) partendo dalla categoryArray che gli viene passata
    {
        //L'arrayList di String che passo al metodo contiene una domanda in ogni suo elemento
        ArrayList<Question> categoryQuestions = new ArrayList<>();
        int excepetionCounter = 0;
        ArrayList<Integer> exceptions = new ArrayList<>();
            for (int i = 1; i < fileContentArr.size(); i++) //inizio a leggere dalla seconda riga così salto l'header
            {
                String line = fileContentArr.get(i); //prendo il primo elemento dell'array che passo al metodo, cioè una domanda
                line.trim();
                String[] elements = line.split("~"); // divido la domanda secondo i suoi singoli elementi (difficoltà, tipologia, domanda, risposte, risposta corretta) e ne riempio un'array
                if(integrityCheck(elements))
                {
                    String category = elements[0];
                    String difficulty = elements[1];
                    String type = elements[2];
                    String question = elements[3];
                    ArrayList<String> wrongAnswers = new ArrayList<>();
                    String correctAnswer = null;

                    if (type.equalsIgnoreCase("Aperta")) //se la domanda è di tipologia aperta, per determinarlo potrei fare un controllo sulla stringa type, o sulla dimensione dell'array della domanda
                    {
                        correctAnswer = elements[4];
                        Question ql = new OpenQuestion(category, difficulty, type, question, correctAnswer.replace("//", ""));
                        categoryQuestions.add(ql);
                    }
                    else if (type.equalsIgnoreCase("Riempimento"))
                    {
                        correctAnswer = elements[4];
                        Question ql = new FillQuestion(category, difficulty, type, question, correctAnswer.replace("//", ""));
                        categoryQuestions.add(ql);
                    }
                    else
                    {
                        for (int j = 4; j < elements.length; j++)
                        {
                            if (elements[j].contains("//")) {
                                correctAnswer = elements[j];
                            } else {
                                wrongAnswers.add(elements[j]);
                            }
                        }
                        Question ql = new MultiQuestion(category, difficulty, type, question, correctAnswer.replace("//", ""), wrongAnswers.get(0), wrongAnswers.get(1), wrongAnswers.get(2));
                        categoryQuestions.add(ql);
                    }
                }
                else //se integrityCheck non è andato a buon fine
                {
                    exceptions.add(i);
                    excepetionCounter++;
                }
            }


            if (excepetionCounter > 0)
            {
                    throw new BadFileException(exceptions.toString());
            }
            return categoryQuestions;//mi ritornaList un'array di QUESTIONS, divise per i suoi elementi
    }


    //passo a questo metodo l'arrayList di question di una determinata categoria
    //passo anche la difficoltà selezionata e la tipologia selezionata
    //filtro l'arrayList per difficoltà e tipologia
    //riempio una nuova array con il risultato del filtro
    public static ArrayList<Question> filterQuestions (ArrayList<Question> selectedQuestion, List<CheckBox> selectedDifficulty, List<CheckBox> selectedType)
    {
        if (selectedDifficulty.isEmpty() && selectedType.isEmpty()) //se non si è selezionata alcuna difficoltà e nessuna tipologia
        {
            return selectedQuestion; //ritorno la stessa lista che ho passato al metodo
        }
        else if (selectedDifficulty.isEmpty()) //se non si è selezionata nessuna difficoltà
        {
            return filterByType(selectedQuestion, selectedType); //filtro solo per tipologia
        }
        else if (selectedType.isEmpty())
        {
            return filterByDifficulty(selectedQuestion, selectedDifficulty); //filtro solo per difficoltà
        }
        else
        {
            return filterByType(filterByDifficulty(selectedQuestion, selectedDifficulty), selectedType); //filtro per tipologia e difficoltà
        }
    }

    public static ArrayList<Question> filterByDifficulty (ArrayList<Question> selectedQuestion, List<CheckBox> selectedDifficulty)
    {
        ArrayList<Question> filteredByDifficulty = new ArrayList<>();
        for (Question question : selectedQuestion) // per ogni Question che passo al metodo
        {
            for (int j = 0; j < selectedDifficulty.size(); j++) // ciclo per ogni difficoltà di domanda selezionata
            {
                if (question.getDifficulty().equalsIgnoreCase(selectedDifficulty.get(j).getText()))
                {
                    filteredByDifficulty.add(question);
                }
            }
        }
        return filteredByDifficulty;
    }
    public static ArrayList<Question> filterByType (ArrayList<Question> selectedQuestion, List<CheckBox> selectedType)
    {
        ArrayList<Question> filteredByType = new ArrayList<>();
        for (Question question : selectedQuestion) // per ogni Question che passo al metodo
        {
            for (int j = 0; j < selectedType.size(); j++) // ciclo per ogni difficoltà di domanda selezionata
            {
                if (question.getType().equalsIgnoreCase(selectedType.get(j).getText()))
                {
                    filteredByType.add(question);
                }
            }
        }
        return filteredByType;
    }

    public static void integrityCheck (File file) throws Exception
    {
            ArrayList<Question> integrityCheck = generateQuestions(readCategory(file));
            if (integrityCheck.size() < 1)
            {
                file.delete();
            }
    }
    public static boolean integrityCheck (String[] elements)
    {
        int length = elements.length;
        if (length <= 8 && length > 4) //se ha il numero di elementi tale per essere una domanda, quindi 8 per una multipla e maggiore di 4 per un'aperta
        {
            String difficulty = elements[1].trim();
            if(difficulty.equalsIgnoreCase("Facile")|| difficulty.equalsIgnoreCase("Media") || difficulty.equalsIgnoreCase("Difficile"))
            //se la difficoltà è una di quelle presenti nel gioco vado avanti
            {
                if (elements[2].equalsIgnoreCase("Multipla") && length == 8) //se è multipla e ci sono 8 elementi
                {
                    return true;
                }
                else if (elements[2].equalsIgnoreCase("Aperta") && length == 5 ||elements[2].equalsIgnoreCase("Riempimento") && length == 5)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        return false;
    }

    public static class BadFileException extends Exception {
        public BadFileException(String errorMessage)
        {
            super(errorMessage);
        }
    }
}

