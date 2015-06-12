package com.argonmobile.odinapp.protocol.command;


import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class SwitchPlanRequest extends Request {
    public int planIndex;

    public SwitchPlanRequest(int planIndex) {
        this.command = CommandDefs.CMD_INVOKE_PLAN;
        this.planIndex = planIndex;
    }
    @Override
    public short getPayloadLength() {
        return 1;
    }
    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        byte bIndex = (byte)(planIndex & 0x000000FF);
        byteBuffer.put(bIndex);
    }
}
