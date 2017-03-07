package project.su.com.im.exitApplication;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;

import java.util.LinkedList;
import java.util.List;

public class ExitApplication extends Application {
 private List<Activity> activityList = new LinkedList<>();

 private static ExitApplication instance;
 private ExitApplication()
 {
 }
 //Get a single instance of ExitApplication in singleton pattern
 public static ExitApplication getInstance()
 {
 if(null == instance)
 {
 instance = new ExitApplication();
 }
 return instance; 
 }
 //add the Activity to container
 public void addActivity(Activity activity)
 {
 activityList.add(activity);
 }


 //Traverse all Activity and finish
 public void exit()
 {
   for(Activity activity : activityList)
     {
     activity.finish();
    }

     System.exit(0);
    }


 }