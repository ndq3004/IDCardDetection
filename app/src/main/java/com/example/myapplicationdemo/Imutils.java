package com.example.myapplicationdemo;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Imutils {
    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        if(!storageDir.isDirectory()){
            Log.i("createImageFile_name: ", "make dir!");
            storageDir.mkdir();
            Log.i("createImageFile_name: ", "done make dir!");
        }
        Log.i("createImageFile_name: ", imageFileName);
        Log.i("createImageFile_dir: ", storageDir.getPath() + " \n " + storageDir.getAbsolutePath());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        mCamFileName = image.getAbsolutePath();
        return image;
    }
}
