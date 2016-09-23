package com.example.hungcv.test;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String AUTHORITY = "com.example.hungcv.test.provider";

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final java.lang.String JPEG_FILE_SUFFIX = ".jpg";
    private static final int GET_PIC_REQUEST_CODE = 4;
    private static final int CAPTURE_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 3;
    private static final int PERMISSION_REQUEST_CODE = 5;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        findViewById(R.id.btnCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermission()) {
                    openCameraIntent();
                }
            }
        });

        findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermission()) {
                    openGallery();
                }
            }
        });

        findViewById(R.id.btn_choice_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermission()) {
                    launchGetPicFlow();
                }
            }
        });
    }

    private void launchGetPicFlow() {
        Intent galleryIntent = getPictureChoiceIntent();
        Intent chooserIntent = Intent.createChooser(galleryIntent, "Choose Image");

        List cameraIntents = getCameraIntent();
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, GET_PIC_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    boolean hasPermission() {
        String writeExternalStorage = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String needPermissionExplain = "Our app need write storage permission to make handle picture purpose!";
        return PermissionUtil.checkPermission(this, writeExternalStorage, needPermissionExplain, PERMISSION_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(String writeExternalStorage) {
        requestPermissions(new String[]{writeExternalStorage}, PERMISSION_REQUEST_CODE);
    }

    private void openGallery() {
        Intent pictureChooseIntent = getPictureChoiceIntent();
        startActivityForResult(pictureChooseIntent, GALLERY_REQUEST_CODE);
    }

    @NonNull
    private Intent getPictureChoiceIntent() {
        Intent pictureChooseIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pictureChooseIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pictureChooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            pictureChooseIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        } else {
            pictureChooseIntent = new Intent(Intent.ACTION_GET_CONTENT);
        }
        pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pictureChooseIntent.setType("image/*");
        return pictureChooseIntent;
    }

    Uri uri;

    public void openCameraIntent() {
        try {
            File file = createImageFile();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(this, AUTHORITY, file);
            } else {
                uri = Uri.fromFile(file);
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(takePictureIntent, CAPTURE_REQUEST_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAPTURE_REQUEST_CODE:
                    openCrop(uri, 900, 1400);
                    break;
                case GALLERY_REQUEST_CODE:
                    handleGalleryPickImage(data);
                    break;
                case CROP_REQUEST_CODE:
                    imageView.setImageURI(data.getData());
                    break;
                case GET_PIC_REQUEST_CODE:
                    final boolean isCamera = data == null || data.getData() == null;
                    if (isCamera) {
                        openCrop(uri, 900, 1400);
                    } else {
                        handleGalleryPickImage(data);
                    }
                    break;
            }
        }
    }

    private void handleGalleryPickImage(Intent data) {
        Uri uri = data.getData();
        String path = FileUtils.getRealPath(this, uri);
        uri = FileProvider.getUriForFile(this, AUTHORITY, new File(path));
        openCrop(uri, 900, 1400);
    }

    private static File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, getAlbumDir());
    }

    private static File getAlbumDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File storageDir = new File(dir, "Test");

            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    return null;
                }
            }

            return storageDir;

        } else {
            return null;
        }
    }

    void openCrop(Uri uri, int width, int height) {
        grantUriPermission("com.android.camera", uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri
        cropIntent.setDataAndType(uri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", width);
        cropIntent.putExtra("aspectY", height);
        cropIntent.putExtra("scale", true);
        //indicate output X and Y
        cropIntent.putExtra("outputX", width);
        cropIntent.putExtra("outputY", height);
        //retrieve data on return
        cropIntent.putExtra("return-data", false);

        Uri outUri = null;
        try {
            outUri = Uri.fromFile(createImageFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        grantUriPermission("com.android.camera", outUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);

        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivityForResult(cropIntent, CROP_REQUEST_CODE);
    }

    public List<Intent> getCameraIntent() {
        try {
            File file = createImageFile();

            final List<Intent> cameraIntents = new ArrayList<>();
            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(packageName);

                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(this, AUTHORITY, file);
                } else {
                    uri = Uri.fromFile(file);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                cameraIntents.add(intent);
            }
            return cameraIntents;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
