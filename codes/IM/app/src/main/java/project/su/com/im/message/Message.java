package project.su.com.im.message;

/**
 * Created by su on 2016/10/20.
 */

public class Message {
    public final static int TYPE_SEND = 0; //the message which the user send to others
    public final static int TYPE_RESPONSE = 1; // the message which the user receive from others

    private int type; // message type : TYPE_SEND or TYPE_RESPONSE
    private String time;
    //private int avatar; // message's head
    private String name; // user's name
    private String content; // message's content

    public void setType(int type) {
        this.type = type;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    //public int getAvatar() {
   //     return avatar;
   // }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
