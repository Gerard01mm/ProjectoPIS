package Notes;

import java.util.UUID;
import java.util.Date;

public class Note {

    //Constants
    private final String DEFAULT_TITLE = "Titol per defecte";

    //Atributs
    private String title, id, adress, owner;
    private Date creation_date, modify_date;

    //Falta añadir un DataBaseAdapter.


    /**
     * Constructor de la classe
     * No rep cap parametre i el titol de la nota s'assignarà com al titol per defecte de la classe
     * També es creen dues dates, una de creació i un altre de modificació.
     */
    public Note(String localPath, String owner) {
        this.title = this.DEFAULT_TITLE;
        this.adress = localPath;
        this.owner = owner;
        this.id = UUID.randomUUID().toString();
        this.creation_date = new Date();
        this.modify_date = new Date();
    }


    /**
     * Constructor de la classe
     * Rep copm a parametre una cadena que serà elt itol de la nota.
     * EL titol s'assigna al parametre que es passa, i es crea dues dates, una de modificació
     * i un altre data de creació.
     *
     * @param title Titol de la nota
     */
    public Note(String title, String localPath, String owner) {
        this.title = title;
        this.adress = localPath;
        this.owner = owner;
        this.id = UUID.randomUUID().toString();
        this.creation_date = new Date();
        this.modify_date = new Date();
    }


    //Metodes getters i setters


    /**
     * Metode que ens permetra obtenir el titol de la nota.
     *
     * @return Titol de la nota
     */
    public String getTitle() {
        return this.title;
    }


    /**
     * Aquesta funció ens permetrà modificar el titol de la nota.
     * Donat que hem modificat la nota, es modificarà la data de modificació.
     *
     * @param title Nou titol de la nota
     */
    public void setTitle(String title) {
        this.title = title;
        this.modify_date = new Date();
    }


    /**
     * Aquesta funció ens permetra mostrar la data de creació de la nota.
     *
     * @return data de creació de la nota
     */
    public Date getCreation_date() {
        return this.creation_date;
    }


    /**
     * Aquesta funció ens permetrà mostrar la data de modificació de la nota.
     *
     * @return data de modificació de la nota.
     */
    public Date getModify_date() {
        return this.modify_date;
    }


    public String getId(){ return this.id; }


    private void setId(String nID){ this.id = nID; }


    public String getAdress(){ return this.adress; }


    public String getOwner(){ return this.owner; }
}