package com.argonmobile.odinapp.protocol.command;


import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class CatchEscapeRequest extends Request {
    public String input;

    public CatchEscapeRequest(String input) {
        this.command = CommandDefs.CMD_CATCH_ESCAPE;
        this.input = input;
    }
    @Override
    public short getPayloadLength() {
        return (short)(input.getBytes().length + 1);
    }
    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(input.getBytes());
        byteBuffer.put((byte) '\0');
    }
}
