package com.argonmobile.odinapp.protocol.command;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by sean on 4/16/15.
 */
public class CreateWindowCommand extends Response {
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

    public CreateWindowCommand() {
        this.command = CommandDefs.CMD_CREATE_NEW_WINDOW;
    }

    public static Response.Creator sCreator = new Response.Creator() {
        @Override
        public Response createInstance() {
            return new CreateWindowCommand();
        }
    };

    // TODO: add helper interface to init all the parameters

    @Override
    public short getPayloadLength() {
        if(subInputs == null || url == null || recycleIndexes == null) {
            throw new IllegalStateException("Request should be init first!");
        }
        return (short)(30 + (subInputs.length + recycleIndexes.length) * 2 + url.getBytes().length + 1);
    }
    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(windowId);
        byteBuffer.putShort(inputIndex);
        byteBuffer.putShort(userZOrder);
        byteBuffer.putShort(input);
        byteBuffer.put(divideMode);
        for(short subInput: subInputs)
            byteBuffer.putShort(subInput);
        // put url
        byteBuffer.put(url.getBytes());
        byteBuffer.put((byte) '\0');

        byteBuffer.putShort(panelGroupId);
        byteBuffer.putShort(left);
        byteBuffer.putShort(top);
        byteBuffer.putShort(width);
        byteBuffer.putShort(height);
        byteBuffer.putShort(leftTop);
        byteBuffer.putShort(rightBottom);

        byteBuffer.put(isWindowFixed ? (byte)0x01 : (byte)0x00);
        byteBuffer.putInt(recycleInterval);
        byteBuffer.putShort(recycleListCount);
        for(short recycleIndex: recycleIndexes)
            byteBuffer.putShort(recycleIndex);
    }

    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        // TODO: to be implemented
        //throw new UnsupportedOperationException("Not supported");
        windowId = byteBuffer.getShort();
        inputIndex = byteBuffer.getShort();
        userZOrder = byteBuffer.getShort();
        input = byteBuffer.getShort();
        divideMode = byteBuffer.get();
        subInputs = new short[divideMode];
        for (int index = 0; index < divideMode; ++index) {
            subInputs[index] = byteBuffer.getShort();
        }
        byte[] urlBuffer = new byte[512];
        byte singleChar;
        int charIndex = 0;
        int readCount = 0;
        do {
            singleChar = byteBuffer.get();
            ++readCount;
            if(singleChar != '\0')
                urlBuffer[charIndex++] = singleChar;
            else
                break;
        } while(true);
        url = new String(urlBuffer, 0, charIndex, Charset.forName("UTF-8"));

        panelGroupId = byteBuffer.getShort();
        left = byteBuffer.getShort();
        top = byteBuffer.getShort();
        width = byteBuffer.getShort();
        height = byteBuffer.getShort();
        leftTop = byteBuffer.getShort();
        rightBottom = byteBuffer.getShort();

//        byte windowFixed = byteBuffer.get();
//        if (windowFixed == 0x01) {
//            isWindowFixed = true;
//        } else {
//            isWindowFixed = false;
//        }
//
//        recycleInterval = byteBuffer.getInt();
//        recycleListCount = byteBuffer.getShort();
//
//        recycleIndexes = new short[recycleListCount];
//        for(short recycleIndex: recycleIndexes) {
//            recycleIndex = byteBuffer.getShort();
//        }

    }
}
