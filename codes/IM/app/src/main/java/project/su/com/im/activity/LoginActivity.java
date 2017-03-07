package project.su.com.im.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;
import java.util.HashMap;

import project.su.com.im.R;
import project.su.com.im.connection.ConnectionService;
import project.su.com.im.exitApplication.ExitApplication;
import project.su.com.im.message.MessageType;
import project.su.com.im.threadManager.ThreadManager;

public class LoginActivity extends AppCompatActivity {
    private TextView userName;
    private TextView password;
    private Button login;
    private Button signUp;
    private Socket socket = null;
    private Toast toast = null;
    private ConnectionService connectionService = null;
    private HashMap<String, String> sendMessage = null;
    private final static String TYPE = "type";
    private final static String STATUS = "status";
    private String clientName = null;
    private boolean success = false;
    private Handler handler = null;
    public static boolean successConnection = true;
    public static boolean loginSuccess = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();// get rid of title
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);// full screen
        setContentView(R.layout.activity_login);
        ExitApplication.getInstance().addActivity(this);// close all activities and exit

        userName = (TextView)findViewById(R.id.userName);
        password = (TextView)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        signUp = (Button)findViewById(R.id.register);
        connectionService = new ConnectionService();
        MyListener myListener = new MyListener();
        login.setOnClickListener(myListener);
        signUp.setOnClickListener(myListener);
        handler = new Handler();
        sendMessage = new HashMap<>();

        connection();

    }

    private void connection(){
        ThreadManager.runInWorkThread(new Runnable() {
            @Override
            public void run() {

                while (success == false){
                    success = connectionService.conncetion();
                    if (success == false){
                        try {
                            Thread.sleep(30*1000);// try to connect to the server every 30 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        break;
                    }
                }

                connectionService.addOnClickMessageListener(listener);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        toast = Toast.makeText(getApplicationContext(), "Connection successfully", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });


            }
        });
    }

    class MyListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String nameTem = userName.getText().toString().replaceAll(" ", "");
            String passwordTem = password.getText().toString().replaceAll(" ", "");
            String nameValue = userName.getText().toString().trim();
            clientName = nameValue;
            String passwordValue = password.getText().toString().trim();

            //check the format of user name and password
            if (nameValue.equals("")||passwordValue.equals("")){
                toast = Toast.makeText(getApplicationContext(),"UserName and password should not be empty", Toast.LENGTH_SHORT);
                toast.show();
            }else if (nameTem.length() != nameValue.length() || passwordTem.length() != passwordValue.length()) {
                toast = Toast.makeText(getApplicationContext(), "UserName and password should not have blank", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                if (success == false ){
                    toast = Toast.makeText(getApplicationContext(), "Server doesn't work, try later", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (success == true && successConnection == false){
                    toast = Toast.makeText(getApplicationContext(), "Server doesn't work, try later", Toast.LENGTH_SHORT);
                    toast.show();
                    successConnection = true;
                    success = false;
                    connection();
                } else{
                    if (v.getId() == R.id.login){
                        sendMessage.put(TYPE, MessageType.MSG_TYPE_LOGIN);
                        sendMessage.put("name", nameValue);
                        sendMessage.put("password", passwordValue);
                        // dealing with sending message to server should be executed in work thread
                        ThreadManager.runInWorkThread(new Runnable() {
                            @Override
                            public void run() {
                                connectionService.sendMessage(sendMessage);
                            }
                        });

                    }else {// sign up
                        sendMessage.put(TYPE, MessageType.MSG_TYPE_REGISTER);
                        sendMessage.put("name",nameValue);
                        sendMessage.put("password", passwordValue);
                        // dealing with sending message to server should be executed in work thread
                        ThreadManager.runInWorkThread(new Runnable() {
                            @Override
                            public void run() {
                                connectionService.sendMessage(sendMessage);
                            }
                        });
                    }
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
                    if (message.get(TYPE).equals(MessageType.MSG_TYPE_LOGIN)){
                        if (message.get(STATUS).equals(MessageType.MSG_TYPE_SUCCESS)){
                            // get the MyApplication object and set the variable for other activities or codes to use
                            MyApplication myApplication = (MyApplication)getApplication();
                            myApplication.setConnectionService(connectionService);
                            myApplication.setClientName(clientName);
                            connectionService.setClientName(clientName);
                            loginSuccess = true;
                            // jump to other activity
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, FragmentContinerActivity.class);
                            startActivity(intent);
                        }else {
                            toast = Toast.makeText(getApplicationContext(), "Failure: " + message.get("reason"),Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    if (message.get(TYPE).equals(MessageType.MSG_TYPE_REGISTER)){
                        if (message.get(STATUS).equals(MessageType.MSG_TYPE_SUCCESS)){
                            toast = Toast.makeText(getApplicationContext(), "Success" ,Toast.LENGTH_SHORT);
                        }else {
                            toast = Toast.makeText(getApplicationContext(), "Failure: " + message.get("reason"),Toast.LENGTH_SHORT);
                        }
                        toast.show();
                    }
                }
            });
        }
    };

    @Override
    //protected void onStop() {
     //   super.onStop();
        // when quite the activity, remove the listener
     //   connectionService.removeOnClickMessageListener(listener);
   // }



    public boolean onKeyDown(int keyCode, KeyEvent event ){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.pikaqiu);
                builder.setTitle("Alter");
                builder.setMessage("Do you want to exit ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
                break;
        }
        return false;
    }
}
