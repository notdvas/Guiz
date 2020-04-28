package guiz;

import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;

import java.io.*;
import java.net.StandardSocketOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Settings {
    private static String path = FirstLaunchRoutine.getGuizPath();
    private static QuestionsDatabase db = QuestionsDatabase.getInstance();
    private static Settings settings;

    private Settings(){}

    public static Settings getInstance()
    {
        if (settings == null)
        {
            settings = new Settings();
        }
        return settings;
    }

    public static boolean login(String inputPsw) throws IOException
    {
        File loginf = new File(path + "/lib/Pass/pass.txt");
        Scanner read = new Scanner(loginf);//SCANNER PER LEGGERE NEL FILE
        String psw = read.next();
        if (inputPsw.equals(psw))
        {
            read.close();
            return true;
        }
        else
        {
            read.close();
            return false;
        }
    }

    public static boolean editPassword (String newPsw, String repeatPsw, String oldPsw) throws IOException
    {
        File loginf = new File(path + "/lib/Pass/pass.txt");
        if(login(oldPsw) && newPsw.equals(repeatPsw))
        {
            PrintWriter writer = new PrintWriter(loginf);
            writer.write(newPsw);
            writer.close();
            return true;
        }
        return false;
    }
    public static void resetPassword () throws IOException
    {
        File loginf = new File(path + "/lib/Pass/pass.txt");
        File dir = new File(path + "/lib/Pass/");
        if(!dir.exists())
        {
            dir.mkdir();
        }
        PrintWriter writer = new PrintWriter(loginf);
        writer.write("guiz");
        writer.close();

    }


    public static ArrayList<Question> fetchQuestions(List<CheckBox> selectedCategory, List<CheckBox> selectedDifficulty, List<CheckBox> selectedType) throws QuestionsDatabase.BadFileException, IOException//questo metodo filtra tutte le categorie scegliendo solo quelle selezionate dall'utente
    {
        ArrayList<Question> selectedQuestions = new ArrayList<>();
        if (selectedCategory.isEmpty()) //se non ho selezionato alcuna categoria
        {
            for (int i = 0; i < db.getCategories().size(); i++)
            {
                selectedQuestions.addAll(db.generateQuestions(db.readCategory(db.getCategories().get(i).getFile()))); //creo un'ArrayList contenente TUTTE LE DOMANDE (senza filtrarle per difficoltà e tipologia)
            }
            return db.filterQuestions(selectedQuestions, selectedDifficulty, selectedType); // passo l'ArrayList di TUTTE le domande a db che le filtra EVENTUALMENTE per categoria e difficoltà
        }

        else {
            for (int i = 0; i < db.getCategories().size(); i++)
            {
                for (int j = 0; j < selectedCategory.size(); j++) //ciclo fra le categorie scelte e la lista di categorie
                {
                    if (db.getCategories().get(i).getName().replace(".txt", "").equalsIgnoreCase(selectedCategory.get(j).getText())) //se la categoria è stata scelta dall'utente
                    {
                        selectedQuestions.addAll(db.generateQuestions(db.readCategory(db.getCategories().get(i).getFile()))); // creo un unica ArrayList contenente tutte le Question delle categorie scelte
                    }
                }
            }
            return db.filterQuestions(selectedQuestions, selectedDifficulty, selectedType); //la passo al db che procede a filtrarla EVENTUALMENTE per difficoltà e tipologia
        }
    }

    public static void exportQuestions(ObservableList<Question> questions,File file) throws IOException {
        //il metodo writeQuestion non sovrascrive, ma aggiunge righe al file, quindi, in caso qualcuno stia scrivendo su un file già esistente, si otterebbe un file che non rispetta la formattazione richiesta dal gioco
        //writeHeader invece, è più aggressivo e prima svuota il file e poi scrivo l'header
                writeHeader(file);
                for (int i = 0; i < questions.size(); i++)
                writeQuestion(questions.get(i), file);

    }

    public static void importQuestions(File userQuestions) throws IOException, QuestionsDatabase.BadFileException {
        ArrayList<String> fileContent = db.readCategory(userQuestions);
        ArrayList<Question> questions = db.generateQuestions(fileContent); // se non riesco a leggere il file questo metodo mi lancia una BadFileExcepetion che gestisco nel controller
        for (int i = 0; i < questions.size(); i++) // prendo la domanda, parto da 1 poiché 0 è l'header del file
        {
            ArrayList<Category> categories = db.getCategories();
            boolean categoryExists = false;
            File file = null;
            for (int j = 0; j < categories.size() && !categoryExists; j++) // ciclo fra le categorie
            {
                if (questions.get(i).getCategory().equalsIgnoreCase(categories.get(j).getName())) //se trovo un riscontro entro qui
                {
                    categoryExists = true;
                    file = categories.get(j).getFile();
                }
            } //in questo caso, trovo la domanda su cui scrivere
            if (categoryExists)
            {
                writeQuestion(questions.get(i), file);
            }
            else
                {
                    file = new File(path + "/lib/Domande/" + questions.get(i).getCategory() + ".txt");
                    writeHeader(file);
                    writeQuestion(questions.get(i), file);
                    db.generateCategories();
            }

        }
    }
    public static void createQuestion(Question question) throws IOException //questo metodo si occupa di elaborare i dati relativi alla nuova domanda
            //se si tratta di una domanda di una categoria già presente nel gioco, passa a writeQuestion il file su cui scrivere
            //se si tratta di una nuova categoria, crea il file, e poi lo passa a WriteQuestion
    {
        String category = question.getCategory();
        File selectedCategory;
        boolean categoryExists = false;
        for (int i = 0; i < db.getCategories().size(); i++)
        {
            if (category.equals(db.getCategories().get(i).getName())) //trovo la categoria su cui scrivere
            {
                selectedCategory = db.getCategories().get(i).getFile();
                writeQuestion(question, selectedCategory);
                categoryExists = true;
            }

        }
        if (!categoryExists)
        {
            File file = new File(path + "/lib/Domande/"+category+".txt");
            writeHeader(file);
            writeQuestion(question, file);
        }



    }

    public static void writeQuestion (Question question, File category) throws IOException //questo metodo scrive la domanda che gli viene passata nel file che gli viene passato
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(category, true));
        writer.write(question.toString());
        writer.newLine();
        writer.flush();
        writer.close();
    }
    public static void writeHeader (File category) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(category));
        writer.write("Categoria Difficoltà Tipologia Domanda Risposta corretta Risposte sbagliate");
        writer.newLine();
        writer.flush();
        writer.close();
    }

    public static void deleteQuestion(Question question) throws QuestionsDatabase.BadFileException, IOException {
        //Prendo la categoria della domanda da eliminare
        //la confronto con le categorie presenti nel gioco per risalire al FILE su cui lavorare
        //una volta che ho il file
        ArrayList<String> fileContent = new ArrayList<>();
        String category = question.getCategory();
        File file = null;
        BufferedReader reader;

        for (int i = 0; i < db.getCategories().size(); i++)
        {
            if (category.equalsIgnoreCase(db.getCategories().get(i).getName()))
            {
                file = db.getCategories().get(i).getFile();
            }
        }
        // ho trovato la categoria su cui lavorare
        //insomma, per ora ho la domanda da elminare sotto forma di obj question, e il file in cui si trova
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine(); //lo chiamo una volta così salto l'header

            fileContent.add(line); //tuttavia l'header lo devo aggiungere nel file temporaneo, poiché sarà poi generateQuestions a rimuoverlo nuovamente
            while((line = reader.readLine()) != null)
            {
                Question questionToCheck;
                line.trim();
                String[] elements = line.split("~");

                if(elements[2].equalsIgnoreCase("Multipla"))
                {
                    questionToCheck = new MultiQuestion(elements[0], elements[1], elements[2], elements[3], elements[4].replace("//", ""), elements[5], elements[6], elements[7]);
                }
                else if(elements[2].equalsIgnoreCase("Aperta"))
                {
                    questionToCheck = new OpenQuestion(elements[0], elements[1], elements[2], elements[3], elements[4].replace("//", ""));
                }
                else
                {
                    questionToCheck = new FillQuestion(elements[0], elements[1], elements[2], elements[3], elements[4].replace("//", ""));
                }
                if(!question.equals(questionToCheck))
                {
                    fileContent.add(line);
                }

            }
            reader.close();
            ArrayList<Question> questions = db.generateQuestions(fileContent);
            if(!questions.isEmpty())
            {
                writeHeader(file); //oltre a scrivere l'header, svuota pure il file
                for (int i = 0; i < questions.size(); i++)
                {
                    writeQuestion(questions.get(i), file);
                }
            }
            else
            {
                file.delete();
            }

    }
    //Questo metodo in parte ripete il codice di readCategory, tuttavia lo fa con un approccio diverso teso al permettere che l'utente possa importare effettivamente le sue domande
    public static void deleteBadQuestion(File offendingFile, Boolean libFile) throws IOException, QuestionsDatabase.BadFileException {
        ArrayList<String> fileContent = new ArrayList<>();
        File temp = new File("temp.txt");
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(offendingFile)); //Leggo il file, passato al metodo
        String line = reader.readLine(); //lo chiamo una volta così salto l'header
        fileContent.add(line); //tuttavia l'header lo devo aggiungere nel file temporaneo, poiché sarà poi generateQuestions a rimuoverlo nuovamente
        while((line = reader.readLine()) != null)
        {
            String[] elements = line.split("~");
            if (db.integrityCheck(elements)) //mi assicuro che leggendo il file ogni riga possa costituire una domanda, se può la salvo
            {
                fileContent.add(line);
            }
        }
        reader.close();
        ArrayList<Question> questions = db.generateQuestions(fileContent); //quindi genero le domande

        if(!questions.isEmpty()) //se ci sono delle domande
        {
            if (libFile) //Se è un libFile, cioè un file già interno al gioco, lo cancello prima di riscriverlo
            {
                offendingFile.delete();
                db.generateCategories();
            }
            writeHeader(temp); //oltre a scrivere l'header, svuota pure il file
            for (int i = 0; i < questions.size(); i++)
            {
                writeQuestion(questions.get(i), temp);
            }
            importQuestions(temp);
            temp.delete();
        }
        else
        {
            temp.delete();
        }

    }
    public static Question convertDisplayedQuestion(DisplayQuestion displayedQuestion)
    {
        String type = displayedQuestion.getType();
        Question question;
        if(type.equalsIgnoreCase("Multipla"))
        {
            question = displayedQuestion.toMultiQuestion();
        }
        else if(type.equalsIgnoreCase("Aperta"))
        {
            question = displayedQuestion.toOpenQuestion();
        }
        else
        {
            question = displayedQuestion.toFillQuestion();
        }
        return question;
    }
    public static void nuclearButton()
    {
        File file = new File(path + "/lib/");
        if (file.exists()) {
            File[] folderContents = file.listFiles();
            if (folderContents != null) //se ci sono sotto cartelle in lib
            {
                for (File f : folderContents) //per ogni sottocartella
                {
                    folderContents = f.listFiles(); // array dei contenuti di sottocartelle
                    for (File t : folderContents) {
                        t.delete();
                    }
                    f.delete();
                }
            }
            file.delete();
        }

    }
}





