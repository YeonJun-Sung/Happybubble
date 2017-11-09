package software.happybubble;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;


public class ItemListView extends AppCompatActivity{
    private String Name;

    public ItemListView(String Name){ this.Name = Name; }
    public String getName(){ return Name; }
    public void setName(String Name){ this.Name = Name; }
}