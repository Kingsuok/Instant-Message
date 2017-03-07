package project.su.com.im.message;

/*
 * the protocol between clients and server. clients and server will know the types of messages when sending messages
 */
public class MessageType {
    public static final String MSG_TYPE_REGISTER = "register";
    public static final String MSG_TYPE_LOGIN = "login";
    public static final String MSG_TYPE_LOGOUT = "logout";
    public static final String MSG_TYPE_JOIN_GROUP = "joinGroup"; //join a group
    public static final String MSG_TYPE_LEAVE_GROUP = "leaveGroup"; //leave a group
    public static final String MSG_TYPE_GROUP_CHAT = "groupChat"; // client's chat in a group
    public static final String MSG_TYPE_GROUP_LIST = "groupList"; // show all the group
    public static final String MSG_TYPE_GROUP_BUDDY = "buddyList"; // show talkers in the group
    public static final String MSG_TYPE_SUCCESS = "success"; // flag : success
    public static final String MSG_TYPE_FAILURE = "failure"; // flag : failure
    public static final String MSG_TYPE_CREATE_GROUP = "createGroup"; //create a group
    public static final String MSG_TYPE_DELETE_GROUP = "deleteGroup"; //delete a group
    public static final String MSG_TYPE_NET_PROBLEM_BACK = "netProblemBack";
    public static final String MSG_TYPE_NET_PROBLEM_LEAVE = "netProblemLeave";
}
