package fyp.ntu.scse.homeautomation;

import android.content.Context;
import android.content.Intent;
import fyp.ntu.scse.homeautomation.controller.BLEManager;
import fyp.ntu.scse.homeautomation.controller.ServiceDiscoveryTask;


import static fyp.ntu.scse.homeautomation.model.ti.Sensor.ACCELEROMETER;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.BAROMETER;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.GYROSCOPE;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.HUMIDITY;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.IR_TEMPERATURE;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.LUXOMETER;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.MAGNETOMETER;


    /*To implement BLEManager Bluetooth connection to TI SensorTag CC2650*/
    class Bluetooth {
        public static fyp.ntu.scse.homeautomation.controller.BLEManager {
            BLEManager BLEManagerobj = new BLEManager();
            BLEManagerobj.BroadcastReceiver();
        }

        /*Request Discovery for TI SensorTag Bluetooth Receiver*/
        public static ServiceDiscoveryTask {
            ServiceDiscoveryTask ServiceDiscoveryTaskobj = new ServiceDiscoveryTask();
            ServiceDiscoveryTaskobj.ServiceDiscoveryTask();
        }
    }

/*Receiving and Displaying data from the TI SensorTag*/

public class ScanActivity extends MainActivity{
    public ScanActivity(Context, Intent intent) {

        /*Accelerometer*/
        if (intent.getAction().equals(Sensor.ACCELEROMETER)) {
            System.out.println("Accelerometer");
            System.out.print(ACCELEROMETER);
            System.out.print("m/s^2");
        }

        /*Magnetometer*/
        if (intent.getAction().equals(Sensor.MAGNETOMETER)) {
            System.out.println("Magnetometer");
            System.out.print(MAGNETOMETER);
            System.out.print("µT");
        }

        /*Gyroscope*/
        if (intent.getAction().equals(Sensor.GYROSCOPE)) {
            System.out.println("Gyroscope");
            System.out.print(GYROSCOPE);
            System.out.print("rad/s");
        }

        /*Temperature*/
        if (intent.getAction().equals(Sensor.IR_TEMPERATURE)) {
            System.out.println("Temperature ");
            System.out.print(IR_TEMPERATURE);
            System.out.print("℃");
        }

        /*Humidity*/
        if (intent.getAction().equals(Sensor.HUMIDITY)){
            System.out.println("Humidity");
            System.out.print(HUMIDITY);
            System.out.print("%");
        }

        /*Light Intensity Sensor*/
        if (intent.getAction().equals(Sensor.LUXOMETER)){
            System.out.println("Luxometer");
            System.out.print(LUXOMETER);
            System.out.print(" lux");
        }

        /*Barometer*/
        if (intent.getAction().equals(Sensor.BAROMETER)){
            System.out.println("Barometer");
            System.out.print(BAROMETER);
            System.out.println("hPa");
        }

    }
}