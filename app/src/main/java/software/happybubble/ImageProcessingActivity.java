package software.happybubble;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ImageProcessingActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    ImageView imageVIewInput;
    ImageView imageVIewOutput;
    Button camera, album, lable, doitR, doitY, doitB, doitW;
    public static ItemListViewAdapter lableingListAdapter;
    public static ItemListView lableingList;
    ArrayList<ListView> list = new ArrayList<ListView>();
    ArrayList<Bitmap> img = new ArrayList<Bitmap>();
    ArrayList<String> txt = new ArrayList<String>();
    private View view;
    private Activity activity;
    private Mat img_input, img_output, img_stats, img_centroids;
    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_ALBUM = 2;
    private static final String TAG = "opencv";
    static final int PERMISSION_REQUEST_CODE = 1;
    static int index = 0;
    String[] PERMISSIONS  = {"android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int ret = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){
            ret = checkCallingOrSelfPermission(perms);
            if (!(ret == PackageManager.PERMISSION_GRANTED)){
                //퍼미션 허가 안된 경우
                return false;
            }

        }
        //모든 퍼미션이 허가된 경우
        return true;
    }

    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d( TAG, "copyFile :: 다음 경로로 파일복사 "+ pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 "+e.toString() );
        }

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (!writeAccepted )
                        {
                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                            return;
                        }else
                        {
                            read_image_file();
                            imageprocess_and_showResult(0);
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(  ImageProcessingActivity.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        activity = this;

        imageVIewInput = (ImageView)findViewById(R.id.imageViewInput);
        imageVIewOutput = (ImageView)findViewById(R.id.imageViewOutput);
        camera = (Button)findViewById(R.id.camera);
        album = (Button)findViewById(R.id.album);
        lable = (Button)findViewById(R.id.lable);
        doitR = (Button)findViewById(R.id.doitR);
        doitY = (Button)findViewById(R.id.doitY);
        doitB = (Button)findViewById(R.id.doitB);
        doitW = (Button)findViewById(R.id.doitW);

        if (!hasPermissions(PERMISSIONS))//퍼미션 허가를 했었는지 여부를 확인
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        else {
            read_image_file();
            camera.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    doTakePhotoAction();
                }
            });
            album.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    doTakeAlbumAction();
                }
            });
            lable.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"start",Toast.LENGTH_SHORT).show();
                    String numberOfLables = imageLableing(img_input.getNativeObjAddr(), img_stats.getNativeObjAddr(), img_centroids.getNativeObjAddr());
                    /*
                    Bitmap bitmapInput = Bitmap.createBitmap(img_input.cols(), img_input.rows(), Bitmap.Config.ARGB_8888);
                    Bitmap bitmapOutput;
                    Utils.matToBitmap(img_input, bitmapInput);
                    imageVIewInput.setImageBitmap(bitmapInput);
*/
                    Toast.makeText(getApplicationContext(),numberOfLables,Toast.LENGTH_SHORT).show();
/*
                    for(int i = 0;i < numberOfLables;i++) {
                        getLableingImg(numberOfLables, i, img_input.getNativeObjAddr(), img_output.getNativeObjAddr(), img_stats.getNativeObjAddr(), img_centroids.getNativeObjAddr());
                        bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(img_output, bitmapOutput);
                        img.add(bitmapOutput);
                        Toast.makeText(getApplicationContext(), ""+numberOfLables, Toast.LENGTH_SHORT).show();
                    }

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                    alertBuilder.setTitle("항목중에 하나를 선택하세요.");

                    lableingList = (ItemListView) findViewById(R.id.lableList);
                    lableingListAdapter = new ItemListViewAdapter(activity, R.layout.activity_select_clipart, list);
                    for(int i = 0; i < img.size(); i++){
                        list.add(new ItemListView(img.get(i)));
                    }
                    lableingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            Log.d("test",""+position);
                            imageVIewOutput.setImageBitmap(img.get(position));
                            Toast.makeText(getApplicationContext(),txt.get(position),Toast.LENGTH_SHORT).show();
                        }
                    });

                    alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) { dialog.dismiss(); }
                    });

                    alertBuilder.setAdapter(lableingListAdapter,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {

                                    long strName = lableingListAdapter.getItemId(id);
                                    AlertDialog.Builder innBuilder = new AlertDialog.Builder(activity);
                                    innBuilder.setMessage("" + strName);
                                    innBuilder.setTitle("당신이 선택한 것은 ");
                                    innBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    innBuilder.show();
                                }
                            });
                    alertBuilder.show();*/
                }
            });
            doitR.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    imageprocess_and_showResult(0);
                }
            });
            doitY.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    imageprocess_and_showResult(1);
                }
            });
            doitB.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    imageprocess_and_showResult(2);
                }
            });
            doitW.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    imageprocess_and_showResult(3);
                }
            });

        }
    }

    private void imageprocess_and_showResult(int color) {
        imageProcessing(img_input.getNativeObjAddr(), img_output.getNativeObjAddr(), color);
        Bitmap bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);
        imageVIewOutput.setImageBitmap(bitmapOutput);
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

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && !data.equals(null)) {
                try {
                    Bundle extras = data.getExtras();
                    if(extras != null) {
                        bm = extras.getParcelable("data");
                        imageVIewInput.setImageBitmap(bm);
                        Utils.bitmapToMat(bm, img_input);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_ALBUM && !data.equals(null)) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    imageVIewInput.setImageBitmap(bm);
                    Utils.bitmapToMat(bm, img_input);
                    imageVIewInput.setScaleType(ImageView.ScaleType.FIT_XY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String imageLableing(long inputImage, long statsImage, long centroidsImage);
    public native void getLableingImg(int numOfLables, int returnLable, long addrInputImage, long addrOutputImage,long addrStatsImage, long addrCentroidsImage);
    public native void imageProcessing(long inputImage, long outputImage, int color);
}