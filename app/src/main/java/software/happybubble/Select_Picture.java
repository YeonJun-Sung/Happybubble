package software.happybubble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class Select_Picture extends AppCompatActivity {

    ArrayList<Select_List> list_picture = new ArrayList<Select_List>();
    ListView picture_listview;

    int[] image = {R.drawable.goo, R.drawable.o1, R.drawable.o2, R.drawable.g2, R.drawable.l, R.drawable.e};
    String[] image_name = {"G", "o", "o", "g", "l", "e"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__picture);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        picture_listview = (ListView)findViewById(R.id.listview_select_picture);

        Select_Picture_Adapter adapter = new Select_Picture_Adapter(this, R.layout.select_list, list_picture);

        for(int i = 0; i < image.length; i++){
            list_picture.add(new Select_List(image[i], image_name[i]));
        }

        picture_listview.setAdapter(adapter);
        picture_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("test",""+position);
                Intent intent = new Intent(getApplicationContext(), Select_Show_Picture.class);
                intent.putExtra("image", image[position]);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}