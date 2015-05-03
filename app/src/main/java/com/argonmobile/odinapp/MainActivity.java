package com.argonmobile.odinapp;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.argonmobile.odinapp.model.WindowStructure;
import com.argonmobile.odinapp.protocol.command.Command;
import com.argonmobile.odinapp.protocol.command.GetInputInfoResponse;
import com.argonmobile.odinapp.protocol.command.GetOutputInfoResponse;
import com.argonmobile.odinapp.protocol.command.GetPlanListResponse;
import com.argonmobile.odinapp.protocol.command.GetPlanWindowInfoResponse;
import com.argonmobile.odinapp.protocol.command.GetPlanWindowListResponse;
import com.argonmobile.odinapp.protocol.command.GetWindowStructureResponse;
import com.argonmobile.odinapp.protocol.command.Request;
import com.argonmobile.odinapp.protocol.command.RequestFactory;
import com.argonmobile.odinapp.protocol.connection.CommandListener;
import com.argonmobile.odinapp.protocol.connection.ConnectionManager;
import com.argonmobile.odinapp.protocol.connection.ControlConnection;
import com.argonmobile.odinapp.protocol.deviceinfo.InputInfo;
import com.argonmobile.odinapp.protocol.deviceinfo.OutputInfo;
import com.argonmobile.odinapp.protocol.deviceinfo.PlanInfo;
import com.argonmobile.odinapp.protocol.deviceinfo.ScreenGroup;
import com.argonmobile.odinapp.protocol.image.ImageUpdater;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    private Handler handler;
    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == ConnectionManager.MSG_ON_CTRL_CON_CONNECTED) {
                onGetControlConnection();
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler(callback);
        ConnectionManager.defaultManager.connect(handler, this);
    }

    private void onGetControlConnection() {
        Log.i(TAG, "onGetControlConnection");

        ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
        con.addCommandListener(commandListener);


        { // test get window structure
            Request req = RequestFactory.createGetWindowStructureRequest();
            con.sendCommand(req);
        }

        { // test get input info
            Request req = RequestFactory.createGetInputInfoRequest();
            con.sendCommand(req);
        }

        { // test get output info
            Request req = RequestFactory.createGetOutputInfoRequest();
            con.sendCommand(req);
        }

        { // test get plan list
            Request req = RequestFactory.createGetPlanListRequest();
            con.sendCommand(req);
        }

        { // test get plan window list
            Request req = RequestFactory.createGetPlanWindowListRequest();
            con.sendCommand(req);
        }

        { // 获得预案窗口信息
            Request req = RequestFactory.createGetPlanWindowInfoRequest();
            con.sendCommand(req);
        }
        /*
        {
            Request req = RequestFactory.createJpgRequest(CommandDefs.PARAM_SIGNAL_IMAGE, true,
                    (short)480, (short)270, new byte[]{0x05});
            con.sendCommand(req);
        }*/
    }


    CommandListener commandListener = new CommandListener() {
        @Override
        public void onSentCommand(Command cmd) {
            Log.i(TAG, "onSentCommand:" + cmd.command);
        }

        @Override
        public void onReceivedCommand(Command cmd) {
            Log.i(TAG, "onReceivedCommand:" + cmd);
            if (cmd instanceof GetWindowStructureResponse) {
                GetWindowStructureResponse r = (GetWindowStructureResponse) cmd;
                WindowStructure.getInstance().screenGroups = r.screenGroups;
                for (ScreenGroup sg : r.screenGroups) {
                    Log.i(TAG, "get window structure, sg:" + sg);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(MainActivity.this, ChosenProfileActivity.class);

                        startActivity(intent);
                        finish();

                    }
                });
            } else if(cmd instanceof GetPlanListResponse) {
                GetPlanListResponse r = (GetPlanListResponse) cmd;
                for(PlanInfo pi : r.planInfos) {
                    Log.i(TAG, "get plan info, pi:" + pi);
                }
            } else if(cmd instanceof GetPlanWindowListResponse) {
                GetPlanWindowListResponse r = (GetPlanWindowListResponse) cmd;
                Log.i(TAG, "GetPlanWindowListResponse list sg window count:" + r.windowCount);
                for (ScreenGroup sg : r.screenGroups) {
                    Log.i(TAG, "get plan window list sg:" + sg);
                }
            } else if(cmd instanceof GetOutputInfoResponse) {
                GetOutputInfoResponse r =(GetOutputInfoResponse)cmd;
                for (OutputInfo oi : r.outputInfos) {
                    Log.i(TAG, "get output info, oi:" + oi);
                }
            } else if(cmd instanceof GetPlanWindowInfoResponse) {
                GetPlanWindowInfoResponse r = (GetPlanWindowInfoResponse) cmd;
                for (ScreenGroup sg : r.screenGroups) {
                    Log.i(TAG, "get plan window info sg:" + sg);
                }
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        // release connection before go to background
        //ConnectionManager.defaultManager.disconnect();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
