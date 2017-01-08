package fyp.ntu.scse.homeautomation.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fyp.ntu.scse.homeautomation.model.SensorTag;
import fyp.ntu.scse.homeautomation.model.ti.profiles.BaseProfile;

import android.bluetooth.BluetoothGatt;
import android.os.Environment;

public class BtDeviceManager {

    private static String[] DEVICE_FILTER = { "CC2650 SensorTag" };

    private static BtDeviceManager mThis;

    // List of Connected Device
    private List<SensorTag> mDeviceList;

    public static BtDeviceManager getInstance() {
        if(mThis == null) {
            mThis = new BtDeviceManager();
        }
        return mThis;
    }

    public static boolean checkDeviceFilter(String deviceName) {
        if (deviceName == null)
            return false;

        int n = DEVICE_FILTER.length;
        if (n > 0) {
            boolean found = false;
            for (int i = 0; i < n && !found; i++) {
                found = deviceName.equals(DEVICE_FILTER[i]);
            }
            return found;
        } else
            // Allow all devices if the device filter is empty
            return true;
    }

    public boolean addDevice(SensorTag device) {
        return mDeviceList.add(device);
    }

    public void removeDevice(String address) {
        for(int i = 0; i < mDeviceList.size(); i++) {
            SensorTag device = mDeviceList.get(i);
            if(device.equals(address)) {
                BluetoothGatt mBluetoothGatt = device.getBluetoothGatt();
                mBluetoothGatt.close();
                device.setBluetoothGatt(null);
                mDeviceList.remove(i);
            }
        }
    }

    public void removeAllDevices() {
        for(int i = 0; i < mDeviceList.size(); i++) {
            SensorTag device = mDeviceList.get(i);
            BluetoothGatt mBluetoothGatt = device.getBluetoothGatt();
            mBluetoothGatt.close();
            device.setBluetoothGatt(null);
            mDeviceList.remove(i);
        }
    }

    public boolean deviceExist(String address) {
        if(getDevice(address) != null) {
            return true;
        }
        return false;
    }

    public SensorTag getDevice(String address) {
        for(SensorTag device : mDeviceList) {
            if(device.equals(address)) {
                return device;
            }
        }
        return null;
    }

    public void discoverServices() {
        for(SensorTag device : mDeviceList) {
            BluetoothGatt btGatt = device.getBluetoothGatt();
            if(btGatt != null) {
                if(btGatt.getServices().isEmpty()) {
                    btGatt.discoverServices();
                }
            }
        }
    }

    public void configureServices() {
        for(SensorTag device : mDeviceList) {
            for(BaseProfile p : device.getProfiles()) {
                p.enableNotification();
                p.enableService();
            }
        }
    }

    public void deconfigureServices() {
        for(SensorTag device : mDeviceList) {
            for(BaseProfile p : device.getProfiles()) {
                p.disableService();
                p.disableNotification();
            }
        }
    }

    public SensorTag[] getDeviceList() {
        return mDeviceList.toArray(new SensorTag[getNoOfDevices()]);
    }

    public int getNoOfDevices() {
        return mDeviceList.size();
    }

    public boolean isEmpty() {
        return mDeviceList.size() == 0;
    }

    public void onDestroy() {
        for(int i = 0; i < mDeviceList.size(); i++) {
            SensorTag device = mDeviceList.get(i);
            BluetoothGatt mBluetoothGatt = device.getBluetoothGatt();
            if(mBluetoothGatt != null) {
                mBluetoothGatt.close();
            }
            device.setBluetoothGatt(null);
            mDeviceList.remove(i);
        }
    }

//    private void saveResult(String filePath, String result) {
//
//        Date dateNow = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        String dateNowString = sdf.format(dateNow);
//
//        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SGFoodOnline/History");
//
//        if (!directory.exists()) {
//            if (!directory.mkdirs()) {
//                return; //Cannot make directory
//            }
//        }
//
//        File mFile = new File(directory.getAbsolutePath(), "history.csv");
//
//        try {
//            FileWriter fw = new FileWriter(mFile, true); //true denotes append
//
//            fw.append(SensorTag);
//            fw.append(",");
//            fw.append(latitude + ", " + longitude);
//            fw.append(",");
//            fw.append(filePath);
//            fw.append(",");
//            fw.append(result);
//            fw.append("\n");
//
//            fw.flush();
//            fw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String processJSON(String rawResult) {
//        List<String> resultList = null;
//
//        /*
//        try {
//            JSONObject reader = new JSONObject(rawResult);
//            resultList = (List<String>) reader.get("result");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        for (int i=0; i < resultList.size(); i++) {
//            Log.e("Result " + String.valueOf(i), resultList.get(i));
//        }
//*/
//        return "burger";
//    }

}
