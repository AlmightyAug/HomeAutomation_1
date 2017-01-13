package fyp.ntu.scse.homeautomation;


import java.util.List;
import java.util.Random;

import fyp.ntu.scse.homeautomation.controller.BtDeviceManager;
import fyp.ntu.scse.homeautomation.model.ti.BluetoothLeService;
import fyp.ntu.scse.homeautomation.BtDeviceManager;
import fyp.ntu.scse.homeautomation.BLEManager;
import fyp.ntu.scse.homeautomation.controller.ServiceDiscoveryTask;
import fyp.ntu.scse.homeautomation.model.ProgramInfo;
import fyp.ntu.scse.homeautomation.controller.BluetoothLeService;
import fyp.ntu.scse.homeautomation.model.ti.Conversion;
import fyp.ntu.scse.homeautomation.model.ti.GattInfo;
import fyp.ntu.scse.homeautomation.model.ti.HCIDefines;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static short REQ_SCAN_ACT = 0;
    private final static Integer[] file_sizes = {0x0010, 0x0100, (32*0x1000)};
    private final static Integer[] distances = {50,100,500,1000};

    //Parameters for MainActivity.java
    /*Intervals between each connection
     */

    private static final short OAD_CONN_INTERVAL = 10;

    /*Timeout value*/
    private static final short OAD_SUPERVISION_TIMEOUT = 1000;
    private int fileSize = file_sizes[0];
    private boolean mProgramming = false;
    private ProgramAdapter programAdapter;

    /*Bluetooth Low Energy*/
    private BluetoothLeService mBtLeService;
    private BtLeManager mBtLeManager;
    private BtDeviceManager mBtDeviceManager;
    private static boolean mIsReceiving = false;
    private static boolean mIsScanning = false;

    private Toast avocado;
    private Spinner distanceChooser;

    private void showToast(String msg){

        if(avocado!= null){
            avocado.cancel();
        }

        avocado = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        avocado.show();


    }

    public MainActivity(){
        mBtLeManager = BtLeManager.getInstance();
        mBtDeviceManager = BtDeviceManager.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(mBtLeManager == null){
            mBtLeManager = new BtLeManager(getApplicationContext());

        }

        if (!mIsReceiving) {
            IntentFilter filter = new IntentFilter();

            filter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
            filter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
            filter.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
            filter.addAction(BluetoothLeService.ACTION_DATA_WRITE);
            filter.addAction(BluetoothLeService.ACTION_DATA_READ);
            registerReceiver(mGattUpdateReceiver, filter);

            mIsReceiving = true;
        }

        /* Bluetooth Generic Attribute Database*/

        Resources res = getResources();
        XmlResourceParser xpp = res.getXml(R.xml.gatt_uuid);
        new GattInfo(xpp);

        if(mBtDeviceManager.isEmpty()){
            startScanActivity();
        }
    }




}

/*After clicking on the buttons*/

class ScanActivity implements Bluetooth.Scan {

    public static Scan (Bundle savedInstanceState){
        Scan Scanobj = new Scan();
        Scanobj.Bluetooth();
        setContentView(R.layout.activity_scan);
    }

    public static fyp.ntu.scse.homeautomation.controller.BLEManager {
        Alert Alertobj = new Alert();
        Alertobj.ScanActivity();
        setContentView(R.layout.sensor_cardview);
    }

}
