package software.happybubble;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DrawingState extends AppCompatActivity {
    InetAddress serverAddr;
    Bitmap getImage;
    private Socket socket;
    BufferedInputStream bis;
    String ip = "192.168.219.103";
    int port = 8765;
    int[] paperSize, color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_state);
        Intent intent = getIntent();
        getImage = (Bitmap) intent.getParcelableExtra("img");
        color = intent.getIntArrayExtra("color");
        paperSize = intent.getIntArrayExtra("size");

        ImageView image = (ImageView)findViewById(R.id.drawImage);
        Button draw = (Button)findViewById(R.id.drawBt);
        TextView colorRGB = (TextView)findViewById(R.id.checkColor);
        TextView checkSize = (TextView)findViewById(R.id.checkSize);

        image.setImageBitmap(getImage);
        colorRGB.setText("R : " + color[0] + " / G : " + color[1] + " / B : " + color[2]);
        colorRGB.setBackgroundColor(Color.rgb(color[0], color[1], color[2]));
        colorRGB.setTextColor(Color.rgb(255 - color[0], 255 - color[1], 255 - color[2]));
        checkSize.setText("Width : " + paperSize[0] + " / Height : " + paperSize[1]);

        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "draw",Toast.LENGTH_SHORT).show();
                Thread send = new Thread(){
                    public void run(){
                        try{
                            serverAddr = InetAddress.getByName(ip);
                            socket = new Socket(serverAddr, port);
                            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            getImage.compress(Bitmap.CompressFormat.PNG, 100, out);
                            byte[] imgData = out.toByteArray();
                            byte[] size = getByte(imgData.length);
                            byte[] width = getByte(paperSize[0]);
                            byte[] height = getByte(paperSize[1]);
                            Log.d("send data", "" + imgData);
                            Log.d("send data", "" + size);
                            Log.d("send data", "" + paperSize[0] + ", " + paperSize[1]);
                            os.write(size, 0, size.length);
                            os.flush();
                            os.write(imgData, 0, imgData.length);
                            os.flush();
                            os.write(width, 0, width.length);
                            os.flush();
                            os.write(height, 0, height.length);
                            os.flush();
                            socket.close();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                send.start();
            }
        });
    }
    private byte[] getByte(int num) {
        byte[] buf = new byte[4];
        buf[0] = (byte)( (num >>> 24) & 0xFF );
        buf[1] = (byte)( (num >>> 16) & 0xFF );
        buf[2] = (byte)( (num >>>  8) & 0xFF );
        buf[3] = (byte)( (num >>>  0) & 0xFF );

        return buf;
    }
}
