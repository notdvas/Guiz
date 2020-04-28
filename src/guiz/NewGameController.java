package guiz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NewGameController  {
    //questo va rifatto
    @FXML
    private Button backButton, startGameButton;
    @FXML
    private VBox categorySelector;
    @FXML
    private CheckBox easyCheck, mediumCheck, hardCheck, multiQuestion, openQuestion, fillQuestion;
    @FXML
    private ComboBox numeroGiocatori,numeroDomande;


    QuestionsDatabase db = QuestionsDatabase.getInstance();
    Settings settings = Settings.getInstance();
    private List<CheckBox> categoryCheckBoxes = new ArrayList<CheckBox>();
    private ObservableList<Question> questions = FXCollections.observableArrayList();
    private Alert errorDialog = new Alert(Alert.AlertType.ERROR);
    private Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);




    @FXML
    public void initialize() //Initialize viene chiamato appena attivo il controller, una sola volta. mi creo gli elementi di UI di cui ho bisogno.
    {
        for (int i = 0; i < db.getCategoriesNames().size(); i++) {
            CheckBox catCheckBox = new CheckBox("" + db.getCategoriesNames().get(i));
            categoryCheckBoxes.add(catCheckBox);
            categorySelector.getChildren().add(catCheckBox);
        }

        numeroGiocatori.getItems().addAll(1,2,3,4,5,6,7,8);
        numeroDomande.getItems().addAll(5,10,15);
        numeroDomande.getSelectionModel().selectFirst();
    }

    @FXML
    public void IODialog(Boolean fxml)
    {
        if(fxml)
        {
            errorDialog.setHeaderText("Mi dispiace...");
            errorDialog.setContentText("Sembra ci sia un problema ad accedere ad alcuni file fondamentali per l'esecuzione del gioco, ti consigliamo di effettuare un backup della cartella lib e riscaricarlo");
            errorDialog.showAndWait();
        }
        else
        {
            errorDialog.setHeaderText("Mi dispiace...");
            errorDialog.setContentText("Sembra che non possiamo accedere ad alcuni file, assicurati che i file della libreria non siano aperti in un altro programma o che siano in una cartella su cui hai pieno accesso");
            errorDialog.showAndWait();
        }

    }
    @FXML
    public void badFileRoutine(String error) {
        confirmationDialog.setHeaderText("C'è un errore di formattazione!");
        confirmationDialog.setContentText("Uno dei file delle categoria a cui hai provato ad accedere ha un errore alla\\e linea\\e " + error + " apri la cartella lib\\Domande nella directory del gioco e assicurati che i file al suo interno rispettino la formattazione come indicato nella seziona tutorial del gioco");
        boolean libFile = true;

        if (confirmationDialog.showAndWait().get() == ButtonType.OK)
        {
            for (int i = 0; i < db.getCategories().size(); i++) {
                File file = db.getCategories().get(i).getFile();
                try {
                    settings.deleteBadQuestion(file, libFile);
                } catch (IOException ioEx) {
                    IODialog(false);
                } catch (QuestionsDatabase.BadFileException reallyBadFile) {
                    errorDialog.setHeaderText("Mi dispiace...");
                    errorDialog.setContentText("Più di questo non potevamo fare");
                    errorDialog.showAndWait();
                }
            }
        }
    }

    @FXML
    public void onButtonClicked(ActionEvent e) {
        //torno indietro al menù iniziale
        if (e.getSource().equals(backButton))
        {
            try {
                Parent newRoot = FXMLLoader.load(getClass().getResource("/lib/Scenes/main.fxml")); //tutti i nostri bottoni back, per fortuna, rendirizzano al menù principale
                backButton.getScene().setRoot(newRoot);
            }catch(IOException fxml)
            {
                IODialog(true);
            }
        }
        else if (e.getSource().equals(startGameButton)) {

            if((numeroGiocatori.getSelectionModel().getSelectedItem())!=null)
            {   //L'ho messo qua perchè fatto fatica a farlo diventare metodo
                List<CheckBox> selectedCategory = new ArrayList<>();
                List<CheckBox> selectedDifficulty = Stream.of(easyCheck, mediumCheck, hardCheck).filter(CheckBox::isSelected).collect(Collectors.toList());
                List<CheckBox> selectedType = Stream.of(multiQuestion, openQuestion, fillQuestion).filter(CheckBox::isSelected).collect(Collectors.toList());


                for (int i = 0; i< categoryCheckBoxes.size(); i++)
                {
                    if (categoryCheckBoxes.get(i).isSelected())
                    {
                        selectedCategory.add(categoryCheckBoxes.get(i));
                    }
                }
                try{
                    questions.setAll(settings.fetchQuestions(selectedCategory, selectedDifficulty, selectedType)); //Aggiungo tutte le domande filtrate dall'utente in una ObservableL
                } catch (IOException io) {
                    IODialog(false);
                } catch (QuestionsDatabase.BadFileException badFile) {
                    badFileRoutine(badFile.getMessage());
                }
                int numPlayers= (Integer)numeroGiocatori.getSelectionModel().getSelectedItem();
                //Qua ho impostato il numero di domande
                int numQuestion= (Integer)numeroDomande.getSelectionModel().getSelectedItem();

                if(numQuestion*numPlayers<=questions.size()) //numQuestions va moltiplicato per numero di giocatori, partita da 5 domande e 3 giocatori, ha bisogno di 5 domande per giocatore, quindi numQuestions è 15
                {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lib/Scenes/createPlayers.fxml"));
                        Parent parent = fxmlLoader.load();
                        Scene scene = new Scene(parent, 600, 290);
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(scene);
                        CreatePlayersController controller = fxmlLoader.<CreatePlayersController>getController();
                        //qua passo il numero di giocatori
                        controller.setPartita(numPlayers);
                        stage.showAndWait();
                    } catch (IOException fxml)
                    {
                        IODialog(true);
                    }
                    try {

                        //forse questo if si può togliere però lo lascio intanto
                        if((!(CreatePlayersController.getPlayers().isEmpty())&&CreatePlayersController.getOk()==true)){
                            FXMLLoader fxmlLoad = new FXMLLoader(getClass().getResource("/lib/Scenes/game.fxml"));
                            Parent root = fxmlLoad.load();
                            startGameButton.getScene().setRoot(root);
                            GameController gController = fxmlLoad.<GameController>getController();
                            gController.setGame(CreatePlayersController.getPlayers(),questions,numQuestion);
                        }
                    } catch (IOException fxml)
                    {
                        IODialog(true);
                    }
                }
                else {

                    errorDialog.setTitle("Attenzione!");
                    errorDialog.setHeaderText("Non ci sono abbastanza domande nel database!");
                    errorDialog.setContentText(" Cambia i numeri di giocatori o il numero di domande!");
                    errorDialog.showAndWait();
                }


            }
            else
            {
                errorDialog.setTitle("Attenzione!");
                errorDialog.setHeaderText("Non hai impostato i giocatori!");
                errorDialog.setContentText(" Fallo ora!");
                errorDialog.showAndWait();

            }


        }

    }


}
