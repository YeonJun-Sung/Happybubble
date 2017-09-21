package software.happybubble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Select_Show_Picture extends AppCompatActivity {
    int get_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__show__picture);

        Intent intent = getIntent();
        get_position = intent.getExtras().getInt("Position");
        Toast.makeText(getApplicationContext(),""+get_position,Toast.LENGTH_SHORT).show();
    }
}
