package com.argonmobile.odinapp.protocol.deviceinfo;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sean on 5/20/15.
 */
public class Node {
    public final String mIdentity;
    public final String mDisplayName;
    public final Map<String, Node> mChildNodes;
    public final boolean mIsLeafNode;
    public final WeakReference<Node> mParentNode;
    public Node(String identity, String displayName, boolean isLeafNode, Node parent) {
        mIdentity = identity;
        mDisplayName = displayName;
        mIsLeafNode = isLeafNode;
        mChildNodes = new HashMap<String, Node>();
        mParentNode = new WeakReference<Node>(parent);
    }
    public Node findNodeById(String id) {
        return mChildNodes.get(id);
    }
    public void addChildNode(Node node) {
        mChildNodes.put(node.mIdentity, node);
    }
}
