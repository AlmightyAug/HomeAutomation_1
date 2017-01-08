package fyp.ntu.scse.homeautomation.model;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;
import java.util.List;

import fyp.ntu.scse.homeautomation.model.ti.profiles.BaseProfile;
import fyp.ntu.scse.homeautomation.model.ti.profiles.DeviceInformationServiceProfile;

public class SensorTag {

    private BluetoothGatt mBluetoothGatt;
    private List<BaseProfile> mProfiles;
    private DeviceInformationServiceProfile deviceInfo;

    // OAD Service
    private BluetoothGattService mOadService;
    // Connection Control Service
    private BluetoothGattService mConnControlService;

    public SensorTag() {
        mBluetoothGatt = null;
        mProfiles = new ArrayList<>();
    }

    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }

    public void setBluetoothGatt(BluetoothGatt mBluetoothGatt) {
        this.mBluetoothGatt = mBluetoothGatt;
    }

    public List<BaseProfile> getProfiles() {
        return mProfiles;
    }

    public BaseProfile getProfile(int location) {
        return mProfiles.get(location);
    }

    public int getProfileSize() {
        return mProfiles.size();
    }

    public DeviceInformationServiceProfile getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInformationServiceProfile deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public BluetoothGattService getOadService() {
        return mOadService;
    }

    public void setOadService(BluetoothGattService mOadService) {
        this.mOadService = mOadService;
    }

    public BluetoothGattService getConnControlService() {
        return mConnControlService;
    }

    public void setConnControlService(BluetoothGattService mConnControlService) {
        this.mConnControlService = mConnControlService;
    }

    public String getAddress() {
        return mBluetoothGatt.getDevice().getAddress();
    }

    /**
     * Find profile based on characteristic's uuid
     * @param uuidStr
     * @return
     */
    public BaseProfile findProfile(String uuidStr) {
        for(BaseProfile p : mProfiles) {
            if(p.hasCharacteristic(uuidStr)) {
                return p;
            }
        }
        return null;
    }

    public boolean addProfile(BaseProfile p) {
        return mProfiles.add(p);
    }

    @Override
    public boolean equals(Object o) {
        if(this.mBluetoothGatt == null) {
            return false;
        }

        String thisAddress = this.mBluetoothGatt.getDevice().getAddress();
        String deviceAddress = "";

        if(o instanceof SensorTag) {
            SensorTag device = (SensorTag) o;
            deviceAddress = device.mBluetoothGatt.getDevice().getAddress();
        } else if(o instanceof String) {
            deviceAddress = (String) o;
        }

        return deviceAddress.equals(thisAddress);
    }
}