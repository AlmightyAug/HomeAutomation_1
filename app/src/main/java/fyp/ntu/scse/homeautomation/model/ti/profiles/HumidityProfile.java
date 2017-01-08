package fyp.ntu.scse.homeautomation.model.ti.profiles;

import java.util.List;
import java.util.Locale;



import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import fyp.ntu.scse.homeautomation.model.ti.Point3D;
import fyp.ntu.scse.homeautomation.model.ti.Sensor;
import fyp.ntu.scse.homeautomation.model.ti.SensorTagGatt;

public class HumidityProfile extends BaseProfile {
	
	public HumidityProfile(String address, List<BluetoothGattCharacteristic> characteristics) {
		super(address, characteristics);
		
		for(BluetoothGattCharacteristic c : characteristics) {
			if(c.getUuid().equals(SensorTagGatt.UUID_HUM_DATA)) {
				super.setDataCharac(c);
			} else if(c.getUuid().equals(SensorTagGatt.UUID_HUM_CONF)) {
				super.setConfigCharac(c);
			} else if(c.getUuid().equals(SensorTagGatt.UUID_HUM_PERI)) {
				super.setPeriodCharac(c);
			}
		}
	}

	public static boolean isCorrectService(BluetoothGattService service) {
		return service.getUuid().equals(SensorTagGatt.UUID_HUM_SERV);
	}

	/*@Override
	public Point3D getConvertedData(byte[] value) {
		return Sensor.HUMIDITY.convert(value);
	}*/

	@Override
	public String getConvertedDataString(byte[] value) {
		//Point3D v = getConvertedData(value);
		Point3D v = Sensor.HUMIDITY.convert(value);
		return String.format(Locale.getDefault(), "%.1f %%rH", v.x);
	}

}