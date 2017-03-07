package project.su.com.im.activity;

import android.app.Application;
import android.os.Handler;
import android.widget.TextView;

import java.util.ArrayList;

import project.su.com.im.connection.ConnectionService;
import project.su.com.im.message.Message;

/**
 * Created by su on 2016/10/21.
 * this class: let all activities and views of the app use variables
 */

public class MyApplication extends Application {
    private ConnectionService connectionService = null;
    private String clientName = null;
    private ArrayList<Message> messagelist = null;// not use, can delete
    private String sendMessageTime = null;//not use, can delete
    //private Handler handler= null;
    //private TextView status = null;


    public String getSendMessageTime() {
        return sendMessageTime;
    }

    //public Handler getHandler() {
    //    return handler;
    //}

   // public void setHandler(Handler handler) {
   //     this.handler = handler;
   // }

    //public TextView getStatus() {
   //     return status;
    //}

   // public void setStatus(TextView status) {
    //    this.status = status;
   // }

    public void setSendMessageTime(String sendMessageTime) {
        this.sendMessageTime = sendMessageTime;
    }

    public ArrayList<Message> getMessagelist() {
        return messagelist;
    }

    public void setMessagelist(ArrayList<Message> messagelist) {
        this.messagelist = messagelist;
    }

    public ConnectionService getConnectionService() {
        return connectionService;
    }

    public void setConnectionService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public String getClientName()
    {
        return clientName;
    }

    public void setClientName(String clientName){
        this.clientName = clientName;
    }
}
