package guiz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.awt.Color;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.geometry.*;
import java.util.ArrayList;
import java.util.Optional;


public class CreatePlayersController {


    @FXML
    private Button aggiungiGiocatore,confermaGiocatori;
    @FXML
    private TextField nomeField;
    @FXML
    private ComboBox colorPicker;
    @FXML
    private Label messageLabel;
    @FXML
    private VBox playerButtons;

    //ho usato questa solo per passarla all'altro controller
    private int giocatori;
    private static  ArrayList<Player> players = new ArrayList<>();
    private Alert errorDialog = new Alert(Alert.AlertType.ERROR);
    private static boolean ok=false;


    @FXML
    public void initialize() {
        players.clear();
        colorPicker.getItems().addAll("Blu", "Verde", "Rosso", "Giallo", "Arancione", "Bianco", "Nero", "Rosa");
        colorPicker.getSelectionModel().selectFirst();
        confermaGiocatori.setDisable(true);

    }
    @FXML
    public void nullPointer(){
        errorDialog.setHeaderText("ATTENTO");
        errorDialog.setContentText("Ci sono problemi con l'inserimento del colore!");
        errorDialog.showAndWait();
    }
    //ho usato questo metodo per passare a questo controller
    public void setPartita(int giocatori) {
        this.giocatori = giocatori;

    }
    public static ArrayList<Player> getPlayers(){
        return players;
    }

    //Qua devo provare con più di 4-5 giocatori , ora provo ,
    //forse non va qua questo ma intanto lo lascio qua, si piò mettere in player...
    //quando viene aggiunto un giocatore nell'arrayList viene aggiunto un bottone premendo su questo si possono modificare i giocatori
    @FXML
    public void addPlayerButton(ArrayList<Player> player) {
        //fa questa operazione solo quando viene aggiunto un elemento all'arraylist
        for (int i = player.size() - 1; i < player.size(); i++) {
            Button but = new Button(player.get(i).getName());
            but.setMinWidth(playerButtons.getPrefWidth());

            //ha bisogno di una variabile final
            final int a = i;
            //converto in colore esadecimale
            String coloreStringa = String.format("#%06x", player.get(i).getColor().getRGB() & 0x00FFFFFF);
            //assegno al bottone il colore
            but.setStyle("-fx-background-color: " + coloreStringa);
            //quando viene premuto sul bottone parte questo metodo
            but.setOnAction(e -> {
                String modifiche[] = modificaGiocatore(a);
                //ritorna un array di due stringhe , se le modifiche non vengono effetuaute ritornano due stringhe vuote

                //in caso la stringa NON fosse vuota (cioè non viene fatto nulla)
                if (!modifiche[0].equals(""))
                    but.setText(modifiche[0]);
                // in caso non fosse settato nessun colore , non fa nulla altrimenti cambia il colore
                if (!modifiche[1].equals("")) {
                    String colore = String.format("#%06x", Player.returnColor(modifiche[1]).getRGB() & 0x00FFFFFF);
                    but.setStyle("-fx-background-color: " + colore);

                }

            });

            //aggiungo il bottone
            playerButtons.getChildren().add(but);

        }

    }

