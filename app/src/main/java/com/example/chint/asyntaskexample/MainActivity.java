package com.example.chint.asyntaskexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
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
    String imageURL = "https://cdn.pixabay.com/photo/2017/01/06/19/15/soap-bubble-1958650_960_720.jpg";
    //String imageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgurl = (EditText)findViewById(R.id.url);
        pbar = (ProgressBar)findViewById(R.id.progress);
        //imageName = Uri.parse(imageURL).getLastPathSegment();
    }
    public void onClick(View view){
        url = imgurl.getText().toString();
        imgurl.setText("");
        if(url!=null && url.length()>0) {
            MyTask myTask = new MyTask();
            myTask.execute(url);
        }
        else
            Toast.makeText(this, "Please Enter URL", Toast.LENGTH_LONG).show();
    }

    public void onSelect(View view){
        imgurl.setText(imageURL);
    }

    public void showImage(View view){
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
}


