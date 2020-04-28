package guiz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.*;
import java.util.ArrayList;

public class GameController
{
    @FXML
    private Label playerNameLabel, scoreLabel, questionLabel, questionAnsweredName, questionAnsweredScore, questionAnsweredResult, winnerName;
    @FXML
    private VBox multiQuestionBox,gameVBox,boxFillQuestion; // questa contiene i bottoni delle domande multiple
    @FXML
    private HBox openAnswerBox,boxLettere,rispostaButton, saltaTurnoBox, questionHBox; //questa contiene il textField della domanda aperta
    @FXML
    private GridPane singleQuestionResult, sceltaSpeciale, victoryPane; //questo va mostrato dopo aver risposto
    @FXML
    private Button nextPlayer, impostazioni,rispostaUno,rispostaDue,rispostaTre,rispostaQuattro,submitOpen, questionAnswredProgress, saltaTurnoButton, doublePointsButton, backButton; //questi sono entrambi child del gridPane ^ sopra
    //fra i button, mancano un button per fare il submit della openQuestion
    @FXML
    private TextField openAnswerTextField;
    @FXML
    private Button[] but  = new Button[26];
    @FXML
    private List<Button>  butRisp = new ArrayList();
    @FXML
    private TableView<Player> finalScoreTable;
    @FXML
    private TableColumn<Player, String> nameColumn, scoreColumn;

    private ArrayList<Player> players = new ArrayList<>();
    private ObservableList<Question> questions = FXCollections.observableArrayList();
    private ArrayList<Button> multiButtons = new ArrayList<>();
    private int playersNumber;
    private int maxRounds;
    private int round = 1;
    private int playingPlayer;
    private Random rand = new Random();
    private Question questionToShow;
    private int bottoniAggiunti=0;
    private int riempimentoErrore=0;
    public int scoreMultiplier = 1;
    public int roundWithMultiplier = 0;
    public int playerWhoStartedMultiplier = 0;



    public ArrayList<Player> getPlayers(){
        return  players;
    }

   /* public void setPlayers(ArrayList<Player> players){
        this.players=players;
    }*/

    public ObservableList<Question> getQuestions(){
        return  questions;
    }

