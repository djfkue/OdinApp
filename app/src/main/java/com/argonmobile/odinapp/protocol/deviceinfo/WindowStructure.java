package com.argonmobile.odinapp.protocol.deviceinfo;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by sean on 5/4/15.
 */
public class WindowStructure {
    public short windowId;
    public short inputIndex;
    public short userZOrder;
    public short input;
    public byte divideMode;
    public short[] subInputs;
    public String url;
    public short panelGroupId;
    public short left, top;
    public short width, height;
    public short leftTop, rightBottom;
    public boolean isWindowFixed;
    public int recycleInterval;
    public short recycleListCount;
    public short[] recycleIndexes;

    public int load(ByteBuffer bb) {
        int readLength = 0;
        // load window structure
        windowId = bb.getShort();
        inputIndex = bb.getShort();
        userZOrder = bb.getShort();
        input = bb.getShort();
        divideMode = bb.get();

        int subInputCount = divideMode & 0x000000FF;
        subInputs = new short[subInputCount];
        for(int index = 0; index < subInputCount; ++index) {
            subInputs[index] = bb.getShort();
        }

        // read url
        // TODO: make sure the description is no longer than 512 bytes
        byte[] urlBuffer = new byte[512];
        byte singleChar;
        int charIndex = 0;
        int readCount = 0;
        do {
            singleChar = bb.get();
            ++readCount;
            if(singleChar != '\0')
                urlBuffer[charIndex++] = singleChar;
            else
                break;
        } while(true);
        url = new String(urlBuffer, 0, charIndex, Charset.forName("UTF-8"));

        panelGroupId = bb.getShort();
        left = bb.getShort();
        top = bb.getShort();
        width = bb.getShort();
        height = bb.getShort();
        leftTop = bb.getShort();
        rightBottom = bb.getShort();

        isWindowFixed = (bb.get() == (byte)0x01);
        recycleInterval = bb.getInt();
        recycleListCount = bb.getShort();
        recycleIndexes = new short[recycleListCount];
        for(int index = 0; index < recycleListCount; ++index) {
            recycleIndexes[index] = bb.getShort();
        }

        readLength += 30 + (subInputs.length + recycleIndexes.length) * 2 + readCount;
        return readLength;
    }
}
