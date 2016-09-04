package com.example.evan.opencv;


import android.Manifest;

import com.kairos.*;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.bytedeco.javacpp.opencv_face;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 200);
        }
        final Kairos myKairos = new Kairos();
        String app_id = "a5511edf";
        String api_key = "03a203d5f7ed2650eaa9f3cb37fd5b92";
        myKairos.setAuthentication(this, app_id, api_key);
        final KairosListener listener = new KairosListener() {

            @Override
            public void onSuccess(String response) {
                Log.d("KAIROS DEMO", response);
            }

            @Override
            public void onFail(String response) {
                Log.d("KAIROS DEMO", response);
            }
        };





        Button cameraButton = (Button) findViewById(R.id.btn_open_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();

            }
        });
        Button analyzeButton = (Button) findViewById(R.id.btn_analyze);
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapFactory.Options option = new BitmapFactory.Options();
                Bitmap image = BitmapFactory.decodeFile(mCurrentPhotoPath, option);
                image = Bitmap.createScaledBitmap(image, 267, 200, true);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                image = Bitmap.createBitmap(image , 0, 0, image.getWidth(), image.getHeight(), matrix, true);
                try {
                    myKairos.enroll(image, "Test", "students", "FULL", "false", "0.25", listener);
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
                catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                Log.v("Test", mCurrentPhotoPath);
            }
               /* Intent i = new Intent(getApplicationContext(), PhotoDisplayActivity.class);
                i.putExtra("filePath", mCurrentPhotoPath);
                startActivity(i);
            }*/


        });





    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.evan.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}