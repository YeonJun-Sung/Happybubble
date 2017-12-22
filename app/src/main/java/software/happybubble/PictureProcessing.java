package software.happybubble;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PictureProcessing extends AppCompatActivity {
    Bitmap input_image;
    String get_image_name, get_url;
    ImageView show_image;
    Button r_bt, g_bt, b_bt;
    SeekBar r_bar, g_bar, b_bar;
    int[] c_value = {0, 0, 0};
    Mat img_input, img_color_output, img_binary_output;
    Boolean check_binary = false;
    Activity activity;

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_processing);
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);

        activity = this;
        show_image = (ImageView)findViewById(R.id.show_image);
        r_bt = (Button)findViewById(R.id.r_bt);
        g_bt = (Button)findViewById(R.id.g_bt);
        b_bt = (Button)findViewById(R.id.b_bt);
        r_bar = (SeekBar)findViewById(R.id.r_bar);
        g_bar = (SeekBar)findViewById(R.id.g_bar);
        b_bar = (SeekBar)findViewById(R.id.b_bar);
        setButtonsBackgroundColor();

        try {
            Intent intent = getIntent();
            get_image_name = intent.getExtras().getString("img");
            get_url = intent.getExtras().getString("url");
            if(get_url.equals("resource")) {
                AssetManager path = getResources().getAssets();
                InputStream is;
                is = path.open(get_image_name);
                input_image = BitmapFactory.decodeStream(is);
            }
            else if(get_url.equals("cacheDir")) {
                input_image = BitmapFactory.decodeFile(getExternalCacheDir().getPath() + "/" + get_image_name);
            }
            show_image.setImageBitmap(input_image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        r_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageprocess_and_showResult();
                check_binary = true;
            }
        });
        g_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageprocess_and_showResult();
                check_binary = true;
            }
        });
        b_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageprocess_and_showResult();
                check_binary = true;
            }
        });

        r_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seek_bar, int i, boolean b) {
                c_value[0] = i;
                r_bt.setText("R : " + c_value[0]);
                setButtonsBackgroundColor();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seek_bar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seek_bar) { }
        });
        g_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seek_bar, int i, boolean b) {
                c_value[1] = i;
                g_bt.setText("G : " + c_value[1]);
                setButtonsBackgroundColor();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seek_bar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seek_bar) { }
        });
        b_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seek_bar, int i, boolean b) {
                c_value[2] = i;
                b_bt.setText("B : " + c_value[2]);
                setButtonsBackgroundColor();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seek_bar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seek_bar) { }
        });
    }

    public void setButtonsBackgroundColor(){
        r_bt.setBackgroundColor(Color.rgb(c_value[0], c_value[1], c_value[2]));
        g_bt.setBackgroundColor(Color.rgb(c_value[0], c_value[1], c_value[2]));
        b_bt.setBackgroundColor(Color.rgb(c_value[0], c_value[1], c_value[2]));
        r_bt.setTextColor(Color.rgb(255 - c_value[0], 255 - c_value[1], 255 - c_value[2]));
        g_bt.setTextColor(Color.rgb(255 - c_value[0], 255 - c_value[1], 255 - c_value[2]));
        b_bt.setTextColor(Color.rgb(255 - c_value[0], 255 - c_value[1], 255 - c_value[2]));
    }

    private void read_image_file() {
        img_input = new Mat();
        img_color_output = new Mat();
        img_binary_output = new Mat();
    }

    private void imageprocess_and_showResult() {
        read_image_file();
        Utils.bitmapToMat(input_image, img_input);
        imageProcessing(img_input.getNativeObjAddr(), img_binary_output.getNativeObjAddr(), img_color_output.getNativeObjAddr(), img_input.cols(), img_input.rows(), c_value[0], c_value[1], c_value[2]);
        Bitmap bitmapOutput = Bitmap.createBitmap(img_color_output.cols(), img_color_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_color_output, bitmapOutput);
        show_image.setImageBitmap(bitmapOutput);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_processing, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.origin:
                read_image_file();
                Utils.bitmapToMat(input_image, img_input);
                Bitmap bitmapInput = Bitmap.createBitmap(img_input.cols(), img_input.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(img_input, bitmapInput);
                show_image.setImageBitmap(bitmapInput);
                check_binary = false;
                return true;
            case R.id.draw:
                if (check_binary) {
                    String binary_file_nm = "binary_img.png";
                    Intent intent = new Intent(getApplicationContext(), DrawingState.class);
                    Bitmap bitmap_binary = Bitmap.createBitmap(img_binary_output.cols(), img_binary_output.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_binary_output, bitmap_binary);
                    try {
                        File save_img = new File(getExternalCacheDir(), binary_file_nm);
                        FileOutputStream fos = new FileOutputStream(save_img);
                        bitmap_binary.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("img", binary_file_nm);
                    intent.putExtra("color", c_value);
                    startActivity(intent);
                }
                else if (!check_binary) Toast.makeText(getApplicationContext(),"색상을 선택해주세요.",Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public native void imageProcessing(long input_image, long binary_output_image, long color_output_image, float origin_w, float origin_h, float color_r, float color_g, float color_b);
}
