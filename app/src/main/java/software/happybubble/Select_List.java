package software.happybubble;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by SAMSUNG on 2017-09-20.
 */

public class Select_List extends AppCompatActivity{
    private String ImageID;
    private String Name;

    public Select_List(String Name){
        //this.ImageID = ImageID;
        this.Name = Name;
    }

    public String getImageID(){
        return ImageID;
    }
    public void setImageID(String ImageID){
        this.ImageID = ImageID;
    }
    public String getName(){
        return Name;
    }
    public void setName(String Name){
        this.Name = Name;
    }
}