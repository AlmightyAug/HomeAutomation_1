package fyp.ntu.scse.homeautomation.model.ti.profiles;

import java.util.List;
import java.util.Locale;

import fyp.ntu.scse.homeautomation.model.ti.Point3D;
import fyp.ntu.scse.homeautomation.model.ti.Sensor;
import fyp.ntu.scse.homeautomation.model.ti.SensorTagGatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

public class TemperatureProfile extends BaseProfile {

	public TemperatureProfile(String address, List<BluetoothGattCharacteristic> characteristics) {
		super(address, characteristics);
		
		setPeriodMinVal(200);
		
		for(BluetoothGattCharacteristic c : characteristics) {
			if(c.getUuid().equals(SensorTagGatt.UUID_IRT_DATA)) {
				super.setDataCharac(c);
			} else if(c.getUuid().equals(SensorTagGatt.UUID_IRT_CONF)) {
				super.setConfigCharac(c);
			} else if(c.getUuid().equals(SensorTagGatt.UUID_IRT_PERI)) {
				super.setPeriodCharac(c);
			}
		}
	}
	
	public static boolean isCorrectService(BluetoothGattService service) {
		return service.getUuid().equals(SensorTagGatt.UUID_IRT_SERV);
	}

	@Override
	public String getConvertedDataString(byte[] value) {
		Point3D v = Sensor.IR_TEMPERATURE.convert(value);
		
		String text = "";
		
		text += "Ambient:\t[";
		text += String.format(Locale.getDefault(), "%.1f'C", v.x);
		text += "]\n";
		
		text += "IR:\t\t\t\t\t[";
		text += String.format(Locale.getDefault(), "%.1f'C", v.z);
		text += "]\n";
		
		return text;
	}
	
	@Override
	public String getTitle() {
		return "Temperature Data";
	}

}
