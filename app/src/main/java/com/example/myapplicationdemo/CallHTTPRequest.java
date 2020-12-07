package com.example.myapplicationdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CallHTTPRequest {
    public Context context;
    Imutils imutils;
    public String camFileUrl;
    public int REQUEST_CODE = 123;
    public static final int REQUEST = 112;
//    public String base64ImageCard;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;
//    String localIp = "192.168.1.66";
    String localIp = "192.168.137.68";
    public  CallHTTPRequest(Context context){
        this.context = context;
        this.imutils = new Imutils();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build();
    }
    public Intent getCaptureImageIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        method = "POST";
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

                for (String p:PERMISSIONS) {
                    int res = context.checkCallingOrSelfPermission(p);
                    if(res == PackageManager.PERMISSION_DENIED){

                        ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, REQUEST );
                    }
                }
            }
            File imageFile = this.imutils.createImageFile();
            Uri outUri = FileProvider.getUriForFile(Objects.requireNonNull(context),
                    BuildConfig.APPLICATION_ID + ".provider", imageFile);
            this.camFileUrl = imageFile.getAbsolutePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
            Log.i("putExtra", "putExtra");
//            startActivityForResult(intent, REQUEST_CODE);
            return intent;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Response getIDCardInfo(){
        File file = new File(this.camFileUrl);
        if(!this.camFileUrl.isEmpty() && file.exists()){
            Bitmap bm = BitmapFactory.decodeFile(this.camFileUrl);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            String base64ImageCard = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            if(!base64ImageCard.isEmpty()){
                base64ImageCard = base64ImageCard.replace("\n", "");
                Log.i("SuccessPOST", base64ImageCard);
                String dataStringJson = "{\"image\":\"" + base64ImageCard +"\"}";
                RequestBody body = RequestBody.create(dataStringJson, JSON);
                try {
                    Request request = new Request.Builder()
                            .url("http://" + localIp + ":5000/postimg")
                            .post(body)
                            .build();
                    Log.i("build request", "success");

                    Response response = client.newCall(request).execute();
                    return response;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
