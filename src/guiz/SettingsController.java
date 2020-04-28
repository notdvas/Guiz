package guiz;

import guiz.QuestionsDatabase.BadFileException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.security.auth.callback.ConfirmationCallback;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SettingsController {

    @FXML
    private TextField pswField, newPswField, repeatPswField;
    @FXML
    private Button backButton, pswChangeButton, confirmChangeButton, loginButton, importQButton, exportQButton, submitButton, createQuestionButton, deleteButton, editQButton, nuclearButton;
    @FXML
    private Label newPswLabel, repeatPswLabel, pswErrorLabel, printQuestions;//CREO LABEL 'randomLabel' dove andrò un eventuale messaggio di errore
    @FXML
    private VBox settingsLoginScreen, visualizeQuestionsSelector, categoryVBoxSelector;
    @FXML
    private SplitPane visualizeQuestionsBox;
    @FXML
    private CheckBox easyCheck, mediumCheck, hardCheck, multiQuestion, openQuestion, fillQuestion;
    @FXML
    private TableView<DisplayQuestion> visualizeQuestionsTable;
    @FXML
    private TableColumn<DisplayQuestion, String> categoryColumn, difficultyColumn, typeColumn, questionColumn, correctAnswerColumn, wrongAnswerColumn1, wrongAnswerColumn2, wrongAnswerColumn3;
    @FXML
    private ScrollPane categoriesScrollPane;


    private Label spacer = new Label();
    private List<CheckBox> categoryCheckBoxes = new ArrayList<CheckBox>();
    private ObservableList<Question> questions = FXCollections.observableArrayList(); //Lista di domande filtrate dall'utente
    private FileChooser filePicker = new FileChooser();
    private Alert exceptionDialog = new Alert(Alert.AlertType.ERROR);
    private Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
    QuestionsDatabase db = QuestionsDatabase.getInstance();
    Settings settings = Settings.getInstance();




    @FXML
    public void initialize() //Initialize viene chiamato appena attivo il controller, una sola volta. mi creo gli elementi di UI di cui ho bisogno.
    {
        loginButton.setDefaultButton(true);
        exceptionDialog.getDialogPane().setMinHeight(300);
        confirmationDialog.getDialogPane().setMinHeight(300);
        visualizeQuestionsSelector.getChildren().remove(submitButton);
        filePicker.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        refreshCategories();

    }
    @FXML
    public void refreshCategories() //refresho le checkBox categories per aggiungerne eventualmente di nuove
    {
        categoriesScrollPane.setVisible(false);
        db.generateCategories();
        for (int i = 0; i < categoryCheckBoxes.size(); i++)
        {
            categoryVBoxSelector.getChildren().remove(categoryCheckBoxes.get(i));
        }
        categoryCheckBoxes.clear();
        visualizeQuestionsSelector.getChildren().remove(spacer);
        visualizeQuestionsSelector.getChildren().remove(submitButton);
        for (int i = 0; i < db.getCategoriesNames().size(); i++)
        {
            CheckBox catCheckBox = new CheckBox("" + db.getCategoriesNames().get(i));
            categoryCheckBoxes.add(catCheckBox);
            categoryVBoxSelector.getChildren().add(catCheckBox);
        }
        categoriesScrollPane.setMaxHeight(200.0);
        categoriesScrollPane.setVisible(true);
        visualizeQuestionsSelector.getChildren().add(spacer);
        visualizeQuestionsSelector.getChildren().add(submitButton);


    }
    @FXML
    public void pswErorrRoutine() {
        confirmationDialog.setTitle("Brutte Notizie!");
        confirmationDialog.setHeaderText("Sembra che ci sia un errore nel leggere il file!");
        confirmationDialog.setContentText("Può essere che tu non abbia diritti sulla cartella in cui stai eseguendo il gioco, o che tu abbia cancellato la password. Premi ok se vuoi che proviamo a resettare la password a quella di default che trovi in tutorial!");
        if (confirmationDialog.showAndWait().get() == ButtonType.OK) {
            try {
                settings.resetPassword();
            } catch (IOException pswResetException) {
                confirmationDialog.setHeight(250);
                confirmationDialog.setTitle("Bruttissime Notizie!");
                confirmationDialog.setHeaderText("Sembra che ci sia stato un errore nel generare la password di default");
                confirmationDialog.setContentText("Verrai reindirizzato automaticamente alla Home di guiz e proveremo a risolvere il problema rigenerando le impostazioni di default, se i problemi dovessero persistere prova a spostare il gioco in una nuova cartella su cui sei certo di avere diritti di scrittura");
                confirmationDialog.showAndWait();
                goBack();
            }
        }
    }

    @FXML
    public void IODialog(Boolean fxml)
    {
        if(fxml)
        {
            exceptionDialog.setHeaderText("Mi dispiace...");
            exceptionDialog.setContentText("Sembra ci sia un problema ad accedere ad alcuni file fondamentali per l'esecuzione del gioco, ti consigliamo di effettuare un backup della cartella lib e riscaricarlo");
            exceptionDialog.showAndWait();
        }
        else
        {
            exceptionDialog.setHeaderText("Mi dispiace...");
            exceptionDialog.setContentText("Sembra che non possiamo accedere ad alcuni file, assicurati che i file della libreria non siano aperti in un altro programma o che siano in una cartella su cui hai pieno accesso");
            exceptionDialog.showAndWait();
        }

    }
    @FXML
    public void NullDialog(){
        exceptionDialog.setHeaderText("Mi dispiace...");
        exceptionDialog.setContentText("La domanda che stai provando ad eliminare appartiene ad una categoria contenente in un file il cui nome non è lo stesso della categoria. Puoi utilizzarla nel gioco, ma se vuoi poterla modificare o rimuovere devi rinominare il file affinché il nome sia lo stesso della categoria al suo interno");
        exceptionDialog.showAndWait();

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
                } catch (BadFileException reallyBadFile) {
                    exceptionDialog.setHeaderText("Mi dispiace...");
                    exceptionDialog.setContentText("Più di questo non potevamo fare");
                    exceptionDialog.showAndWait();
                }
            }
        }
    }
    @FXML
    public void badFileRoutine(String error, File offendingFile)
    {
        confirmationDialog.setHeaderText("C'è un errore di formattazione!");
        confirmationDialog.setContentText("Il file che stai provando ad importare ha un errore alla\\e linea\\e " + error + " assicurati che il file rispetti le regole di formattazione indicate nella sezione tutorial");
        boolean libFile = false;
        if (confirmationDialog.showAndWait().get() == ButtonType.OK)
        {
            try {
                settings.deleteBadQuestion(offendingFile, libFile);
            } catch (IOException ioEx) {
                IODialog(false);
            } catch (BadFileException reallyBadFile) {
                exceptionDialog.setHeaderText("Mi dispiace...");
                exceptionDialog.setContentText("Più di questo non potevamo fare");
                exceptionDialog.showAndWait();
            }
        }
    }



    @FXML
    public void onButtonClicked(ActionEvent e) {
        //Cambio psw
        if (e.getSource().equals(pswChangeButton)) //se decido di cambiare psw, abilito gli elementi per farlo, disabilito il bottone per il cambio
        {
            newPswLabel.setVisible(true);
            newPswField.setVisible(true);
            repeatPswLabel.setVisible(true);
            repeatPswField.setVisible(true);
            confirmChangeButton.setVisible(true);
            pswChangeButton.setVisible(false);
            pswErrorLabel.setVisible(true);
            pswErrorLabel.setText("Inserisci prima la vecchia password nel campo sopra, poi la nuova nei campi sottostanti");
            pswErrorLabel.setTextFill(Color.rgb(0, 0, 0));
        }
        //Conferma cambio psw
        else if (e.getSource().equals(confirmChangeButton)) //se clicco conferma passo i due campi nuova psw al metodo editPassword,se modifica la psw ritorna true e disattivo i bottoni
        {
            try {
                if (settings.editPassword(newPswField.getText(), repeatPswField.getText(), pswField.getText())) {
                    newPswLabel.setVisible(false);
                    newPswField.setVisible(false);
                    repeatPswLabel.setVisible(false);
                    repeatPswField.setVisible(false);
                    confirmChangeButton.setVisible(false);
                    pswChangeButton.setVisible(true);
                    pswErrorLabel.setVisible(false);
                    newPswField.setText("");
                    repeatPswField.setText("");
                    pswField.setText("");
                } else {
                    pswErrorLabel.setVisible(true);
                    pswErrorLabel.setText("Le password non corrispondono");
                    pswErrorLabel.setTextFill(Color.rgb(178, 34, 34));
                }
            } catch (IOException pswFile) {
                pswErorrRoutine();
            }


        }
        //Login
        else if (e.getSource().equals(loginButton)) {
            try {
                if (settings.login(pswField.getText())) {
                    settingsLoginScreen.setVisible(false); //disattivo il pane che contiene l'ui di login
                    visualizeQuestionsBox.setVisible(true); //attivo il pane che conterrà le impostazioni;
                    editQButton.setVisible(true);
                    deleteButton.setVisible(true);
                    exportQButton.setVisible(true);
                    importQButton.setVisible(true);
                    nuclearButton.setVisible(true);
                    createQuestionButton.setVisible(true);
                    refreshCategories();
                } else {
                    pswErrorLabel.setVisible(true);
                    pswErrorLabel.setText("Password errata");//MESSAGGIO DI ERRORE
                    pswErrorLabel.setTextFill(Color.rgb(178, 34, 34));//IMPOSTO IL COLORE DEL MESSAGGIO
                    pswField.setText("");
                    pswField.setStyle("-fx-control-inner-background:#ff3333");//  BACKGROUND USO COMANDO CSS
                }
            } catch (IOException pswException) {
                pswErorrRoutine();
            }
        }

    }
    @FXML
    public void tableViewSelected()
    {
        if(visualizeQuestionsTable.getSelectionModel().getSelectedItem() != null)
        {
            exportQButton.setDisable(false);
            deleteButton.setDisable(false);
            editQButton.setDisable(false);
        }

    }
    @FXML
    public void submitFilters()
    {
        //la prima parte del codice serve a determinare lo stato delle checkBox
        //per quello che riguarda le checkbox difficoltà e tipologia, essendo hardcoded si crea una lista con Stream.of e filtrandola.
        //per quelle categoria, essendo dinamiche, ciclo in maniera esplicita su ogni categoria creata
        List<CheckBox> selectedCategory = new ArrayList<>();
        List<CheckBox> selectedDifficulty = Stream.of(easyCheck, mediumCheck, hardCheck).filter(CheckBox::isSelected).collect(Collectors.toList());
        List<CheckBox> selectedType = Stream.of(multiQuestion, openQuestion, fillQuestion).filter(CheckBox::isSelected).collect(Collectors.toList());


        for (int i = 0; i < categoryCheckBoxes.size(); i++) {
            if (categoryCheckBoxes.get(i).isSelected()) {
                selectedCategory.add(categoryCheckBoxes.get(i));
            }
        }
        //fine prima parte
        try {
            questions.setAll(settings.fetchQuestions(selectedCategory, selectedDifficulty, selectedType)); //Aggiungo tutte le domande filtrate dall'utente in una ObservableList
        } catch (IOException io) {
            IODialog(false);
        } catch (BadFileException badFile) {
            badFileRoutine(badFile.getMessage());
        }


        ObservableList<DisplayQuestion> questionsToDisplay = FXCollections.observableArrayList();
        for (Question question : questions) {
            questionsToDisplay.add(new DisplayQuestion(question));
        }
        visualizeQuestionsTable.setItems(questionsToDisplay); // riempio la tableView con gli elementi filtrati dall'utente
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        correctAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));
        wrongAnswerColumn1.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer1"));
        wrongAnswerColumn2.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer2"));
        wrongAnswerColumn3.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer3"));
        visualizeQuestionsTable.getColumns().setAll(categoryColumn, difficultyColumn, typeColumn, questionColumn, correctAnswerColumn, wrongAnswerColumn1, wrongAnswerColumn2, wrongAnswerColumn3);
    }
    @FXML
    public void importQ()
    {
        confirmationDialog.setTitle("Importa domande");
        confirmationDialog.setHeaderText("Stai importando delle nuove domande dentro Guiz!");
        confirmationDialog.setContentText("Assicurati che il file che stai importando rispetti le regole che trovi nella sezione tutorial, se lo hai già fatto, buon divertimento con le tue nuove domande!");
        if (confirmationDialog.showAndWait().get() == ButtonType.OK) {
            File importFile = filePicker.showOpenDialog(new Stage());
            try {
                settings.importQuestions(importFile);
            } catch (IOException importIO) {
                IODialog(false);
            } catch (BadFileException importBadFile) {
                badFileRoutine(importBadFile.getMessage(), importFile);
            }
        }
        refreshCategories();
    }
    @FXML
    public void exportQ()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Esporta le domande");

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);


        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                settings.exportQuestions(questions, file);
            } catch (IOException exportIO) {
                IODialog(false);
            }
        }
    }
    @FXML
    public void createQ()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lib/Scenes/createQuestionDialog.fxml"));
        AddEditQuestionController addQ = new AddEditQuestionController(); //ne creo una nuova istanza a cui non passo nulla
        fxmlLoader.setController(addQ); //e la setto come controller dell'fxml
        try {
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException fxml) {
            IODialog(true);
        }
        refreshCategories();
    }
    @FXML
    public void deleteQ()
    {
        DisplayQuestion displayedQuestion = visualizeQuestionsTable.getSelectionModel().getSelectedItem();
        String type = displayedQuestion.getType();
        Question question = settings.convertDisplayedQuestion(displayedQuestion);
        try {
            settings.deleteQuestion(question);
        } catch (IOException IO) {
            IODialog(false);
        } catch (BadFileException deleteBad) {
            badFileRoutine(deleteBad.getMessage());
        } catch (NullPointerException categoriaNominataMale)
        {
            NullDialog();
        }

        refreshCategories(); //ogni volta che compio un'operazione sulle domande, rigenero le categorie e le checkBox relative
    }
    @FXML
    public void editQ ()
    {
        DisplayQuestion displayedQuestion = visualizeQuestionsTable.getSelectionModel().getSelectedItem();
        Question question = settings.convertDisplayedQuestion(displayedQuestion);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lib/Scenes/createQuestionDialog.fxml"));
        AddEditQuestionController editQ = new AddEditQuestionController(question); //in questo caso passo al controller anche la question
        fxmlLoader.setController(editQ);
        try{
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException fxml)
        {
            IODialog(true);
        }

        refreshCategories();
    }
    @FXML
    public void nuclearAction()
    {
        confirmationDialog.setHeaderText("Attenzione!");
        confirmationDialog.setContentText("Questa operazione cancellerà tutti i contenuti del gioco e lo riporterà al suo stato originale");
        Optional<ButtonType> choice = confirmationDialog.showAndWait();
        if(choice.get() == ButtonType.OK)
        {
            settings.nuclearButton();
            try
            {
                Parent newRoot = FXMLLoader.load(getClass().getResource("/lib/Scenes/main.fxml")); //tutti i nostri bottoni back, per fortuna, rendirizzano al menù principale
                nuclearButton.getScene().setRoot(newRoot);
            } catch (IOException fxml)
            {
                IODialog(true);
            }
        }

    }
    @FXML
    public void goBack()
    {
        try
        {
            Parent newRoot = FXMLLoader.load(getClass().getResource("/lib/Scenes/main.fxml")); //tutti i nostri bottoni back, per fortuna, rendirizzano al menù principale
            backButton.getScene().setRoot(newRoot);
        } catch(IOException fxml)
        {
            IODialog(true);
        }
    }

}




