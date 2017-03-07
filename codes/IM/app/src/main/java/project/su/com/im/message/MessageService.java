package project.su.com.im.message;

import java.util.HashMap;

/**
 * Created by su on 2016/10/20.
 * this is for dealing with message
 */

public interface MessageService {
    // methods for sending
    public void sendMessage(HashMap<String, String> message);


    // methods for receiving message
    public void registerReceive(HashMap<String, String> message);
    public void loginReceive(HashMap<String, String> message);
    public void logoutReceive(HashMap<String, String> message);
    public void joinGroupReceive(HashMap<String, String> message);
    //public void leaveGroupReceive(HashMap<String, String> message);
    public void groupChatReceive(HashMap<String, String> message);
    public void groupListReceive(HashMap<String, String> message);
    public void buddyListReceive(HashMap<String, String> message);
    //public void success();
    //public void failure();
    public void createGroupReceive(HashMap<String, String> message);
    public void deleteGroupReceive(HashMap<String, String> message);

}
