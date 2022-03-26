package com.example.mlseriesdemo.helpers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mlseriesdemo.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImageHelperActivity extends AppCompatActivity {

    private int REQUEST_PICK_IMAGE = 1000;
    private int REQUEST_CAPTURE_IMAGE = 1001;

    private ImageView inputImageView;
    private TextView outputTextView;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_helper);

        inputImageView = findViewById(R.id.imageViewInput);
        outputTextView = findViewById(R.id.textViewOutput);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    public void onPickImage(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    public void onStartCamera(View view){
        photoFile = createPhotoFile();

        Uri fileUri = FileProvider.getUriForFile(this, "com.example.fileprovider", photoFile);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    }

    private File createPhotoFile() {
        File photoFileDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ML_IMAGE_HELPER");

        if(!photoFileDir.exists()){
            photoFileDir.mkdirs();
        }

        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(photoFileDir.getPath() + File.separator + name);
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_PICK_IMAGE){
                Uri uri = data.getData();
                Bitmap bitmap = loadFromUri(uri);
                inputImageView.setImageBitmap(bitmap);
                runClassification(bitmap);

            }else if (requestCode == REQUEST_CAPTURE_IMAGE){
                Log.d("ML", "received callback from camera");
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                inputImageView.setImageBitmap(bitmap);
                runClassification(bitmap);
            }
        }
    }

    private Bitmap loadFromUri(Uri uri){
        Bitmap bitmap = null;

        try {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1){
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                bitmap = ImageDecoder.decodeBitmap(source);

            }else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    protected void runClassification(Bitmap bitmap){

    }

    protected TextView getOutputTextView(){ return outputTextView; }

    protected ImageView getInputImageView(){ return inputImageView; }

    protected void drawDetectionResult(List<BoxWithLabel> boxes, Bitmap bitmap){
        Bitmap outputBimap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(outputBimap);
        Paint penRect = new Paint();
        penRect.setColor(Color.RED);
        penRect.setStyle(Paint.Style.STROKE);
        penRect.setStrokeWidth(8f);

        Paint penLabel = new Paint();
        penLabel.setColor(Color.YELLOW);
        penLabel.setStyle(Paint.Style.FILL_AND_STROKE);
        penLabel.setTextSize(96f);
        penLabel.setStrokeWidth(2f);

        for (BoxWithLabel boxWithLabel : boxes) {
            canvas.drawRect(boxWithLabel.rect, penRect);

            //Rect
            Rect labelSize = new Rect(0,0,0,0);
            penLabel.getTextBounds(boxWithLabel.label, 0,boxWithLabel.label.length(),labelSize);

            float fontSize = penLabel.getTextSize() * boxWithLabel.rect.width() / labelSize.width();
            if (fontSize < penLabel.getTextSize()){
                penLabel.setTextSize(fontSize);
            }

            canvas.drawText(boxWithLabel.label, boxWithLabel.rect.left, boxWithLabel.rect.top + labelSize.height(), penLabel);
        }

        getInputImageView().setImageBitmap(outputBimap);
    }
}