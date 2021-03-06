package com.argonmobile.odinapp.protocol.connection;

import android.content.Context;
import android.util.Log;

import com.argonmobile.odinapp.protocol.base.IState;
import com.argonmobile.odinapp.protocol.command.Request;
import com.argonmobile.odinapp.protocol.command.RequestFactory;
import com.argonmobile.odinapp.protocol.command.ResponseParser;
import com.argonmobile.odinapp.protocol.command.ServiceDiscoverResponse;

import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Discover service in LAN(UDP)
 * Created by sean on 4/8/15.
 */
public class ServiceDiscoverer extends IState {
    private final static String TAG = "ServiceDiscoverer";
    public final static ServiceDiscoverer defaultDiscoverer = new ServiceDiscoverer();
    private ServiceDiscoverer() {}

    private List<Observer> observers = new ArrayList<Observer>();
    private Context ctx;
    private SendBroadcastThread sendThread;
    private ReceiveBroadcastThread recThread;
    private DatagramSocket socket;
    private String ipAddress;

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }
    public void addObserver(Observer ob) {
        synchronized (observers) {
            if(!observers.contains(ob)) observers.add(ob);
        }
    }

    public void removeObserver(Observer ob) {
        synchronized (observers) {
            observers.remove(ob);
        }
    }

    public interface Observer {
        void onDiscoveryFailed();
        void onDiscoveryStarted();
        void onDiscoveryService(String ipAddress, int commandPort, int jpgPort);
        void onDiscoveryFinished();
    }

    @Override
    public void onStart() {
        if(ctx == null) throw new IllegalStateException("Application context must be set before start!!!");
        onNotifyDiscoveryStarted();
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, e.getMessage());
            onNotifyDiscoveryFailed();
            return;
        }
        ipAddress = Utils.getLocalIpAddress(ctx);
        Log.i(TAG, "ipAddress:" + ipAddress + ", local port:" + socket.getLocalPort());

        // start rec broadcast thread
        startRecThread();
    }

    @Override
    public void onStop() {
        release();
    }

    public void release() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            sendThread = null;
            recThread = null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, e.getMessage());
        }
    }

    protected void startRecThread() {
        // start receive broadcast thread
        recThread = new ReceiveBroadcastThread(this, socket);
        recThread.start();
    }
    protected void startSendThread() {
        // start send broadcast thread
        sendThread = new SendBroadcastThread(this, socket);
        sendThread.start();
    }
    private void onNotifyDiscoveryFailed() {
        synchronized (this) {
            started = false;
            release();
        }

        synchronized (observers) {
           for(Observer ob : observers)
               ob.onDiscoveryFailed();
        }
    }
    private void onNotifyDiscoveryStarted() {
        synchronized (observers) {
            for(Observer ob : observers)
                ob.onDiscoveryStarted();
        }
    }
    private void onNotifyDiscoveryService(String ipAddress, int commandPort, int jpgPort) {
        synchronized (observers) {
            for(Observer ob : observers)
                ob.onDiscoveryService(ipAddress, commandPort, jpgPort);
        }
    }
    private void onNotifyDiscoveryFinished() {
        synchronized (this) {
            started = false;
            release();
        }
        synchronized (observers) {
            for(Observer ob : observers)
                ob.onDiscoveryFinished();
        }
    }
    private static class ReceiveBroadcastThread extends Thread {
        WeakReference<ServiceDiscoverer> dicoverer;
        String ipAddress;
        DatagramSocket socket;
        public ReceiveBroadcastThread(ServiceDiscoverer discoverer, DatagramSocket socket) {
            this.dicoverer = new WeakReference<ServiceDiscoverer>(discoverer);
            this.socket = socket;
            this.ipAddress = discoverer.ipAddress;
        }
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                //socket.connect(InetAddress.getByName(Configure.BROADCAST_ADDRESS), Configure.BROADCAST_DEST_PORT);
                if (dicoverer.get() != null) {
                    dicoverer.get().startSendThread();
                }
                Log.i(TAG, "before receive bc!");
                byte[] headerAndLength = new byte[16];
                DatagramPacket packet = new DatagramPacket(headerAndLength, headerAndLength.length);
                socket.receive(packet);
                Log.i(TAG, "before receive bc!-->" + packet.getAddress().toString());
                int localPort = socket.getLocalPort();

                Log.i(TAG, "after receive bc!");
                ServiceDiscoverResponse response = (ServiceDiscoverResponse) ResponseParser.parserResponse(ByteBuffer.wrap(headerAndLength));

                dicoverer.get().stop();
                if (response != null) {
                    if (dicoverer.get() != null) {
                        dicoverer.get().onNotifyDiscoveryService(response.ipAddress, response.commandPort, localPort);
                    }
                }
                if (dicoverer.get() != null) {
                    dicoverer.get().onNotifyDiscoveryFinished();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.w(TAG, e.getMessage());
                if (dicoverer.get() != null) {
                    dicoverer.get().onNotifyDiscoveryFailed();
                }
            }
        }
    }

    private static class SendBroadcastThread extends Thread {
        WeakReference<ServiceDiscoverer> dicoverer;
        DatagramSocket socket;
        String ipAddress;
        public SendBroadcastThread(ServiceDiscoverer discoverer, DatagramSocket socket) {
            this.dicoverer = new WeakReference<ServiceDiscoverer>(discoverer);
            this.socket = socket;
            this.ipAddress = discoverer.ipAddress;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                // send packet
                Request req = RequestFactory.createServiceDiscoverRequest(ipAddress, Configure.JPG_PORT);
                final DatagramPacket packet = new DatagramPacket(req.getDataPacket(), 0, req.getDataPacket().length,
                        InetAddress.getByName(Configure.BROADCAST_ADDRESS), Configure.BROADCAST_DEST_PORT);
                Thread.sleep(50);
                Log.i(TAG, "before send search req to port:" + Configure.BROADCAST_DEST_PORT + ", bind port:" + socket.getLocalPort());
                socket.send(packet);
                Log.i(TAG, "after send search req");
            } catch (Exception e) {
                e.printStackTrace();
                Log.w(TAG, e.getMessage());
                if (dicoverer.get() != null) {
                    dicoverer.get().onNotifyDiscoveryFailed();
                }
            }
        }
    }
}
