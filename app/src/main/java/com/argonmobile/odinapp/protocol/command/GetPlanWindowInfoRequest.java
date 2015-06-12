package com.argonmobile.odinapp.protocol.command;


import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class GetPlanWindowInfoRequest extends Request {
    public int planIndex;

    public GetPlanWindowInfoRequest(int planIndex) {
        this.command = CommandDefs.CMD_GET_PLAN_WINDOW_INFO;
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
