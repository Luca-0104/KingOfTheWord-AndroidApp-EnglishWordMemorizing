package com.example.kingofthewords.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.kingofthewords.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MyNotification extends Service {

    Timer timer = null; //the timer for counting the time to send notification
    private String notifyTime;     //here to set when to send everyday notification

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Every time calling startService, this method will be called
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //every time start the service, we need to update the notify time first (get from intent)
        this.notifyTime = intent.getStringExtra("notify_time");

        //test
        System.out.println("-----------------------------------notify time get from intent: " + notifyTime);

        long period = 24 * 60 * 60 * 1000; // 24hours, we send notification ever 24 hours
        long delay = calculateDelay();

        //initialize the timer
        if (timer == null){
            timer = new Timer();
        }
        //start the timer task to send notification
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                //set the notification channel
                NotificationChannel channel = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    channel = new NotificationChannel("1", "my_channel", NotificationManager.IMPORTANCE_DEFAULT);
                }

                //get the manager for notification service
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    manager.createNotificationChannel(channel);
                }

                //create a new notification object
                Notification notification = new NotificationCompat.Builder(MyNotification.this)
                        .setContentTitle("Learning Task Reminder")
                        .setContentText("Don't forget to finish today's task and get the rewards~")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setChannelId("1")
                        .build();

                //send notification
                manager.notify(1, notification);

                //for test
                System.out.println("-----------------------------------------------notification sent------------------------------------------");
            }

        }, delay, period);


        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * This is method is used to calculate the delay time that should between now and the set time
     * @return the delay in millisecond
     */
    private long calculateDelay(){
        long delay = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //get the date and time string of current datetime
        String nowDate = sdf.format(System.currentTimeMillis()).split(" ")[0];
        String nowTime = sdf.format(System.currentTimeMillis()).split(" ")[1];
        //for test
        System.out.println(nowDate);
        System.out.println(nowTime);

        //determine if the current time exceeds the notify time today
        int i = nowTime.compareTo(notifyTime);

        if (i > 0){
            //current time is after notify time

            /*
                we will set the delay to tomorrow's notify time
            */

            //the time millisecond gap of 24 hours
            long timeGap = 24 * 60 * 60 * 1000;

            String notifyDateTimeString = nowDate + " " + notifyTime;
            long notifyTimeMillis = 0;  //the millisecond of next notification
            try {
                notifyTimeMillis = sdf.parse(notifyDateTimeString).getTime() + timeGap;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            delay = notifyTimeMillis - System.currentTimeMillis();

        }else{
            //current time is before notify time

            //set delay
            String notifyDateTimeString = nowDate + " " + notifyTime;
            long notifyTimeMillis = 0;  //the millisecond of next notification
            try {
                notifyTimeMillis = sdf.parse(notifyDateTimeString).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            delay = notifyTimeMillis - System.currentTimeMillis();

        }

        //for test
        System.out.println("delay " + delay);

        return delay;
    }

}
