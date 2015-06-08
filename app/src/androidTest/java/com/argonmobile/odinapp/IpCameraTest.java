package com.argonmobile.odinapp;

import android.test.AndroidTestCase;

import com.argonmobile.odinapp.protocol.IPCameraParser;
import com.argonmobile.odinapp.protocol.deviceinfo.IPCameraNode;
import com.argonmobile.odinapp.protocol.deviceinfo.Node;

/**
 * Created by sean on 6/8/15.
 */
public class IpCameraTest extends AndroidTestCase {
    public void testipCameraParser() {
        String ipCameraInfo = "<IPCLists><雨花区 Name=\"T130758898957037250\" Type=\"Folder\"><板桥派出所 Name=\"T130758899048902505\" Type=\"Folder\"><凤台南路 Name=\"T130758899147408139\" Type=\"Folder\"><XX路口 Name=\"T130758899260354599\" Type=\"Folder\"><监控摄像机1 Name=\"T130758899418983672\" Type=\"IPC\"><IP地址 Name=\"T130758899530720063\" Type=\"SUBIPC\" /><用户名 Name=\"T130758899626645550\" Type=\"SUBIPC\" /><密码 Name=\"T130758899674258273\" Type=\"SUBIPC\" /><端口 Name=\"T130758899725821222\" Type=\"SUBIPC\" /></监控摄像机1></XX路口></凤台南路></板桥派出所><软件园派出所 Name=\"T130758899850548356\" Type=\"Folder\" /></雨花区></IPCLists>";
        Node rootNode = IPCameraParser.parserIPCameras(ipCameraInfo);
        assertNotNull(rootNode);

        Node currentNode;
        assertEquals(1, rootNode.mChildNodes.size());
        currentNode = rootNode.mChildNodes.get("T130758898957037250");
        assertNotNull(currentNode);
        assertEquals("雨花区", currentNode.mDisplayName);
        assertEquals(2, currentNode.mChildNodes.size());

        Node childNode = currentNode.mChildNodes.get("T130758899850548356");
        assertNotNull(childNode);
        assertEquals("软件园派出所", childNode.mDisplayName);

        currentNode =  currentNode.mChildNodes.get("T130758899048902505");
        assertNotNull(currentNode);
        assertEquals(1, currentNode.mChildNodes.size());
        assertEquals("板桥派出所", currentNode.mDisplayName);
        assertEquals("雨花区", currentNode.mParentNode.get().mDisplayName);

        currentNode =  currentNode.mChildNodes.get("T130758899147408139");
        assertNotNull(currentNode);
        assertEquals(1, currentNode.mChildNodes.size());
        assertEquals("凤台南路", currentNode.mDisplayName);
        assertEquals("板桥派出所", currentNode.mParentNode.get().mDisplayName);

        currentNode =  currentNode.mChildNodes.get("T130758899260354599");
        assertNotNull(currentNode);
        assertEquals(1, currentNode.mChildNodes.size());
        assertEquals("XX路口", currentNode.mDisplayName);
        assertEquals("凤台南路", currentNode.mParentNode.get().mDisplayName);

        currentNode =  currentNode.mChildNodes.get("T130758899418983672");
        assertNotNull(currentNode);
        assertEquals(0, currentNode.mChildNodes.size());
        assertEquals("监控摄像机1", currentNode.mDisplayName);
        assertEquals("XX路口", currentNode.mParentNode.get().mDisplayName);
        assertTrue(currentNode instanceof IPCameraNode);
    }
}
