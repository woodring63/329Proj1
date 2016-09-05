package com.example.evan.opencv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PhotoDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);
        Intent intent = getIntent();
        String filePath = intent.getExtras().getString("filename");
        String name = intent.getExtras().getString("studentName");
        BitmapFactory.Options option = new BitmapFactory.Options();
        Bitmap image = BitmapFactory.decodeFile(filePath, option);
        image = Bitmap.createScaledBitmap(image, image.getWidth(), image.getHeight(), true);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(image , 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        ImageView view = (ImageView) findViewById(R.id.imageView);
        view.setImageBitmap(rotatedBitmap);
        TextView nameView = (TextView) findViewById(R.id.studentName);
        nameView.setText(name);
    }
}
