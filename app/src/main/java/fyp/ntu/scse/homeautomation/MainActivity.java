package fyp.ntu.scse.homeautomation;


import java.util.List;
import java.util.Random;

import fyp.ntu.scse.homeautomation.controller.BtDeviceManager;
import fyp.ntu.scse.homeautomation.model.SensorTag;
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
import android.app.Service;
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

import static android.R.attr.action;


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

/* Byte array generation*/

private byte[] generateRandomImage(int size) {
    byte[] dataBuffer = new byte[size];
    Random rand = new Random();
    rand.nextBytes(dataBuffer);
    return dataBuffer;

}

private void prepareDevice(SensorTagDevice device) {
    BluetoothGattService mOadService = device.getOadService();
    BluetoothGattService mConnControlService = device.getConnControlService();

    /*Characteristics list*/

    List<BluetoothGattCharacteristic> mCharListOad = mOadService.getCharacteristics();
    List<BluetoothGattCharacteristic> mCharListCc = mConnControlService.getCharacteristics();

    String deviceAddress = device.getAccess();

    boolean mServiceOk = mCharListOad.size() == 2 && mCharListCc.size() >= 3;

    if (mServiceOk) {
        BluetoothGattCharacteristic mCharIdentify = mCharListCc.get(0);
        BluetoothGattCharacteristic mCharBlock = mCharListOad.get(1);
        BluetoothGattCharacteristic mCharConnReq = mCharListCc.get(1);

        mCharBlock.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBtLeService.setCharacteristicNotification(deviceAddress, mCharBlock, true);

        if (Build.VERSION.SDK_INT >= 22) {
            mBtLeService.requestConnectionPriority(deviceAddress, BluetoothGatt.CONNECTION_PRIORITY_HIGH);

            mCharConnReq.setValue(getConnectionParameters());
            mBtLeService.writeCharacteristic(deviceAddress, mCharConnReq);

            ProgramInfo programinfo = new ProgramInfo(this, deviceAddress, mCharIdentify, mCharBlock);

            programAdapter.add(programinfo);

        } else {
            mBtLeService.disconnect(deviceAddress);
        }

        /*Android Bluetooth Connection interval = 7.5ms, long enough for OAD*/

        private byte[] getConnectionParameters () {
            byte[] value = {Conversion.loUint16(OAD_CONN_INTERVAL),
                    Conversion.loUint16(OAD_CONN_INTERVAL),
                    Conversion.loUint16(OAD_CONN_INTERVAL),

                    Conversion.hiUint16(OAD_SUPERVISION_TIMEOUT)};
            return value;

        }

        private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
            private ServiceDiscoveryTask.DiscoveryListener discoveryListener = new ServiceDiscoveryTask.DiscoveryListener() {
                @Override
                public void onDiscoveryFinish(ServiceDiscoveryTask.STATUS status, SensorTagDevice device) {
                    if (status == Service.STATUS.SUCESS) {
                        prepareDevice(device);
                    } else {
                        mBtLeService.disconnect(device.getAddress());
                    }
                }
            };

            @Override

            public void onReceive(Context context, Intent intent){
                final String action = intent.getAction();
                final int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS, BluetoothGatt.GATT_SUCCESS);
                final String address = intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS);
                final SensorTagDevice device = BtDeviceManager.getInstance().getDevice(address);

                if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals().getDevice(address));
                if(status == BluetoothGatt.GATT_SUCCESS){
                    new ServiceDiscoveryTask(MainActivity.this,discoveryListener).execute(device);

                }else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)){
                    msg = "Disconnect Status: " + HCIDefines.hciErrorCodeStrings.get(status);

                }
            }

            showToast(msg);

            programAdapter.remove(address);
            mBtLeService.close(address);

            updateTitle();

            if(mBtDeviceManager.isEmpty()){
                startScanActivity();
            }else if(BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)){

                Log.d(TAG, "ACTION_DATA_NOTIFY: (" + address + " )");
                byte[] value = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);

                ProgramInfo programInfo = programAdapter.getItem(address);
                if (uuidStr.equals(programInfo.getCharBlock().getUuid().toString())){

                    //Check object here
                    String blockobj = String.format("%02x%02x", value[1],value[0]);
                    Log.d(TAG, " Received block req: " + blockobj + " from [" + address + "]");

                    programInfo.programBlock();

                    programAdapter.notifyDataSetChanged();

                }

                if(programAdapter.isComplete()){
                    stopProgramming();
                }
            }else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)){
                Log.d(TAG, "ACTION_DATA_WRITE : [" + address + "], Status: " + status);

                if (status != BluetoothLeService.GATT_SUCCESS){
                    String err = "GATT error: status =" + status;
                    Log.e(TAG,err);
                    showToast(err);
                }
            }
        };
    }

}

