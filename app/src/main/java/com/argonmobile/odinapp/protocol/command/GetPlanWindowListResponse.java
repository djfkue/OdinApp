package com.argonmobile.odinapp.protocol.command;

import com.argonmobile.odinapp.protocol.deviceinfo.WindowStructure;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sean on 4/16/15.
 */
public class GetPlanWindowListResponse extends Response {
    public int totalCount;
    public int index;
    public int windowCount;
    public List<WindowStructure> windowStructures;

    private GetPlanWindowListResponse() {}

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new GetPlanWindowListResponse();
        }
    };
    public static Repacker sRepacker = new Repacker() {
        @Override
        public Response repack(List<Response> subResponses) {
            GetPlanWindowListResponse firstResponse = (GetPlanWindowListResponse)subResponses.get(0);
            if(firstResponse.totalCount > subResponses.size()) return null;

            List<WindowStructure> windowStructures = new ArrayList<WindowStructure>();
            for(Response r : subResponses) {
                GetPlanWindowListResponse sr = (GetPlanWindowListResponse) r;
                windowStructures.addAll(sr.windowStructures);
            }
            firstResponse.windowStructures = windowStructures;
            return firstResponse;
        }
        @Override
        public boolean needRepack(Response response) {
            return (response instanceof GetPlanWindowListResponse) && (((GetPlanWindowListResponse)response).totalCount > 1);
        }
    };

    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        totalCount = byteBuffer.get();
        index = byteBuffer.get();
        windowCount = byteBuffer.get();

        windowStructures = new ArrayList<WindowStructure>();
        int remainPayloadLength = payloadLength - 3;
        while(remainPayloadLength > 0) {
            WindowStructure ws = new WindowStructure();
            remainPayloadLength -= ws.load(byteBuffer);
            windowStructures.add(ws);
        }
    }
}
