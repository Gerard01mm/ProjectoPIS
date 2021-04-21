package Notes;

import android.graphics.Color;
import android.util.Log;

import com.example.my_notes.DatabaseAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import Exceptions.NoteException;

public class NoteFolder {

    //Constants
    private final String DEFAULT_NAME = "Titol per defecte";
    private final String NOTE_NOT_FOUND = "La nota que vols eliminar no es troba a la carpeta";
    private final int DEFAULT_COLOR = Color.WHITE; //Color predeterminat que tindrà la carpeta.

    //Atributs
    private ArrayList<Note> list;
    private String title, id, owner;
    private int color;

    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    /**
     * Constructor principal de la classe. Inicialitzarà l'arrayList de notes on emmagatzarem les
     * notes i crearem una carpeta amb el titol per defecte.
     */
    public NoteFolder(){
        this.list = new ArrayList<>();
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
        this.list = new ArrayList<>();
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
        this.list = new ArrayList<>();
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
     * @param id Id de la carpeta
     */
    public NoteFolder(String title, int color, String id){
        this.list = new ArrayList<>();
        this.title = title;
        this.color = color;
        this.owner = adapter.getCurrentUser();
        this.id = id;
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
        this.list = new ArrayList<>();
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
     * Aquetsa funció permet recueprar el numero de notes que té la carpeta
     *
     * @return retorna el numero de notes que té l'usuari.
     */
    public int get_size(){
        return this.list.size();
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
