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
import android.widget.Toast;

import org.bytedeco.javacpp.opencv_face;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    Bitmap image;
    String studentName;

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

        //listener for the actual recognizing
        final KairosListener listener = new KairosListener() {

            @Override
            public void onSuccess(String response) {


                Log.d("KAIROS DEMO", response);

                if (response.contains("failure")) {
                    Intent i = new Intent(getApplicationContext(), EnterNewStudentActivity.class);
                    i.putExtra("filename", mCurrentPhotoPath);
                    startActivity(i);
                } else {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray arr = (JSONArray) json.get("images");
                        json = (JSONObject) arr.get(0);
                        json = (JSONObject) json.get("transaction");
                        studentName = (String) json.get("subject");
                        Log.v("Test",studentName);


                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                    Intent i = new Intent(getApplicationContext(), PhotoDisplayActivity.class);
                    i.putExtra("filename", mCurrentPhotoPath);
                    i.putExtra("studentName", studentName);
                    startActivity(i);
                }
            }


            @Override
            public void onFail(String response) {
                Log.d("KAIROS DEMO", response);
            }
        };
        //listener for detecting a face in the image
        final KairosListener detectListener = new KairosListener() {
            @Override
            public void onSuccess(String s) {
                if (s.contains("ErrCode")) {
                    Toast.makeText(getApplicationContext(), "There seems to have been an error, " +
                            "try retaking the photo and try again", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        myKairos.recognize(image, "students", "FULL", "0.5", "0.25", "1", listener);
                        //if not recognized, prompt user for the student's name
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFail(String s) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();

            }
        };

        //launches the camera
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

            }
        });
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPhotoPath != null) {
                    Toast.makeText(getApplicationContext(), "Processing...", Toast.LENGTH_LONG).show();
                    //Convert last photo taken to a Bitmap and rotate
                    BitmapFactory.Options option = new BitmapFactory.Options();
                    image = BitmapFactory.decodeFile(mCurrentPhotoPath, option);
                    if(image != null) {
                        image = Bitmap.createScaledBitmap(image, 267, 200, true);
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
                        //detect faces in the image
                        try {
                            myKairos.detect(image, "FULL", "0.25", detectListener);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "You need to take a picture first!", Toast.LENGTH_LONG).show();

                    }
                    //if there are faces, try recognizing


                } else {
                    Toast.makeText(getApplicationContext(), "You need to take a picture first!", Toast.LENGTH_LONG).show();
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
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}