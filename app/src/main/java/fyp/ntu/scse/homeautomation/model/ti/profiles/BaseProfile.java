package fyp.ntu.scse.homeautomation.model.ti.profiles;

import java.util.List;
import java.util.UUID;
import fyp.ntu.scse.homeautomation.model.ti.BluetoothLeService;
import fyp.ntu.scse.homeautomation.model.ti.GattInfo;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

public abstract class BaseProfile {
	
	private static String TAG = BaseProfile.class.getSimpleName();
	
	protected static final int GATT_TIMEOUT = 250; // milliseconds
	
	private String deviceAddress;
	private int numCharacteristics;
	private BluetoothGattCharacteristic dataCharac;
	private BluetoothGattCharacteristic configCharac;
	private BluetoothGattCharacteristic periodCharac;
	private int periodMinVal = 100;
	private boolean hasPeriod = true;
	
	private boolean _isConfigured;
	private boolean _isEnabled;
	
	protected BaseProfile(String address, List<BluetoothGattCharacteristic> characteristics) {
		this.deviceAddress = address;
		this.numCharacteristics = characteristics.size();
	}
	
	protected final void setDataCharac(BluetoothGattCharacteristic dataCharac) {
		this.dataCharac = dataCharac;
	}
	
	protected final BluetoothGattCharacteristic getDataCharac() {
		return dataCharac;
	}

	protected final void setConfigCharac(BluetoothGattCharacteristic configCharac) {
		this.configCharac = configCharac;
	}
	
	protected final BluetoothGattCharacteristic getConfigCharac() {
		return configCharac;
	}

	protected final void setPeriodCharac(BluetoothGattCharacteristic periodCharac) {
		this.periodCharac = periodCharac;
	}
	
	protected final BluetoothGattCharacteristic getPeriodCharac() {
		return periodCharac;
	}

	public int getNumCharacteristics() {
		return numCharacteristics;
	}
	
	public int getPeriodMinVal() {
		return periodMinVal;
	}

	protected void setPeriodMinVal(int periodMinVal) {
		this.periodMinVal = periodMinVal;
	}
	
	public boolean hasPeriod() {
		return hasPeriod;
	}

	protected void hasPeriod(boolean hasPeriod) {
		this.hasPeriod = hasPeriod;
	}

	public void enableNotification() {
		BluetoothLeService mBtLeService = BluetoothLeService.getInstance();
        int error = mBtLeService.setCharacteristicNotification(deviceAddress, dataCharac, true);
        if (error != 0) {
            if (this.dataCharac != null) {
            	Log.d(TAG, "Sensor notification enable failed: " + this.dataCharac.getUuid().toString());
            }
        }
        
		_isConfigured = true;
	}
	
	public void disableNotification() {
		BluetoothLeService mBtLeService = BluetoothLeService.getInstance();
        int error = mBtLeService.setCharacteristicNotification(deviceAddress, dataCharac, false);
        if (error != 0) {
            if (this.dataCharac != null) {
            	Log.d(TAG, "Sensor notification disable failed: " + this.dataCharac.getUuid().toString());
            }
        }
        
		_isConfigured = false;
	}
	
	public boolean isConfigured() {
		return _isConfigured;
	}
	
	public void enableService () {
		BluetoothLeService mBtLeService = BluetoothLeService.getInstance();
        int error = mBtLeService.writeCharacteristic(deviceAddress, this.configCharac, (byte)0x01);
        if (error != 0) {
            if (this.configCharac != null) {
            	Log.d(TAG, "ensor enable failed: " + this.configCharac);
            }
        }
        
        mBtLeService.readCharacteristic(deviceAddress, periodCharac);
        
        _isEnabled = true;
	}
	
	public void disableService () {
		BluetoothLeService mBtLeService = BluetoothLeService.getInstance();
        int error = mBtLeService.writeCharacteristic(deviceAddress, this.configCharac, (byte)0x00);
        if (error != 0) {
            if (this.configCharac != null) {
            	Log.d(TAG, "ensor disable failed: " + this.configCharac);
            }
        }
        
        _isEnabled = false;
	}
	
	public boolean isEnabled() {
		return _isEnabled;
	}
	
	protected void isEnabled(boolean isEnabled) {
		_isEnabled = isEnabled;
	}
	
	protected String getDeviceAddress() {
		return this.deviceAddress;
	}
	
	public boolean hasCharacteristic(String uuidStr) {
		if(uuidStr.equals(dataCharac.getUuid().toString())) {
			return true;
		} else if(uuidStr.equals(configCharac.getUuid().toString())) {
			return true;
		} else if(uuidStr.equals(dataCharac.getUuid().toString())) {
			return true;
		} else {
			return false;
		}
	}
	
	public abstract String getConvertedDataString(byte[] value);
	public String getConvertedDataString() {
		byte[] value = dataCharac.getValue();
		
		if(value != null) {
			return getConvertedDataString(value);
		}
		return null;
	}
	public String getTitle() {
		String title = GattInfo.uuidToName(UUID.fromString(this.dataCharac.getUuid().toString()));
		return title.trim();
	}
	
	public void updatePeriod(int period) {
		if (period > 2450) period = 2450; 
		if (period < 100) period = 100;
		
		byte p = (byte)((period / 10));
		
		Log.d(TAG,"Period characteristic set to :" + period);
		
		BluetoothLeService mBtLeService = BluetoothLeService.getInstance();
		int error = mBtLeService.writeCharacteristic(deviceAddress, periodCharac, p);
		if (error != 0) {
			if (this.periodCharac != null) {
				Log.e(TAG, "Sensor period failed: " + this.periodCharac.getUuid().toString() + " Error: " + error);
			}
		}
	}
	
	public int getPeriodValue() {
		if(periodCharac != null) {
			byte[] value = periodCharac.getValue();
			// signed byte to unsigned byte conversion
			return ((value[0] & 0xFF) * 10);
		}
		return -1;
	}
}
