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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    String localIp = "192.168.1.66";
//    String localIp = "172.20.10.2";
    public  CallHTTPRequest(Context context){
        this.context = context;
        this.imutils = new Imutils();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
        Log.i("IP address", getIPAddress(true));
    }
    public void setIpAddress(String ipAdd){
        try {
            String[] numberInIP = ipAdd.split("\\.");
            for (String s : numberInIP) {
                int valid = Integer.parseInt(s);
            }
            if(!ipAdd.isEmpty() && numberInIP.length == 4){
                this.localIp = ipAdd;
                Log.i("valid ip", "valid ip");
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public Intent getCaptureImageIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
                String dataStringJson = "{\"image\":\"" + base64ImageCard +"\"}";
                RequestBody body = RequestBody.create(dataStringJson, JSON);
                Log.i("localIp", localIp);
                try {
                    Request request = new Request.Builder()
                            .url("http://" + localIp + ":5000/postimg")
                            .post(body)
                            .build();
                    Log.i("build request", "success");

                    Response response = client.newCall(request).execute();
                    return response;
                }catch (Exception e){
//                    e.printStackTrace();
                }
            }
        }

        return null;
    }
    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }
}
