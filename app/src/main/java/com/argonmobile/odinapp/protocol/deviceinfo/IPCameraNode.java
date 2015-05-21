package com.argonmobile.odinapp.protocol.deviceinfo;

/**
 * Created by sean on 5/20/15.
 */
public class IPCameraNode extends Node {
    public final String mIpAddress;
    public final String mUserName;
    public final String mPasswd;
    public final int mPort;
    public IPCameraNode(String identity, String displayName, boolean isLeafNode,
                        String ipAddress, String userName, String passwd, int port) {
        super(identity, displayName, isLeafNode);
        mIpAddress = ipAddress;
        mUserName = userName;
        mPasswd = passwd;
        mPort = port;
    }
}
