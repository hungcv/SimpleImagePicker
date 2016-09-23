package com.example.hungcv.test.picker;

import android.net.Uri;

/**
 * Created by hungcv on 9/23/16.
 */

public interface ImagePickerListener {
    /**
     * Return Uri after pick image successfully
     *
     * @param uri
     */
    void onPickSuccess(Uri uri);
}