    //Un metodo per creare una schermata di dialogo e in caso modificare i giocatori
    //ho trovato questo su internet , non volevo creare un'altra classe
    public String[] modificaGiocatore(int i) {
        //creazione del dialogo
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Modifica Giocatore");
        dialog.setHeaderText("Per modifiche o per eliminare");
        ButtonType modificaGiocatori = new ButtonType("Modifica", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(modificaGiocatori, ButtonType.CANCEL);
        //setto l'interfaccia dialogo
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nome = new TextField();
        nome.setPromptText(players.get(i).getName());
        ComboBox combo = new ComboBox();
        //la comboBox prende gli elementi dalla comboBox ColorPicker
        combo.setItems(colorPicker.getItems());
        //aggiungo alla combo Box il colore scelto del player(i)
        combo.getSelectionModel().select(Player.returnNameColor(players.get(i).getColor()));

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nome, 1, 0);
        grid.add(new Label("Colore:"), 0, 1);
        grid.add(combo, 1, 1);
        dialog.getDialogPane().setContent(grid);

        //nel caso venga premuto sul bottone della modifica
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == modificaGiocatori) {
                return new Pair<>(nome.getText(), combo.getSelectionModel().getSelectedItem().toString());
            }
            return null;
        });


        Optional<Pair<String, String>> result = dialog.showAndWait();
        //dichiaro una stringa vuota
        String[] a = {"", ""};
        //nel caso si prema su modifica
        result.ifPresent(e -> {
            //aggiungo al colorPicker il colore appena cambiato
            colorPicker.getItems().add(Player.returnNameColor(players.get(i).getColor()));
            //se le modifiche sono state apportate chiama il metodo che modifica l'arrayList
            confirmModifica(nome.getText(), combo.getSelectionModel().getSelectedItem().toString(), i);
            //assegno all'array di stringhe i valori
            a[0] = nome.getText();
            a[1] = combo.getSelectionModel().getSelectedItem().toString();
            //rimuovo dal colorPicker il colore appena scelto
            colorPicker.getItems().remove(a[1]);


        });
        //rimuovo la comboBox ogni volta, cosi ogni volta la rigenera in base al colorPicker
        grid.getChildren().remove(combo);
        //ritorna l'array di Stringhe (con stringhe vuote in caso non è stato premuto il bottone Modifica)
        // nel caso fosse premuto ritorna le stringhe modificate
        return a;
    }
    public static boolean getOk(){
        return ok;
    }

    public void confirmModifica(String nome, String coloreStringa, int i) {
        //nel caso si fosse premuto modifica ma per esempio è stato cambiato solo il colore , non modifica il nome
        if (!nome.equals(""))
            players.get(i).setName(nome);
        //in caso il colore modificato e il colore del giocatore non coincidono
        //In questo caso ,se si vuole modificare solo il colore e il nome
        Color colore = players.get(i).getColor();
        if (!coloreStringa.equals(Player.returnNameColor(colore)))
            //nel caso in cui il colore sia diverso lo modifico
            players.get(i).setColor(Player.returnColor(coloreStringa));
    }

    @FXML
    public void onButtonClicked(ActionEvent e) {
        if (e.getSource().equals(aggiungiGiocatore)) {
            //nel caso non fosse stato inserito nulla e nel caso nel fosse nullo il colore
            //forse comunque va il try con il nullPointer
            try {
                if (!nomeField.getText().isEmpty() && !(colorPicker.getSelectionModel().getSelectedItem().toString().equals(null))) {


                    String nome = nomeField.getText();
                    //funzione per controllare che il nome non sia già stato scelto da altri giocatori

                    String color = colorPicker.getSelectionModel().getSelectedItem().toString();
                    //ho fatto un metodo molto stupido per gestire i colori
                    Color c1 = Player.returnColor(color);
                    Player p1 = new Player(nome, c1);
                    players.add(p1);
                    addPlayerButton(players);
                    colorPicker.getItems().remove(color);
                    int mancanti = giocatori - players.size();
                    if (mancanti == 0) {
                        aggiungiGiocatore.setDisable(true);
                        confermaGiocatori.setDisable(false);
                        colorPicker.setDisable(true);
                        nomeField.setDisable(true);
                    }
                    messageLabel.setText("Aggiungi ancora " + mancanti + " gicatori");
                    nomeField.setText("");
//                nomeField.setText("");

                }

                else {
                    errorDialog.setTitle("Attento");
                    errorDialog.setHeaderText("Si e' verificato un errore nell'insermiento");
                    errorDialog.setContentText(" Riprova!");
                    errorDialog.showAndWait();
                }
            } catch (NullPointerException n) {
                nullPointer();
            }

        }
        else if(e.getSource().equals(confermaGiocatori)) {
            ok = true;
            Stage stage = (Stage) confermaGiocatori.getScene().getWindow();
            stage.close();
        }
    }
}
