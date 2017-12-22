package software.happybubble;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DrawingState extends AppCompatActivity {
    int error;
    InetAddress server_addr;
    Bitmap get_image;
    String get_image_nm;
    private Socket socket;
    EditText set_ip;
    String ip = "192.168.10.214";
    int port = 8765;
    int[] color;
    Boolean socket_error = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_state);
        Intent intent = getIntent();
        get_image_nm = intent.getStringExtra("img");

        get_image = BitmapFactory.decodeFile(getExternalCacheDir().getPath() + "/" + get_image_nm);
        color = intent.getIntArrayExtra("color");
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);

        ImageView image = (ImageView)findViewById(R.id.draw_image);
        Button draw = (Button)findViewById(R.id.draw_bt);
        TextView color_rgb = (TextView)findViewById(R.id.check_color);
        set_ip = (EditText)findViewById(R.id.set_ip);
        set_ip.setText(ip);

        image.setImageBitmap(get_image);
        color_rgb.setText("R : " + color[0] + " / G : " + color[1] + " / B : " + color[2]);
        color_rgb.setBackgroundColor(Color.rgb(color[0], color[1], color[2]));
        color_rgb.setTextColor(Color.rgb(255 - color[0], 255 - color[1], 255 - color[2]));

        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ip = set_ip.getText().toString();
                Thread send = new Thread(){
                    public void run(){
                        try {
                            server_addr = InetAddress.getByName(ip);
                            socket = new Socket(server_addr, port);

                            while (socket_error) {
                                DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                get_image.compress(Bitmap.CompressFormat.PNG, 100, out);
                                byte[] img_data = out.toByteArray();
                                byte[] size = getByte(img_data.length);
                                os.write(size, 0, size.length);
                                os.flush();
                                os.write(img_data, 0, img_data.length);
                                os.flush();

                                InputStream inputStream = socket.getInputStream();
                                error = inputStream.read();

                                if(error != 100)
                                    socket_error = false;
                                else
                                    socket_error = true;
                            }
                            socket.close();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                send.interrupt();
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