  /*  public void setQuestions(ObservableList<Question> questions){

        this.questions=questions;
    }*/
      @FXML
      public void initialize()
      {
          setLettere();
      }
    // entry point del controller, quando entra comincia con questo
    public void setGame(ArrayList<Player> players,ObservableList<Question> questions, int rounds){
        this.players=players;
        this.questions=questions;
        this.maxRounds=rounds;
        this.playingPlayer = 0;
        this.playersNumber=(players.size());
        System.out.println(players.size());
        for (int i = 0; i < players.size(); i++)
        {
            System.out.println(players.get(i).getName() + i);
        }
        gameRoutine();
    }
    @FXML
    public void gameRoutine()
    {
         if(players.get(playingPlayer).getSaltaTurno())
        {
            System.out.println("il giocatore " + playingPlayer + " deve saltare il turno");
            players.get(playingPlayer).setSaltaTurno(false);
            progressGame();
        }
        else
        {
            if(players.get(playingPlayer).getStrike()==3)
            {
                showSpecialEvent();
            }
            System.out.println("il giocatore " + playingPlayer + " deve rispondere alla domanda");
            int n = rand.nextInt(questions.size());
            this.questionToShow = questions.get(n);
            setGiocatore();
            showQuestion(questionToShow);
            questions.remove(n);
        }

    }
    @FXML
    public void setGiocatore(){
        playerNameLabel.setText(players.get(playingPlayer).getName());
        playerNameLabel.setTextFill(players.get(playingPlayer).getPaintColor());
        scoreLabel.setText(players.get(playingPlayer).getScore()+ " Punti");
    }
    @FXML
    public void showSpecialEvent()
    {
        saltaTurnoBox.getChildren().clear();
        sceltaSpeciale.setVisible(true);
        players.get(playingPlayer).setStrike(0);
        if(playersNumber==1)
            saltaTurnoButton.setDisable(true);
    }
    @FXML
    public void saltaTurno() {
        for (Player player : players) {
            if (player != players.get(playingPlayer)) {
                Button but = new Button(player.getName());
                String coloreStringa = String.format("#%06x", player.getColor().getRGB() & 0x00FFFFFF);
                but.setStyle("-fx-background-color: " + coloreStringa);
                saltaTurnoBox.getChildren().add(but);
                but.setOnAction(e ->
                {
                player.setSaltaTurno(true);
                sceltaSpeciale.setVisible(false);
                    //ritorna un array di due stringhe , se le modifiche non vengono effetuaute ritornano due stringhe vuote
                });
                //aggiungo il bottone
            }
        }
    }
    @FXML
    public void finishGame()
    {
        double highScore = 0;
        Player winningPlayer = players.get(0);
        for (Player player : players)
        {
            if (player.getScore() > highScore)
            {
                highScore = player.getScore();
                winningPlayer = player;
            }
        }
        victoryPane.setVisible(true);
        winnerName.setText(winningPlayer.getName() + "\n HA VINTO! CON BEN \n" + highScore + " PUNTI");
        winnerName.setTextFill(winningPlayer.getPaintColor());
        players.remove(winningPlayer);
        ObservableList<Player> playersFinalScore = FXCollections.observableArrayList(players);
        finalScoreTable.setItems(playersFinalScore);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        multiQuestionBox.setVisible(false);
        openAnswerBox.setVisible(false);
        boxFillQuestion.setVisible(false);
        questionHBox.setVisible(false);
    }
    @FXML
    public void doubleScore()
    {
        this.scoreMultiplier = 2;
        this.roundWithMultiplier = round;
        this.playerWhoStartedMultiplier = playingPlayer;
        sceltaSpeciale.setVisible(false);
    }
    @FXML
    public void showQuestion(Question question)
    {
        questionLabel.setText(question.getQuestion());
        if(question instanceof MultiQuestion)
        {
            fillMultiButtons();
            MultiQuestion multi = (MultiQuestion) question ;
            openAnswerBox.setVisible(false);
            multiQuestionBox.setVisible(true);
            int n = rand.nextInt(multiButtons.size());
            multiButtons.get(n).setText(multi.getCorrectAnswer());
            multiButtons.remove(n);
            multiButtons.get(0).setText(multi.getWrongAnswer1());
            multiButtons.get(1).setText(multi.getWrongAnswer2());
            multiButtons.get(2).setText(multi.getWrongAnswer3());
            boxFillQuestion.setVisible(false);
            boxLettere.setVisible(false);
        }
        else if(question instanceof OpenQuestion)
        {
            //fillMultiButtons();
            boxFillQuestion.setVisible(false);
            boxLettere.setVisible(false);
            openAnswerBox.setVisible(true);
            multiQuestionBox.setVisible(false);
            openAnswerTextField.setVisible(true);
            openAnswerTextField.setText("");
            submitOpen.setVisible(true);

        }
        else
        {
            //riattivo le lettere
            for ( int i =  0  ; i <but.length ; i++)
                if (but[i].isDisable())
                    but[i].setDisable(false);
            // but.

            for ( int i =  0  ; i <butRisp.size() ; i++)
                butRisp.remove(i);

            boxFillQuestion.setVisible(true);
            openAnswerBox.setVisible(false);
            boxLettere.setVisible(true);

            setButRisposta(question);
            multiQuestionBox.setVisible(false);
            openAnswerTextField.setVisible(false);
            submitOpen.setVisible(false);
            // codice fillQuestion

        }
    }
    public void showQuestionResult(boolean correct)
    {
        singleQuestionResult.setVisible(true);
        questionAnsweredName.setText(players.get(playingPlayer).getName());
        questionAnsweredName.setTextFill(players.get(playingPlayer).getPaintColor());
        if(correct)
        {
            //questo if si accerta che in caso il multiplier non sia stato usato da chi lo ha attivato, e si è propagato per un round intero, non torni a chi lo ha attivato
            if(playingPlayer == playerWhoStartedMultiplier && round != roundWithMultiplier)
            {
                this.scoreMultiplier = 1;
            }
            double earnedPoints = questionToShow.setDiffScore(questionToShow.getDifficulty())*scoreMultiplier;
            players.get(playingPlayer).setScore(players.get(playingPlayer).getScore() + earnedPoints);
            players.get(playingPlayer).setStrike(players.get(playingPlayer).getStrike()+1);
            questionAnsweredScore.setVisible(true);
            questionAnsweredResult.setText("Ha risposto correttamente");
            questionAnsweredResult.setTextFill(Color.GREEN);
            questionAnsweredScore.setText("Ha guadagnato: \n" + earnedPoints + "Punti");
            //questo if, lo disabilita se chi lo ha attivato ne ha usufruito
            if(playingPlayer == playerWhoStartedMultiplier)
            {
                this.scoreMultiplier = 1;
            }
        }
        else
        {
            players.get(playingPlayer).setStrike(0);
            questionAnsweredScore.setVisible(false);
            questionAnsweredResult.setText("Ha sbagliato risposta!");
            questionAnsweredResult.setTextFill(Color.RED);
            System.out.println(players.get(playingPlayer).getStrike());
        }
    }
    @FXML
    public void progressGame()
    {
        singleQuestionResult.setVisible(false);
        Boolean lastRound = round == maxRounds;
        Boolean lastPlayerOfLastRound = lastRound && playingPlayer == (playersNumber -1);
        System.out.println(lastRound);
        if(!lastPlayerOfLastRound)
        {
            if(playingPlayer == (playersNumber -1))
            {
                System.out.println("è finito il round");
                this.playingPlayer = 0;
                gameRoutine();
                round++;
            }
            else
            {
                System.out.println("Il round continua ma cambio player");
                playingPlayer++;
                gameRoutine();
            }

        }
        else if (lastPlayerOfLastRound)
        {
            finishGame();
        }
    }

