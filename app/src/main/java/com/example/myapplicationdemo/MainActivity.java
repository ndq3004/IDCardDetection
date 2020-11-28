package com.example.myapplicationdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Security;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

//import ok
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ImageView imgCap;
    Button btnCap;
    Button btnGet;
    TextView info;
    int REQUEST_CODE = 123;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;
    Thread thread;
    String method = "GET";
    String dataImage;
    Uri image;
    String mCamFileName;
    String resText;
    private Context mContext = MainActivity.this;
    private static final int REQUEST = 112;
    File sendFile;
    CallHTTPRequest callHTTPRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        imgCap = (ImageView) findViewById(R.id.imageView);
        btnCap = (Button) findViewById(R.id.button1);
        btnGet = (Button) findViewById(R.id.button2);
        info = (TextView) findViewById(R.id.textView);

        callHTTPRequest = new CallHTTPRequest(mContext);

        btnCap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = callHTTPRequest.getCaptureImageIntent();
                mCamFileName = callHTTPRequest.camFileUrl;
                startActivityForResult(intent, callHTTPRequest.REQUEST_CODE);
            }
        });
        btnGet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            }
        });
    }

    private <T> Iterable<T> iterate(final Iterator<T> i){
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return i;
            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i("onActivityResult", "resultCode: " + Integer.toString(resultCode));
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CODE){
                if(data != null){
                    image = data.getData();
                    imgCap.setImageURI(image);
                    imgCap.setVisibility(View.VISIBLE);
                }
                if(image == null && mCamFileName != null){
                    image = Uri.fromFile(new File(mCamFileName));
                    imgCap.setImageURI(image);
                    imgCap.setVisibility(View.VISIBLE);
                }
            }
        }
        File file = new File(mCamFileName);
        if(!file.exists()){
            file.mkdir();
        }else{
            Response response = callHTTPRequest.getIDCardInfo();
            Log.i("reponse get", "respnse");
            if(response != null){
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    jsonObject = jsonObject.getJSONObject("predictions");
                    jsonObject = jsonObject.getJSONObject("infomation");
//                    for(String key : iterate(jsonObject.keys()))
//                    {
//                        System.out.println(key);
//                        System.out.println(jsonObject.getString(key));
//                    }
                    System.out.println(jsonObject.toString());

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}