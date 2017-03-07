package project.su.com.im.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by su on 2016/10/20.
 */

public class MyTime {
    //current date project.su.com.im.time
    public static String getTime(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
        return format.format(date);
    }

    //long --> String
    public static String getTime(Long time){
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
        return format.format(date);
    }

    //String-->long
    public static Long getTime(String dateString) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
        return format.parse(dateString).getTime();
    }
}
