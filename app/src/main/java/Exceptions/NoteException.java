package Exceptions;

public class NoteException extends Exception {

    /**
     * Constructor principal de la classe
     *
     * Aquest constructor s'encarregarà de crear una excepcio cridant al super cosntructor de la
     * classe Exception passant com a parametre el missatge per defecte.
     */
    public NoteException (){
        super("S'ha produit un error");
    }


    /**
     * Cosntructor de la classe
     *
     * Aquest cosntructor rep com a parametre un missatge de error que serà apssat al super
     * constructor de la classe exception.
     *
     * @param error Missatge d'error
     */
    public NoteException(String error){
        super(error);
    }
}
