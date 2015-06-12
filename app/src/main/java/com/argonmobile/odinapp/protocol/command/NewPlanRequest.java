package com.argonmobile.odinapp.protocol.command;


import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class NewPlanRequest extends Request {
    public int planIndex;
    public String planName;

    public NewPlanRequest(int planIndex, String planName) {
        this.command = CommandDefs.CMD_NEW_PLAN;
        this.planIndex = planIndex;
        this.planName = planName;
    }
    @Override
    public short getPayloadLength() {
        return (short)(1 + planName.getBytes().length + 1);
    }
    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        byte bIndex = (byte)(planIndex & 0x000000FF);
        byteBuffer.put(bIndex);
        byteBuffer.put(planName.getBytes());
        byteBuffer.put((byte)'\0');
    }
}
