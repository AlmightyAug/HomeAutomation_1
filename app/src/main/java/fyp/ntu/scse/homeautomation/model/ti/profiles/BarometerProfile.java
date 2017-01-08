package fyp.ntu.scse.homeautomation.model.ti.profiles;

import java.util.List;
import java.util.Locale;
import fyp.ntu.scse.homeautomation.model.ti.BarometerCalibrationCoefficients;
import fyp.ntu.scse.homeautomation.model.ti.BluetoothLeService;
import fyp.ntu.scse.homeautomation.model.ti.Point3D;
import fyp.ntu.scse.homeautomation.model.ti.Sensor;
import fyp.ntu.scse.homeautomation.model.ti.SensorTagGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

public class BarometerProfile extends BaseProfile {
	private BluetoothGattCharacteristic calibCharac;
	private boolean isCalibrated;
	private boolean isHeightCalibrated;
	private static final double PA_PER_METER = 12.0;

	public BarometerProfile(String address, List<BluetoothGattCharacteristic> characteristics) {
		super(address, characteristics);
		
		for(BluetoothGattCharacteristic c : characteristics) {
			if(c.getUuid().equals(SensorTagGatt.UUID_BAR_DATA)) {
				super.setDataCharac(c);
			} else if(c.getUuid().equals(SensorTagGatt.UUID_BAR_CONF)) {
				super.setConfigCharac(c);
			} else if(c.getUuid().equals(SensorTagGatt.UUID_BAR_PERI)) {
				super.setPeriodCharac(c);
			} else if(c.getUuid().equals(SensorTagGatt.UUID_BAR_CALI)) {
				this.calibCharac = c;
			}
		}
		
		// We are dealing with CC2650 SensorTag
		// Barometer is already calibrated
		this.isCalibrated = true;
		this.isHeightCalibrated = false;
	}
	
	public static boolean isCorrectService(BluetoothGattService service) {
		return service.getUuid().equals(SensorTagGatt.UUID_BAR_SERV);
	}
	
	@Override
	public void enableService() {
		BluetoothLeService mBtLeService = BluetoothLeService.getInstance();
		String deviceAddress = getDeviceAddress();
		
		while (!(mBtLeService.checkGatt(deviceAddress))) {
			mBtLeService.waitIdle(GATT_TIMEOUT);
		}
		
		if (!(this.isCalibrated)) {
			// Write the calibration code to the configuration registers
			mBtLeService.writeCharacteristic(deviceAddress, getConfigCharac(), Sensor.CALIBRATE_SENSOR_CODE);
			mBtLeService.waitIdle(GATT_TIMEOUT);
			System.out.println("calibCharac:" + calibCharac);
			mBtLeService.readCharacteristic(deviceAddress, calibCharac);
			mBtLeService.waitIdle(GATT_TIMEOUT);
		} else {
			super.enableService();
		}
		
		isEnabled(true);
	}

	public Point3D getConvertedData(byte[] value) {
		Point3D v = Sensor.BAROMETER.convert(value);
		if (!(this.isHeightCalibrated)) {
			BarometerCalibrationCoefficients.INSTANCE.heightCalibration = v.x;
			this.isHeightCalibrated = true;
		}
		return v;
	}

	@Override
	public String getConvertedDataString(byte[] value) {
		Point3D v = getConvertedData(value);
		double h = (v.x - BarometerCalibrationCoefficients.INSTANCE.heightCalibration) / PA_PER_METER;
		h = (double) Math.round(-h * 10.0) / 10.0;
		return String.format(Locale.getDefault(), "%.1f mBar %.1f meter", v.x / 100, h);
	}

}
