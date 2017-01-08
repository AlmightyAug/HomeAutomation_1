package fyp.ntu.scse.homeautomation;

import android.content.Context;
import android.content.Intent;

import static fyp.ntu.scse.homeautomation.model.ti.Sensor.MOVEMENT_ACC;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.MOVEMENT_GYRO;
import static fyp.ntu.scse.homeautomation.model.ti.Sensor.MOVEMENT_MAG;


public class ScanActivity extends MainActivity {

    public ScanActivity(Intent intent) {
        /*Change in accelerometer value*/
        if (intent.getAction().equals(Sensor.MOVEMENT_ACC)) {
            System.out.println("Accelerometer movement");
            System.out.print(MOVEMENT_ACC);
            System.out.print("m/s^2");
        }

        /*Change in Magnetometer value*/
        if (intent.getAction().equals(Sensor.MOVEMENT_MAG)) {
            System.out.println("Magnetometer movement");
            System.out.print(MOVEMENT_MAG);
            System.out.print("µT");
        }

        /*Change in gyroscope value*/
        if (intent.getAction().equals(Sensor.MOVEMENT_GYRO)) {
            System.out.println("Gyroscope Movement");
            System.out.print(MOVEMENT_GYRO);
            System.out.print("rad/s");
        }

        if(MOVEMENT_ACC-MOVEMENT_ACC!=0){
            System.out.println(/*Write something here e.g. sound too loud, relocation of device, device stolen*/);
        }else{
            System.out.println("Nothing here");
        }

        if(MOVEMENT_MAG-MOVEMENT_MAG!=0) {
            System.out.println(/*Write something here e.g. something had happened, geomagnetic/environmental change, new intruder device alert,
            Wireless failure, Too much Wi-Fi interference time to change channel*/);
        }else{
            System.out.println("Nothing here");
        }

        if(MOVEMENT_GYRO-MOVEMENT_GYRO!=0) {
            System.out.println(/*Write something here e.g. Earthquake, tsunami, typhoon, natural disaster, speed of rotating wheel */);
        }else{
            System.out.println("Nothing here");
        }
    }


}
