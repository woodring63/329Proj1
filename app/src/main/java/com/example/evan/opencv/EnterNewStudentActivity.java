package com.example.evan.opencv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class EnterNewStudentActivity extends AppCompatActivity {
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_new_student);
        Intent i = getIntent();
        String filename = i.getExtras().getString("filename");
        BitmapFactory.Options option = new BitmapFactory.Options();
        image = BitmapFactory.decodeFile(filename, option);
        image = Bitmap.createScaledBitmap(image, 267, 200, true);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

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
        Button registerButton = (Button) findViewById(R.id.btn_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView name = (TextView) findViewById(R.id.editText);
                if(name.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "You need to enter a student name",Toast.LENGTH_LONG).show();
                }
                else {

                    Log.v("Test", name.getText().toString().replaceAll(" ", ""));
                    try {
                        myKairos.enroll(image, name.getText().toString().replaceAll(" ", ""), "students", "FULL", "false", "0.25", listener);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
