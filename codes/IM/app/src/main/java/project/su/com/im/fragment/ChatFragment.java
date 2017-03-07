package project.su.com.im.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import project.su.com.im.R;
import project.su.com.im.activity.FragmentContinerActivity;
import project.su.com.im.activity.MyApplication;
import project.su.com.im.adapter.BuddyAndGroupAdapter;
import project.su.com.im.adapter.ContentAdapter;
import project.su.com.im.connection.ConnectionService;
import project.su.com.im.exitApplication.ExitApplication;
import project.su.com.im.message.Message;
import project.su.com.im.message.MessageType;
import project.su.com.im.threadManager.ThreadManager;
import project.su.com.im.time.MyTime;

/**
 * Created by su on 2016/10/19.
 */

public class ChatFragment extends Fragment{

    private TextView groupName = null;
    private Button join = null;
    private Button buddy = null;
    private Button quit = null;
    private ListView chatList = null;
    private EditText sendMessage = null;
    private Button sendButton = null;
    //private ArrayList<Message> messageList = null;
    private ArrayList<Message> messageList = null;
    private ArrayList<Message> joinedGroupMessageList = null;
    private HashMap<String, ArrayList<Message>> groupMessage = null;
    private Toast toast = null;
    private PopupWindow popupWindowGroupList = null;
    private PopupWindow popupWindowBuddyList = null;
    private static final String TYPE = "type";
    private HashMap<String, String> sendMessageMap = null; // send message to server such click button
    private FragmentContinerActivity fragmentContinerActivity = null;
    private String groupNameValue = null; // the name of group which the client choose to join
    private MyApplication myApplication = null;
    private ArrayList<String> buddyList = null;
    private ArrayList<String> groupList = null;
    static ContentAdapter contentAdapter = null;//because chatFragment and GroupFragment will use the variable,so static
    private String sendMessageTime = null;
    private ListView listView = null;
    private BuddyAndGroupAdapter buddyAndGroupAdapter = null;
    private ArrayList<String> groupBuddyDataSource = null;
    private HashMap<String, Boolean> stillJoinedGroup = null;
    private boolean addOrNotMessage = true; // join group: judge whether to add the message to the list or not
    //static String sendMessageTime = null;//because chatFragment and GroupFragment will use the variable,so static

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragement_chat,null);
        groupName = (TextView)view.findViewById(R.id.groupName);
        join = (Button)view.findViewById(R.id.join);
        buddy = (Button)view.findViewById(R.id.buddy);
        quit = (Button)view.findViewById(R.id.quit);
        chatList = (ListView)view.findViewById(R.id.chatList);
        sendMessage = (EditText)view.findViewById(R.id.sendMessage);
        sendButton = (Button)view.findViewById(R.id.sendButton);
        fragmentContinerActivity = (FragmentContinerActivity)getActivity();

        myApplication = (MyApplication)getActivity().getApplication();
        stillJoinedGroup = FragmentContinerActivity.stillJoinedGroup;
        //messageList = ((FragmentContinerActivity)getActivity()).getMessageList();
        groupMessage = ((FragmentContinerActivity)getActivity()).getMessage();

        /*
        // because ChatFragment and groupFragment will use the same messageList, so messageList will be set as share varable
        if (myApplication.getMessagelist() == null){
            messageList = new ArrayList<Message>();
            myApplication.setMessagelist(messageList);
        }else{
            messageList = myApplication.getMessagelist();
        }
        */
        messageList = ((FragmentContinerActivity)getActivity()).getMessageList();

        String joinedGroup = ((FragmentContinerActivity)getActivity()).getJoinedGroup();
        groupName.setText(joinedGroup);
        //if (!joinedGroup.equals("Not Connected")){
        //    messageList.clear();
        //    joinedGroupMessageList = groupMessage.get(joinedGroup);
        //    messageList.addAll(joinedGroupMessageList);
       // }
        if (!joinedGroup.equals("Not Connected")) {
            joinedGroupMessageList = groupMessage.get(joinedGroup);
            messageList.clear();
            messageList.addAll(joinedGroupMessageList);
            if (stillJoinedGroup.get(joinedGroup) == true){
                chatList.setBackgroundColor(Color.parseColor("#ffcccc")); // new message, set background to give a notice
                stillJoinedGroup.put(joinedGroup, false);
            }
        }



        String formerJoinedGroup = ((FragmentContinerActivity)getActivity()).getFormerJoinedGroup();
        if ( formerJoinedGroup != null){
            joinedGroupMessageList = groupMessage.get(formerJoinedGroup);
            messageList.clear();
            messageList.addAll(joinedGroupMessageList);
            ((FragmentContinerActivity)getActivity()).setFormerJoinedGroup(null);
            groupMessage.remove(formerJoinedGroup);
            chatList.setBackgroundColor(Color.parseColor("#ffcccc")); // new message, set background to give a notice

        }

        sendMessageMap = new HashMap<>();

        contentAdapter = ((FragmentContinerActivity)getActivity()).getContentAdapter();
        chatList.setAdapter(contentAdapter);

        //fresh the message
        if (contentAdapter != null){
            contentAdapter.notifyDataSetChanged();
        }
        // show the least message on the middle of the screen
        if (messageList.size() > 0){
            chatList.setSelection(messageList.size() - 1);
        }

        chatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // pressing the button indicates that the client have see the new message, so reset the background1 of no new message
                chatList.setBackgroundColor(Color.parseColor("#E1FFFF"));
                return false;
            }
        });
        MyClickListener myClickListener = new MyClickListener();

        join.setOnClickListener(myClickListener);
        buddy.setOnClickListener(myClickListener);
        quit.setOnClickListener(myClickListener);
        sendButton.setOnClickListener(myClickListener);


        // must empty the listeners, or the message will be repeated on the chat list.
        myApplication.getConnectionService().emptyListeners();
        myApplication.getConnectionService().addOnClickMessageListener(onClickMessageListener);


        return view;
    }

    class MyClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.join){
                // pressing the button indicates that the client have see the new message, so reset the background1 of no new message
                //chatList.setBackgroundColor(Color.parseColor("#f8f8ff"));
                if (fragmentContinerActivity.getStatus().equals("Up/Down")){
                    toast = Toast.makeText(getActivity(), "Connection is not stable, try later" ,Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    sendMessageMap.put(TYPE,MessageType.MSG_TYPE_GROUP_LIST);
                    ThreadManager.runInWorkThread(new Runnable() {
                        @Override
                        public void run() {
                            myApplication.getConnectionService().sendMessage(sendMessageMap);
                        }
                    });
                }

            } else if (v.getId() == R.id.buddy){
                //chatList.setBackgroundColor(Color.parseColor("#f8f8ff"));
                if (groupName.getText().toString().equals("Not Connected")){
                    toast = Toast.makeText(getActivity(), "Join a group first" ,Toast.LENGTH_SHORT);
                    toast.show();
                }else if (fragmentContinerActivity.getStatus().equals("Up/Down")){
                    toast = Toast.makeText(getActivity(), "Connection is not stable, try later" ,Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    sendMessageMap.put(TYPE, MessageType.MSG_TYPE_GROUP_BUDDY);
                    ThreadManager.runInWorkThread(new Runnable() {
                        @Override
                        public void run() {
                            myApplication.getConnectionService().sendMessage(sendMessageMap);
                        }
                    });

                }
            } else if (v.getId() == R.id.quit){
                //chatList.setBackgroundColor(Color.parseColor("#f8f8ff"));
                if (groupName.getText().toString().equals("Not Connected")){
                    toast = Toast.makeText(getActivity(), "Join a group first" ,Toast.LENGTH_SHORT);
                    toast.show();
                }else if (fragmentContinerActivity.getStatus().equals("Up/Down")){
                    toast = Toast.makeText(getActivity(), "Connection is not stable, try later" ,Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    sendMessageMap.put(TYPE, MessageType.MSG_TYPE_LEAVE_GROUP);
                    sendMessageMap.put("name", myApplication.getClientName());
                    sendMessageMap.put("group",groupName.getText().toString());
                    sendMessageTime = MyTime.getTime();
                    ThreadManager.runInWorkThread(new Runnable() {
                        @Override
                        public void run() {
                            myApplication.getConnectionService().sendMessage(sendMessageMap);
                        }
                    });
                }
            } else if (v.getId() == R.id.sendButton){
                //chatList.setBackgroundColor(Color.parseColor("#f8f8ff"));
                if (groupName.getText().toString().equals("Not Connected")){
                    toast = Toast.makeText(getActivity(), "Join a group first" ,Toast.LENGTH_SHORT);
                    toast.show();
                }else if (fragmentContinerActivity.getStatus().equals("Up/Down")){
                    toast = Toast.makeText(getActivity(), "Connection is not stable, try later" ,Toast.LENGTH_SHORT);
                    toast.show();
                }else{ //send a new message
                    String newSendMessage = sendMessage.getText().toString().trim();
                    if (newSendMessage.equals("")){
                        toast = Toast.makeText(getActivity(),"Message should not be empty", Toast.LENGTH_SHORT);
                    }else {
                        sendMessageTime = MyTime.getTime();
                        sendMessageMap.put(TYPE, MessageType.MSG_TYPE_GROUP_CHAT);
                        sendMessageMap.put("content", newSendMessage);
                        ThreadManager.runInWorkThread(new Runnable() {
                            @Override
                            public void run() {
                                myApplication.getConnectionService().sendMessage(sendMessageMap);
                            }
                        });
                    }
                }
            }
        }
    }


    private void showPopupWindow(View view, ArrayList<String> list, String item){
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragement_buddy_group_list, null);
        if (item.equals("group")){
            if (popupWindowGroupList == null){
                popupWindowGroupList = new PopupWindow(contentView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
            }
        }
        if (item.equals("buddy")){
            if (popupWindowBuddyList == null){
                popupWindowBuddyList = new PopupWindow(contentView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
            }
        }

        if (groupBuddyDataSource == null){
            groupBuddyDataSource = new ArrayList<>();
        }
        groupBuddyDataSource.clear();
        groupBuddyDataSource.addAll(list);

        if (buddyAndGroupAdapter == null){
            buddyAndGroupAdapter = new BuddyAndGroupAdapter(getActivity(),groupBuddyDataSource);// buddyAndGroupAdapter must be only the same one, can not be new a new one every pressing. if create new one, the list view will not update when the data changes.

        }

        listView = (ListView)contentView.findViewById(R.id.listView);
        listView.setAdapter(buddyAndGroupAdapter);
        if (buddyAndGroupAdapter != null){
            buddyAndGroupAdapter.notifyDataSetChanged();
        }
        if (item.equals("group")){
            popupWindowGroupList.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.background1));// must set the background1, or it will not dismiss
            //popupWindowGroupList.setFocusable(true);
       }
       if (item.equals("buddy")){
           popupWindowBuddyList.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.background2));// must set the background1, or it will not dismiss
           //popupWindowBuddyList.setFocusable(true);
       }
        if (item.equals("group")){// because only groupList can be click to choose a group to join, for the buddyList, it can only be seen not be chosen
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    groupNameValue = (String)listView.getItemAtPosition(position);// get which item is selected
                    if (groupNameValue.equals(groupName.getText().toString())){
                        toast = Toast.makeText(getActivity(), "You have been in the group now", Toast.LENGTH_SHORT);
                        toast.show();
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setIcon(R.drawable.pikaqiu);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Are you sure to join this group ?");
                        builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                sendMessageMap.put(TYPE, MessageType.MSG_TYPE_JOIN_GROUP);
                                sendMessageMap.put("name",myApplication.getClientName());
                                sendMessageMap.put("group",groupNameValue);
                                // because sever's time is later than the time the message is sent out, so the client who send the message will use the time of phone, others will use server's time
                                //myApplication.setSendMessageTime(MyTime.getTime());
                                //sendMessageTime = myApplication.getSendMessageTime();
                                sendMessageTime = MyTime.getTime();

                                ThreadManager.runInWorkThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myApplication.getConnectionService().sendMessage(sendMessageMap);
                                    }
                                });
                                popupWindowGroupList.dismiss();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.create();
                        builder.show();
                    }

                }
            });
        }

        if (item.equals("group")){
            popupWindowGroupList.showAtLocation(view,Gravity.CENTER,0,0);

        }
        if (item.equals("buddy")){
            popupWindowBuddyList.showAtLocation(view,Gravity.CENTER,0,0);

        }
    }

    private ConnectionService.OnClickMessageListener onClickMessageListener = new ConnectionService.OnClickMessageListener() {
        @Override
        public void onReceive(final HashMap<String, String> message) {
            ThreadManager.runInMainThread(new Runnable() {
                @Override
                public void run() {

                    // because MSG_TYPE_GROUP_LIST will be sent from the server, when pressing buddy or entering groupFragment. but in groupFragment, group list is not used popupWindow to show,so need to judge the message come from which one
                    // and other message will also be executed by both
                   // if (((FragmentContinerActivity)getActivity()).isFragmentActive()){
                        if (message.get(TYPE).equals(MessageType.MSG_TYPE_GROUP_BUDDY)) {
                            buddyList = JSON.parseObject(message.get("buddyList"), new TypeReference<ArrayList<String>>(){});
                            chatList.setBackgroundColor(Color.parseColor("#E1FFFF"));
                            showPopupWindow(buddy, buddyList,"buddy");
                        }else if(message.get(TYPE).equals(MessageType.MSG_TYPE_GROUP_LIST ) ){
                            // because MSG_TYPE_GROUP_LIST will be sent from the server, when pressing buddy or entering groupFragment. but in groupFragment, group list is not used popupWindow to show,so need to judge the message come from which one
                            //if (((FragmentContinerActivity)getActivity()).isFragmentActive()){
                                groupList = JSON.parseObject(message.get("groupList"), new TypeReference<ArrayList<String>>(){});
                                chatList.setBackgroundColor(Color.parseColor("#E1FFFF"));
                                showPopupWindow(join, groupList,"group");
                          //  }
                        }else if (message.get(TYPE).equals(MessageType.MSG_TYPE_JOIN_GROUP)){
                            boolean update = false;
                            if (message.get("name").equals(myApplication.getClientName())){
                                update = true;

                                groupName.setText(groupNameValue); // show group name on the APP
                                if (!groupMessage.keySet().contains(groupNameValue)){

                                    ArrayList<Message> messageList = new ArrayList<Message>();
                                    groupMessage.put(groupNameValue, messageList);
                                    //chatList.setBackgroundColor(Color.parseColor("#E1FFFF"));

                                }else {
                                    if (stillJoinedGroup.keySet().contains(groupNameValue)){
                                        addOrNotMessage = false;
                                    }else {
                                        addOrNotMessage = true;
                                    }

                                }
                                stillJoinedGroup.put(groupNameValue, false);
                                joinedGroupMessageList = groupMessage.get(groupNameValue);
                                messageList.clear();
                                messageList.addAll(joinedGroupMessageList);
                                ((FragmentContinerActivity)getActivity()).setJoinedGroup(groupNameValue);// store which group is joined
                            }else {
                                //if (groupMessage.keySet().contains(message.get("group"))){
                                    if (message.get("group").equals(((FragmentContinerActivity)getActivity()).getJoinedGroup())){
                                        update = true;
                                    }else{
                                        update = false;
                                    }
                                stillJoinedGroup.put(message.get("group"), true);
                                    joinedGroupMessageList = groupMessage.get(message.get("group"));
                                    messageList.clear();
                                    messageList.addAll(joinedGroupMessageList);
                               // }
                            }
                            updateMessage(message, update);
                        }else if (message.get(TYPE).equals(MessageType.MSG_TYPE_LEAVE_GROUP)){
                            boolean update = false;
                            if (message.get("name").equals(myApplication.getClientName())){

                                ((FragmentContinerActivity)getActivity()).setJoinedGroup("Not Connected");// store which group is joined
                                String joinedGroup = groupName.getText().toString();
                                stillJoinedGroup.put(joinedGroup, false);
                                //message.remove(joinedGroup);
                                groupName.setText("Not Connected");
                                joinedGroupMessageList = groupMessage.get(joinedGroup);
                                messageList.clear();
                                messageList.addAll(joinedGroupMessageList);
                                update = true;
                                updateMessage(message, update);
                                stillJoinedGroup.remove(joinedGroup);
                            }else {
                                //if (groupMessage.keySet().contains(message.get("group"))){
                                    if (message.get("group").equals(((FragmentContinerActivity)getActivity()).getJoinedGroup())){
                                        update = true;
                                    }else{
                                        update = false;
                                    }
                                    stillJoinedGroup.put(message.get("group"), true);
                                    joinedGroupMessageList = groupMessage.get(message.get("group"));
                                    messageList.clear();
                                    messageList.addAll(joinedGroupMessageList);
                                //}
                                updateMessage(message, update);
                            }

                        }else if (message.get(TYPE).equals(MessageType.MSG_TYPE_GROUP_CHAT)){
                            boolean update = false;
                            if (message.get("name").equals(myApplication.getClientName())){
                                sendMessage.setText("");
                                stillJoinedGroup.put(message.get("group"), false);

                            }else{
                                stillJoinedGroup.put(message.get("group"), true);
                            }

                            if (message.get("group").equals(((FragmentContinerActivity)getActivity()).getJoinedGroup())){
                                update = true;
                            }else{
                                update = false;
                            }
                            joinedGroupMessageList = groupMessage.get(message.get("group"));
                            messageList.clear();
                            messageList.addAll(joinedGroupMessageList);

                            updateMessage(message, update);

                        }else if (message.get("type").equals(MessageType.MSG_TYPE_DELETE_GROUP)){
                            boolean update = false;
                            if (message.get("status").equals(MessageType.MSG_TYPE_SUCCESS)){

                                if (message.get("group").equals(groupName.getText().toString())){
                                    groupName.setText("Not Connected");
                                    ((FragmentContinerActivity)getActivity()).setJoinedGroup("Not Connected");
                                    update = true;
                                    joinedGroupMessageList = groupMessage.get(message.get("group"));
                                    messageList.clear();
                                    messageList.addAll(joinedGroupMessageList);
                                    stillJoinedGroup.put(message.get("group"), true);
                                    updateMessage(message, update);
                                }else {
                                    toast = Toast.makeText(getActivity(),message.get("group") + " is deleted by its creator", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                                stillJoinedGroup.remove(message.get("group"));
                                message.remove(message.get("group"));

                            }
                        }else if (message.get("type").equals(MessageType.MSG_TYPE_NET_PROBLEM_BACK)){
                                sendMessageTime = message.get("time");
                            boolean update = false;


                            if (message.get("group").equals(((FragmentContinerActivity)getActivity()).getJoinedGroup())){
                                update = true;

                            }else{
                                update = false;

                            }
                            if (message.get("name").equals(myApplication.getClientName())){
                                stillJoinedGroup.put(message.get("group"), false);
                            }else {
                                stillJoinedGroup.put(message.get("group"), true);
                            }
                            joinedGroupMessageList = groupMessage.get(message.get("group"));
                            messageList.clear();
                            messageList.addAll(joinedGroupMessageList);

                            updateMessage(message, update);

                        }else if (message.get("type").equals(MessageType.MSG_TYPE_NET_PROBLEM_LEAVE)) {
                            boolean update = false;

                            if (message.get("group").equals(((FragmentContinerActivity)getActivity()).getJoinedGroup())){
                                update = true;
                            }else{
                                update = false;
                            }
                            stillJoinedGroup.put(message.get("group"), true);
                            joinedGroupMessageList = groupMessage.get(message.get("group"));
                            messageList.clear();
                            messageList.addAll(joinedGroupMessageList);

                            updateMessage(message, update);
                        }
                   // }

                }
            });
        }
    };

    // This function is used for the upper function "onReceive()": create new message to show on the chat screen
    public void updateMessage(HashMap<String, String> message,boolean update){
        //create new message
        if (addOrNotMessage == true){
            Message msg = new Message();
            if (message.get("name").equals(myApplication.getClientName())){
                msg.setType(Message.TYPE_SEND);
                msg.setTime(sendMessageTime);
                //chatList.setBackgroundColor(Color.parseColor("#E1FFFF"));
            }else {
                //chatList.setBackgroundColor(Color.parseColor("#ffcccc"));// receive a new message from other people, the background of chat dialog will change
                msg.setType(Message.TYPE_RESPONSE);
                msg.setTime(message.get("time"));
            }
            msg.setName(message.get("name"));
            msg.setContent(message.get("content"));
            messageList.add(msg);// add to message list to show
            joinedGroupMessageList.add(msg);
        }
           addOrNotMessage = true;

        if (update == true){
            if (stillJoinedGroup.get(message.get("group")) == false){
                chatList.setBackgroundColor(Color.parseColor("#E1FFFF"));
            }else {
                chatList.setBackgroundColor(Color.parseColor("#ffcccc"));// receive a new message from other people, the background of chat dialog will change
            }
            stillJoinedGroup.put(message.get("group"),false);
            //fresh the message
            if (contentAdapter != null){
                contentAdapter.notifyDataSetChanged();
            }
            // show the least message on the middle of the screen
            if (messageList.size() > 0){
                chatList.setSelection(messageList.size() - 1);
            }
        }


    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
