package project.su.com.im.fragment;

import android.app.Fragment;
import android.app.FragmentContainer;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;

import project.su.com.im.R;
import project.su.com.im.activity.FragmentContinerActivity;
import project.su.com.im.activity.LoginActivity;
import project.su.com.im.activity.MyApplication;
import project.su.com.im.adapter.ContentAdapter;
import project.su.com.im.connection.ConnectionService;
import project.su.com.im.exitApplication.ExitApplication;
import project.su.com.im.message.Message;
import project.su.com.im.message.MessageType;
import project.su.com.im.threadManager.ThreadManager;
import project.su.com.im.time.MyTime;

import static project.su.com.im.activity.FragmentContinerActivity.stillJoinedGroup;

/**
 * Created by su on 2016/10/19.
 */

public class GroupFragment extends Fragment {
    private ListView groupList = null;
    private EditText createdGroupName = null;
    private Button createButton = null;
    private ArrayList<Message> messageList = null;
    private HashMap<String, ArrayList<Message>> groupMessage = null;
    //private ArrayList<Message> joinedGroupMessageList = null;
    private MyApplication myApplication = null;
    private ArrayAdapter<String> adapter = null;
    private HashMap<String, String> sendMessageMap = null;
    private String sendMessageTime = null;
    private Toast toast = null;
    private ArrayList<String> groupListData = null;// the data that group list will show
    private String selectedGroup = null; // choose to delete a group
    private FragmentContinerActivity fragmentContinerActivity = null;
    private boolean pressJoin = false;
    private ContentAdapter contentAdapter = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group,null);
        groupList = (ListView)view.findViewById(R.id.groupList);
        createdGroupName = (EditText)view.findViewById(R.id.createdGroupName);
        createButton = (Button)view.findViewById(R.id.createButton);
        fragmentContinerActivity = (FragmentContinerActivity)getActivity();
        groupListData = new ArrayList<String>();
        myApplication = (MyApplication)getActivity().getApplication();
        /*
        // because ChatFragment and groupFragment will use the same messageList, so messageList will be set as share varable
        if (myApplication.getMessagelist() == null){
            messageList = new ArrayList<Message>();
            myApplication.setMessagelist(messageList);
        }else{
            messageList = myApplication.getMessagelist();
        }
        */
        //messageList = ((FragmentContinerActivity)getActivity()).getMessageList();

        String joinedGroup = ((FragmentContinerActivity)getActivity()).getJoinedGroup();
        //if (!joinedGroup.equals("Not Connected")){
         //   joinedGroupMessageList = groupMessage.get(joinedGroup);
        //}
        //messageList = ((FragmentContinerActivity)getActivity()).getMessageList();

        groupMessage = ((FragmentContinerActivity)getActivity()).getMessage();

        contentAdapter = ((FragmentContinerActivity)getActivity()).getContentAdapter();

        if (fragmentContinerActivity.getStatus().equals("Up/Down")){
            toast = Toast.makeText(getActivity(), "Connection is not stable, try later" ,Toast.LENGTH_LONG);
            toast.show();
        }
        sendMessageMap = new HashMap<>();
        sendMessageMap.put("type", MessageType.MSG_TYPE_GROUP_LIST);
        ThreadManager.runInWorkThread(new Runnable() {
            @Override
            public void run() {
                myApplication.getConnectionService().sendMessage(sendMessageMap);
            }
        });

        // must empty the listeners, or the message will be repeated on the chat list.
        myApplication.getConnectionService().emptyListeners();
        myApplication.getConnectionService().addOnClickMessageListener(listener);
       // myApplication.getConnectionService().addOnClickMessageListener(listener);

        //adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, null);


        groupList.setTranscriptMode(groupList.TRANSCRIPT_MODE_NORMAL);//

        MyClickListener myClickListener = new MyClickListener();
        createButton.setOnClickListener(myClickListener);



        return view;
    }

    class MyClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (fragmentContinerActivity.getStatus().equals("Up/Down")){
                toast = Toast.makeText(getActivity(), "Connection is not stable, try later" ,Toast.LENGTH_LONG);
                toast.show();
            }else{
                String createdGroupNameValue = createdGroupName.getText().toString().trim();
                if (createdGroupNameValue.equals("")){
                    toast = Toast.makeText(getActivity(), "Group name should not be empty", Toast.LENGTH_SHORT);
                }else {
                    sendMessageMap.put("type",MessageType.MSG_TYPE_CREATE_GROUP);
                    sendMessageMap.put("time", MyTime.getTime());
                    sendMessageMap.put("groupName",createdGroupNameValue);
                    sendMessageMap.put("creator",myApplication.getClientName());
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

    // create new OnClickMessageListener object
    private ConnectionService.OnClickMessageListener listener = new ConnectionService.OnClickMessageListener() {
        @Override
        public void onReceive(final HashMap<String, String> message) {
            // because these codes will be executed in the work thread, so in order to let the codes related to UI run in main thread,
            //using handler to realize
            ThreadManager.runInMainThread(new Runnable() {
                @Override
                public void run() {

                    //if (!((FragmentContinerActivity)getActivity()).isFragmentActive()){
                        if (message.get("type").equals(MessageType.MSG_TYPE_GROUP_LIST)){
                            groupListData.addAll(JSON.parseObject(message.get("groupList"), new TypeReference<ArrayList<String>>(){}));
                            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, groupListData);
                            groupList.setAdapter(adapter);
                            groupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    selectedGroup = (String)groupList.getItemAtPosition(position);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setIcon(R.drawable.pikaqiu);
                                    builder.setTitle("Confirmation");
                                    builder.setMessage("Are you sure to delete this group ?");
                                    builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            sendMessageMap.put("type", MessageType.MSG_TYPE_DELETE_GROUP);
                                            sendMessageMap.put("groupName",selectedGroup);
                                            sendMessageMap.put("creator",myApplication.getClientName());
                                            sendMessageMap.put("content", selectedGroup + " has been dismissed by its creator ( "+ myApplication.getClientName() +" ), " + "every member is forced out of the group");
                                            // because sever's time is later than the time the message is sent out, so the client who send the message will use the time of phone, others will use server's time

                                            sendMessageTime = MyTime.getTime();

                                            ThreadManager.runInWorkThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    myApplication.getConnectionService().sendMessage(sendMessageMap);
                                                }
                                            });
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder.create();
                                    builder.show();
                                    return false;
                                }
                            });
                        }

                    if (message.get("type").equals(MessageType.MSG_TYPE_JOIN_GROUP)){
                        messageList = groupMessage.get(message.get("group"));
                        updateMessage(message);
                    }else if (message.get("type").equals(MessageType.MSG_TYPE_LEAVE_GROUP)){
                        messageList = groupMessage.get(message.get("group"));
                        updateMessage(message);
                    }else if (message.get("type").equals(MessageType.MSG_TYPE_GROUP_CHAT)){
                        messageList = groupMessage.get(message.get("group"));
                        updateMessage(message);

                    }else if (message.get("type").equals(MessageType.MSG_TYPE_NET_PROBLEM_BACK)){
                        sendMessageTime = message.get("time");
                        messageList = groupMessage.get(message.get("group"));
                        updateMessage(message);

                    }else if (message.get("type").equals(MessageType.MSG_TYPE_NET_PROBLEM_LEAVE)) {
                        messageList = groupMessage.get(message.get("group"));
                        updateMessage(message);

                    }


                        if (message.get("type").equals(MessageType.MSG_TYPE_DELETE_GROUP)){
                            if (message.get("status").equals(MessageType.MSG_TYPE_SUCCESS)){


                                groupListData.remove(message.get("group"));//update group list,
                                adapter.notifyDataSetChanged(); // refresh the data

                                if (message.get("name").equals(myApplication.getClientName())){
                                    toast = Toast.makeText(getActivity(),"Success", Toast.LENGTH_SHORT);
                                    toast.show();
                                }else{
                                    toast = Toast.makeText(getActivity(),message.get("group") + " is deleted by its creator", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                FragmentContinerActivity.stillJoinedGroup.remove(message.get("group"));
                                // update the status of connected group
                                ///TextView groupName = (TextView)getFragmentManager().findFragmentById(R.layout.fragement_chat).getView().findViewById(R.id.groupName);// this code can be used in the activity which include the fragment. but can not be used in the fragment,
                                // because when one fragment disappears, the views of the fragment are null, can be used. only set a shared variable to read when the fragment appears.
                                String connectedGroupName = ((FragmentContinerActivity)getActivity()).getJoinedGroup();
                                if (connectedGroupName.equals(message.get("group"))){

                                    //updateMessage(message);
                                    ((FragmentContinerActivity)getActivity()).setFormerJoinedGroup(connectedGroupName);
                                    ((FragmentContinerActivity)getActivity()).setJoinedGroup("Not Connected");//set connection status, when return to chatFragment, will show the message
                                    //groupName.setText("Not Connected");
                                }
                                messageList = groupMessage.get(message.get("group"));
                                if (messageList != null){
                                    updateMessage(message);
                                }
                                if (!connectedGroupName.equals(message.get("group"))){
                                    groupMessage.remove(message.get("group"));
                                }


                            }else {
                                toast = Toast.makeText(getActivity(), message.get("reason"), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                        if (message.get("type").equals(MessageType.MSG_TYPE_CREATE_GROUP)){
                            if (message.get("status").equals(MessageType.MSG_TYPE_SUCCESS)){

                                groupListData.add(message.get("newGroup"));//update group list,
                                adapter.notifyDataSetChanged(); // refresh the data
                                if (message.get("creator").equals(myApplication.getClientName())){
                                    createdGroupName.setText("");
                                    toast = Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            }else{
                                toast = Toast.makeText(getActivity(), message.get("reason"), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                   // }

                }
            });
        }
    };
    // This function is used for the upper function "onReceive()": create new nessage to show on the chat screen
    public void updateMessage(HashMap<String, String> message){
        //create new message
        Message msg = new Message();
        //find the chatList in ChatFragment
        //ListView chatList = (ListView)getFragmentManager().findFragmentById(R.layout.fragement_chat).getView().findViewById(R.id.chatList);
        if (message.get("name").equals(myApplication.getClientName())){
            msg.setType(Message.TYPE_SEND);
            msg.setTime(sendMessageTime);
        }else {

            FragmentContinerActivity.stillJoinedGroup.put(message.get("group"), true);
            //chatList.setBackgroundColor(Color.parseColor("#ffcccc"));// receive a new message from other people, the background of chat dialog will change
            msg.setType(Message.TYPE_RESPONSE);
            msg.setTime(message.get("time"));
        }
        msg.setName(message.get("name"));
        msg.setContent(message.get("content"));
        messageList.add(msg);// add to message list to show
        /*
        //refresh the message
        if (contentAdapter != null){
            ChatFragment.contentAdapter.notifyDataSetChanged();
        }
        // show the least message on the middle of the screen
        if (messageList.size() > 0){
            chatList.setSelection(messageList.size() - 1);
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
    }



}
