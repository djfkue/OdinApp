package com.argonmobile.odinapp.protocol.command;


import com.argonmobile.odinapp.protocol.IPCameraParser;
import com.argonmobile.odinapp.protocol.deviceinfo.IPCameraNode;
import com.argonmobile.odinapp.protocol.deviceinfo.Node;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sean on 4/16/15.
 */
public class GetIpListResponse extends Response {
    public int totalCount;
    public int index;
    public byte[] data;
    public Node rootNode;
    public ArrayList<IPCameraNode> ipCameraNodes;
    private GetIpListResponse() {}

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new GetIpListResponse();
        }
    };
    public static Repacker sRepacker = new Repacker() {
        @Override
        public Response repack(List<Response> subResponses) {
            GetIpListResponse firstResponse = (GetIpListResponse)subResponses.get(0);
            if(firstResponse.totalCount > subResponses.size()) return null;

            int totalDataLength = 0;
            for(Response r : subResponses) {
                GetIpListResponse sr = (GetIpListResponse) r;
                totalDataLength += sr.data.length;
            }
            byte[] tmpData = new byte[totalDataLength];
            // copy data
            int offset = 0;
            for(Response r : subResponses) {
                GetIpListResponse sr = (GetIpListResponse) r;
                System.arraycopy(sr.data, 0, tmpData, offset, sr.data.length);
                offset += sr.data.length;
            }
            firstResponse.data = tmpData;

            // parse node here
            firstResponse.ipCameraNodes = new ArrayList<IPCameraNode>();
            try {
                String ipCamInfo = new String(tmpData, "UTF-8");
                firstResponse.rootNode = IPCameraParser.parserIPCameras(ipCamInfo, firstResponse.ipCameraNodes);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return firstResponse;
        }
        @Override
        public boolean needRepack(Response response) {
            return (response instanceof GetIpListResponse) && (((GetIpListResponse)response).totalCount > 1);
        }
    };
    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        totalCount = byteBuffer.getShort();
        index = byteBuffer.getShort();
        data = new byte[payloadLength - 4];
        byteBuffer.get(data);
    }
}
