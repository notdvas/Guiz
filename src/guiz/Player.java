package guiz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Player {


    private ObservableList<Question> questions = FXCollections.observableArrayList();
    private Alert errorDialog = new Alert(Alert.AlertType.ERROR);

    private String name;
    private Color color;
    private Double score;
    private int strike;
    private boolean saltaTurno;

    public Player (String name, Color color, Double score, int strike,boolean saltaTurno)
    {
        this.name = name;
        this.color = color;
        this.score = score;
        this.strike = strike;
    }
    public Player(String nome, Color color){
        this.name=nome;
        this.color=color;
        this.score=0.0;
        this.strike=0;
        this.saltaTurno=false;
    }
    public String getName()
    {
        return name;
    }
    public Color getColor()
    {
        return color;
    }
    public Double getScore()
    {
        return score;
    }
    public int getStrike() {
        return strike;
    }
    public boolean getSaltaTurno()
    {
        return saltaTurno;
    }
   /* public Color getColoreDue(){
        return new Color((color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
    }*/
   public javafx.scene.paint.Color getPaintColor(){
       java.awt.Color awtColor = color;
       int r = awtColor.getRed();
       int g = awtColor.getGreen();
       int b = awtColor.getBlue();
       int a = awtColor.getAlpha();
       double opacity = a / 255.0 ;
       javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(r, g, b, opacity);
       return fxColor;
   }
    public void setName(String name)
    {
        this.name = name;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setStrike(int strike) {
        this.strike = strike;
    }
    public void setSaltaTurno(boolean saltaTurno) {
        this.saltaTurno = saltaTurno;
    }
    public String toString(){
        return "Il nome e'"+name+"il colore e'"+returnNameColor(color);
    }


    public static Color returnColor(String colore){
        Color c1=null;
        if(colore.equals("Blu"))
            c1=Color.blue;
        else if(colore.equals("Verde"))
            c1=Color.green;
        else if(colore.equals("Rosso"))
            c1=Color.red;
        else if(colore.equals("Giallo"))
            c1=Color.yellow;
        else if(colore.equals("Arancione"))
            c1=Color.orange;
        else if(colore.equals("Bianco"))
            c1=Color.white;
        else if(colore.equals("Nero"))
            c1=Color.black;
        else  if(colore.equals("Rosa"))
            c1=Color.pink;

        return  c1;

    }
    public static String returnNameColor(Color color){
        String nomeColore="";
        if(color.getRGB()==Color.blue.getRGB())
            nomeColore="Blu";
        else if(color.getRGB()==Color.green.getRGB())
            nomeColore="Verde";
        else if(color.getRGB()==Color.red.getRGB())
            nomeColore="Rosso";
        else if(color.getRGB()==Color.yellow.getRGB())
            nomeColore="Giallo";
        else if(color.getRGB()==Color.orange.getRGB())
            nomeColore= "Arancione";
        else if(color.getRGB()==Color.white.getRGB())
            nomeColore="Bianco";
        else if(color.getRGB()==Color.black.getRGB())
            nomeColore="Nero";
        else  if(color.getRGB()==Color.pink.getRGB())
            nomeColore="Rosa";

        return  nomeColore;

    }
}
