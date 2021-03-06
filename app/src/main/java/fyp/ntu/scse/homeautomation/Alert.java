package fyp.ntu.scse.homeautomation;

import android.content.Context;
import android.content.Intent;

import static fyp.ntu.scse.homeautomation.model.ti.Sensor.ACCELEROMETER;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.GYROSCOPE;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.MAGNETOMETER;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.MOVEMENT_ACC;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.MOVEMENT_GYRO;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.MOVEMENT_MAG;


public class ScanActivity extends MainActivity {

    /*To display changes in the sensors*/
    class SensorData {
        public void Sensor() {
            MAGNETOMETER(1), ACCELEROMETER(2),
            GYROSCOPE(3),
        }

    }

    /*To display changes in Magnetometer*/
    class movement_magnetometer {
        public class Sensor(){
            MOVEMENT_MAG MOVEMENT_MAGobj = new MOVEMENT_MAG();
            return MOVEMENT_MAG;
        }
    }

    /*To display changes in the Accelerometer*/
    class movement_accelerometer {
        public void Sensor(){
            MOVEMENT_ACC MOVEMENT_ACCobj = new MOVEMENT_ACC();
            return MOVEMENT_ACC;
        }
    }

    /*To display changes in the Gyroscope*/
    class movement_gyroscope {
        public void Sensor(){
            MOVEMENT_GYRO MOVEMENT_GYROobj = new MOVEMENT_GYRO();
            return MOVEMENT_GYRO;
        }
    }

}
