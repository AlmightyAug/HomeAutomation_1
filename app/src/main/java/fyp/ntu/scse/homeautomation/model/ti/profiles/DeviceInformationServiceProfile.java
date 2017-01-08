package fyp.ntu.scse.homeautomation.model.ti.profiles;

import java.io.UnsupportedEncodingException;
import java.util.List;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import fyp.ntu.scse.homeautomation.model.ti.BluetoothLeService;

public class DeviceInformationServiceProfile extends BaseProfile {
	private static final String dISService_UUID = "0000180a-0000-1000-8000-00805f9b34fb";
	private static final String dISSystemID_UUID = "00002a23-0000-1000-8000-00805f9b34fb";
	private static final String dISModelNR_UUID = "00002a24-0000-1000-8000-00805f9b34fb";
	private static final String dISSerialNR_UUID = "00002a25-0000-1000-8000-00805f9b34fb";
	private static final String dISFirmwareREV_UUID = "00002a26-0000-1000-8000-00805f9b34fb";
	private static final String dISHardwareREV_UUID = "00002a27-0000-1000-8000-00805f9b34fb";
	private static final String dISSoftwareREV_UUID = "00002a28-0000-1000-8000-00805f9b34fb";
	private static final String dISManufacturerNAME_UUID = "00002a29-0000-1000-8000-00805f9b34fb";
	
	private BluetoothGattCharacteristic systemIDc;
	private BluetoothGattCharacteristic modelNRc;
	private BluetoothGattCharacteristic serialNRc;
	private BluetoothGattCharacteristic firmwareREVc;
	private BluetoothGattCharacteristic hardwareREVc;
	private BluetoothGattCharacteristic softwareREVc;
	private BluetoothGattCharacteristic manufacturerNAMEc;

	public DeviceInformationServiceProfile(String address, List<BluetoothGattCharacteristic> characteristics) {
		super(address, characteristics);
		
		for(BluetoothGattCharacteristic c : characteristics) {
			if(c.getUuid().toString().equals(dISSystemID_UUID)) {
				this.systemIDc = c;
			} else if(c.getUuid().toString().equals(dISModelNR_UUID)) {
				this.modelNRc = c;
			} else if(c.getUuid().toString().equals(dISSerialNR_UUID)) {
				this.serialNRc = c;
			} else if(c.getUuid().toString().equals(dISFirmwareREV_UUID)) {
				this.firmwareREVc = c;
			} else if(c.getUuid().toString().equals(dISHardwareREV_UUID)) {
				this.hardwareREVc = c;
			} else if(c.getUuid().toString().equals(dISSoftwareREV_UUID)) {
				this.softwareREVc = c;
			} else if(c.getUuid().toString().equals(dISManufacturerNAME_UUID)) {
				this.manufacturerNAMEc = c;
			}
		}
	}
	
	public static boolean isCorrectService(BluetoothGattService service) {
		return service.getUuid().toString().equals(dISService_UUID);
	}

	@Override
	public void enableService() {
		BluetoothLeService mBtLeService = BluetoothLeService.getInstance();
		String deviceAddress = getDeviceAddress();
		
		// Read all values
		mBtLeService.readCharacteristic(deviceAddress, this.systemIDc);
		mBtLeService.waitIdle(GATT_TIMEOUT);
		mBtLeService.readCharacteristic(deviceAddress, this.modelNRc);
		mBtLeService.waitIdle(GATT_TIMEOUT);
		mBtLeService.readCharacteristic(deviceAddress, this.serialNRc);
		mBtLeService.waitIdle(GATT_TIMEOUT);
		mBtLeService.readCharacteristic(deviceAddress, this.firmwareREVc);
		mBtLeService.waitIdle(GATT_TIMEOUT);
		mBtLeService.readCharacteristic(deviceAddress, this.hardwareREVc);
		mBtLeService.waitIdle(GATT_TIMEOUT);
		mBtLeService.readCharacteristic(deviceAddress, this.softwareREVc);
		mBtLeService.waitIdle(GATT_TIMEOUT);
		mBtLeService.readCharacteristic(deviceAddress, this.manufacturerNAMEc);
	}
	
	@Override
	public void enableNotification() {
		// Nothing to do here
	}

	@Override
	public void disableService() {
		// Nothing to do here
	}
	
	@Override
	public void disableNotification() {
		// Nothing to do here
	}

	@Override
	public String getConvertedDataString(byte[] value) {
		return getConvertedDataString();
	}

	@Override
	public String getConvertedDataString() {
		String text = "";
		
		if (systemIDc != null) {
			text += "System ID: ";
			for (byte b : systemIDc.getValue()) {
				text += String.format("%02x:", b);
			}
			text += "\n";
		}
		
		if (modelNRc != null) {
			try {
				text += "Model NR: ";
				text += new String(modelNRc.getValue(),"UTF-8");
				text += "\n";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
		}
		
		if (serialNRc != null) {
			try {
				text += "Serial NR: ";
				text += new String(serialNRc.getValue(),"UTF-8");
				text += "\n";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		if (firmwareREVc != null) {
			text += "Firmware Revision: ";
			text += getFirmwareRev();
			text += "\n";
		}
		
		if (hardwareREVc != null) {
			try {
				text += "Hardware Revision: ";
				text += new String(hardwareREVc.getValue(),"UTF-8");
				text += "\n";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		if (softwareREVc != null) {
			try {
				text += "Software Revision: ";
				text += new String(softwareREVc.getValue(),"UTF-8");
				text += "\n";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		if (manufacturerNAMEc != null) {
			try {
				text += "Manufacturer Name: ";
				text += new String(manufacturerNAMEc.getValue(),"UTF-8");
				text += "\n";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return text;
	}

	@Override
	public String getTitle() {
		return "Device Information";
	}
	
	public String getFirmwareRev() {
		try {
			return new String(firmwareREVc.getValue(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
