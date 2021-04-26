package Notes;

import java.io.Serializable;
import java.util.ArrayList;

public class TextNote extends Note {

    //Atributs de la classe
    private String text;
    private ArrayList<String> images_adress;

    /**
     * Segon constructor de la classe.
     *
     * Rep com a parametre una cadena que serà el titol de la nota. Aquest titol es pasara
     * com a parametre al super constructor de la classe Note, i l'atribut text s'assignarà com
     * una cadena buida.
     *
     * @param title titol de la notas.
     */
    public TextNote(String title, String owner){
        super(title, owner);
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
