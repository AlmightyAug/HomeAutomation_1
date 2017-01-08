package fyp.ntu.scse.homeautomation.model;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fyp.ntu.scse.homeautomation.model.ti.BluetoothLeService;
import fyp.ntu.scse.homeautomation.model.ti.Conversion;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.util.Log;

public class ProgramInfo {
    private final static String TAG = ProgramInfo.class.getSimpleName();

    // Programming parameters
    /**
     * Connection interval (units of 1.25ms, 80=100ms)
     */
    private static final short OAD_CONN_INTERVAL = 12; // 12 * 1.25 = 15 milliseconds
    /**
     * Supervision timeout value (units of 10ms, 1000=10s)
     */
    private static final short OAD_SUPERVISION_TIMEOUT = 50; // 50 * 10 = 500 milliseconds

    private static final int OAD_BLOCK_SIZE = 16;
    private static final int HAL_FLASH_WORD_SIZE = 4;
    private static final long TIMER_INTERVAL = 1000;

    //private static final int FILE_BUFFER_SIZE = 0x40000;
    private static final int OAD_BUFFER_SIZE = 2 + OAD_BLOCK_SIZE;

    private int iBytes = 0; // Number of bytes programmed
    private short iBlocks = 0; // Number of blocks programmed
    private short nBlocks = 0; // Total number of blocks
    private int iTimeElapsed = 0; // Time elapsed in milliseconds

    private boolean mServiceOk;
    private boolean mProgramming;
    private int packetsSent;

    private BluetoothGattCharacteristic mCharConnReq;
    private BluetoothGattCharacteristic mCharIdentify = null;
    private BluetoothGattCharacteristic mCharBlock = null;

    private BluetoothLeService mBtLeService;
    private String deviceAddress;

    private Timer mTimer;

    // Programming
    private final byte[] mFileBuffer;
    private final byte[] mOadBuffer = new byte[OAD_BUFFER_SIZE];
    private ImageHdr mFileImgHdr;

    public ProgramInfo(byte[] mImage, SensorTag device) {
        //this.mFileImgHdr = mFileImgHdr;
        mFileImgHdr = new ImageHdr(mImage);
        mFileBuffer = mImage;
        this.mBtLeService = BluetoothLeService.getInstance();
        this.deviceAddress = device.getAddress();

        BluetoothGattService mOadService = device.getOadService();
        BluetoothGattService mConnControlService = device.getConnControlService();
        // Characteristics list
        List<BluetoothGattCharacteristic> mCharListOad = mOadService.getCharacteristics();
        List<BluetoothGattCharacteristic> mCharListCc = mConnControlService.getCharacteristics();

        mServiceOk = mCharListOad.size() == 2 && mCharListCc.size() >= 3;
        if (mServiceOk) {
            mCharIdentify = mCharListOad.get(0);
            mCharBlock = mCharListOad.get(1);
            mCharBlock.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            mCharConnReq = mCharListCc.get(1);

            mBtLeService.setCharacteristicNotification(deviceAddress, mCharBlock, true);

            if ( Build.VERSION.SDK_INT >= 21) {
                mBtLeService.requestConnectionPriority(deviceAddress, BluetoothGatt.CONNECTION_PRIORITY_HIGH);
            }

            setConnectionParameters();
        }
    }

    /**
     * Make sure connection interval is long enough for OAD (Android default
     * connection interval is 7.5 ms)
     */
    private void setConnectionParameters() {
        byte[] value = {
                Conversion.loUint16(OAD_CONN_INTERVAL),			Conversion.hiUint16(OAD_CONN_INTERVAL),
                Conversion.loUint16(OAD_CONN_INTERVAL),			Conversion.hiUint16(OAD_CONN_INTERVAL),
                0, 												0,
                Conversion.loUint16(OAD_SUPERVISION_TIMEOUT),	Conversion.hiUint16(OAD_SUPERVISION_TIMEOUT)
        };
        mCharConnReq.setValue(value);
        mBtLeService.writeCharacteristic(deviceAddress, mCharConnReq);
    }

    public boolean isComplete() {
        return iBlocks == nBlocks;
    }

    public BluetoothGattCharacteristic getCharBlock() {
        return mCharBlock;
    }

    public int getProgrammedBlockCount() {
        return iBlocks;
    }

    public int getTotalBlockCount() {
        return nBlocks;
    }

    private void reset() {
        iBytes = 0;
        iBlocks = 0;
        iTimeElapsed = 0;
        nBlocks = (short) (mFileImgHdr.totalBlocks() / (OAD_BLOCK_SIZE / HAL_FLASH_WORD_SIZE));
    }

    public void startProgramming() {
        mProgramming = true;
        packetsSent = 0;

        mCharIdentify.setValue(mFileImgHdr.getRequest());
        mBtLeService.writeCharacteristic(deviceAddress, mCharIdentify);

        this.reset();
        System.out.println("total blocks: " + nBlocks);
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                iTimeElapsed += TIMER_INTERVAL;
            }
        }, 0, TIMER_INTERVAL);
    }

    public void stopProgramming() {
        mTimer.cancel();
        mTimer.purge();
        mProgramming = false;

        mBtLeService.setCharacteristicNotification(deviceAddress, mCharBlock, false);
    }

    public void programBlock() {
        if (!mProgramming)
            return;

        if (iBlocks < nBlocks) {
            mProgramming = true;

            // Prepare block
            mOadBuffer[0] = Conversion.loUint16((short)iBlocks);
            mOadBuffer[1] = Conversion.hiUint16((short)iBlocks);
            System.arraycopy(mFileBuffer, iBytes, mOadBuffer, 2, OAD_BLOCK_SIZE);

            // Send block
            mCharBlock.setValue(mOadBuffer);
            boolean success = mBtLeService.writeCharacteristicNonBlock(deviceAddress, mCharBlock);
            Log.d(TAG, "Sent block : " + iBlocks + " to [" + deviceAddress + "]");
            if (success) {
                // Update stats
                packetsSent++;
                iBlocks++;
                iBytes += OAD_BLOCK_SIZE;

                if(isComplete()) {
                    Log.d(TAG, "OAD Completed on [" + deviceAddress + "]");
                }
            } else {
                mProgramming = false;
                Log.e(TAG, "GATT writeCharacteristic failed on [" + deviceAddress + "]");
            }
        } else {
            mProgramming = false;
        }

        if(!mProgramming) {
            stopProgramming();
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ProgramInfo) {
            ProgramInfo p = (ProgramInfo) o;
            return deviceAddress.equals(p.deviceAddress);
        } else if (o instanceof String) {
            String s = (String) o;
            return deviceAddress.equals(s);
        }
        return super.equals(o);
    }
}
