package com.argonmobile.odinapp.protocol.deviceinfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sean on 5/20/15.
 */
public class Node {
    public final String mIdentity;
    public final String mDisplayName;
    public final Map<String, Node> mSubNodes;
    public final boolean mIsLeafNode;
    public Node(String identity, String displayName, boolean isLeafNode) {
        mIdentity = identity;
        mDisplayName = displayName;
        mIsLeafNode = isLeafNode;
        mSubNodes = new HashMap<String, Node>();
    }
    public Node findNodeById(String id) {
        return mSubNodes.get(id);
    }
}
