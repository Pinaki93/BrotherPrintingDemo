package com.passthruu.demo.printdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.NetPrinter;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 1000;
    private static final int PICK_IMAGE = 2000;
    private String path="";
    private NetPrinter[] netPrinters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        if(info!=null){
            Log.d(TAG, "onCreate: info:"+info.ipAddress+"\nmac address:"+info.macAddress);
        }
        */

    }

    public void scanDevices(View view) {

        AsyncTask.execute(new Runnable() {
            public String s;

            @Override
            public void run() {
                Printer printer=new Printer();
                netPrinters = printer.getNetPrinters("QL-720NW");

                s = "printers count:" + netPrinters.length+"\n----------------------\n";

                for(int i=0;i<netPrinters.length;i++){
                    NetPrinter netPrinter = netPrinters[i];
                    s+=("IP Address:"+netPrinter.ipAddress+"\nMac Address:"+netPrinter.macAddress+"\nDevice name:"+netPrinter.modelName+"\nLabel:"+"\n--------------\n");

                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.tv)).setText(s);
                    }
                });

            }
        });

    }

    void printImage(final String ipAddress, final String macAddress){


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.tv)).setText("starting printing");
                    }
                });

                Printer myPrinter = new Printer();

                PrinterInfo printInfo = new PrinterInfo();
                printInfo.printerModel = PrinterInfo.Model.QL_720NW ;
                printInfo.port = PrinterInfo.Port. NET ;
                printInfo.ipAddress = ipAddress;
                /*printInfo.macAddress=macAddress;*/
                printInfo.labelNameIndex=15;
                printInfo.orientation= PrinterInfo.Orientation.LANDSCAPE;
                myPrinter.setPrinterInfo(printInfo);
                myPrinter.startCommunication();



//                PrinterStatus status = myPrinter.printImage(bitmap);
                PrinterStatus status = myPrinter.printFile(path);


                String status_code = ""+status.errorCode;
                Log.d(TAG, "printImage: status code:"+status_code);

                final String s="Status code:"+status;

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.tv)).setText(s);
                    }
                });

                myPrinter.endCommunication();
            }
        });



    }

    public void printCopy(View view) {

        NetPrinter object = netPrinters[0];

        String ipAddress=object.ipAddress;
        String macAddress=object.macAddress;

        printImage(ipAddress,macAddress);



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isExternalStoragePermissionGranted() {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            return true;
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_EXTERNAL_STORAGE_PERMISSION);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        pickImageFromGallery();
    }

    public void pickImage(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(isExternalStoragePermissionGranted()){
                pickImageFromGallery();
            }
        }else {
            pickImageFromGallery();
        }
    }

    private void pickImageFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Uri uri=data.getData();
            path=FileUtils.getPath(this,uri);

            ((TextView)findViewById(R.id.tvPickedImagePath)).setText(path);
        }
    }
}
