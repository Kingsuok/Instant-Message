package project.su.com.im.connection;

import android.os.Handler;
import android.widget.TextView;

import java.net.SocketException;
import java.util.HashMap;

import project.su.com.im.activity.FragmentContinerActivity;
import project.su.com.im.activity.MyApplication;
import project.su.com.im.message.MessageType;
import project.su.com.im.threadManager.ThreadManager;
import project.su.com.im.time.MyTime;

/**
 * Because win7 SYSTEM have some problem for sendUrgentData, after 17times, the APP will close the socket, so not use it
 * Created by su on 2016/10/20.
 * this is for the connection, whether the connection is still connected or not,
 * if not , the app should deal with this problem.
 * this is a heart packet for this function
 */

public class CheckConnection extends Thread {
    private Handler handler = null;
    private TextView status = null;
    private ConnectionService connectionService = null;
    private HashMap<String, String> sendMessageMap = null;
    private String clientName;
    //private MyApplication myApplication = null;
    public CheckConnection(ConnectionService connectionService, Handler handler, TextView status, String clientName){
        this.connectionService = connectionService;
        this.handler = handler;
        this.status = status;
        this.clientName = clientName;
        //myApplication = (MyApplication)getApplication();
    }
    @Override
    public void run() {
        super.run();
        try {
           connectionService.socket.setKeepAlive(true);
          //  connectionService.socket.setOOBInline(false);
        } catch (SocketException e) {
            e.printStackTrace();
       }
        while(true){

            ConnectionService.exit = false;
            try {
                while (true){

                    connectionService.socket.sendUrgentData(0xff);
                    Thread.sleep(3000);
                }
            } catch (Exception e){
                ConnectionService.exit = true; // close the thread of receiving messages
                connectionService.closeResource(); // close all the resource
                Runnable r = new Runnable(){

                    @Override
                    public void run() {
                        status.setText("Up/Down");
                    }
                };
                handler.post(r);
                // pass the signal to UI --> Up/Down
                e.printStackTrace();
            }finally {
                boolean success = false;
                while(success == false ) { // always trying to connect to the server

                    success = connectionService.conncetion(); // connect to the server again
                    if (success == false){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //connectionService.receiveMessageWaitThread();// start the thread of receiving messages again
                String groupName = FragmentContinerActivity.joinedGroup;
                String time = MyTime.getTime();
                sendMessageMap = new HashMap<String, String>();
                sendMessageMap.put("type", MessageType.MSG_TYPE_NET_PROBLEM_BACK);
                sendMessageMap.put("name", clientName);
                sendMessageMap.put("group",groupName);
                sendMessageMap.put("time", time);

                //myApplication.getConnectionService().setSocket();
                connectionService.sendMessage(sendMessageMap);
                sendMessageMap.clear();

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        status.setText("Up");
                    }
                };
                handler.post(r);
                //pass the signal to UI --> On
            }
        }
    }
}
