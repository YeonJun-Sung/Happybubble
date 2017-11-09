package software.happybubble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SelectMode extends AppCompatActivity {
    Button album, camera, clipart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        album = (Button)findViewById(R.id.album);
        camera = (Button)findViewById(R.id.camera);
        clipart = (Button)findViewById(R.id.clipart);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"show album TODO",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"image lableing TODO",Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(getApplicationContext(), PictureProcessing.class);
                startActivity(intent);
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"open camera  TODO",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"get save picture  TODO",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"image lableing TODO",Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(getApplicationContext(), PictureProcessing.class);
                startActivity(intent);
            }
        });
        clipart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectClipart.class);
                startActivity(intent);
            }
        });
    }
}