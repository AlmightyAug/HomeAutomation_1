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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        programAdapter = new ProgramAdapter(this);
        ListView listview = (ListView) findViewById(MainActivity);
        listview.setAdapter(ProgramAdapter);

        Spinner fileChooser = (Spinner) findViewById(R.mipmap);
        fileChooser.setOnItemSelectedListener newAdapterView.OnItemSelectedListener){
            @Override

                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                fileSize = file_sizes [position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            };
        }
        ArrayAdapter <Integer> fileAdapter = new ArrayAdapter<Integer>(MainActivity.this, android.R.layout.simple_list_item_1, file_sizes);
        fileChooser.setAdapter(fileAdapter);

        distanceChooser.setAdapter(distanceAdapter);


    }

    @Override
    protected void ActivityOutput(int requestCode, int outputCode, Intent data){
        super.ActivityOutput(requestCode, outputCode, data);

        if (requestCode==REQ_SCAN_ACT){
            mIsScanning = false;

            if(resultCode == RESULT_OK){
                mBtLeService = BluetoothLeService.getInstance();
                mBtDeviceManager.discoverServices();

            }else if (outputCode == ScanActivity.RESULT_FINISH){
                finish();
            }
        }
    }

    @Override
    protected void onResume(){
        super.onPostResume();
        updateTitle();
    }

    @Override
    protected void onPause(){
        super.onPause();
        updateTitle();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(mIsReceiving){
            unregisterReceiver(mGattUpdateReceiver);
            mIsReceiving = false;
        }

        mBtLeManager.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        /*Put up menu items for use in Action Bar*/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getitemId()){
            case.R.id.action_add:
                startScanActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

/*Getting data from sensors on TI SensorTag*/

private void startScanActivity(){
    if(!mIsScanning){
        Intent scanIntent = new Intent(MainActivity.this, ScanActivity.class);
        startActivityForResult(scanIntent, REQ_SCAN_ACT);
        mIsScanning = true;
    }
}

private void updateTitle() {
    int devCount = mBtDeviceManager.getNoOfDevices();
    setTitle(devCount + "device(s) connected");

    Log.d(TAG, devCount + "device(s) connected");
}

    /*Activate scanning and collecting of data when button is clicked*/
public void onStart(View v) {
    if (mProgramming){
        stopProgramming();

    }else{
        startProgramming();
    }
}

private void startProgramming(){
    byte[] mImage = generateRandomImage(fileSize);

    int distance = (Integer) distanceChooser.getSelectedItem();
    programAdapter.startProgramming(mImage, distance);

    TextView btnStart = (TextView) findViewById(R.id.sensor_RecyclerView);
    btnStart.setText("Stop Programming");

    mProgramming = true;
}

private void stopProgramming(){
    programAdapter.stopProgramming();

    TextView btnStart = (TextView) findViewById(R.id.sensor_RecyclerView);

    btnStart.setText("Start Programming");

    mProgramming = false;

}


