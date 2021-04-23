package Notes;

import android.util.Log;

import com.example.my_notes.DatabaseAdapter;

import java.util.Date;


public class ImageNote extends Note {

    private String imagepath;
    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    public ImageNote(String folderId, String imagepath){
        super(folderId);
        this.imagepath = imagepath;
    }

    public ImageNote(String title, String folderId, String imagepath){
        super(title, folderId);
        this.imagepath = imagepath;
    }

    public ImageNote(String title, String id, String folderId, String owner,
                     Date creation, Date modify, String imagepath){
        super(title, folderId, id, owner, creation, modify);
        this.imagepath = imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void saveImageNote(){
        Log.d("saveImageNote", "adapter-> saveImageNote");
        adapter.saveImageNote(getTitle(), getId() , getFolderId() , getOwner(),
                getCreation_date(), getModify_date(), imagepath);
    }

}