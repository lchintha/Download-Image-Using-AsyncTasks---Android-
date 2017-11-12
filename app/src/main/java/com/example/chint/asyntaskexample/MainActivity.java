package com.example.chint.asyntaskexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText imgurl;
    ProgressBar pbar;
    String url;
    ImageView img;
    String imageURL = "http://www.lawner.pl/wp-content/uploads/2016/01/park.jpg";
    int STORAGE_REQUEST_CODE = 0;
    int READ_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgurl = (EditText)findViewById(R.id.url);
        pbar = (ProgressBar)findViewById(R.id.progress);
    }
    public void onClick(View view){
        askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_REQUEST_CODE);
    }

    private void askPermission(String permission, int requestCode){
        if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }else{
            //Toast.makeText(this, "Permission has already granted", Toast.LENGTH_LONG).show();
            url = imgurl.getText().toString();
            imgurl.setText("");
            if(url!=null && url.length()>0) {
                MyTask myTask = new MyTask();
                myTask.execute(url);
            }
            else
                Toast.makeText(this, "Please Enter URL", Toast.LENGTH_LONG).show();
        }
    }
    private void askPermission2(String permission, int requestCode){
        if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }else{
            img = (ImageView)findViewById(R.id.showimage);
            File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File directory = new File(sdcard.getAbsolutePath());
            File file = new File(directory, Uri.parse(imageURL).getLastPathSegment());
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(file);
                Bitmap bm = BitmapFactory.decodeStream(stream);
                img.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onSelect(View view){
        imgurl.setText(imageURL);
    }

    public void showImage(View view){
        askPermission2(Manifest.permission.READ_EXTERNAL_STORAGE,READ_REQUEST_CODE);
    }

    public void sendImage(View view) throws FileNotFoundException {
        String fileName = Uri.parse(imageURL).getLastPathSegment();
        File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File directory = new File(sdcard.getAbsolutePath());
        File file = new File(directory, fileName);
        FileInputStream stream = null;
        stream = new FileInputStream(file);

        //Uri image = Uri.parse("/storage/emulated/0/Pictures/park.jpg");
        Bitmap image = BitmapFactory.decodeStream(stream);
        //Uri image = FileProvider.getUriForFile(this, "com.example.chint.asyntaskexample", file);

        Intent i = new Intent(Intent.ACTION_SEND);

        //i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        i.putExtra(Intent.EXTRA_STREAM,image);
        i.setType("image/*");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(i, "Send Image");
        if (i.resolveActivity(getPackageManager()) != null)
            startActivity(chooser);
        else
            Toast.makeText(this, "Nothing", Toast.LENGTH_SHORT).show();
    }

    class MyTask extends AsyncTask<String, Integer, Boolean>{
        int progress = 0;
        int content = -1;
        @Override
        protected void onPreExecute() {
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            int counter = 0;
            boolean status = false;
            URL downloadURL = null;
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            File file = null;
            HttpURLConnection connection = null;
            try {
                downloadURL = new URL(url);
                connection = (HttpURLConnection) downloadURL.openConnection();
                content = connection.getContentLength();
                inputStream = connection.getInputStream();
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        +"/"+ Uri.parse(url).getLastPathSegment());
                fileOutputStream = new FileOutputStream(file);
                int read = -1;
                byte[] buffer = new byte[1024];
                while ((read = inputStream.read(buffer))!=-1){
                    fileOutputStream.write(buffer, 0, read);
                    counter += read;
                    publishProgress(counter);
                }
                status = true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(connection!=null)
                    connection.disconnect();
                if(inputStream!=null)
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (fileOutputStream!=null)
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return status;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress = (int)(((double)values[0]/content)*100);
            pbar.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pbar.setVisibility(View.INVISIBLE);
            Toast.makeText(MainActivity.this, "File Successfully Downloaded..!!!", Toast.LENGTH_LONG).show();
        }
    }

    // New change goes here....

    /*  *.iml
        .gradle
        /local.properties
        /.idea/workspace.xml
        /.idea/libraries
        /.idea/modules.xml
        .DS_Store
        /build
        /captures
        .externalNativeBuild
    */
}


