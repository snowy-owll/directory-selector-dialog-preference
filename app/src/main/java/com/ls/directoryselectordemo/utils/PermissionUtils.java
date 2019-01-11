package com.ls.directoryselectordemo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PermissionUtils {
    public static final int REQUEST_READWRITE_STORAGE = 0;

    public static boolean checkExternalStoragePermission(Context context) {
        return (ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE)
                == PERMISSION_GRANTED);
    }

    public static void requestExternalStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_READWRITE_STORAGE);
    }

    public static void requestExternalStoragePermission(Fragment fragment) {
        fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_READWRITE_STORAGE);
    }
}
