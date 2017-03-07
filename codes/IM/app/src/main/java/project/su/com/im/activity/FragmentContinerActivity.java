package project.su.com.im.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import project.su.com.im.R;
import project.su.com.im.adapter.ContentAdapter;
import project.su.com.im.connection.CheckConnection;
import project.su.com.im.exitApplication.ExitApplication;
import project.su.com.im.fragment.ChatFragment;
import project.su.com.im.fragment.GroupFragment;
import project.su.com.im.message.Message;
import project.su.com.im.message.MessageType;
import project.su.com.im.threadManager.ThreadManager;

/**
 * Created by su on 2016/10/19.
 */

public class FragmentContinerActivity extends AppCompatActivity implements View.OnClickListener{
    private Button chatManager = null;
    private Button groupManager = null;
    private TextView status = null;
    private FragmentManager fragementManager = null;
    private FragmentTransaction fragementTransaction = null;
    private CheckConnection checkConnection = null;
    private Handler handler = null;
    private MyApplication myApplication = null;
    private HashMap<String, String> sendMessageMap = null;
    public static String joinedGroup = "Not Connected"; // current chat group
    public static HashMap<String,Boolean> stillJoinedGroup = new HashMap<String,Boolean>(); // key:joined group, value: whether the message should be regarded as a new message to change the color
    private HashMap<String, ArrayList<Message>> groupMessage = new HashMap<String, ArrayList<Message>>();
    //private HashMap<String, Boolean> groupNewMessageFlag = new HashMap<>();
    private ArrayList<Message> messageList = new ArrayList<>();
    private ContentAdapter contentAdapter = null;
    //private HashMap<String,Boolean> groupJoined = new HashMap<String,Boolean>();

    // true: receive a new chat message which needs to show on the chat list when groupFragment is active and chatFragemt is inactive;
    // false: no chat message is received when ....
    // this variable is used for groupFragment codes, when a new message appears, and set the color of  background of chatlist
    //private boolean newMessage = false;
    private String formerJoinedGroup = null;
    //public HashMap<String,Boolean> getGroupJoined(){
    //    return groupJoined;
   // }

    public String getFormerJoinedGroup() {
        return formerJoinedGroup;
    }

    public void setFormerJoinedGroup(String formerJoinedGroup) {
        this.formerJoinedGroup = formerJoinedGroup;
    }



    public ContentAdapter getContentAdapter() {
        return contentAdapter;
    }

    public ArrayList<Message> getMessageList() {
        return messageList;
    }

    public HashMap<String, ArrayList<Message>> getMessage(){
        return groupMessage;
    }

    //public HashMap<String, Boolean> getGroupNewMessageFlag(){
   //     return groupNewMessageFlag;
   // }


    public String getJoinedGroup() {
        return joinedGroup;
    }

    public void setJoinedGroup(String joinedGroup) {
        this.joinedGroup = joinedGroup;
    }

    // chatFragment and groupFragment will call to judge which fragment is active
    //public boolean isFragmentActive() {
     //   return fragmentActive;
    //}


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();// get rid of title
        setContentView(R.layout.activity_fragment_continer);
        ExitApplication.getInstance().addActivity(this);// close all activities and exit

        chatManager = (Button)findViewById(R.id.chatManager);
        groupManager = (Button)findViewById(R.id.groupManager);
        status = (TextView)findViewById(R.id.status);
        chatManager.setOnClickListener(this);
        groupManager.setOnClickListener(this);
        fragementManager = getFragmentManager();



        fragementTransaction = fragementManager.beginTransaction();

        contentAdapter = new ContentAdapter(this,messageList);


        //entering the activity, show the chat fragement. without these codes, it will show a blank view
        chatManager.setTextColor(Color.RED);
        ChatFragment chatFragmentFragment = new ChatFragment();
        fragementTransaction.replace(R.id.fragments, chatFragmentFragment,"chatFragmentFragment" );
        // do not add the fragement to BackStack, just let user press button to switch fragment
        //fragementTransaction.addToBackStack("chatFragmentFragment");
        fragementTransaction.commit();// start the fragment to show

        // start heart package to check the connection status, heart packet runs in a new thread, that's right
        handler = new Handler();
        myApplication = (MyApplication)getApplication();
        //myApplication.setHandler(handler);
        //myApplication.setStatus(status);
        myApplication.getConnectionService().setStatus(status);
        myApplication.getConnectionService().setHandler(handler);

        //checkConnection = new CheckConnection(myApplication.getConnectionService(),handler, status, myApplication.getClientName());
        //checkConnection.start();
    }

    @Override
    public void onClick(View v) {
        //must have this sentence, do not think fragementTransaction has been initialed above, missing it will have bug
        // because one fragementTransaction can only be fragementTransaction.commit(); only once, if want to be committed again, should create new fragementTransaction
        fragementTransaction = fragementManager.beginTransaction();
        switch (v.getId()){
            case R.id.chatManager:
                chatManager.setTextColor(Color.RED);
                groupManager.setTextColor(Color.BLACK);
                ChatFragment chatFragmentFragment = new ChatFragment();
                fragementTransaction.replace(R.id.fragments, chatFragmentFragment,"chatFragmentFragment" );
                // do not add the fragement to BackStack, just let user press button to switch fragment
                //fragementTransaction.addToBackStack("chatFragmentFragment");
                //fragmentActive = true;
                break;
            case R.id.groupManager:
                chatManager.setTextColor(Color.BLACK);
                groupManager.setTextColor(Color.RED);
                GroupFragment groupFragmentFragment = new GroupFragment();
                fragementTransaction.replace(R.id.fragments, groupFragmentFragment,"groupFragmentFragment");
                // do not add the fragement to BackStack, just let user press button to switch fragment
                //fragementTransaction.addToBackStack("groupFragmentFragment");
               // fragmentActive = false;
                break;
        }
        fragementTransaction.commit();
    }

    //This method is for the tow fragments to call to get the status of the connection
    public String getStatus(){
        String status = null;
        status = this.status.getText().toString();
        return status;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.pikaqiu);
                builder.setTitle("Alert");
                builder.setMessage("Do you want to Exit?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // if the user does not quiet group before pressing back, send message to tell other members of group
                        sendMessageMap = new HashMap<String, String>();

                        ThreadManager.runInWorkThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!stillJoinedGroup.isEmpty()){
                                    for (String groupName : stillJoinedGroup.keySet()){
                                        sendMessageMap.put("type", MessageType.MSG_TYPE_LEAVE_GROUP);
                                        sendMessageMap.put("name", myApplication.getClientName());
                                        sendMessageMap.put("group",groupName);
                                        myApplication.getConnectionService().sendMessage(sendMessageMap);
                                    }
                                }
                                sendMessageMap.put("type", MessageType.MSG_TYPE_LOGOUT);
                                sendMessageMap.put("name",myApplication.getClientName());
                                myApplication.getConnectionService().sendMessage(sendMessageMap);
                            }
                        });

                        ExitApplication.getInstance().exit(); // exit the application
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show(); // show the dialog
                break;
        }
        return false;
    }


}
