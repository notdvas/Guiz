package guiz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class Main extends Application {
    private static Stage stage;
    public static Stage getStage() { return stage; }
    //se bisogna cambiare cosa è mostrato nello stage, bisogna cambiare la scene
    @Override
    public void start(Stage primaryStage) throws Exception{
        Font.loadFont(getClass().getResourceAsStream("/lib/Font/freshmarker.ttf"), 14);
        Parent root = FXMLLoader.load(getClass().getResource("/lib/Scenes/main.fxml"));
        primaryStage.setTitle("guiz");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(720);
        primaryStage.setMinWidth(1280);
        primaryStage.show();


    }


    public static void main(String[] args) {
        Application.launch(args);
    }

}
