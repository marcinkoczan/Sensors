package net.marcinkoczan.sensors;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class main extends ActionBarActivity implements SensorEventListener{


    TextView tv;
    int i=1;
    View view;
    ImageView imageView;
    float poprzednieX, poprzednieY;
    SensorManager sm;
    Sensor acc;
    float gf = 8.0f;
    Bitmap obraz;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    File usun=null;
    File img=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imgView);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        wczytajzdj();
    }

    public void wczytajzdj()
    {
        File file = new File("/sdcard/Pictures/");

        Log.d("", file.list()[1]);

        if(i>0 && i<file.list().length)
        {
            usun=new File("/sdcard/Pictures/",file.list()[i]);
            obraz = Bitmap.createScaledBitmap(BitmapFactory.decodeFile("/sdcard/Pictures/"+file.list()[i]),300,300,true);
            imageView.setImageBitmap(obraz);
        }
        else {i=1;wczytajzdj();}
    }


    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
        //sth
    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {
        float x = event.values[0];
        float y = event.values[1];
            float dX = Math.abs(poprzednieX - x);
            float dY = Math.abs(poprzednieY - y);
            if (dX < gf) dX = (float)0.0;
            if (dY < gf) dY = (float)0.0;
            poprzednieX = x;
            poprzednieY = y;
            if (dX > dY) {
                //poziomo
                i+=1;
                wczytajzdj();
            } else if (dY > dX) {
                //pionowo
                i-=1;
                wczytajzdj();
            }

    }

    public void zrob_zdj(View view)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {}
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File("/sdcard/Pictures/");
        File image = new File("/sdcard/Pictures/",imageFileName+".jpg");
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        img=image;
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode==1&&resultCode==RESULT_OK)
        {
            obraz=null;
            usun=new File(img.getAbsolutePath());
            obraz = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()),100,100,true);
            imageView.setImageBitmap(obraz);
        }
    }

    public void usun_zdj(View view)
    {
        usun.delete();
        i-=1;
        wczytajzdj();
    }


    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) , SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sm.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
