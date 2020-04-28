package guiz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MainMenuController {
    @FXML
    private Button newGameButton, tutorialButton, settingsButton, creditsButton, backButton;
    @FXML
    private Label tutorialLabel;

    @FXML
    private VBox mainMenuVBox, creditsVBox;
    @FXML
    private ScrollPane tutorialSp;
    private Alert errorDialog = new Alert(Alert.AlertType.ERROR);
    private Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);

    QuestionsDatabase db = QuestionsDatabase.getInstance();
    Settings settings = Settings.getInstance();
    private FirstLaunchRoutine flr = new FirstLaunchRoutine();
    private static final String tutorial =
            "Guiz è un gioco a quiz multigiocatore! \n" +
            "Dal menù impostazioni è possibile modificare, aggiungere o rimuovere domande. \n" +
            "Per accedere al menù impostazioni è necessario utilizzare una password, di default la password è: guiz, ma è possibile modificarla in ogni momento. \n" +
            "Una volta dentro le impostazioni è possibile filtrare tutte le domande presenti nel gioco e visualizzare solo quelle desiderate. \n" +
            "Le domande filtrate sono esportabili in un file esterno premendo Esporta Domande, questo file sarà formattato per poter essere reimportato in ogni versione di guiz. \n" +
            "Selezionando una domanda fra quelle visualizzate è possibile cancellarla o modificarla premendo i rispettivi bottoni. \n\n" +
            "Premendo Crea Domanda sarà possibile creare una nuova domanda, di qualsiasi delle tre tipologie presenti nel gioco o difficoltà e di una categoria fra quelle presenti o crearne una nuova \n"+
            "Premendo Importa Domande verrà richiesto all'utente di selezionare un file in formato .txt contenente al suo interno delle domande da importare nel gioco \n" +
            "Questo file dovrà essere formattato nel seguente modo: \n" +
            "La prima riga dovrà contenere un'intestazione qualsiasi, consigliamo di usare la seguente per comodità: \n" +
            "Categoria Difficoltà Tipologia Domanda Risposta corretta Risposte sbagliate \n\n" +
            "Le righe successive conterrano le domande, ogni elemento della domanda deve essere diviso dal simbolo ~ \n" +
            "La risposta correttà dovrà essere seguita dal doppio simbolo // \n" +
            "Esempio: \n\n" +
            "Geografia~Facile~Aperta~In che regione italiana si trova il Monte Bianco?~Valle d'Aosta//\n" +
            "Questa domanda è di tipologia aperta, quindi prevede solo la risposta corretta \n" +
            "Geografia~Facile~Multipla~Qual'è la capitale del Belgio?~Bruxelles//~Londra~Copenaghen~Amsterdam \n" +
            "Questa è multipla, quindi oltre ad avere la risposta corretta ne ha tre seguenti sbagliate. \n" +
            "Geografia~Facile~Riempimento~Il palazzo reale più famoso della Campania?~Reggia di Caserta//\n" +
            "In ultimo, le domande a riempimento mantengono la struttura delle domande aperte, la differenza, come vedremo in seguito, è di gameplay. \n"+
            "Il gioco ha al suo interno dei metodi per controllare che le domande rispettino questa formattazione, ti avviserà se non dovessero farlo, e se vuoi proverà a correggerle \n\n" +
            "La partita: \n" +
            "Sarà possibile premere il pulsante inizia partita solo a patto che ci siano delle domande nel gioco, e che le stesse rispettino le regole di formattazione. \n"+
            "Una volta premuto, sarà possibile scegliere il numero di giocatori (1-8) e il numero di domande a cui ogni giocatore dovrà rispondere nella partita (5-15) \n" +
            "Inoltre, sarà possibile selezionare quali categorie, tipologie e difficoltà di domande dovranno comparire in partita. \n" +
            "Sarà possibile iniziare la partita a patto che nel gioco ci siano abbastanza domande per ogni giocatore, ad esempio, in una partita da 3 giocatori, con 5 domande ciascuno, c'è bisogno di 15 domande \n" +
            "Prima d'iniziare la partita vera e propria ogni giocatore potrà impostare un proprio nome e un colore identificativo \n\n" +
            "Le domande a risposta aperta prevedono l'inserimento della risposta in un campo di testo e il premere un bottone per confermare, le risposte non sono case sensitive \n"+
            "Le domande a risposta multipla prevedono di premere il bottone corrispondente alla risposta corretta \n"+
            "Le domande a riempimento prevedono di fornire una risposta, premendo i bottoni presenti sullo schermo (Contenenti le lettere dell'alfabeto) al fine di visualizzarla, dopo 5 errori, la domanda si ritiene sbagliata \n"+
            "Ogni giocatore che risponde a tre domande consecutive correttamente totalizzerà uno strike \n"+
            "Alla domanda successiva alla terza risposta corretta consecutiva il giocatore potrà scegliere se provare a raddoppiare il punteggio della domanda o far saltare il turno ad un altro giocatore \n"+
            "In caso dovesse decidere di provare a raddoppiare il punteggio, dovrà rispondere normalmente alla domanda posta, in caso di risposta corretta raddoppierà i punti della domanda e il gioco continuerà normalmente, in caso contrario tutti i giocatori seguenti fino a che non si torna nuovamente a lui rispondendo correttamente alla lora domanda riceveranno punti doppi. \n" +
            "Se dopo che un giocatore ha deciso di raddoppiare i punti, e ha sbagliato, un altro giocatore dovesse fare la stessa scelta, rispondendo correttamente, toglierebbe ai giocatori seguenti la possibilità di raddoppiare a loro volta i punti\n"+
            "Direi che è tutto! Buon divertimento! \n\n" +
            "Ah, dimenticavo, l'obiettivo è vincere, e vince chi fa più punti, facile, no?"
            ;

    @FXML
    public void initialize() //Initialize viene chiamato appena attivo il controller, una sola volta. mi creo gli elementi di UI di cui ho bisogno.
    {
        newGameButton.setDisable(false);        //prima di tutto disabilito il bottone, che abiliterò solamente se si verificheranno le condizioni necessarie
        String path = flr.getGuizPath() + "/lib/"; //path alla libreria di Guiz
        boolean firstLaunch = false;

        if(new File(path).exists() && new File(path + "Domande").exists())
        {
            firstLaunch = true;
        }
        if (!firstLaunch) //se la libreria non esiste, è il primo lancio
        {
            try{
                flr.start(); //inizio la routine del primo lancio
            }
            catch (QuestionsDatabase.BadFileException e)
            {
                errorDialog.setTitle("Notizie orribili");
                errorDialog.setHeaderText("No, davvero...");
                errorDialog.setContentText("Questo è il peggior errore che potesse venire fuori, l'eseguibile è corrotto, in base alla gravità della corruzione il gioco si avvierà o meno ma non assicuriamo che sia giocabile");
                errorDialog.showAndWait();
            } catch (IOException e)
            {
                errorDialog.setTitle("Notizie orribili");
                errorDialog.setHeaderText("Abbastanza orribili...");
                errorDialog.setContentText("Non è il peggior errore che potesse venire fuori, ma è grave. Sembra che non possiamo scrivere nella cartella in cui stai eseguendo il gioco, controlla i permessi o sposta solamente il file .Jar i una nuova cartella");
                errorDialog.showAndWait();
            }
        }
        // firstLaunch o meno, arrivato qui devo ancora fare dei controlli
        //prima di tutto devo controllare che ci siano categorie
        //se non ci sono disabilito il bottone newGame
        boolean integrity = true;
        db.generateCategories();
        if(db.getCategories().size() == 0)
        {
            integrity = false; //da ora in poi uso questa booleana per determinare lo stato del bottone newGame
        }
        //se ci sono categorie, controllo anche che siano integre
        else {
            for (int i = 0; i < db.getCategories().size(); i++) {
                File file = db.getCategories().get(i).getFile();
                try {
                    db.integrityCheck(file);
                } catch (Exception badFile) {
                    integrity = false;
                    confirmationDialog.setTitle("Brutte notizie!");
                    confirmationDialog.setHeaderText("Sembra che la categoria " + db.getCategories().get(i).getName() + " non sia formattata bene...");
                    confirmationDialog.setContentText("La categoria presente problemi alla linea\\e " + badFile.getMessage() + " Premi OK se vuoi che proviamo a sistemarla cancellando la domanda\\e problematiche, oppure accedi a \\lib\\Domande dentro la cartella del gioco per sistemarla manualmente");
                    Optional<ButtonType> choice = confirmationDialog.showAndWait();

                    if (choice.get() == ButtonType.OK) {
                        try {
                            settings.deleteBadQuestion(file, true);
                            integrity = true;
                        } catch (QuestionsDatabase.BadFileException reallyBadFile) {
                            errorDialog.setHeaderText("Mi dispiace...");
                            errorDialog.setContentText("Più di questo proprio non potevamo...");
                            errorDialog.showAndWait();
                            integrity = false;
                        } catch (IOException ioexc) {
                            //fai qualcosa con questa exception
                            integrity = false;
                        }

                    }
                }
                db.generateCategories();
            }
        }
        if (db.getCategories().size() >0 && integrity)
        {
            newGameButton.setDisable(false);
        }
        else
        {
            newGameButton.setDisable(true);
        }
    }

    @FXML
    public void onButtonClicked(ActionEvent e)
    {
        if (e.getSource().equals(tutorialButton))
        {
            tutorialLabel.setText(tutorial);
            mainMenuVBox.setVisible(false);
            tutorialSp.setVisible(true);
            backButton.setVisible(true);
        }

        else if (e.getSource().equals(creditsButton))
        {
            mainMenuVBox.setVisible(false);
            creditsVBox.setVisible(true);
            backButton.setVisible(true);
        }
        else if (e.getSource().equals(backButton))
        {
            mainMenuVBox.setVisible(true);
            tutorialSp.setVisible(false);
            creditsVBox.setVisible(false);
            backButton.setVisible(false);

        }
    }
    @FXML
    public void changeRootButton(ActionEvent e)
    {
        try {
            if (e.getSource().equals(newGameButton)) { //getSource cerca l'id del bottone assegnato nel file xml
                Parent newRoot = FXMLLoader.load(getClass().getResource("/lib/Scenes/new_game.fxml")); //creo una nuova reference per il root node
                newGameButton.getScene().setRoot(newRoot); //cambio il root node della scene su cui si trova il bottone premuto
            } else if (e.getSource().equals(settingsButton)) {
                Parent newRoot = FXMLLoader.load(getClass().getResource("/lib/Scenes/settings.fxml"));
                settingsButton.getScene().setRoot(newRoot);
            }
        } catch (IOException fxmlMissing)
        {
            errorDialog.setTitle("Notizie orribili");
            errorDialog.setHeaderText("No, davvero...");
            errorDialog.setContentText("Questo è il peggior errore che potesse venire fuori, l'eseguibile è corrotto, in base alla gravità della corruzione il gioco si avvierà o meno ma non assicuriamo che sia giocabile");
            errorDialog.showAndWait();
        }
    }

}
