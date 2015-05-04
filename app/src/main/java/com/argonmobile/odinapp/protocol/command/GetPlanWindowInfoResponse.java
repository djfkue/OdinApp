package com.argonmobile.odinapp.protocol.command;

import com.argonmobile.odinapp.protocol.deviceinfo.WindowInfo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sean on 4/16/15.
 */
public class GetPlanWindowInfoResponse extends Response {
    public int totalCount;
    public int index;
    public int windowCount;
    public int planIndex;
    public boolean isChanged;
    public List<WindowInfo> windowInfos;

    private GetPlanWindowInfoResponse() {}

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new GetPlanWindowInfoResponse();
        }
    };
    public static Repacker sRepacker = new Repacker() {
        @Override
        public Response repack(List<Response> subResponses) {
            GetPlanWindowInfoResponse firstResponse = (GetPlanWindowInfoResponse)subResponses.get(0);
            if(firstResponse.totalCount > subResponses.size()) return null;

            List<WindowInfo> screenGroups = new ArrayList<WindowInfo>();
            for(Response r : subResponses) {
                GetPlanWindowInfoResponse sr = (GetPlanWindowInfoResponse) r;
                screenGroups.addAll(sr.windowInfos);
            }
            firstResponse.windowInfos = screenGroups;
            return firstResponse;
        }
        @Override
        public boolean needRepack(Response response) {
            return (response instanceof GetPlanWindowInfoResponse) && (((GetPlanWindowInfoResponse)response).totalCount > 1);
        }
    };

    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        totalCount = byteBuffer.get();
        index = byteBuffer.get();
        windowCount = byteBuffer.get();
        planIndex = byteBuffer.get();
        isChanged = (byteBuffer.get() == 0x01);

        windowInfos = new ArrayList<WindowInfo>();
        int remainPayloadLength = payloadLength - 5;
        while(remainPayloadLength > 0) {
            WindowInfo ws = new WindowInfo();
            remainPayloadLength -= ws.load(byteBuffer);
            windowInfos.add(ws);
        }
    }
}
