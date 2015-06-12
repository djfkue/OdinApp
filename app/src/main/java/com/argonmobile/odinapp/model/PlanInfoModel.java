package com.argonmobile.odinapp.model;

import com.argonmobile.odinapp.protocol.deviceinfo.PlanInfo;
import com.argonmobile.odinapp.protocol.deviceinfo.WindowInfo;

import java.util.List;

/**
 * Created by argon on 5/4/15.
 */
public class PlanInfoModel {

    private static PlanInfoModel mInstance;

    public List<PlanInfo> planInfos;

    private PlanInfoModel() {

    }

    public static PlanInfoModel getInstance() {
        if (mInstance == null) {
            mInstance = new PlanInfoModel();
        }
        return mInstance;
    }

}
