package Notes;

import java.util.UUID;
import java.util.Date;

public class Note {

    //Constants
    private final String DEFAULT_TITLE = "Titol per defecte";

    //Atributs
    private String title, id, adress, owner, folderName;
    private Date creation_date, modify_date;

    //Falta añadir un DataBaseAdapter.


    /**
     * Constructor de la classe
     * No rep cap parametre i el titol de la nota s'assignarà com al titol per defecte de la classe
     * També es creen dues dates, una de creació i un altre de modificació.
     */
    public Note(String localPath, String owner, String folderName) {
        this.title = this.DEFAULT_TITLE;
        this.adress = localPath;
        this.owner = owner;
        this.folderName = folderName;
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
    public Note(String title, String localPath, String owner, String folderName) {
        this.title = title;
        this.adress = localPath;
        this.owner = owner;
        this.folderName = folderName;
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


    /**
     * Aquesta funció ens permetra recuperar el ID de la nota.
     * @return Cadena amb l'ID de la nota.
     */
    public String getId(){ return this.id; }


    /**
     * Aquesta funció ens permetrà modificar el valor del ID de la nota.
     * @param nID nou ID de la nota
     */
    private void setId(String nID){ this.id = nID; }


    /**
     * Aquesta funció ens permetrà recuperar l'adreça de la nota.
     * @return Cadena amb l'adreça de la nota.
     */
    public String getAdress(){ return this.adress; }


    /**
     * Aquesta funció ens permetrà recuperar el owner de la nota.
     * @return Cadena amb l'owner de la nota.
     */
    public String getOwner(){ return this.owner; }


    /**
     * Aquesta funció permet canaviar el nom del owner de la nota
     * @param nOwner nou owner.
     */
    public void setOwner(String nOwner){ this.owner = nOwner; }


    /**
     * Aquesta funció ens retorna el nom de la carpeta a la qual pertany
     * @return nom de la carpeta a la que pertany
     */
    public String getFolderName() { return this.folderName; }


    /**
     * Aquesta funció ens permet canviar el nom de la carpeta a la qual pertany la nota
     * @param folderName nou nom de la carpeta a la que pertany
     */
    public void setFolderName(String folderName){ this.folderName = folderName; }
}