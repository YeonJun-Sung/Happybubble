package software.happybubble;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class GetImage extends AppCompatActivity {
    ImageView showImage;
    Button removeBt, lableingBt, nextBt;
    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_ALBUM = 2;
    Boolean lableingCheck = false;
    String[] fileName;
    String originFileNm = "origin.png";
    String Url;
    int lableingIdx = 0;
    int mode;
    Mat img_input, img_output, img_stats, img_centroids;
    ArrayList<String> lableList = new ArrayList<String>();

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_image);
        Intent intent = getIntent();
        mode = intent.getExtras().getInt("mode");

        read_image_file();
        if(mode == REQUEST_ALBUM) {
            Toast.makeText(getApplicationContext(), "ALBUM TODO", Toast.LENGTH_SHORT).show();
            doTakeAlbumAction();
        }
        else if(mode == REQUEST_CAMERA) {
            Toast.makeText(getApplicationContext(), "CAMERA TODO", Toast.LENGTH_SHORT).show();
            doTakePhotoAction();
        }

        showImage = (ImageView) findViewById(R.id.showImage);
        removeBt = (Button) findViewById(R.id.removeBt);
        lableingBt = (Button) findViewById(R.id.lableingBt);
        nextBt = (Button) findViewById(R.id.nextBt);

        removeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Remove background TODO", Toast.LENGTH_SHORT).show();
            }
        });
        lableingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Lableing image TODO", Toast.LENGTH_SHORT).show();

                int numberOfLables = imageLableing(img_input.getNativeObjAddr(), img_stats.getNativeObjAddr(), img_centroids.getNativeObjAddr());
                int lableNum = 0;
                for(int i = 1;i < numberOfLables;i++) {
                    if(getLableingImg(i, img_input.getNativeObjAddr(), img_output.getNativeObjAddr(), img_stats.getNativeObjAddr(), img_centroids.getNativeObjAddr())){
                        Bitmap imgSaveTemp = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(img_output, imgSaveTemp);
                        String fileNm = "lableImg" + lableNum + ".png";
                        lableList.add(fileNm);
                        lableNum++;
                        try{
                            File saveImg = new File(getExternalCacheDir(), fileNm);
                            FileOutputStream fos = new FileOutputStream(saveImg);
                            imgSaveTemp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.flush();
                            fos.close();
                            Log.d("file","save " + fileNm);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                fileName = new String[lableList.size()];
                for(int i = 0;i < lableList.size();i++) fileName[i] = lableList.get(i);
                lableingCheck = true;
                Toast.makeText(getApplicationContext(), "객체 나누기 완료", Toast.LENGTH_SHORT).show();
            }
        });
        nextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Send Image TODO", Toast.LENGTH_SHORT).show();
                Intent intent;
                Url = "cacheDir";
                if(lableingCheck){
                    intent = new Intent(getApplicationContext(), SelectClipart.class);
                    intent.putExtra("url", Url);
                    intent.putExtra("fileName", fileName);
                    startActivity(intent);
                }
                else {
                    intent = new Intent(getApplicationContext(), PictureProcessing.class);
                    intent.putExtra("url", Url);
                    intent.putExtra("img", originFileNm);
                    startActivity(intent);
                }
            }
        });
    }

    private void read_image_file() {
        img_input = new Mat();
        img_output = new Mat();
        img_stats = new Mat();
        img_centroids = new Mat();
    }

    private void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        intent.putExtra("outputX", 350);
        intent.putExtra("outputY",450);

        try {
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void doTakeAlbumAction() {
        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bm;
        Matrix rotateMatrix = new Matrix();

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && !data.equals(null)) {
                try {
                    Bundle extras = data.getExtras();
                    if(extras != null) {
                        bm = extras.getParcelable("data");
                        rotateMatrix.postRotate(90);
                        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), rotateMatrix, false);
                        showImage.setImageBitmap(bm);
                        Utils.bitmapToMat(bm, img_input);
                        try {
                            File saveImg = new File(getExternalCacheDir(), originFileNm);
                            FileOutputStream fos = new FileOutputStream(saveImg);
                            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.flush();
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_ALBUM && !data.equals(null)) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    rotateMatrix.postRotate(90);
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), rotateMatrix, false);
                    showImage.setImageBitmap(bm);
                    Utils.bitmapToMat(bm, img_input);
                    showImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    try {
                        File saveImg = new File(getExternalCacheDir(), originFileNm);
                        FileOutputStream fos = new FileOutputStream(saveImg);
                        bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public native int imageLableing(long inputImage, long statsImage, long centroidsImage);
    public native boolean getLableingImg(int numberOfLables, long addrInputImage, long addrOutputImage,long addrStatsImage, long addrCentroidsImage);
}
