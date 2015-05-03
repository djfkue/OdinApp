package com.argonmobile.odinapp.protocol.connection;

import com.argonmobile.odinapp.protocol.command.Command;

/**
 * Created by sean on 4/19/15.
 */
public interface CommandListener {
    void onSentCommand(Command cmd);
    void onReceivedCommand(Command cmd);
}
