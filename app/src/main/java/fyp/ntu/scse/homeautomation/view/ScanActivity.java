package fyp.ntu.scse.homeautomation.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fyp.ntu.scse.homeautomation.R;
import fyp.ntu.scse.homeautomation.controller.BLEManager;
import fyp.ntu.scse.homeautomation.controller.BtDeviceManager;
import fyp.ntu.scse.homeautomation.model.ti.BleDeviceInfo;
import fyp.ntu.scse.homeautomation.model.ti.BluetoothLeService;


public class ScanActivity extends Activity implements AdapterView.OnItemClickListener {
    private final static String TAG = ScanActivity.class.getSimpleName();
    private final static short PERMISSION_REQUEST_COARSE_LOCATION = 0x1;
    public final static short RESULT_FINISH = 0x2;

    private BLEManager mBtLeManager;
    private List<BleDeviceInfo> mDeviceInfoList;
    private ArrayAdapter<BleDeviceInfo> mDeviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        checkLocationPermissions();

        ListView mDeviceList = (ListView) findViewById(R.id.device_list);
        mDeviceList.setOnItemClickListener(this);

        mDeviceInfoList = new ArrayList<BleDeviceInfo>();
        mDeviceListAdapter = new ArrayAdapter<BleDeviceInfo>(ScanActivity.this, android.R.layout.simple_list_item_1, mDeviceInfoList);
        mDeviceList.setAdapter(mDeviceListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBtLeManager = BLEManager.getInstance();
        if(mBtLeManager == null) {
            mBtLeManager = new BLEManager(getApplicationContext());
        }

        registerReceiver(mLeGATTReceiver, new IntentFilter(BluetoothLeService.ACTION_GATT_CONNECTED));

        if(mBtLeManager.isBtEnabled()) {
            scan(true);
        } else {
            mBtLeManager.enableBt();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mLeGATTReceiver);

        if(mBtLeManager.isBtEnabled()) {
            scan(false);
        }
    }

    /**
     * OnItemClickListener
     * <p>
     * Callback function that is called when an item on the DeviceList
     * (ListView) is clicked
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BleDeviceInfo bleDeviceInfo = mDeviceInfoList.get(position);
        BluetoothDevice btDevice = bleDeviceInfo.getBluetoothDevice();

        if(mBtLeManager.connectBtDevice(btDevice)) {
            scan(false);
        }
    }

    private void scan(boolean start) {
        if(start) {
            if(!mBtLeManager.isScanning()) {
                mDeviceListAdapter.clear();
                mBtLeManager.scanLeDevice(true, mLeScanCallback);
                setProgressBarIndeterminateVisibility(true);
            }
        } else {
            if(mBtLeManager.isScanning()) {
                mBtLeManager.scanLeDevice(false, mLeScanCallback);
                setProgressBarIndeterminateVisibility(false);
            }
        }
    }

    private boolean deviceInfoExists(String address) {
        return findDeviceInfo(address) != null;
    }

    private BleDeviceInfo findDeviceInfo(BluetoothDevice device) {
        return findDeviceInfo(device.getAddress());
    }

    private BleDeviceInfo findDeviceInfo(String address) {
        for(BleDeviceInfo deviceInfo : mDeviceInfoList) {
            BluetoothDevice btDevice = deviceInfo.getBluetoothDevice();
            if(btDevice.getAddress().equals(address)) {
                return deviceInfo;
            }
        }
        return null;
    }

    private BroadcastReceiver mLeGATTReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                // GATT connect
                int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS, BluetoothGatt.GATT_FAILURE);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    String address = intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS);

                    Log.i(TAG, "GATT Connected");
                    Toast.makeText(ScanActivity.this, "Connected to:" + address, Toast.LENGTH_LONG).show();

                    BleDeviceInfo bleDeviceInfo = findDeviceInfo(address);

                    mDeviceListAdapter.remove(bleDeviceInfo);
                    scan(false);

                    ScanActivity.this.setResult(RESULT_OK);
                    finish();
                } else {
                    String error = "Connect failed. Status: " + status;
                    Log.e(TAG, error);

                    Toast.makeText(ScanActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private ScanCallback mLeScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.i(TAG, result.toString());

            if(result.getDevice() != null) {
                BluetoothDevice device = result.getDevice();
                if(BtDeviceManager.checkDeviceFilter(device.getName())) {
                    if (!deviceInfoExists(device.getAddress())) {
                        BleDeviceInfo deviceInfo = new BleDeviceInfo(device, result.getRssi());
                        mDeviceInfoList.add(deviceInfo);
                    } else {
                        // Already in list, update RSSI info
                        BleDeviceInfo deviceInfo = findDeviceInfo(device);
                        deviceInfo.updateRssi(result.getRssi());
                    }

                    mDeviceListAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "LeScan failed with an error code: " + errorCode);
        }

    };

    /**
     * Location permission is required to be granted on Android 6.0 (Marshmallow)
     * <p>
     * This checks for the required permission
     * and request for the user's permission to access location
     * if the permission has not been granted.
     */
    @TargetApi(23)
    private void checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ScanActivity.this);
                dialogBuilder.setTitle("This app needs location access");
                dialogBuilder.setMessage("Please grant location access so this app can detect SensorTags.");
                dialogBuilder.setPositiveButton(android.R.string.ok, null);
                dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[] {
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        }, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                dialogBuilder.show();
            }
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    dialogBuilder.setCancelable(false);
                    dialogBuilder.setTitle("Functionality limited");
                    dialogBuilder.setMessage("Since location access has not been granted, this app will not be able to discover SensorTags.");
                    dialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialogBuilder.setNegativeButton("Close App", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_FINISH);
                            finish();
                        }
                    });
                    dialogBuilder.show();
                }
                break;
        }
    }
}
