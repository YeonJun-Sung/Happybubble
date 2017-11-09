package software.happybubble;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class SelectClipart extends AppCompatActivity {
    int imgIdx = 0;
    Activity activity;
    AlertDialog.Builder alertBuilder;
    Button draw;
    Drawable bg;
    RelativeLayout background;
    ArrayAdapter<String> adapter;

    String[] fileName = {"ascent.png", "bicycle priority road.png", "bicycle road.png", "changing lane-left.png"
            , "changing lane-right.png", "child protection area.png", "concession line.png", "concession.png"
            , "crosswalk.png", "derivation lane.png", "derivation-round.png", "derivation-square.png"
            , "derivation-triangle.png", "disabled person protection area.png", "Don't go straight.png"
            , "don't stop.png", "Don't turn left and go straight.png", "Don't turn left.png"
            , "Don't turn right and go straight.png", "Don't turn right and left.png", "Don't turn right.png"
            , "elderly protection area.png", "go straight or turn left.png", "go straight or turn right.png"
            , "go straight.png", "inclined parking.png", "lead wire.png", "parallel parking.png"
            , "right angle parking.png", "safety zone.png", "slowly-mark.png", "slowly-text.png"
            , "speed limit(child protection).png", "speed limit.png", "stop.png", "turn left or u-turn.png"
            , "turn left.png", "turn right.png", "U-turn prohibited.png", "U-turn.png", "unprotected turning.png"
            , "emoticon01.png", "emoticon02.png", "emoticon03.png", "emoticon04.png"
            , "emoticon05.png", "emoticon06.png", "emoticon07.png", "emoticon08.png"
            , "emoticon09.png", "emoticon10.png", "emoticon11.png", "emoticon12.png"
            , "emoticon13.png", "emoticon14.png", "emoticon15.png", "emoticon16.png", "test.png"};
    Bitmap[] image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_clipart);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        activity = this;
        adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_singlechoice);

        AssetManager path = getResources().getAssets();
        InputStream is;
        image = new Bitmap[fileName.length];
        try {
            for(int i = 0;i < fileName.length;i++){
                adapter.add(fileName[i].replace(".png",""));
                is = path.open(fileName[i]);
                image[i] = BitmapFactory.decodeStream(is);
                is.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button pre = (Button) findViewById(R.id.pre);
        Button next = (Button) findViewById(R.id.next);
        draw = (Button) findViewById(R.id.draw);
        background = (RelativeLayout) findViewById(R.id.bgImg);
        bg = new BitmapDrawable(image[0]);
        background.setBackground(bg);
        draw.setText("선택 : " + fileName[0].replace(".png",""));

        alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setTitle("항목 중에 하나를 선택하세요.");
        alertBuilder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertBuilder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        imgIdx = id;
                        Log.d("imgIdx : ", "" + imgIdx);
                        bg = new BitmapDrawable(image[imgIdx]);
                        background.setBackground(bg);
                        draw.setText("선택 : " + fileName[imgIdx].replace(".png",""));
                    }
                });

        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgIdx--;
                if (imgIdx < 0) imgIdx = image.length - 1;
                Log.d("imgIdx : ", "" + imgIdx);
                bg = new BitmapDrawable(image[imgIdx]);
                background.setBackground(bg);
                draw.setText("선택 : " + fileName[imgIdx].replace(".png",""));
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgIdx++;
                if (imgIdx >= image.length) imgIdx = 0;
                Log.d("imgIdx : ", "" + imgIdx);
                bg = new BitmapDrawable(image[imgIdx]);
                background.setBackground(bg);
                draw.setText("선택 : " + fileName[imgIdx].replace(".png",""));
            }
        });
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), fileName[imgIdx], Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), PictureProcessing.class);
                intent.putExtra("img", fileName[imgIdx]);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_clipart, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.search:
                alertBuilder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}