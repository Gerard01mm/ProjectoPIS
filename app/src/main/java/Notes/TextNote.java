package Notes;

import java.util.ArrayList;

public class TextNote extends Note {

    //Atributs de la classe
    private String text;
    private ArrayList<String> images_adress;


    /**
     * Constructor de la classe.
     *
     * No rep cap parametre, crida al super constructor de la classe.
     *
     */
    public TextNote(String localPath, String owner){
        super(localPath, owner); //Cridem al superconstructor sense parametres.
        this.text = ""; //Inicialitzem la cadena de text a 0
        //this.images_adress = new ArrayList<>(); //Inicialitzem l'arrayLiast de direccions de les fotos
    }


    /**
     * Segon constructor de la classe.
     *
     * Rep com a parametre una cadena que serà el titol de la nota. Aquest titol es pasara
     * com a parametre al super constructor de la classe Note, i l'atribut text s'assignarà com
     * una cadena buida.
     *
     * @param title titol de la notas.
     */
    public TextNote(String title, String localPath, String owner){
        super(title, localPath, owner);
        this.text = "";
    }


    /**
     * Aquest mètode ens permet modificar el contingut del text de la nota.
     *
     * Rep com a parametre el nou text que s'assignarà al atribut text de la classe.
     *
     * @param text nou text que voldrem emmagatzemar.
     */
    public void setText(String text){
        this.text = text;
    }


    /**
     * Aquesta funció ens permetrà recuperar el contingut del text de la nota.
     *
     * @return String text de la nota.
     */
    public String getText(){
        return this.text;
    }
}
