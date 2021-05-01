package Notes;

import java.io.Serializable;
import java.util.Date;

import android.util.Log;

import com.example.my_notes.DatabaseAdapter;

public class AudioNote extends Note {
    private String adress;
    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    public AudioNote(String title, String localPath, String folderId){
        super(title, folderId);
        this.adress = localPath;
    }
    public AudioNote(String title,String id, String localPath, String folderId, String owner, Date creation, Date modify){
        super(title, folderId, id, owner, creation, modify);
        this.adress = localPath;
    }

    public String getAdress(){
        return this.adress;
    }

    public void saveAudioNote(){
        Log.d("saveAudioNote", "adapter-> saveAudioNote");
        adapter.saveAudioNoteWithFile(getId(), getTitle(), getOwner(), getFolderId(), getCreation_date(), getModify_date(), this.adress);
    }

    public void updateAudioNote(){
        Log.d("updateAudioNote", "adapter-> updateAudioNote");
        adapter.updateAudioNote(getId(), getTitle(), getOwner(), getFolderId(), getCreation_date(), getModify_date(), this.adress);
    }

    public void removeAudioNote(){
        Log.d("removeAudioNote", "adapter-> removeAudioNote");
        adapter.removeAudioNote(getId());
    }
}
