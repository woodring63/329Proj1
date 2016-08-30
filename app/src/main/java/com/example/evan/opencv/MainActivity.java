package com.example.evan.opencv;


import android.Manifest;

import com.kairos.*;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    Kairos myKairos;
    KairosListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myKairos = new Kairos();

// set authentication
        String app_id = "a5511edf";
        String api_key = "03a203d5f7ed2650eaa9f3cb37fd5b92";
        myKairos.setAuthentication(this, app_id, api_key);

        listener = new KairosListener() {

            @Override
            public void onSuccess(String response) {
                // your code here!
                Log.d("KAIROS DEMO", response);
            }

            @Override
            public void onFail(String response) {
                // your code here!
                Log.d("KAIROS DEMO", response);
            }
        };
        /*try {
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.test_photo);
            String subjectId = "Chris Soules";
            String galleryId = "students";
            String selector = "FULL";
            String multipleFaces = "false";
            String minHeadScale = "0.25";
            myKairos.enroll(image,
                    subjectId,
                    galleryId,
                    selector,
                    multipleFaces,
                    minHeadScale,
                    listener);        } catch (UnsupportedEncodingException e) {

        } catch (JSONException e) {

        }*/


        Button cameraButton = (Button) findViewById(R.id.btn_open_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 200);
                }
                dispatchTakePictureIntent();
            }
        });
        Button analyzeButton = (Button) findViewById(R.id.btn_analyze);
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.test_photo);
                    String galleryId = "students";
                    String selector = "FULL";
                    String threshold = "0.75";
                    String minHeadScale = "0.25";
                    String maxNumResults = "25";
                    myKairos.recognize(image,
                            galleryId,
                            selector,
                            threshold,
                            minHeadScale,
                            maxNumResults,
                            listener);
                }
                catch (JSONException e){

                }
                catch (UnsupportedEncodingException e){

                }

            }
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
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

}