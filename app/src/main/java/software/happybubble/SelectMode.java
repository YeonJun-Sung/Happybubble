package software.happybubble;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectMode extends AppCompatActivity {
    Button album, camera, clipart;
    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_ALBUM = 2;
    static final int PERMISSION_REQUEST_CODE = 1;
    String[] file_name = {"ascent.png", "bicycle priority road.png", "bicycle road.png", "changing lane-left.png"
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
            , "emoticon13.png", "emoticon14.png", "emoticon15.png", "emoticon16.png"};
    String[] PERMISSIONS  = {"android.permission.WRITE_EXTERNAL_STORAGE"};
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        album = (Button)findViewById(R.id.album);
        camera = (Button)findViewById(R.id.camera);
        clipart = (Button)findViewById(R.id.clipart);


        if (!hasPermissions(PERMISSIONS))//퍼미션 허가를 했었는지 여부를 확인
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        else{
            album.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), GetImage.class);
                    intent.putExtra("mode", REQUEST_ALBUM);
                    startActivity(intent);
                }
            });
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), GetImage.class);
                    intent.putExtra("mode", REQUEST_CAMERA);
                    startActivity(intent);
                }
            });
            clipart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), SelectClipart.class);
                    intent.putExtra("fileName", file_name);
                    intent.putExtra("url", "resource");
                    startActivity(intent);
                }
            });
        }
    }

    private boolean hasPermissions(String[] permissions) {
        int ret = 0;
        for (String perms : permissions){
            ret = checkCallingOrSelfPermission(perms);
            if (!(ret == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestNecessaryPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int perms_request_code, String[] permissions, int[] grant_results){
        switch(perms_request_code){
            case PERMISSION_REQUEST_CODE:
                if (grant_results.length > 0) {
                    boolean writeAccepted = grant_results[1] == PackageManager.PERMISSION_GRANTED;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!writeAccepted ) {
                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder my_dialog = new AlertDialog.Builder(activity);
        my_dialog.setTitle("알림");
        my_dialog.setMessage(msg);
        my_dialog.setCancelable(false);
        my_dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        my_dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        my_dialog.show();
    }
}