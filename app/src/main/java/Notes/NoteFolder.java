package Notes;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;

import Exceptions.NoteException;

//Haremos la clase serializable para poder guardarla en los datos.
public class NoteFolder implements Serializable {

    //Constants
    private final String DEFAULT_NAME = "Titol per defecte";
    private final String NOTE_NOT_FOUND = "La nota que vols eliminar no es troba a la carpeta";
    private final int DEFAULT_COLOR = Color.WHITE;

    //Atributs
    private ArrayList<Note> list;
    private String title;
    private int color;

    /**
     * Constructor principal de la classe. Inicialitzarà l'arrayList de notes on emmagatzarem les
     * notes i crearem una carpeta amb el titol per defecte.
     */
    public NoteFolder(){
        this.list = new ArrayList<>();
        this.title = this.DEFAULT_NAME;
        this.color = this.DEFAULT_COLOR;
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
        this.list = new ArrayList<>();
        this.title = title;
        this.color = this.DEFAULT_COLOR;
    }


    /**
     * Constructor de la clase.
     *
     * Rep un enter que s'asignara al color per poder afegir el color de fons
     * @param color Color que volem per a la carpeta
     */
    public NoteFolder(int color){
        this.list = new ArrayList<>();
        this.title = this.DEFAULT_NAME;
        this.color = color;
    }


    /**
     * Constructor de la classe.
     *
     * Rep com a parametres un titol per a la carpeta i despres un enter que servira per
     * assignar el color de la carpeta.
     *
     * @param title Titul de la carpeta.
     * @param color Color de la carpeta
     */
    public NoteFolder(String title, int color){
        this.list = new ArrayList<>();
        this.title = title;
        this.color = color;
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
     * Aquetsa funció permet recueprar el numero de notes que té la carpeta
     *
     * @return retorna el numero de notes que té l'usuari.
     */
    public int get_size(){
        return this.list.size();
    }


    /*Funciones*/


    /**
     * Aquesta funció permetrà afegir una nota a la llista.
     *
     * @param note Nota que volem afegir a la carpeta.
     */
    public void add_note(Note note){
        this.list.add(note);
    }


    /**
     * Aquesta funció ens permet eliminar una nota passant com a parametre la propia nota.
     *
     * @param note Nota que volem eliminar.
     * @throws NoteException Llença excepció en cas que la nota introduida no existeixi
     */
    public void remove_note(Note note) throws NoteException {
        boolean removed = this.list.remove(note);

        if (removed){
            System.out.println("Nota eliminada correctamente");
        }
        else{
            throw new NoteException(this.NOTE_NOT_FOUND);
        }
    }


    /**
     * Aquesta funció ens permet eliminar una nota passant com a parametre l'index de la nota.
     *
     * Utilitzarem la funció get_index passant el index per comprobar que l'index sigui vàlid.
     * Un cop tenim la nota, elimianrem la nota de la llista.
     *
     * @param index Index de la nota que volem eliminar
     * @throws NoteException En cas que l'index no sigui vàlid, saltarà l'excepció
     */
    public void remove_note(int index) throws NoteException{
        Note removed = this.get_index(index);

        if (removed == null){
            throw new NoteException (this.NOTE_NOT_FOUND);
        }
        else{
            this.list.remove(removed);
        }
    }


    /**
     * Aquesta funció permetrà obtenir una nota de la carpeta introduint un index.
     *
     * En cas que l'index no sigui vàlid, retornara null, i en cas que l'index sigui vàlid,
     * retornarà la nota.
     *
     * @param index Index de la nota que volguem obtenir
     * @return Nota seleccionada que ocupa l'index passat a parametre
     */
    public Note get_index(int index){
        if (index >= this.get_size() || index < 0){
            return null;
        }
        else{
            return this.list.get(index);
        }
    }


    /**
     * Aquesta funció ens permetrà ordenar la llista pels titols de les notes
     */
    public void sortList_by_title(){

    }


    /**
     * Aquesta funció ens permetrà ordenar la llsita de notes per la data de modificació
     */
    public void sortList_by_modifyDate(){

    }


    /**
     * Aquesta funció ens permetrà ordenar la llista de notes per la data de creació
     */
    public void sortList_by_CreationDate(){

    }


    /**
     * Aquesta funció ens permetrà buscar una nota introduitn el seu titol
     * @param title Titol de la nota que volem trobar.
     */
    public void search_by_title(String title){

    }
}