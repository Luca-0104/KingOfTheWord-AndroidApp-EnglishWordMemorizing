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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kingofthewords.dbModels.Book;
import com.example.kingofthewords.dbModels.Task;
import com.example.kingofthewords.dbModels.User;
import com.example.kingofthewords.dbModels.Word;
import com.google.android.material.navigation.NavigationView;

import org.litepal.LitePal;

import java.util.List;

public class TaskCheckActivity extends AppCompatActivity implements View.OnClickListener {

    //the whole DrawerLayout in the 'my Daily Task' activity
    private DrawerLayout mDLMenu;

    //the task that is doing (might be null)
    private Task currentTask;
    //the book in this task
    private Book taskBook;

    //elements in this activity
    private LinearLayout mLLNoTask;
    private LinearLayout mLLWhole;
    private ImageView mIvCoverPic;
    private Button mBtnNoTaskSetting;
    private Button mBtnSetting;
    private Button mBtnRewardLearn;
    private Button mBtnRewardReview;
    private TextView mTvBookName;
    private TextView mTvNewNum;
    private TextView mTvReviewNum;
    private TextView mTvTotalLearn;
    private TextView mTvTotalMax;
    private TextView mTvTodayLearned;
    private TextView mTvDailyLearnedMax;
    private TextView mTvTodayReviewed;
    private TextView mTvDailyReviewedMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_check);

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
        MainActivity.setSideMenu(mNVMenu, TaskCheckActivity.this, mDLMenu);


        //initialize the elements
        initElements();

        //initialize the currentTask
        if (initCurrentTask()){ //if there is a task
            //initialize the book of this task
            this.taskBook = LitePal.where("id = ?", String.valueOf(MainActivity.currentUser.getSelectedBookId())).findFirst(Book.class);
            //set values on elements
            setElementsValues();
            //set click listener for buttons
            mBtnSetting.setOnClickListener(this);
            mBtnRewardLearn.setOnClickListener(this);
            mBtnRewardReview.setOnClickListener(this);
            //set clickable for reward buttons
            initRewardButtons();

        }else{  //if there is no task yet
            //set click listener for buttons
            mBtnNoTaskSetting.setOnClickListener(this);
            //set the visibility
            setElementsInvisible();
        }



    }


    /**
     * add our self-designed menu into this activity
     * (toolbar at the top, not the side menu)
     * @param menu our self-designed toolbar at the top
     * @return
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
     * @return
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


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btn_task_check_no_task_setting:
                //go to the task setting activity
                intent = new Intent(TaskCheckActivity.this, TaskManageActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_task_check_setting:
                //go to the task setting activity
                intent = new Intent(TaskCheckActivity.this, TaskManageActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_task_check_reward_learn:
                //increase the user balance
                User userForUpdate = new User();
                userForUpdate.setBalance(MainActivity.currentUser.getBalance() + 8);
                userForUpdate.updateAll("id = ?", String.valueOf(MainActivity.currentUser.getId()));

                //record the reward getting event in today's learning task
                Task taskForUpdate = new Task();
                taskForUpdate.setLearnRewardGet(true);
                taskForUpdate.updateAll("id = ?", String.valueOf(currentTask.getId()));

                //update the data in current user
                MainActivity.updateCurrentUser();

                Toast.makeText(TaskCheckActivity.this, "coins +8", Toast.LENGTH_SHORT).show();
                //disable the button
                mBtnRewardLearn.setEnabled(false);
                mBtnRewardLearn.setText("Received");
                break;

            case R.id.btn_task_check_reward_review:
                //increase the user balance
                User userForUpdate2 = new User();
                userForUpdate2.setBalance(MainActivity.currentUser.getBalance() + 8);
                userForUpdate2.updateAll("id = ?", String.valueOf(MainActivity.currentUser.getId()));
                MainActivity.updateCurrentUser();

                //record the reward getting event in today's reviewing task
                Task taskForUpdate2 = new Task();
                taskForUpdate2.setReviewRewardGet(true);
                taskForUpdate2.updateAll("id = ?", String.valueOf(currentTask.getId()));

                //update the data in current user
                MainActivity.updateCurrentUser();

                Toast.makeText(TaskCheckActivity.this, "coins +8", Toast.LENGTH_SHORT).show();
                //disable the button
                mBtnRewardReview.setEnabled(false);
                mBtnRewardLearn.setText("Received");
                break;
        }
    }


    /**
     * Initializing all the elements
     */
    private void initElements(){
        this.mLLNoTask = findViewById(R.id.ll_task_check_no_task);
        this.mLLWhole = findViewById(R.id.ll_task_check_whole_page);
        this.mBtnNoTaskSetting = findViewById(R.id.btn_task_check_no_task_setting);
        this.mBtnSetting = findViewById(R.id.btn_task_check_setting);
        this.mBtnRewardLearn = findViewById(R.id.btn_task_check_reward_learn);
        this.mBtnRewardReview = findViewById(R.id.btn_task_check_reward_review);
        this.mTvNewNum = findViewById(R.id.tv_task_check_new_word_num);
        this.mTvReviewNum = findViewById(R.id.tv_task_check_review_word_num);
        this.mTvTotalLearn = findViewById(R.id.tv_task_check_total_learn);
        this.mTvTotalMax = findViewById(R.id.tv_task_check_total_max);
        this.mTvTodayLearned = findViewById(R.id.tv_task_check_progress_learn);
        this.mTvDailyLearnedMax = findViewById(R.id.tv_task_check_max_learn);
        this.mTvTodayReviewed = findViewById(R.id.tv_task_check_progress_review);
        this.mTvDailyReviewedMax = findViewById(R.id.tv_task_check_max_review);
        this.mTvBookName = findViewById(R.id.tv_task_check_book_name);
        this.mIvCoverPic = findViewById(R.id.iv_task_check_book_image);
    }

    /**
     * Set values on elements
     */
    private void setElementsValues(){
        mTvNewNum.setText(String.valueOf(currentTask.getNewWordNum()));
        mTvReviewNum.setText(String.valueOf(currentTask.getReviewWordNum()));
        mTvTotalLearn.setText(String.valueOf(currentTask.getLearnedWordNum()));
        mTvTotalMax.setText(String.valueOf(taskBook.getWordNumber()));
        mTvTodayLearned.setText(String.valueOf(currentTask.getCountTodayLearn()));
        mTvDailyLearnedMax.setText(String.valueOf(currentTask.getNewWordNum()));
        mTvTodayReviewed.setText(String.valueOf(currentTask.getCountTodayReview()));
        mTvDailyReviewedMax.setText(String.valueOf(currentTask.getReviewWordNum()));
        mTvBookName.setText(taskBook.getBookName());
        Glide.with(this).load(taskBook.getCoverPicture()).into(mIvCoverPic);

        //initialize the reward buttons
        if (currentTask.isLearnRewardGet()){
            mBtnRewardLearn.setText("Received");
        }else{
            mBtnRewardLearn.setText("Get Reward");
        }
        if (currentTask.isReviewRewardGet()){
            mBtnRewardReview.setText("Received");
        }else{
            mBtnRewardReview.setText("Get Reward");
        }

    }

    /**
     * Initialize the reword buttons, if task done, they are clickable
     * otherwise, they are not clickable
     */
    private void initRewardButtons(){
        //if new learning task is not finished or the reward has been gotten, its reward button should be not clickable
        if (currentTask.getCountTodayLearn() < currentTask.getNewWordNum() ||
            currentTask.isLearnRewardGet()){

            mBtnRewardLearn.setEnabled(false);
        }
        //if new reviewing task is not finished or the reward has been gotten, its reward button should be not clickable
        if (currentTask.getCountTodayReview() < currentTask.getReviewWordNum() ||
            currentTask.isReviewRewardGet()){

            mBtnRewardReview.setEnabled(false);
        }
    }

    /**
     * initialize the currentTask
     * @return true means here is a current task, false means no task yet
     */
    private boolean initCurrentTask(){
        //get the id of selected book
        int selectedBookId = MainActivity.currentUser.getSelectedBookId();
        //if there is not a selected book yet
        if(selectedBookId < 0){
            return false;
        }

        //get the task according to user and book
        this.currentTask = LitePal.where("userId = ? and bookId = ?", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(selectedBookId)).findFirst(Task.class);
        //if there is not a task
        if(currentTask == null){
            return false;
        }

        return true;
    }

    /**
     * set all elements invisible, and show only a text for reminding
     */
    private void setElementsInvisible(){
        mLLNoTask.setVisibility(View.VISIBLE);
        mLLWhole.setVisibility(View.INVISIBLE);
    }


}