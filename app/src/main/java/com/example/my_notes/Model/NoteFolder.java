package com.example.my_notes.Model;

import android.graphics.Color;
import android.util.Log;

import com.example.my_notes.DatabaseAdapter;

import java.util.UUID;

public class NoteFolder {

    //Constants
    private final String DEFAULT_NAME = "Titol per defecte";
    private final String NOTE_NOT_FOUND = "La nota que vols eliminar no es troba a la carpeta";
    private final int DEFAULT_COLOR = Color.WHITE; //Color predeterminat que tindrà la carpeta.

    //Atributs
    private String title, id, owner;
    private int color;

    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    /**
     * Constructor principal de la classe. Inicialitzarà l'arrayList de notes on emmagatzarem les
     * notes i crearem una carpeta amb el titol per defecte.
     */
    public NoteFolder(){
        this.title = this.DEFAULT_NAME;
        this.color = this.DEFAULT_COLOR;
        this.owner = adapter.getCurrentUser();
        this.id = UUID.randomUUID().toString();
    }


    /**
     * Constructor de la classe.
     *
     * Inicialitzarà l'arraylist de notes on emmagatzemarem les notes i crearem una carpeta amb el
     * titol passat com a parametre.
     *
     * @param title titol de la carpeta
     */
    public NoteFolder(String title) {
        this.title = title;
        this.color = this.DEFAULT_COLOR;
        this.owner = adapter.getCurrentUser();
        this.id = UUID.randomUUID().toString();
    }


    /**
     * Constructor de la clase.
     *
     * Rep un enter que s'asignara al color per poder afegir el color de fons
     * @param color Color que volem per a la carpeta
     */
    public NoteFolder(int color){
        this.title = this.DEFAULT_NAME;
        this.color = color;
        this.owner = adapter.getCurrentUser();
        this.id = UUID.randomUUID().toString();
    }


    /**
     * Constructor de la classe.
     *
     * Rep com a parametres un titol per a la carpeta i despres un enter que servira per
     * assignar el color de la carpeta.
     *
     * @param title Títol de la carpeta.
     * @param color Color de la carpeta
     */
    public NoteFolder(String title, int color){
        this.title = title;
        this.color = color;
        this.owner = adapter.getCurrentUser();
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Constructor de la classe.
     *
     * Rep com a parametres un titol per a la carpeta i despres un enter que servira per
     * assignar el color de la carpeta.
     *
     * @param title Títol de la carpeta.
     * @param color Color de la carpeta
     * @param owner Owner de la carpeta
     * @param id Id de la carpeta
     */
    public NoteFolder(String title, String owner, int color, String id){
        this.title = title;
        this.color = color;
        this.owner = owner;
        this.id = id;
    }

    /*Funciones getters*/

    /**
     * Aquesta funcion retorna el titol de la carpeta.
     *
     * @return titol de la carpeta
     */
    public String get_Title(){ return this.title;}


    /**
     * Aquesta funció permetrà intercanviar el titol de les carpetes.
     *
     * @param title titol nou de la carpeta
     */
    public void set_title(String title){
        this.title = title;
    }

    /**
     * Aquesa funció ens permetrà recuperar l'enter del color de la carpeta.
     *
     * @return enter del color de la carpeta.
     */
    public int getColor(){
        return this.color;
    }


    /**
     * Aquesta funció ens permetrà canviar el color de la carpeta.
     *
     * @param nColor Enter del cou color.
     */
    public void setColor(int nColor){
        this.color = nColor;
    }


    /**
     * Aquesta funció ens retornarà el ID de la carpeta.
     * @return Cadena amb el Id de la carpeta.
     */
    public String getId(){ return this.id; }


    /**
     * Aquesta funció ens retornarà una cadena amb el owner de la nota.
     * @return Cadena amb el owner de la carpeta.
     */
    public String getOwner(){ return this.owner; }


    /**
     * Aquesta funció ens permetrà modificar el owner de la carpeta, rebent com a parametre
     * el nou owner.
     * @param owner nou owner de la carpeta.
     */
    public void setOwner(String owner){ this.owner = owner; }


    /*Funciones*/

    public void saveFolder(){
        Log.d("saveFolder", "adapter-> saveFolder");
        adapter.saveFolder(this.title, this.id, this.owner, this.color);
    }

    public void removeFolder(){
        Log.d("removeFolder", "adapter-> removeFolder");
        adapter.deleteFolder(this.id);
    }

    public void updateFolder(){
        Log.d("updateFolder", "adapter-> updateFolder");
        adapter.updateFolder(this.title, this.id, this.owner, this.color);
    }
}
