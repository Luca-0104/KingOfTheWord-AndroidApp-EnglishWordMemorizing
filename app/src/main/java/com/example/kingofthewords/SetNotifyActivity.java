package com.example.kingofthewords;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kingofthewords.dbModels.User;
import com.example.kingofthewords.services.MyNotification;
import com.google.android.material.navigation.NavigationView;

public class SetNotifyActivity extends AppCompatActivity implements View.OnClickListener {

    //the whole DrawerLayout in the 'setNotify' activity
    private DrawerLayout mDLMenu;

    //the elements in this page
    private TextView mTvNotifyTime; //notify time of the current user
    private TimePicker mTpTimepicker;
    private Button mBtnSetTime;

    //the formatted selected time in the timepicker
    private String selectedTime; //should in format of "hh:mm:ss"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_notify);

        //----------------------------------------------------------------------------------------
        //replace the default actionbar with the toolbar I have defined by myself
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set the left top button's functionality as turning on the side menu
        mDLMenu = findViewById(R.id.dl_menu);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_side_menu);
        }

        //setting the navigationView inside the side menu
        NavigationView mNVMenu = findViewById(R.id.nv_side_menu);
        MainActivity.setSideMenu(mNVMenu, SetNotifyActivity.this, mDLMenu);
        //----------------------------------------------------------------------------------------

        //initialize the elements
        initElements();
        //set values on elements
        setElementsValues();

        //initialize the selected time using the current notify time of the current user
        selectedTime = MainActivity.currentUser.getNotifyTime();

        //set onclick listener of the buttons
        mBtnSetTime.setOnClickListener(this);

        //set the timepicker as 24-hours mode
        mTpTimepicker.setIs24HourView(true);

        //set listener for the timepicker
        mTpTimepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minutes) {
                //update the selected time
                updateSelectedTime(hour, minutes);
            }
        });

    }


    /**
     * add our self-designed menu into this activity
     * (toolbar at the top, not the side menu)
     * @param menu our self-designed toolbar at the top
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add our self-designed menu(toolbar at the top, not the side menu) into this activity
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     *  Setting the clicking listener for the items on the top toolbar
     * @param item The item on the top toolbar, which is been clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //when clicking on the items in our self-designed menu (toolbar)
        switch (item.getItemId()){
            //if the side menu is clicked
            case android.R.id.home:
                //open the side menu
                mDLMenu.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    /**
     * Initialize the elements
     */
    private void initElements(){
        mTvNotifyTime = findViewById(R.id.tv_notify_user_time);
        mTpTimepicker = findViewById(R.id.notify_timepicker);
        mBtnSetTime = findViewById(R.id.btn_notify_change);
    }

    /**
     * Set values on elements
     */
    private void setElementsValues(){
        mTvNotifyTime.setText(MainActivity.currentUser.getNotifyTime());
    }

    /**
     * Define what to do when clicking on the buttons on this page.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_notify_change:
                /*
                    when clicking on the confirm button,
                    the "selectedTime" will be assigned to the "notifyTime"
                    of current user in database.
                */
                //update the selected time of current user
                User userForUpdate = new User();
                userForUpdate.setNotifyTime(this.selectedTime);
                userForUpdate.updateAll("id = ?", String.valueOf(MainActivity.currentUser.getId()));
                MainActivity.updateCurrentUser();

                //restart the notification service
                startNotificationService(MainActivity.currentUser.getNotifyTime());

                //Toast
                Toast.makeText(SetNotifyActivity.this, "Your notify time is changed to " + selectedTime, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(SetNotifyActivity.this, SetNotifyActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * This will update the selected time, which is recorded as a instance variable
     * @param hour the int number represents hour
     * @param minutes the int number represents minutes
     */
    private void updateSelectedTime(int hour, int minutes){
        String timeString = "";
        String hourStr = "";
        String minutesStr = "";

        //unify the format of hour and minutes
        if(hour < 10){
            hourStr += "0";
        }

        if (minutes < 10){
            minutesStr += "0";
        }

        //change the int time into string
        hourStr += String.valueOf(hour);
        minutesStr += String.valueOf(minutes);

        //concat the hour and the minutes
        timeString += hourStr;
        timeString += ":";
        timeString += minutesStr;
        timeString += ":00";

        //update the selected time
        this.selectedTime = timeString;
    }

    /**
     * Start the notification service
     */
    private void startNotificationService(String notifyTime){
        Intent intent = new Intent(SetNotifyActivity.this, MyNotification.class);
        //pass the notify time to the MyNotification
        /*
            we have to do this because, the 'startService' always be executed first, do not know why.
            Therefore, it always be called before all the initializing operations in the 'onCreate' method of MainActivity.
            This is so weird!
            So that we 'currentUser' is not initialized if we use it in MyNotification.
            So that we have to pass the notify time to the MyNotification by intent.
        */
        intent.putExtra("notify_time", notifyTime);
        SetNotifyActivity.this.startService(intent);
    }

}