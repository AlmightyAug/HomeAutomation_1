package fyp.ntu.scse.homeautomation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import fyp.ntu.scse.homeautomation.controller.BLEManager;


enum Sensor{
        /*Variable Initialise for Magnetometer*/
        MAGNETOMETER,

        /*Variable Initialise for Change in Magnetometer Value*/
         MOVEMENT_MAG,

        /*Variable Initialise for Humidity Sensor*/
        HUMIDITY,

        /*Variable Initialise for Luxometer*/
        LUXOMETER,

        /*Variable Initialise for Gyroscope*/
        GYROSCOPE,

        /*Variable Initialise for Change in Gyroscope Value*/
        MOVEMENT_GYRO,

        /*Variable Initialise for Infrared Thermometer*/
        IR_TEMPERATURE,

        /*Variable Initialise for Accelerometer*/
        ACCELEROMETER,

        /*Variable Initialise for Change in Accelerometer Value*/
        MOVEMENT_ACC,

        /*Variable Initialise for Barometer*/
        BAROMETER,
    }

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}

/*After clicking on the "Scan" buttons*/

class ScanActivity implements Bluetooth.Scan {

    public static Scan (Bundle savedInstanceState){
        Scan Scanobj = new Scan();
        Scanobj.Bluetooth();
        setContentView(R.layout.activity_scan);
    }

    public static fyp.ntu.scse.homeautomation.controller.BLEManager {
        Alert Alertobj = new Alert()
        Alertobj.ScanActivity();
        setContentView(R.layout.sensor_cardview);
    }

}