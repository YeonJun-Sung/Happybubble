package software.happybubble;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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
import java.util.ArrayList;

public class GetImage extends AppCompatActivity {
    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_ALBUM = 2;
    ImageView show_image;
    Button remove_bt, lableing_bt, next_bt;
    Boolean lableing_check = false;
    String[] file_name;
    String[] parse_num;
    String origin_file_nm = "origin.png";
    String url;
    String number_of_lables;
    int mode, i;
    int lable_num = 0;
    Mat img_origin, img_input, img_output, img_stats;
    ArrayList<String> lable_list = new ArrayList<String>();
    Boolean remove_state = true;
    Boolean lable_state = true;
    Boolean processing_state = true;

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

        show_image = (ImageView) findViewById(R.id.show_image);
        remove_bt = (Button) findViewById(R.id.remove_bt);
        lableing_bt = (Button) findViewById(R.id.lableing_bt);
        next_bt = (Button) findViewById(R.id.next_bt);

        remove_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processing_state = false;
                Bitmap bitmap_output;
                if(remove_state) {
                    removeBackground(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());
                    bitmap_output = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_output, bitmap_output);
                    show_image.setImageBitmap(bitmap_output);
                    img_input.copyTo(img_origin);
                    img_output.copyTo(img_input);
                    remove_bt.setText("원본 보기");
                    remove_state = false;
                }
                else {
                    img_origin.copyTo(img_input);
                    bitmap_output = Bitmap.createBitmap(img_input.cols(), img_input.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_input, bitmap_output);
                    show_image.setImageBitmap(bitmap_output);
                    remove_state = true;
                    remove_bt.setText("배경 제거");
                }
                try {
                    File save_img = new File(getExternalCacheDir(), origin_file_nm);
                    FileOutputStream fos = new FileOutputStream(save_img);
                    bitmap_output.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                processing_state = true;
            }
        });
        lableing_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processing_state = false;
                if(lable_state) {
                    Thread img_process_thread = new Thread() {
                        public void run() {
                            number_of_lables = imageLableing(img_input.getNativeObjAddr(), img_stats.getNativeObjAddr());
                            parse_num = number_of_lables.split(",");
                            lable_num = 0;
                            lable_list.clear();

                            for (i = 0; i < parse_num.length; i++) {
                                if(parse_num[i].equals(""))  continue;
                                getLableingImg(Integer.parseInt(parse_num[i]), img_input.getNativeObjAddr(), img_output.getNativeObjAddr(), img_stats.getNativeObjAddr());
                                Bitmap img_save_temp = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
                                Utils.matToBitmap(img_output, img_save_temp);
                                String file_nm = "lableImg" + lable_num + ".png";
                                lable_list.add(file_nm);
                                lable_num++;
                                try {
                                    File save_img = new File(getExternalCacheDir(), file_nm);
                                    FileOutputStream fos = new FileOutputStream(save_img);
                                    img_save_temp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                    fos.flush();
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if(lable_list.size() != 0) {
                                file_name = new String[lable_list.size()];
                                for (int j = 0; j < lable_list.size(); j++) file_name[j] = lable_list.get(j);
                                lableing_check = true;
                            }
                            processing_state = true;
                        }
                    };
                    img_process_thread.start();
                    lableing_bt.setText("원본 보기");
                    lable_state = false;
                }
                else {
                    Bitmap bitmap_origin = Bitmap.createBitmap(img_input.cols(), img_input.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_input, bitmap_origin);
                    show_image.setImageBitmap(bitmap_origin);
                    lableing_bt.setText("객체 나누기");
                    lable_state = true;
                    processing_state = true;
                    lableing_check = false;
                }
            }
        });
        next_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(processing_state) {
                    Intent intent;
                    url = "cacheDir";
                    if (lableing_check) {
                        intent = new Intent(getApplicationContext(), SelectClipart.class);
                        intent.putExtra("url", url);
                        intent.putExtra("fileName", file_name);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getApplicationContext(), PictureProcessing.class);
                        intent.putExtra("url", url);
                        intent.putExtra("img", origin_file_nm);
                        startActivity(intent);
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "이미지 처리 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        if(mode == REQUEST_ALBUM) {
            doTakeAlbumAction();
        }
        else if(mode == REQUEST_CAMERA) {
            doTakePhotoAction();
        }
    }

    private void read_image_file() {
        img_origin = new Mat();
        img_input = new Mat();
        img_output = new Mat();
        img_stats = new Mat();
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
    protected void onActivityResult(int request_code, int result_code, Intent data) {
        Bitmap bm;
        Matrix rotate_matrix = new Matrix();

        if (result_code == RESULT_OK) {
            if (request_code == REQUEST_CAMERA && !data.equals(null)) {
                try {
                    Bundle extras = data.getExtras();
                    if(extras != null) {
                        bm = extras.getParcelable("data");
                        rotate_matrix.postRotate(90);
                        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), rotate_matrix, false);
                        show_image.setImageBitmap(bm);
                        Utils.bitmapToMat(bm, img_input);
                        try {
                            File save_img = new File(getExternalCacheDir(), origin_file_nm);
                            FileOutputStream fos = new FileOutputStream(save_img);
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
            } else if (request_code == REQUEST_ALBUM && !data.equals(null)) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    rotate_matrix.postRotate(90);
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), rotate_matrix, false);
                    show_image.setImageBitmap(bm);
                    Utils.bitmapToMat(bm, img_input);
                    show_image.setScaleType(ImageView.ScaleType.FIT_XY);
                    try {
                        File save_img = new File(getExternalCacheDir(), origin_file_nm);
                        FileOutputStream fos = new FileOutputStream(save_img);
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

    public native String imageLableing(long input_image, long stats_image);
    public native void getLableingImg(int number_of_lables, long addr_input_image, long addr_output_image, long addr_stats_image);
    public native void removeBackground(long addr_input_image, long addr_output_image);
}
