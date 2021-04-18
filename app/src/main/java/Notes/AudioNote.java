package Notes;

public class AudioNote extends Note {

    private String filename = null;

    public AudioNote(String localPath, String owner){
        super(localPath, owner);
    }


    public AudioNote(String title, String localPath, String owner){
        super(title, localPath, owner);
    }
}
