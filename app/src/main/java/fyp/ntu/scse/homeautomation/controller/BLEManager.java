package fyp.ntu.scse.homeautomation.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import fyp.ntu.scse.homeautomation.model.ti.BluetoothLeService;
import fyp.ntu.scse.homeautomation.R;

public class BLEManager {

    private final static String TAG = BLEManager.class.getSimpleName();
    private static int MAX_BT_RETRY = 5;

    private static BLEManager mThis;

    private int retryCount = -1;
    private Context mContext;

    // BLE Management
    private boolean mBleSupported = true;
    public static BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeService mBluetoothLeService;
    private IntentFilter mFilter;
    private boolean mScanning;

    public BLEManager(Context context) {
        mThis = this;
        mContext = context;
        initialize();
    }

    public static BLEManager getInstance() {
        return mThis;
    }

    public boolean isBLESupported() {
        return mBleSupported;
    }

    public boolean isScanning() {
        return mScanning;
    }

    private void initialize() {
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mContext, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
            mBleSupported = false;
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to BluetoothAdapter through BluetoothManager.
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, R.string.bt_not_supported, Toast.LENGTH_LONG).show();
            mBleSupported = false;
        }

        mFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mReceiver, mFilter);

        startBluetoothLeService();
    }

    public void destroy() {
        mContext.unregisterReceiver(mReceiver);
        Intent bindIntent = new Intent(mContext, BluetoothLeService.class);
        mContext.stopService(bindIntent);
        mContext.unbindService(mServiceConnection);

        mThis = null;
    }

    public boolean isBtEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public void enableBt() {
        if(retryCount < MAX_BT_RETRY) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(enableIntent);
            retryCount++;
        }
    }

    private void startBluetoothLeService() {
        Log.i(TAG, "starting BLE Service");
        boolean f;

        Intent bindIntent = new Intent(mContext, BluetoothLeService.class);
        mContext.startService(bindIntent);
        f = mContext.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        if (!f) {
            Log.e(TAG, "Bind to BluetoothLeService failed");
        }
    }

    public boolean scanLeDevice(boolean enable, ScanCallback callback) {
        if(!isBtEnabled()) {
            mScanning = false;
            return false;
        }

        BluetoothLeScanner mBtLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (enable) {
            mBtLeScanner.startScan(callback);
            mScanning = true;
            Log.i(TAG, "ScanLeDevice Started");
        } else {
            mBtLeScanner.stopScan(callback);
            mScanning = false;
            Log.i(TAG, "ScanLeDevice Stopped");
        }

        return mScanning;
    }

    public boolean connectBtDevice(BluetoothDevice btDevice) {
        int connState = mBluetoothManager.getConnectionState(btDevice, BluetoothGatt.GATT);

        switch (connState) {
            case BluetoothGatt.STATE_CONNECTED:
                mBluetoothLeService.disconnect(btDevice.getAddress());
                return connectBtDevice(btDevice);
            case BluetoothGatt.STATE_DISCONNECTED:
                boolean ok = mBluetoothLeService.connect(btDevice.getAddress());
                if (!ok) {
                    Log.w(TAG, "Connect failed");
                    return false;
                }
                return true;
            default:
                Log.w(TAG, "Device busy (connecting/disconnecting):" + connState);
                return false;
        }
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                switch (mBluetoothAdapter.getState()) {
                    case BluetoothAdapter.STATE_ON:
                        Log.i(TAG, "Bluetooth turned ON!");

                        //startBluetoothLeService();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.i(TAG, "Bluetooth turned OFF!");

                        enableBt();
                        break;
                }
            } else {
                Log.wtf(TAG,"Unknown action: " + action);
            }
        }
    };

    // Code to manage Service life cycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize BluetoothLeService");
                return;
            }

            final int n = mBluetoothLeService.numConnectedDevices();

            Log.i(TAG, "No of Connection(s):" + n);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };
}
