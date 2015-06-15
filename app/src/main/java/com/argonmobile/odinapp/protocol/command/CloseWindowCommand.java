package com.argonmobile.odinapp.protocol.command;

import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class CloseWindowCommand extends Response {
    public short windowId;
    public boolean closeAllWindow;

    public CloseWindowCommand(short windowId) {
        this.command = CommandDefs.CMD_CLOSE_WINDOW;
        this.windowId = windowId;
        closeAllWindow = false;
    }

    public CloseWindowCommand() {
        this.command = CommandDefs.CMD_CLOSE_WINDOW;
        this.closeAllWindow = true;
    }

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new CloseWindowCommand();
        }
    };

    @Override
    public short getPayloadLength() {
        return (short)2;
    }

    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        if(closeAllWindow) {
            byteBuffer.put((byte)0xFF);
            byteBuffer.put((byte)0xFF);
        } else {
            byteBuffer.putShort(windowId);
        }
    }

    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        this.windowId = byteBuffer.getShort();
    }
}
