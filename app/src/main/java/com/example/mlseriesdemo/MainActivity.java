package com.example.mlseriesdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mlseriesdemo.image.FaceDetectionActivity;

public class MainActivity extends AppCompatActivity {

    Button button_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonexit = findViewById(R.id.button_exit);
        buttonexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfoAlert("Вы хотите закрыть приложение?");
            }
        });
    }

    public void onGotoFaceDetection(View view){
        // start the image helper activity
        Intent intent = new Intent(this, FaceDetectionActivity.class);
        startActivity(intent);
    }

    private void showInfoAlert(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Подсказка выхода")
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton("Конечно", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
