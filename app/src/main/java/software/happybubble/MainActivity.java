package software.happybubble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button rc = (Button) findViewById(R.id.rc);
        Button cv = (Button) findViewById(R.id.opencv);
        rc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
        select_button = (Button)findViewById(R.id.select_picture);
        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Select_Picture.class);
                startActivity(intent);
            }
        });

        /*
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        */
    }

            }
        });
        cv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ImageProcessingActivity.class);
                startActivity(intent);
            }
        });
    }
}
