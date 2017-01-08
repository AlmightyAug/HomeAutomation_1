package fyp.ntu.scse.homeautomation.model.ti.profiles;

import java.util.List;
import java.util.Locale;

import fyp.ntu.scse.homeautomation.model.ti.BluetoothLeService;
import fyp.ntu.scse.homeautomation.model.ti.Point3D;
import fyp.ntu.scse.homeautomation.model.ti.Sensor;
import fyp.ntu.scse.homeautomation.model.ti.SensorTagGatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

public class MovementProfile extends BaseProfile {

	public MovementProfile(String address, List<BluetoothGattCharacteristic> characteristics) {
		super(address, characteristics);
		
		for(BluetoothGattCharacteristic c : characteristics) {
			if(c.getUuid().equals(SensorTagGatt.UUID_MOV_DATA)) {
				super.setDataCharac(c);
			} else if(c.getUuid().equals(SensorTagGatt.UUID_MOV_CONF)) {
				super.setConfigCharac(c);
			} else if(c.getUuid().equals(SensorTagGatt.UUID_MOV_PERI)) {
				super.setPeriodCharac(c);
			}
		}
	}
	
	public static boolean isCorrectService(BluetoothGattService service) {
		return service.getUuid().equals(SensorTagGatt.UUID_MOV_SERV);
	}
	
	@Override
	public void enableService() {
		BluetoothLeService mBtLeService = BluetoothLeService.getInstance();
		
		int error = mBtLeService.writeCharacteristic(getDeviceAddress(), getConfigCharac(), new byte[] {0x7F,0x02});
		if (error != 0) {
            if (getConfigCharac() != null) {
            	Log.d(getClass().getSimpleName(), "sensor enable failed: " + getConfigCharac());
            }
        }
		updatePeriod(1000);
        isEnabled(true);
	}
	
	@Override
	public void disableService() {
		BluetoothLeService mBtLeService = BluetoothLeService.getInstance();
		
		int error = mBtLeService.writeCharacteristic(getDeviceAddress(), getConfigCharac(), new byte[] {0x00,0x00});
		if (error != 0) {
            if (getConfigCharac() != null) {
            	Log.d(getClass().getSimpleName(), "sensor disable failed: " + getConfigCharac());
            }
        }
        isEnabled(false);
	}

	@Override
	public String getConvertedDataString(byte[] value) {
		String text = "";
		
		Point3D v;
		v = Sensor.MOVEMENT_ACC.convert(value);
		text += "Accel [";
		text += String.format(Locale.getDefault(), "X:%.2fG, Y:%.2fG, Z:%.2fG", v.x,v.y,v.z);
		text += "]\n";
		
		v = Sensor.MOVEMENT_GYRO.convert(value);
		text += "Gyro [";
		text += String.format(Locale.getDefault(), "X:%.2f'/s, Y:%.2f'/s, Z:%.2f'/s", v.x,v.y,v.z);
		text += "]\n";
		
		v = Sensor.MOVEMENT_MAG.convert(value);
		text += "Magne [";
		text += String.format(Locale.getDefault(), "X:%.2fuT, Y:%.2fuT, Z:%.2fuT", v.x,v.y,v.z);
		text += "]\n";
		
		return text;
	}

}
