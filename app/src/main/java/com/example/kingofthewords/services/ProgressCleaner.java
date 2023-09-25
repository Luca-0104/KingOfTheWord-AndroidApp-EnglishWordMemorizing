package com.example.kingofthewords.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.kingofthewords.MainActivity;
import com.example.kingofthewords.R;
import com.example.kingofthewords.dbModels.Task;
import com.example.kingofthewords.dbModels.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class takes charge of resetting the daily progress of all the users when every day 23:59
 */
public class ProgressCleaner extends Service {

    Timer timer = null; //the timer for counting the time to send notification

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Every time calling startService, this method will be called
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long period = 24 * 60 * 60 * 1000; // 24hours, we reset the daily progress of users every 24 hours
        long delay = calculateDelay();

        //initialize the timer
        if (timer == null) {
            timer = new Timer();
        }

        //start the timer task to reset the daily progress of users every 24 hours
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                Task taskForUpdate = new Task();

                //reset the daily progress
                taskForUpdate.setCountTodayLearn(0);    //reset the new learning progress
                taskForUpdate.setCountTodayReview(0);   //reset the reviewing progress

                //reset the record of reward getting events
                taskForUpdate.setLearnRewardGet(false);  //make the new learning reward reachable
                taskForUpdate.setReviewRewardGet(false); //make the reviewing reward reachable

                taskForUpdate.updateAll();

                MainActivity.updateCurrentUser();       //update the data of current user

            }

        }, delay, period);


        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * This is method is used to calculate the delay time that should between now and 23:59
     * @return the delay in millisecond
     */
    private long calculateDelay(){
        long delay = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //get the date and time string of current datetime
        String nowDate = sdf.format(System.currentTimeMillis()).split(" ")[0];
        String nowTime = sdf.format(System.currentTimeMillis()).split(" ")[1];

        //determine if the current time exceeds the notify time today
        int i = nowTime.compareTo("23:59");

        if (i > 0){
            //current time is after 23:59

            //we will set the delay as 0, which means reset the daily progress right now
            delay = 0;

        }else{
            //current time is before 23:59

            //set delay
            String resetDateTimeString = nowDate + " " + "23:59";
            long resetTimeMillis = 0;  //the millisecond of today's resetting time
            try {
                resetTimeMillis = sdf.parse(resetDateTimeString).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            delay = resetTimeMillis - System.currentTimeMillis();

        }

        //for test
        System.out.println("delay to reset " + delay);

        return delay;
    }

}