    //ogni volta che prendo un bottone random a cui dare la risposta corretta, lo rimuovo, quindi ogni volta che mostro una nuova multi lo riaggiungo
    @FXML
    public void fillMultiButtons()
    {
        multiButtons.clear();
        multiButtons.add(rispostaUno);
        multiButtons.add(rispostaDue);
        multiButtons.add(rispostaTre);
        multiButtons.add(rispostaQuattro);
    }
    public ArrayList<Integer> countChar(String str, String s)
    {
        char c=s.toLowerCase().charAt(0);
        ArrayList<Integer> count = new ArrayList();

        for(int i=0; i < str.length(); i++)
        {  // System.out.println(i);
            if(str.toLowerCase().charAt(i) == c){

                count.add(i);}
            /*if(str..charAt(0) == c){
                count.add(0);
            }*/
        }
        return count;
    }

    @FXML
    public void setLettere(){
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        String[] s = (new String(alphabet)).split("(?<=.)");

        for( int i = 0 ; i< s.length; i++) {
            // but[i].setText(s[i]);
            but[i] = new Button(s[i]);
            but[i].setOnAction(e -> {submitFill(e);
                //System.out.println(s.length);
            });
        }
        boxLettere.getChildren().addAll(but);
    }
    public int numeroButtoniInvisibili(){
        int count = 0;
        for ( int i = 0 ; i<butRisp.size() ; i++ )
            if (!butRisp.get(i).isVisible())
                count++;
        return count;
    }

    public void setButRisposta(Question question){

        bottoniAggiunti=0;
        rispostaButton.getChildren().clear();
        butRisp.clear();
        butRisp.removeAll(butRisp);
        String[] ary = question.getCorrectAnswer().split("");
        for(int i = 0 ; i <ary.length;i++ ){
            Button b1 = new Button();
            butRisp.add(b1);
            if(ary[i].equals(" "))
            {
                b1.setVisible(false);
            }
        }


        rispostaButton.getChildren().addAll(butRisp);

    }
    //Questo metodo è il punto di partenza, sceglie una domanda e la rimuove, imposta il giocatore e delega il mostrare la domanda

    //un unico metodo per mostrare le domande e le risposte

    //Questi sono eventHandler alla fine, ma gestiti dall'fxml
    @FXML
    public void submitOpenAnswer()
    {
        if(openAnswerTextField.getText().equalsIgnoreCase(questionToShow.getCorrectAnswer()))
        {
            showQuestionResult(true);

        }
        else{
            showQuestionResult(false);
        }

    }
    @FXML
    public void submitMulti(ActionEvent a)
    {
        Button e = (Button) a.getSource();
        if(e.getText().equalsIgnoreCase(questionToShow.getCorrectAnswer()))
        {
            showQuestionResult(true);
        }
        else {
            showQuestionResult(false);
        }
    }
    @FXML
    public void submitFill(ActionEvent a)
    {
        //Casto la stringa della risposta corretta
        String[] ary = questionToShow.getCorrectAnswer().split("");
        Button e = (Button) a.getSource();
        //toLowerCAse l'ho messo un po sempre in questa funzione perchè non funziona bene

        if(questionToShow.getCorrectAnswer().toLowerCase().contains(e.getText())) {
            //cre un'arraylist di int per ricevere gli indici delle domande
            ArrayList<Integer> posLettere = countChar(questionToShow.getCorrectAnswer().toLowerCase(), e.getText());
            for (int i = 0; i < posLettere.size(); i++) {
                //setto i bottoni con lettere della risposta
                butRisp.get(posLettere.get(i)).setText(e.getText());
                bottoniAggiunti++;

            }
        }else{
            riempimentoErrore++;
        }
        e.setDisable(true);
        if(questionToShow.getCorrectAnswer().length()<= bottoniAggiunti+numeroButtoniInvisibili() && riempimentoErrore <5)
        {
            showQuestionResult(true);
            riempimentoErrore=0;
        }
        else if(riempimentoErrore == 5)
        {
            showQuestionResult(false);
            riempimentoErrore=0;
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
            System.out.println("FXML missing, riscaricare il gioco");
        }
    }
    //il metodo progressGame si occupa di gestire il progresso del gioco, quindi solo cambiare giocatore, o cambiare round resettando il numero di giocatori
    //in caso non riesce a fare nessuna delle due cose, il gioco è finito, quindi chiama un metodo delegato alla gestione di fine partita

}
