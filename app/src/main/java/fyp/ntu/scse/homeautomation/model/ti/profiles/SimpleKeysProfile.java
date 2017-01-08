package fyp.ntu.scse.homeautomation.model.ti.profiles;

import java.util.List;
import fyp.ntu.scse.homeautomation.model.ti.SensorTagGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

public class SimpleKeysProfile extends BaseProfile {

	public SimpleKeysProfile(String address, List<BluetoothGattCharacteristic> characteristics) {
		super(address, characteristics);

		for(BluetoothGattCharacteristic c : characteristics) {
			if(c.getUuid().equals(SensorTagGatt.UUID_KEY_DATA)) {
				super.setDataCharac(c);
			}
		}
		
		hasPeriod(false);
	}
	
	public static boolean isCorrectService(BluetoothGattService service) {
		return service.getUuid().equals(SensorTagGatt.UUID_KEY_SERV);
	}

	@Override
	public void enableService() {
		isEnabled(true);
	}
	
	@Override 
	public void disableService () {
		isEnabled(false);
	}

	@Override
	public String getConvertedDataString(byte[] value) {
		switch(value[0]) {
		case 0x1:
			return "[Left-1:Right-0:Reed-0]";
		case 0x2:
			return "[Left-0:Right-1:Reed-0]";
		case 0x3:
			return "[Left-1:Right-1:Reed-0]";
		case 0x4:
			return "[Left-0:Right-0:Reed-1]";
		case 0x5:
			return "[Left-1:Right-0:Reed-1]";
		case 0x6:
			return "[Left-0:Right-1:Reed-1]";
		case 0x7:
			return "[Left-1:Right-1:Reed-1]";
		default:
			return "[Left-0:Right-0:Reed-0]";
		}
	}

	@Override
	public void updatePeriod(int period) {
		//Do nothing
	}

}
