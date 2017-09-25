package software.happybubble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Select_Show_Picture extends AppCompatActivity {
    int get_position, get_image;
    ImageView show_image;
    TextView show_text;
    Button blue, red, yellow, black, draw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__show__picture);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        show_image = (ImageView)findViewById(R.id.show_image);
        show_text = (TextView) findViewById(R.id.show_text);
        blue = (Button)findViewById(R.id.blue);
        red = (Button)findViewById(R.id.red);
        yellow = (Button)findViewById(R.id.yellow);
        black = (Button)findViewById(R.id.black);
        draw = (Button)findViewById(R.id.draw);

        Intent intent = getIntent();
        get_position = intent.getExtras().getInt("position");
        get_image = intent.getExtras().getInt("image");

        show_image.setImageResource(get_image);
        Toast.makeText(getApplicationContext(),""+get_position,Toast.LENGTH_SHORT).show();

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_text.setText(blue.getText().toString());
            }
        });

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_text.setText(red.getText().toString());
            }
        });

        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_text.setText(yellow.getText().toString());
            }
        });

        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_text.setText(black.getText().toString());
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
