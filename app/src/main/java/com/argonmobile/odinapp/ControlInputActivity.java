package com.argonmobile.odinapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.argonmobile.odinapp.protocol.ballcontrol.BallActionFactory;
import com.argonmobile.odinapp.protocol.ballcontrol.BallControlAction;
import com.argonmobile.odinapp.protocol.command.RequestFactory;
import com.argonmobile.odinapp.protocol.connection.ConnectionManager;
import com.argonmobile.odinapp.protocol.connection.ControlConnection;


public class ControlInputActivity extends ActionBarActivity implements BallActionFactory.ActionListener {
    private final static String TAG = "ControlInputActivity";
    private BallActionFactory ballActionFactory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_input);
        if(ballActionFactory == null) {
            ballActionFactory = new BallActionFactory(this);
        }
        ballActionFactory.setActionListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control_input, menu);
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

    @Override
    public void onResume() {
        super.onResume();
        ballActionFactory.start();
    }

    @Override
    public void onStop() {
        ballActionFactory.stop();
        super.onStop();
    }

    @Override
    public void onAction(BallControlAction action) {
        Log.i(TAG, "onAction " + action);
        final ControlConnection cc = ConnectionManager.defaultManager.getControlConnection();
        if(cc != null) {
            //
            cc.sendCommand(RequestFactory.createControlBallRequest(action.action, (byte)0x01));
        }
    }
}
