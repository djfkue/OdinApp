package com.argonmobile.odinapp.protocol.command;

import java.nio.ByteBuffer;

/**
 * Created by sean on 4/8/15.
 */
public abstract class Request extends Command {
    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        throw new UnsupportedOperationException("Not supported");
    }
}
