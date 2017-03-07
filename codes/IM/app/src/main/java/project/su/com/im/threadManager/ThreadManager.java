package project.su.com.im.threadManager;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by su on 2016/10/21.
 */

public class ThreadManager {
    /*
     //because sending messages to server in main thread will fail, only in work thread
    // this is for sending messages opening a new thread.
    What about receiving messages? This also needs a new thread to deal with,
    this new thread has been created in the connection code.(after building a connection, then creating a new thread to receive message)
     */
    public static void runInWorkThread(Runnable r){
        new Thread(r).start();
    }

    /*
     // ThreadManager's object is created in which activity, the handler will belong to which activity.
     So the handler.post(r) will run in which activity, the views of the activity will be controlled.
     */
    private static Handler handler = null;
    /*
    This will run in the main thread(UI thread):
    Because the views can not be controlled in the work thread, so this can solve the  problem
    So this the Object of this class must be created in the specific main thread( the oncreate() of activit)
     */
    public static void runInMainThread(Runnable r){
        handler = new Handler(Looper.getMainLooper());
        handler.post(r); // the codes in r will run in main thread.
    }

}
