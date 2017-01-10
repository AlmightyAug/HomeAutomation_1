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

    class SensorData {
        public static Sensor() {
            Sensor SensorObj = new Sensor();
            return SensorObj;
        }
}