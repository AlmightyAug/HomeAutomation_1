package fyp.ntu.scse.homeautomation.model;

import fyp.ntu.scse.homeautomation.model.ti.Conversion;

public class ImageHdr {
    /**
     * CRC16 calculated by client
     */
    private short crc0;

    /**
     * Not used, set to 0xFFFF
     */
    private short crc1;

    /**
     * Always '0 for SensorTag
     */
    private short ver;

    /**
     * Length of image in blocks (block size = 16 bytes)
     */
    private int len;

    /**
     * 'E','E', 'E', 'E' for SensorTag
     */
    private byte[] uid = new byte[4];

    /**
     * Starts address of image in block units (block size = 16 bytes)
     */
    private short addr;

    /**
     * Always '1' (application) for SensorTag
     */
    private byte imgType;

    public ImageHdr(byte[] buf) {
        this.len = buf.length / (16 / 4);
        this.ver = 0;
        this.uid[0] = this.uid[1] = this.uid[2] = this.uid[3] = 'E';
        this.addr = 0;
        this.imgType = 1; // EFL_OAD_IMG_TYPE_APP
        this.crc0 = calcImageCRC((int) 0, buf);
        crc1 = (short) 0xFFFF;
    }

    public int totalBlocks() {
        return this.len;
    }

    public byte[] getRequest() {
        byte[] tmp = new byte[16];
        tmp[0] = Conversion.loUint16(this.crc0); // CRC
        tmp[1] = Conversion.hiUint16(this.crc0); // CRC
        tmp[2] = Conversion.loUint16(this.crc1); // CRC shadow
        tmp[3] = Conversion.hiUint16(this.crc1); // CRC shadow
        tmp[4] = Conversion.loUint16(this.ver); // Version
        tmp[5] = Conversion.hiUint16(this.ver); // Version
        tmp[6] = Conversion.loUint16((short)this.len); // Length
        tmp[7] = Conversion.hiUint16((short)this.len); // Length
        tmp[8] = tmp[9] = tmp[10] = tmp[11] = this.uid[0]; // User ID
        tmp[12] = Conversion.loUint16(this.addr); // Address
        tmp[13] = Conversion.hiUint16(this.addr); // Address
        tmp[14] = imgType; // Image type
        tmp[15] = (byte) 0xFF; // Reserved. Not used, set to 0xFF
        return tmp;
    }

    private short calcImageCRC(int page, byte[] buf) {
        short crc = 0;
        long addr = page * 0x1000;

        byte pageBeg = (byte) page;
        byte pageEnd = (byte) (this.len / (0x1000 / 4));
        int osetEnd = ((this.len - (pageEnd * (0x1000 / 4))) * 4 );

        pageEnd += pageBeg;

        System.out.println("pageEnd:" + pageEnd);
        System.out.println("osetEnd:" + osetEnd);

        while (true) {
            int oset;

            for (oset = 0; oset < 0x1000; oset++) {
                if ((page == pageBeg) && (oset == 0x00)) {
                    // Skip the CRC and shadow.
                    // Note: this increments by 3 because oset is incremented by
                    // 1 in each pass
                    // through the loop
                    oset += 3;
                } else if ((page == pageEnd) && (oset == osetEnd)) {
                    crc = this.crc16(crc, (byte) 0x00);
                    crc = this.crc16(crc, (byte) 0x00);

                    return crc;
                } else {
                    crc = this.crc16(crc, buf[(int) (addr + oset)]);
                }
            }
            page += 1;
            addr = page * 0x1000;
        }
    }

    private short crc16(short crc, byte val) {
        final int poly = 0x1021;
        byte cnt;
        for (cnt = 0; cnt < 8; cnt++, val <<= 1) {
            byte msb;
            if ((crc & 0x8000) == 0x8000) {
                msb = 1;
            } else
                msb = 0;

            crc <<= 1;
            if ((val & 0x80) == 0x80) {
                crc |= 0x0001;
            }
            if (msb == 1) {
                crc ^= poly;
            }
        }

        return crc;
    }
}
