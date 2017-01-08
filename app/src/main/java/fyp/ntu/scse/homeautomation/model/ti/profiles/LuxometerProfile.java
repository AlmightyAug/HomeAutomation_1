package fyp.ntu.scse.homeautomation.model.ti.profiles;

import java.util.List;
import java.util.Locale;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import fyp.ntu.scse.homeautomation.model.ti.Point3D;
import fyp.ntu.scse.homeautomation.model.ti.Sensor;
import fyp.ntu.scse.homeautomation.model.ti.SensorTagGatt;

public class LuxometerProfile extends BaseProfile {

	public LuxometerProfile(String address, List<BluetoothGattCharacteristic> characteristics) {
		super(address, characteristics);
		
		for(BluetoothGattCharacteristic c : characteristics) {
			if(c.getUuid().equals(SensorTagGatt.UUID_OPT_DATA)) {
				super.setDataCharac(c);
			} else if(c.getUuid().equals(SensorTagGatt.UUID_OPT_CONF)) {
				super.setConfigCharac(c);
			} else if(c.getUuid().equals(SensorTagGatt.UUID_OPT_PERI)) {
				super.setPeriodCharac(c);
			}
		}
	}
	
	public static boolean isCorrectService(BluetoothGattService service) {
		return service.getUuid().equals(SensorTagGatt.UUID_OPT_SERV);
	}

	/*@Override
	public Point3D getConvertedData(byte[] value) {
		return Sensor.LUXOMETER.convert(value);
	}*/

	@Override
	public String getConvertedDataString(byte[] value) {
		//Point3D v = getConvertedData(value);
		Point3D v = Sensor.LUXOMETER.convert(value);
		return String.format(Locale.getDefault(), "%.1f Lux", v.x);
	}

}
