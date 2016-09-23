package com.example.hungcv.test;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.hungcv.test.picker.ImagePicker;
import com.example.hungcv.test.picker.ImagePickerListener;
import com.example.hungcv.test.picker.PickType;

public class MainActivity extends AppCompatActivity implements ImagePickerListener {

    private static final int IMAGE_WIDTH = 900;
    private static final int IMAGE_HEIGHT = 1400;

    ImageView imageView;

    ImagePicker imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        findViewById(R.id.btnCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(PickType.CAMERA);
            }
        });

        findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(PickType.GALLERY);
            }
        });

        findViewById(R.id.btn_choice_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(PickType.CAMERA_AND_GALLERY);
            }
        });
    }

    private void pickImage(PickType pickType) {
        imagePicker = ImagePicker.with(this).listener(this).cropSize(IMAGE_WIDTH, IMAGE_HEIGHT).launchFlow(pickType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null) {
            imagePicker.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (imagePicker != null) {
            imagePicker.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onPickSuccess(Uri uri) {
        imageView.setImageURI(uri);
    }
}
