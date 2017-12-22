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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import java.io.IOException;
import java.io.InputStream;

public class SelectClipart extends AppCompatActivity {
    int img_idx = 0;
    Activity activity;
    AlertDialog.Builder alert_builder;
    Button draw;
    Drawable bg;
    RelativeLayout background;
    ArrayAdapter<String> adapter;
    String get_url;
    String[] file_name;
    Bitmap[] image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_clipart);
        Intent intent = getIntent();
        file_name = intent.getStringArrayExtra("fileName");
        get_url = intent.getExtras().getString("url");

        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);
        activity = this;
        adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_singlechoice);

        image = new Bitmap[file_name.length];
        try {
            for(int i = 0;i < file_name.length;i++){
                adapter.add(file_name[i].replace(".png",""));
                if(get_url.equals("resource")) {
                    AssetManager path = getResources().getAssets();
                    InputStream is;
                    is = path.open(file_name[i]);
                    image[i] = BitmapFactory.decodeStream(is);
                    is.reset();
                }
                else if(get_url.equals("cacheDir")) {
                    image[i] = BitmapFactory.decodeFile(getExternalCacheDir().getPath() + "/" + file_name[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button pre = (Button) findViewById(R.id.pre);
        Button next = (Button) findViewById(R.id.next);
        draw = (Button) findViewById(R.id.draw);
        background = (RelativeLayout) findViewById(R.id.bg_img);
        bg = new BitmapDrawable(image[0]);
        background.setBackground(bg);
        draw.setText("선택 : " + file_name[0].replace(".png",""));

        alert_builder = new AlertDialog.Builder(activity);
        alert_builder.setTitle("항목 중에 하나를 선택하세요.");
        alert_builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert_builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                img_idx = id;
                bg = new BitmapDrawable(image[img_idx]);
                background.setBackground(bg);
                draw.setText("선택 : " + file_name[img_idx].replace(".png",""));
            }
        });

        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_idx--;
                if (img_idx < 0) img_idx = image.length - 1;
                bg = new BitmapDrawable(image[img_idx]);
                background.setBackground(bg);
                draw.setText("선택 : " + file_name[img_idx].replace(".png",""));
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_idx++;
                if (img_idx >= image.length) img_idx = 0;
                bg = new BitmapDrawable(image[img_idx]);
                background.setBackground(bg);
                draw.setText("선택 : " + file_name[img_idx].replace(".png",""));
            }
        });
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PictureProcessing.class);
                intent.putExtra("url", get_url);
                intent.putExtra("img", file_name[img_idx]);
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
                alert_builder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}