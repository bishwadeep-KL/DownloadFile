package com.example.downloadfile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    Button btn_download, btn_unzip, btn_view, btn_filePicker;
    TextView txt_viewPath;
    DownloadManager downloadManager;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_download = findViewById(R.id.btn_download);
        btn_unzip = findViewById(R.id.btn_unzip);
        btn_view = findViewById(R.id.btn_view);
        btn_filePicker = findViewById(R.id.btn_filePicker);
        txt_viewPath = findViewById(R.id.txt_viewPath);


        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions,PERMISSION_STORAGE_CODE);
                    }
                    else{
                        startDownloading();
                    }
                }
                else{

                    startDownloading();
                }


            }
        });

        btn_unzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String filePath = String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)+"/zip_2MB.zip");
//                Toast.makeText(MainActivity.this,filePath,Toast.LENGTH_LONG).show();

                String zipFile = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS)+"/zip_2MB.zip"; //your zip file location
                String unzipLocation = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS) + "/unzippedtestNew/"; // destination folder location
                DecompressFast df= new DecompressFast(zipFile, unzipLocation);
                df.unzip();
                Toast.makeText(MainActivity.this,"unzip completed",Toast.LENGTH_LONG).show();



            }
        });

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                startActivity(view);
            }
        });

        btn_filePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filepickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                filepickerIntent.setType("*/*");
                startActivityForResult(filepickerIntent, 10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 10:
                if(resultCode==RESULT_OK){
                    String path = data.getData().getPath();
                    txt_viewPath.setText(path);

                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    startDownloading();
                }
                else{
                    Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void startDownloading(){
        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        Uri url = Uri.parse("https:/file-examples.com/wp-content/uploads/2017/02/zip_2MB.zip");
        DownloadManager.Request request = new DownloadManager.Request(url);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.allowScanningByMediaScanner();
        Long reference =  downloadManager.enqueue(request);
    }
}
