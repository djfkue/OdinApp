package com.argonmobile.odinapp.protocol.deviceinfo;

import com.argonmobile.odinapp.protocol.command.CommandDefs;

/**
 * Created by sean on 4/15/15.
 */
public class InputInfo {
    public int inputIndex;
    public int frequency;
    public int portInputIndex;      // not for mobile client
    public int inputType;           // not for mobile client
    public String inputName;
    public String inputOverlapInfo;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("inputIndex:").append(inputIndex)
                .append(", frequency:").append(frequency)
                .append(", inputName:").append(inputName)
                .append(", inputOverlapInfo:").append(inputOverlapInfo);
        return sb.toString();
    }

    // ip camera input cards or av input cards?
    public boolean isIpInput() {
        return (inputType == CommandDefs.PARAM_INPUT_TYPE_IP);
    }
}
