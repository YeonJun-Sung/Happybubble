package software.happybubble;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;
import java.io.InputStream;

public class PictureProcessing extends AppCompatActivity {
    Bitmap inputImage;
    String getImageName, getUrl;
    ImageView showImage;
    AlertDialog.Builder alertW, alertH;
    EditText widthText, heightText;
    Button rBt, gBt, bBt;
    SeekBar rBar, gBar, bBar;
    int[] cValue = {0, 0, 0};
    int[] paperSize = {1000, 1000};
    Mat img_input, img_colorOutput, img_binaryOutput;
    Boolean checkBinary = false;
    Activity activity;

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_processing);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        activity = this;
        showImage = (ImageView)findViewById(R.id.showImage);
        rBt = (Button)findViewById(R.id.rBt);
        gBt = (Button)findViewById(R.id.gBt);
        bBt = (Button)findViewById(R.id.bBt);
        rBar = (SeekBar)findViewById(R.id.rBar);
        gBar = (SeekBar)findViewById(R.id.gBar);
        bBar = (SeekBar)findViewById(R.id.bBar);
        setButtonsBackgroundColor();

        try {
            Intent intent = getIntent();
            getImageName = intent.getExtras().getString("img");
            getUrl = intent.getExtras().getString("url");
            if(getUrl.equals("resource")) {
                AssetManager path = getResources().getAssets();
                InputStream is;
                is = path.open(getImageName);
                inputImage = BitmapFactory.decodeStream(is);
            }
            else if(getUrl.equals("cacheDir")) {
                Log.d("path",getExternalCacheDir().getPath());
                inputImage = BitmapFactory.decodeFile(getExternalCacheDir().getPath() + "/" + getImageName);
            }
            showImage.setImageBitmap(inputImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(),"set",Toast.LENGTH_SHORT).show();

        rBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageprocess_and_showResult();
                checkBinary = true;
            }
        });
        gBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageprocess_and_showResult();
                checkBinary = true;
            }
        });
        bBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageprocess_and_showResult();
                checkBinary = true;
            }
        });

        rBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cValue[0] = i;
                rBt.setText("R : " + cValue[0]);
                setButtonsBackgroundColor();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        gBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cValue[1] = i;
                gBt.setText("G : " + cValue[1]);
                setButtonsBackgroundColor();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        bBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cValue[2] = i;
                bBt.setText("B : " + cValue[2]);
                setButtonsBackgroundColor();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    public void setButtonsBackgroundColor(){
        rBt.setBackgroundColor(Color.rgb(cValue[0], cValue[1], cValue[2]));
        gBt.setBackgroundColor(Color.rgb(cValue[0], cValue[1], cValue[2]));
        bBt.setBackgroundColor(Color.rgb(cValue[0], cValue[1], cValue[2]));
        rBt.setTextColor(Color.rgb(255 - cValue[0], 255 - cValue[1], 255 - cValue[2]));
        gBt.setTextColor(Color.rgb(255 - cValue[0], 255 - cValue[1], 255 - cValue[2]));
        bBt.setTextColor(Color.rgb(255 - cValue[0], 255 - cValue[1], 255 - cValue[2]));
    }

    private void read_image_file() {
        img_input = new Mat();
        img_colorOutput = new Mat();
        img_binaryOutput = new Mat();
    }

    private void imageprocess_and_showResult() {
        read_image_file();
        Utils.bitmapToMat(inputImage, img_input);
        imageProcessing(img_input.getNativeObjAddr(), img_binaryOutput.getNativeObjAddr(), img_colorOutput.getNativeObjAddr(), cValue[0], cValue[1], cValue[2]);
        Bitmap bitmapOutput = Bitmap.createBitmap(img_colorOutput.cols(), img_colorOutput.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_colorOutput, bitmapOutput);
        showImage.setImageBitmap(bitmapOutput);
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
                Utils.bitmapToMat(inputImage, img_input);
                Bitmap bitmapInput = Bitmap.createBitmap(img_input.cols(), img_input.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(img_input, bitmapInput);
                showImage.setImageBitmap(bitmapInput);
                checkBinary = false;
                return true;
            case R.id.setsize:
                alertW = new AlertDialog.Builder(activity);
                alertH = new AlertDialog.Builder(activity);

                alertW.setTitle("용지 크기를 설정해주세요.");
                alertW.setMessage("용지 너비를 입력해주세요. (단위 : mm)");
                alertH.setTitle("용지 크기를 설정해주세요.");
                alertH.setMessage("용지 높이를 입력해주세요. (단위 : mm)");
                widthText = new EditText(activity);
                heightText = new EditText(activity);
                widthText.setInputType(InputType.TYPE_CLASS_NUMBER);
                heightText.setInputType(InputType.TYPE_CLASS_NUMBER);
                widthText.setHint("미 입력시 " + paperSize[0] + " mm");
                heightText.setHint("미 입력시 " + paperSize[0] + " mm");
                alertW.setView(widthText);
                alertH.setView(heightText);
                alertW.setPositiveButton("다음", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String getW = widthText.getText().toString();
                        if(getW.equals("")) paperSize[0] = 1000;
                        else {
                            paperSize[0] = Integer.parseInt(getW);
                            if(paperSize[0] <= 0)   paperSize[0] = 1000;
                        }
                        dialogInterface.dismiss();
                        alertH.show();
                    }
                });
                alertW.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alertH.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String getH = heightText.getText().toString();
                        if(getH.equals(""))  paperSize[1] = 1000;
                        else {
                            paperSize[1] = Integer.parseInt(getH);
                            if(paperSize[1] <= 0)   paperSize[1] = 1000;
                        }
                        dialogInterface.dismiss();
                    }
                });
                alertH.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertW.show();
                return true;
            case R.id.draw:
                if (checkBinary) {
                    Intent intent = new Intent(getApplicationContext(), DrawingState.class);
                    Bitmap bitmapIntent = Bitmap.createBitmap(img_binaryOutput.cols(), img_binaryOutput.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_binaryOutput, bitmapIntent);
                    intent.putExtra("img", bitmapIntent);
                    intent.putExtra("size", paperSize);
                    intent.putExtra("color", cValue);
                    startActivity(intent);
                }
                else if (!checkBinary) Toast.makeText(getApplicationContext(),"색상을 선택해주세요.",Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public native void imageProcessing(long inputImage, long binaryOutputImage, long colorOutputImage, float colorR, float colorG, float colorB);
}
