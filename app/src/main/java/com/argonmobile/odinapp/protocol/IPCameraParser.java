package com.argonmobile.odinapp.protocol;

import android.util.Log;
import android.util.Xml;

import com.argonmobile.odinapp.protocol.deviceinfo.IPCameraNode;
import com.argonmobile.odinapp.protocol.deviceinfo.Node;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;

/**
 * Created by sean on 5/22/15.
 */
public class IPCameraParser {
    private final static String TAG = "IPCameraParser";
    private final static String ATTR_NAME_ID = "Name";
    private final static String ATTR_NAME_TYPE = "Type";
    private final static String ATTR_TYPE_FOLDER = "Folder";
    private final static String ATTR_TYPE_IPC = "IPC";
    private final static String ATTR_TYPE_SUBIPC = "SUBIPC";

    public static Node parserIPCameras(String ipCameraInfo) {
        int event;
        String type;
        Node rootNode = new Node("root", "root", false, null);
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(ipCameraInfo));
            event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                // parse from start tag
                if(event != XmlPullParser.START_TAG) {
                    event = parser.next();
                    continue;
                }

                // we only parse 'Type' == 'Folder' or 'Type' == 'IPC'
                type = parser.getAttributeValue(null, ATTR_NAME_TYPE);
                if(type == null || (!type.equals(ATTR_TYPE_FOLDER) && !type.equals(ATTR_TYPE_IPC))) {
                    event = parser.next();
                    continue;
                }

                Node child = null;
                if(type.equals(ATTR_TYPE_FOLDER)) {
                    child = parseLocationNode(parser, rootNode);
                } else if(type.equals(ATTR_TYPE_IPC)) {
                    child = parseIpCameraNode(parser, rootNode);
                }
                if(child != null) {
                    rootNode.addChildNode(child);
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.w(TAG, "e:" + e.getMessage());
            e.printStackTrace();
        }
        return rootNode;
    }

    private static Node parseLocationNode(XmlPullParser parser, Node parentNode) throws Exception {
        int event = parser.getEventType();
        if(event != XmlPullParser.START_TAG) throw new IllegalStateException("init event type must be START_TAG");
        String location = parser.getName();
        String id = parser.getAttributeValue(null, ATTR_NAME_ID);
        Log.i(TAG, "parseLocationNode location:" + location + ", id:" + id);
        Node node = new Node(id, location, false, parentNode);
        // fetch next event
        event = parser.next();
        while(event != XmlPullParser.END_TAG) {
            if(event == XmlPullParser.START_TAG) {
                String type = parser.getAttributeValue(null, ATTR_NAME_TYPE);
                Node child = null;
                if(type.equals(ATTR_TYPE_FOLDER)) {
                    child = parseLocationNode(parser, node);
                } else if(type.equals(ATTR_TYPE_IPC)) {
                    child = parseIpCameraNode(parser, node);
                }
                if(child != null) {
                    node.addChildNode(child);
                }
            }
            event = parser.next();
        }
        return node;
    }

    private static Node parseIpCameraNode(XmlPullParser parser, Node parentNode) throws Exception {
        int event = parser.getEventType();
        if(event != XmlPullParser.START_TAG) throw new IllegalStateException("init event type must be START_TAG");
        String location = parser.getName();
        String id = parser.getAttributeValue(null, ATTR_NAME_ID);
        Log.i(TAG, "parseIpCameraNode location:" + location + ", id:" + id);
        IPCameraNode node = new IPCameraNode(id, location, parentNode);
        // skip camera detail info
        skipCameraInfo(parser);
        // update event
        event = parser.getEventType();
        if(event != XmlPullParser.END_TAG) {
            throw new IllegalStateException("current event type should be END_TAG after skipping camera info");
        }
        return node;
    }

    private static void skipCameraInfo(XmlPullParser parser) throws Exception {
        // consume all events with type 'sub ipc' + last event with null type or non sub ipc event
        String type;
        do {
            int event = parser.next();
            type = parser.getAttributeValue(null, ATTR_NAME_TYPE);
            Log.i(TAG, "skipCameraInfo type:" + type + ", event type:" + event);
        } while (type != null && type.equals(ATTR_TYPE_SUBIPC));
    }
}
