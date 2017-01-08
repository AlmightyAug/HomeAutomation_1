package fyp.ntu.scse.homeautomation.controller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import fyp.ntu.scse.homeautomation.model.SensorTag;
import fyp.ntu.scse.homeautomation.model.ti.BluetoothLeService;
import fyp.ntu.scse.homeautomation.model.ti.GattInfo;
import fyp.ntu.scse.homeautomation.model.ti.profiles.*;

public class ServiceDiscoveryTask extends AsyncTask<SensorTag, Integer, ServiceDiscoveryTask.STATUS>{
    private final static String TAG = ServiceDiscoveryTask.class.getSimpleName();

    public interface DiscoveryListener {
        public void onDiscoveryFinish(STATUS status, SensorTag device);
    }

    public enum STATUS {
        SUCCESS, NO_SERVICES, NO_CHARACTERISTICS
    }

    /* target api version > 18 */
    private int maxNotifications = 7;
    private int nrNotificationsOn = 0;

    private int totalServices = 0;
    private int totalCharacteristics = 0;
    private int servicesDiscovered = 0;

    private DiscoveryListener listener;
    private SensorTag device;
    private ProgressDialog progressDialog;

    private Context mContext;

    public ServiceDiscoveryTask(Context context, DiscoveryListener listener) {
        this.listener = listener;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Discovering Services");
        progressDialog.setMessage("");
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.show();

        IntentFilter fi = new IntentFilter();
        fi.addAction(BluetoothLeService.ACTION_DATA_READ);
        mContext.registerReceiver(mGattUpdateReceiver, fi);
    }

    @Override
    protected ServiceDiscoveryTask.STATUS doInBackground(SensorTag... params) {
        device = params[0];

        BluetoothGatt mBluetoothGatt = device.getBluetoothGatt();
        List <BluetoothGattService> serviceList = mBluetoothGatt.getServices();

        if(serviceList.isEmpty()) {
            return STATUS.NO_SERVICES;
        } else {
            totalServices = serviceList.size();

            Log.d(TAG, "totalServices: " + totalServices);

            for(servicesDiscovered = 0; servicesDiscovered < totalServices; servicesDiscovered++) {
                publishProgress(0);

                BluetoothGattService s = serviceList.get(servicesDiscovered);
                List<BluetoothGattCharacteristic> c = s.getCharacteristics();
                totalCharacteristics += c.size();

                if (c.isEmpty()) {
                    Log.e(TAG, "No characteristics found for service " + s.getUuid());
                } else {
                    String address = device.getBluetoothGatt().getDevice().getAddress();
                    BaseProfile profile = null;
                    if(HumidityProfile.isCorrectService(s)) {
                        Log.d(TAG,"Found Humidity !");
                        profile = new HumidityProfile(address, c);
                    } else if(LuxometerProfile.isCorrectService(s)) {
                        Log.d(TAG,"Found Lux !");
                        profile = new LuxometerProfile(address, c);
                    } else if(SimpleKeysProfile.isCorrectService(s)) {
                        Log.d(TAG,"Found Simple Keys !");
                        profile = new SimpleKeysProfile(address, c);
                    } else if (BarometerProfile.isCorrectService(s)) {
                        Log.d(TAG,"Found Barometer !");
                        profile = new BarometerProfile(address, c);
                    } else if (TemperatureProfile.isCorrectService(s)) {
                        Log.d(TAG,"Found Ambient Temperature !");
                        profile = new TemperatureProfile(address, c);
                    } else if (MovementProfile.isCorrectService(s)) {
                        Log.d(TAG,"Found Motion [Movement]!");
                        profile = new MovementProfile(address, c);
                    } else if (DeviceInformationServiceProfile.isCorrectService(s)) {
                        DeviceInformationServiceProfile deviceInfo = new DeviceInformationServiceProfile(address, c);
                        device.setDeviceInfo(deviceInfo);
                        Log.d(TAG,"Found Device Information Service");
                    } else if (s.getUuid().equals(GattInfo.OAD_SERVICE_UUID)) {
                        device.setOadService(s);
                        Log.d(TAG,"Found TI OAD Service");
                    } else if (s.getUuid().equals(GattInfo.CC_SERVICE_UUID)) {
                        device.setConnControlService(s);
                        Log.d(TAG,"Found Connection Control Service");
                    }

                    if(profile != null) {
                        device.addProfile(profile);
                        if (nrNotificationsOn < maxNotifications) {
                            profile.enableNotification();
                            nrNotificationsOn++;

                            Log.i(TAG, "Total No. of Notifications " + nrNotificationsOn);
                        }
                    }
                }
            }

            if(totalCharacteristics == 0){
                return STATUS.NO_CHARACTERISTICS;
            }
        }

        for(BaseProfile p : device.getProfiles()) {
            p.enableService();
        }

        return STATUS.SUCCESS;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        Log.v(TAG, "servicesDiscovered:" + servicesDiscovered);
        Log.v(TAG, "totalCharacteristics: " + totalCharacteristics);

        String msg = "Found " + servicesDiscovered + " services with " + totalCharacteristics + " characteristics on this device";
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(false);

        if(servicesDiscovered < totalServices) {
            progressDialog.setTitle("Generating GUI");
            int progressValue = (int)(((float) servicesDiscovered / totalServices) * 100);
            progressDialog.setProgress(progressValue);
        } else {
            int servicesEnabled = values[0];

            progressDialog.setTitle("Enabling Services");
            progressDialog.setMax(totalServices);
            progressDialog.setProgress(servicesEnabled);
        }
    }

    @Override
    protected void onPostExecute(STATUS result) {
        super.onPostExecute(result);
        device.getDeviceInfo().enableService();

        if(listener != null) {
            listener.onDiscoveryFinish(result, device);
        }

        progressDialog.dismiss();
        mContext.unregisterReceiver(mGattUpdateReceiver);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        private int servicesEnabled = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {
                servicesEnabled++;
                publishProgress(servicesEnabled);
            }
        }
    };
}
