package com.example.hungcv.test.picker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.example.hungcv.test.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hungcv on 9/23/16.
 */
public class ImagePicker {

    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    public static final int CAMERA_REQUEST_CODE = 6969;
    public static final int GALLERY_REQUEST_CODE = 9696;
    public static final int PICK_IMAGE_REQUEST_CODE = 6868;
    public static final int CROP_REQUEST_CODE = 8686;
    public static final int PERMISSION_REQUEST_CODE = 3939;

    private static final int DEFAULT_CROP_WIDTH = 1000;
    private static final int DEFAULT_CROP_HEIGHT = 1000;

    private final FragmentActivity activity;

    private Fragment fragment;

    private PickType pickType;

    private Uri cropUri;

    private int cropWidth = DEFAULT_CROP_WIDTH;

    private int cropHeight = DEFAULT_CROP_HEIGHT;

    private ImagePickerListener onPickSuccessListener;

    private Uri tempUri;

    private boolean needCropAfterPick;

    private String explainPermissionText;

    private ImagePicker(Fragment fragment) {
        this(fragment.getActivity());
        this.fragment = fragment;
    }

    private ImagePicker(FragmentActivity fragmentActivity) {
        this.activity = fragmentActivity;
    }

    public ImagePicker cropSize(int width, int height) {
        this.cropWidth = width;
        this.cropHeight = height;
        this.needCropAfterPick = true;
        return this;
    }

    public ImagePicker cropFile(Uri imageUri) {
        this.cropUri = imageUri;
        return this;
    }

    public ImagePicker launchFlow(PickType pickType) {

        this.pickType = pickType;

        if (!hasPermission()) {
            return this;
        }

        switch (pickType) {
            case CAMERA:
                openCamera();
                break;
            case GALLERY:
                openGallery();
                break;
            case CROP:
                if (cropUri != null) {
                    openCrop(cropUri);
                }
                break;
            case CAMERA_AND_GALLERY:
                launchGetPicFlow();
                break;
        }
        return this;
    }

    private boolean hasPermission() {
        explainPermissionText = explainPermissionText == null ? "Our app need write storage permission to make handle picture purpose!" : explainPermissionText;
        if (fragment == null) {
            return PermissionUtil.checkPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, explainPermissionText, PERMISSION_REQUEST_CODE);
        } else {
            return PermissionUtil.checkPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, explainPermissionText, PERMISSION_REQUEST_CODE);
        }
    }

    public ImagePicker listener(ImagePickerListener listener) {
        this.onPickSuccessListener = listener;
        return this;
    }

    public ImagePicker needCropAfterPick() {
        this.needCropAfterPick = true;
        return this;
    }

    public ImagePicker addExplainPermissionText(String text) {
        this.explainPermissionText = text;
        return this;
    }

    public ImagePicker addExplainPermissionText(int resId) {
        this.explainPermissionText = activity.getString(resId);
        return this;
    }

    public static ImagePicker with(Fragment fragment) {
        return new ImagePicker(fragment);
    }

    public static ImagePicker with(FragmentActivity fragmentActivity) {
        return new ImagePicker(fragmentActivity);
    }


    private void openGallery() {
        Intent pictureChooseIntent = getPictureChoiceIntent();
        startActivity(pictureChooseIntent, GALLERY_REQUEST_CODE);
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

    private void launchGetPicFlow() {
        Intent galleryIntent = getPictureChoiceIntent();
        Intent chooserIntent = Intent.createChooser(galleryIntent, "Choose Image");

        List cameraIntents = getCameraIntent();
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivity(chooserIntent, PICK_IMAGE_REQUEST_CODE);
    }

    private List<Intent> getCameraIntent() {
        try {
            File file = FileUtils.createImageFile();

            final List<Intent> cameraIntents = new ArrayList<>();
            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = activity.getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(packageName);

                if (Build.VERSION.SDK_INT >= 24) {
                    tempUri = FileProvider.getUriForFile(activity, AUTHORITY, file);
                } else {
                    tempUri = Uri.fromFile(file);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                cameraIntents.add(intent);
            }
            return cameraIntents;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void openCrop(Uri uri) {
        activity.grantUriPermission("com.android.camera", uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(uri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", this.cropWidth);
        cropIntent.putExtra("aspectY", this.cropHeight);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("outputX", this.cropWidth);
        cropIntent.putExtra("outputY", this.cropHeight);
        cropIntent.putExtra("return-data", false);

        Uri outUri = null;
        try {
            outUri = Uri.fromFile(FileUtils.createImageFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        activity.grantUriPermission("com.android.camera", outUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);

        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivity(cropIntent, CROP_REQUEST_CODE);
    }

    private void startActivity(Intent cropIntent, int requestCode) {
        if (fragment != null) {
            fragment.startActivityForResult(cropIntent, requestCode);
        } else {
            activity.startActivityForResult(cropIntent, requestCode);
        }
    }

    public void openCamera() {
        try {
            File file = FileUtils.createImageFile();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (Build.VERSION.SDK_INT >= 24) {
                tempUri = FileProvider.getUriForFile(activity, AUTHORITY, file);
            } else {
                tempUri = Uri.fromFile(file);
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
            startActivity(takePictureIntent, CAMERA_REQUEST_CODE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Create temp image file fail!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    if (needCropAfterPick) {
                        openCrop(tempUri);
                    } else if (onPickSuccessListener != null) {
                        onPickSuccessListener.onPickSuccess(tempUri);
                    }
                    break;
                case GALLERY_REQUEST_CODE:
                    handleGalleryPickImage(data);
                    break;
                case CROP_REQUEST_CODE:
                    if (onPickSuccessListener != null) {
                        onPickSuccessListener.onPickSuccess(data.getData());
                    }
                    break;
                case PICK_IMAGE_REQUEST_CODE:
                    final boolean isCamera = data == null || data.getData() == null;
                    if (isCamera) {
                        openCrop(tempUri);
                    } else {
                        handleGalleryPickImage(data);
                    }
                    break;
            }
        }
    }

    private void handleGalleryPickImage(Intent data) {
        Uri uri = data.getData();
        String path = FileUtils.getRealPath(activity, uri);
        uri = FileProvider.getUriForFile(activity, AUTHORITY, new File(path));
        if (needCropAfterPick) {
            openCrop(uri);
        } else if (onPickSuccessListener != null) {
            onPickSuccessListener.onPickSuccess(uri);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchFlow(pickType);
            }
        }
    }

}
