package guiz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AddEditQuestionController {
    @FXML
    private TextField userQuestion, userCorrectAnswer, userWrongAnswer1, userWrongAnswer2, userWrongAnswer3, userCreatedCategory;
    @FXML
    private Button confirmCreateFilters, confirmCreateQuestion, cancelCreateQuestion;
    @FXML
    private ComboBox categoryPicker, difficultyPicker, typePicker;
    @FXML
    Alert alert = new Alert(Alert.AlertType.ERROR);

    private static Settings settings = Settings.getInstance();
    private static QuestionsDatabase db = QuestionsDatabase.getInstance();

    private Question questionToEdit;
    private Boolean editBool = false;
    public AddEditQuestionController(Question question)
    {
        this.questionToEdit = question;
        editBool = true;
    }
    public AddEditQuestionController(){
    }

    @FXML
    public void initialize() //Initialize viene chiamato appena attivo il controller, una sola volta. mi creo gli elementi di UI di cui ho bisogno.
    {
        userQuestion.setDisable(false);
        userCorrectAnswer.setDisable(false);
        categoryPicker.setItems(db.getCategoriesNames());
        categoryPicker.getItems().add("Nuova");
        difficultyPicker.getItems().addAll("Facile", "Media", "Difficile");
        typePicker.getItems().addAll("Multipla", "Aperta", "Riempimento");
        alert.setTitle("Attenzione");
        if (editBool){
            userQuestion.setText(questionToEdit.getQuestion());
            userCorrectAnswer.setText(questionToEdit.getCorrectAnswer());
            categoryPicker.getSelectionModel().select(questionToEdit.getCategory());
            difficultyPicker.getSelectionModel().select(questionToEdit.getDifficulty());
            typePicker.getSelectionModel().select(questionToEdit.getType());
            if (questionToEdit instanceof MultiQuestion)
            {
                userWrongAnswer1.setVisible(true);
                userWrongAnswer1.setText(((MultiQuestion) questionToEdit).getWrongAnswer1());
                userWrongAnswer2.setVisible(true);
                userWrongAnswer2.setText(((MultiQuestion) questionToEdit).getWrongAnswer2());
                userWrongAnswer3.setVisible(true);
                userWrongAnswer3.setText(((MultiQuestion) questionToEdit).getWrongAnswer3());
            }
        }

//
    }
    @FXML
    public void onExitButton(ActionEvent e)
    {
        Stage stage = (Stage) cancelCreateQuestion.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void confirmButton()
    {
        if(selectedFilters()) //se ho selezionato i filtri, controllo di aver riempito i campi
        {
            if(!typePicker.getSelectionModel().getSelectedItem().toString().equals("Multipla") && !userQuestion.getText().isEmpty() && !userCorrectAnswer.getText().isEmpty())
            { //se la domanda NON è multipla, controllo i campi
                if(!editBool)
                {
                    createQuestion();
                }
                else {
                    editQuestion();
                }
            }
            else if (!userQuestion.getText().isEmpty() && !userCorrectAnswer.getText().isEmpty() && !userWrongAnswer1.getText().isEmpty() && !userWrongAnswer2.getText().isEmpty() && !userWrongAnswer3.getText().isEmpty())
            { //se la domanda invece è multipla, controllo tutti i campi, anche quelli delle risposte sbagliate
                if (!editBool)
                {
                    createQuestion();
                }
                else
                {
                    editQuestion();
                }
            }
            else { //se non entro in nessuno degli if sopra, evidentemente c'è un errore
                alert.setContentText("Assicurati di aver riempito tutti i campi!");
                alert.showAndWait();
            }
        }
        else
        {
            alert.setContentText("Assicurati di aver selezionato tutti i filtri!");
            alert.showAndWait();
        }
    }
    @FXML
    public void typePickerAction()
    {
        if (typePicker.getSelectionModel().getSelectedIndex() == 1 || typePicker.getSelectionModel().getSelectedIndex() == 2)
        {
            userWrongAnswer1.setDisable(true);
            userWrongAnswer2.setDisable(true);
            userWrongAnswer3.setDisable(true);
        }
        else if (typePicker.getSelectionModel().getSelectedIndex() == 0)
        {
            userWrongAnswer1.setDisable(false);
            userWrongAnswer2.setDisable(false);
            userWrongAnswer3.setDisable(false);
        }
    }
    @FXML
    public void categoryPickerAction()
    {
        try{
            if(categoryPicker.getSelectionModel().getSelectedItem().equals("Nuova"))
            {
                userCreatedCategory.setDisable(false);
            }
            else
            {
                userCreatedCategory.setDisable(true);
            }
        } catch (NullPointerException nullP)
        {
            //questa excepetion è innocua
        }

    }

    public void createQuestion() //prendo tutti i dati inseriti dall'utente e creo un nuovo obj Question
    {
        Boolean regeneratePicker = false;
        String category;
        String difficulty = difficultyPicker.getSelectionModel().getSelectedItem().toString();
        String type = typePicker.getSelectionModel().getSelectedItem().toString();
        String question = userQuestion.getText();
        String correctAnswer = userCorrectAnswer.getText();
        String wrongAnswer1 = (userWrongAnswer1.getText());
        String wrongAnswer2 = (userWrongAnswer2.getText());
        String wrongAnswer3 =(userWrongAnswer3.getText());
        if (!categoryPicker.getSelectionModel().getSelectedItem().equals("Nuova"))
        {
            category = categoryPicker.getSelectionModel().getSelectedItem().toString();
        }
        else
        {
            category = userCreatedCategory.getText();
            regeneratePicker = true;

        }
        try {
            if (typePicker.getSelectionModel().getSelectedItem().equals("Multipla"))
            {
                Question ql = new MultiQuestion(category, difficulty, type, question, correctAnswer, wrongAnswer1, wrongAnswer2, wrongAnswer3);
                settings.createQuestion(ql); //passo tutto a createQuestion in settings che si occupa di scrivere la domanda nel relativo file
            }
            else if(typePicker.getSelectionModel().getSelectedItem().equals("Aperta"))
            {
                Question ql = new OpenQuestion(category, difficulty, type, question, correctAnswer);
                settings.createQuestion(ql); //passo tutto a createQuestion in settings che si occupa di scrivere la domanda nel relativo file
            }
            else
            {
                Question ql = new FillQuestion(category, difficulty, type, question, correctAnswer);
                settings.createQuestion(ql); //passo tutto a createQuestion in settings che si occupa di scrivere la domanda nel relativo file
            }
        }catch (IOException e) {
            alert.setTitle("Brutte notizie");
            alert.setContentText("Sembra che ci siano problemi nella lettura o scrittura del file");
            alert.showAndWait();
        }

        userWrongAnswer1.clear();
        userWrongAnswer2.clear();
        userWrongAnswer3.clear();
        userCorrectAnswer.clear();
        userQuestion.clear();
        if (regeneratePicker)
        {
            categoryPicker.getItems().clear();
            db.generateCategories();
            categoryPicker.setItems(db.getCategoriesNames());
            categoryPicker.getItems().add("Nuova");
            categoryPicker.getSelectionModel().select(userCreatedCategory.getText());
            userCreatedCategory.clear();
        }
        //per finire pulisco i campi
    }
    public void editQuestion() //Gestire i typePicker
    {
        try
        {
            createQuestion(); //questo aggiunge la domanda
            settings.deleteQuestion(questionToEdit);
        } catch (QuestionsDatabase.BadFileException badFile)
        {
            alert.setTitle("Brutte notizie");
            alert.setContentText(badFile.getMessage());
            alert.showAndWait();
        } catch (IOException e)
        {
            alert.setTitle("Brutte notizie");
            alert.setContentText("Sembra che ci siano problemi nella lettura o scrittura del file");
            alert.showAndWait();

        }
    }

    public boolean selectedFilters()
    {
        if(categoryPicker.getSelectionModel().getSelectedIndex() < 0 && typePicker.getSelectionModel().getSelectedIndex() < 0 && difficultyPicker.getSelectionModel().getSelectedIndex() < 0)
        {
            return false;
        }
        else{
            if(categoryPicker.getSelectionModel().getSelectedItem().equals("Nuova"))
            {
                if(userCreatedCategory.getText().isEmpty())
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }

        }
    }
}
