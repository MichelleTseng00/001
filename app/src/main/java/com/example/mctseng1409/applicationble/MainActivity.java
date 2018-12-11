package com.example.mctseng1409.applicationble;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;


    // 參數設定
    private static final int REQUEST_ENABLE_BT = 1; //startActivityForResult 那一行按是的話回傳1
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1; // Location 權限
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2;
    private static final long SCAN_PERIOD = 30000;// Stops scanning after 10 seconds.
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mHandler = new Handler(); //???



        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(this, R.string.ble_supported, Toast.LENGTH_SHORT).show();
        }


        //如果 API level 是大於等於 M及Marshmallow 23(Android 6.0) 時
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判斷是否具有權限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //判斷是否需要向用戶解釋為什麼需要申請該權限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    //創建Dialog解釋視窗
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("自Android 6.0開始需要打開位置權限才可以搜索到Ble設備")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                            REQUEST_CODE_ACCESS_COARSE_LOCATION);
                                }
                            }).show();//創建Dialog解釋視窗



                }else {
                    //請求權限
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_CODE_ACCESS_COARSE_LOCATION);
                }
            }
        }

    




        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //If BLE is off, turn it on.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            //開啟的話開始掃描
            scanLeDevice(true);
        }
    } //onCreate


    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void scanLeDevice(final boolean enable) {

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {   //這裡面是過10000秒之後要做的事
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    Log.i(TAG, "停止搜索");

                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            Log.v(TAG, "開始搜索");

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Log.v(TAG, "Log RUN3");
        }

    }//scanLeDevice


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device,  int rssi,
                                      byte[] scanRecord) {

                    // 搜尋回饋
                    Log.d("TAG","BLE device : " + device.getName());
                    Log.d("TAG","BLE device : " + device.getAddress());
                    Log.i(TAG, "找到了");

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // 搜尋回饋
//                            Log.d("TAG","BLE device : " + device.getName());
//                            Log.i(TAG, "找到了");
//                        }
//                    });

                }
            };

}

