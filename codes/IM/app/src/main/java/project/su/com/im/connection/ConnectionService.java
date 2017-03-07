package project.su.com.im.connection;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.su.com.im.activity.FragmentContinerActivity;
import project.su.com.im.activity.LoginActivity;
import project.su.com.im.message.MessageService;
import project.su.com.im.message.MessageType;
import project.su.com.im.time.MyTime;

/**
 * Created by su on 2016/10/20.
 */

public class ConnectionService {
    public Socket socket = null;
    private DataInputStream reader = null;
    private DataOutputStream writer = null;
    private Handler handler = null;
    private TextView status = null;
    public volatile static boolean exit = false; // when link breaks, then exit = true. exit form the thread
    private ArrayList<OnClickMessageListener> listeners = null; // store OnClickMessageListener
    private HashMap<String, String> sendMessageMap = null;
    private String clientName = null;
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setStatus(TextView status) {
        this.status = status;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public ArrayList<OnClickMessageListener> getListeners() {
        return listeners;
    }

    public ConnectionService(){
        listeners = new ArrayList<OnClickMessageListener>();
    }
    // the connection must be implemented firstly
    public boolean conncetion(){  //connect to server
        boolean flag = false;

            try {
                socket = new Socket(ConnectionConfiguration.IP, ConnectionConfiguration.port);
            } catch (IOException e) {
                closeResource();
                //e.printStackTrace();
                return false;
            }


        flag = true;
        //if (flag == true){
            writerInitial();
            readerInitial();
            receiveMessageWaitThread();// open the wait thread to wait for receiving message from server
       // }
        return flag;
    }

    public void readerInitial(){
        if (reader == null){
            try {
                reader = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    if (reader != null){
                        reader.close();
                        reader = null;
                    }
                } catch (IOException e1){
                    e1.printStackTrace();
                }

            }
        }
    }
    public void writerInitial(){
        if (writer == null){
            try {
                writer = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    if (writer != null){
                        writer.close();
                        writer = null;
                    }
                } catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
    }
    public void closeResource(){
        try {
            if (socket != null){

                socket.close();
                socket = null;
            }
            if (reader != null){
                reader.close();
                reader = null;
            }
            if (writer != null){
                writer.close();
                writer = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method of sending messages

    public void sendMessage(HashMap<String, String> message) {
       // writerInitial();
        String messageToString = JSON.toJSONString(message);
        message.clear();//empty the message for other sending
        try {
            writer.writeUTF(messageToString);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (writer != null){

                    writer.close();
                    writer = null;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    // thread for waiting to receive message from the server
    public void receiveMessageWaitThread (){

         new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                while (true){
                   // readerInitial();


                        //if (reader.available()>0){
                            // receive message from server, this message is JSON style

                            String receiveMessageString = reader.readUTF();

                            // JSON deserialization to HashMap
                            HashMap<String, String> receiveMessage = JSON.parseObject(receiveMessageString, new TypeReference<HashMap<String, String>>(){});
                            // the message will be dealt based on different type of message in method of onReceive(), such register, login, ....
                            //So one message can only have one listener be dealt.
                            for (OnClickMessageListener listener : listeners){
                                listener.onReceive(receiveMessage);
                            }
                       // }



                }
                } catch (IOException e){
                    LoginActivity.successConnection = false;
                    closeResource();
                    if (handler != null){
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                status.setText("Up/Down");
                            }
                        };
                        handler.post(r);
                    }else {
                        System.out.println(socket + "broke");
                    }

                    e.printStackTrace();

                }finally {
                    if (LoginActivity.loginSuccess == true){
                        boolean success = false;
                        while(success == false ) { // always trying to connect to the server

                            success = conncetion(); // connect to the server again
                            if (success == false){
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (handler != null){
                            String groupName = FragmentContinerActivity.joinedGroup;
                            String time = MyTime.getTime();
                            sendMessageMap = new HashMap<String, String>();
                            sendMessageMap.put("type", MessageType.MSG_TYPE_NET_PROBLEM_BACK);
                            sendMessageMap.put("name", clientName);
                            sendMessageMap.put("group",groupName);
                            sendMessageMap.put("time", time);
                            String stillJoinedGroup = JSON.toJSONString(FragmentContinerActivity.stillJoinedGroup.keySet());
                            sendMessageMap.put("stillJoinedGroup",stillJoinedGroup);
                            //myApplication.getConnectionService().setSocket();
                            sendMessage(sendMessageMap);


                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    status.setText("Up");
                                }
                            };
                            handler.post(r);
                        }
                        //connectionService.receiveMessageWaitThread();// start the thread of receiving messages again

                    }
                    }

            }
        }.start();
    }

    // add a listener
    public void addOnClickMessageListener(OnClickMessageListener listener){
        listeners.add(listener);
    }

    // remove the listener
    public void removeOnClickMessageListener(OnClickMessageListener listener){
        listeners.remove(listener);
    }
    //empty listeners
    public void emptyListeners(){
        listeners.clear();
    }

    //this is a inner interface, defined in a class. so this can let the class have flexible function.
    /*
    This inner interface is used as a variable of class "ConnectionService", so this interface can be used to new interface objects.
     The function of the interface is to realize that when a client click a button or others, after receiving a message from the server,
      UIs will base on the message to react, the reaction will be implemented in onReceive().
      The interface object will be put into work thread to be executed in there.
     */
    public interface OnClickMessageListener {
        public void onReceive(HashMap<String, String> message);
    }

}
