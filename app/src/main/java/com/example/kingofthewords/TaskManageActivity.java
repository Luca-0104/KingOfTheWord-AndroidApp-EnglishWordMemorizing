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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kingofthewords.dbModels.Book;
import com.example.kingofthewords.dbModels.Task;
import com.google.android.material.navigation.NavigationView;

import org.litepal.LitePal;

public class TaskManageActivity extends AppCompatActivity implements View.OnClickListener {

    //the whole DrawerLayout in the 'Task management' activity
    private DrawerLayout mDLMenu;

    //the task that is doing (might be null)
    private Task currentTask;
    //the book in this task
    private Book taskBook;

    //the elements in this activity
    private LinearLayout mLlNoBook;
    private LinearLayout mLlWhole;
    private LinearLayout mLlTaskInfo;
    private ImageView mIvCoverPic;
    private TextView mTvBookName;
    private TextView mTvNewNum;
    private TextView mTvReviewNum;
    private TextView mTvTotalLearn;
    private TextView mTvTotalMax;

    private EditText mEtNewWordNum;
    private EditText mEtReviewWordNum;
    private Button mBtnConfirm;
    private Button mBtnSelectBook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manage);

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
        MainActivity.setSideMenu(mNVMenu, TaskManageActivity.this, mDLMenu);


        //initialize the elements in this activity
        initElements();

        //if this user do not have a book being learning, we show the text to tell the user
        if (MainActivity.currentUser.getSelectedBookId() <= 0){
            mLlNoBook.setVisibility(View.VISIBLE);
            mLlWhole.setVisibility(View.INVISIBLE);

            //set the click listener for the button
            mBtnSelectBook.setOnClickListener(this);


        }else{  //if there is a selected book

            //initialize the book
            this.taskBook = LitePal.where("id = ?", String.valueOf(MainActivity.currentUser.getSelectedBookId())).findFirst(Book.class);

            //initialize the currentTask
            if (initCurrentTask()){ //if there is a task

                //set values on elements
                setElementsValues();

                //make the task info visible
                this.mLlTaskInfo.setVisibility(View.VISIBLE);

            }else{  //if there is no task yet

                //only give the values to picture and book name
                mTvBookName.setText(taskBook.getBookName());
                Glide.with(this).load(taskBook.getCoverPicture()).into(mIvCoverPic);

                //make the task info invisible
                this.mLlTaskInfo.setVisibility(View.INVISIBLE);
            }

            //set click listener for the buttons in this activity
            mBtnConfirm.setOnClickListener(this);
        }

    }



    private void setElementsValues() {
        mTvNewNum.setText(String.valueOf(currentTask.getNewWordNum()));
        mTvReviewNum.setText(String.valueOf(currentTask.getReviewWordNum()));
        mTvTotalLearn.setText(String.valueOf(currentTask.getLearnedWordNum()));
        mTvTotalMax.setText(String.valueOf(taskBook.getWordNumber()));
        mTvBookName.setText(taskBook.getBookName());
        Glide.with(this).load(taskBook.getCoverPicture()).into(mIvCoverPic);
    }

    private void initElements() {
        this.mLlNoBook = findViewById(R.id.ll_task_manage_no_book);
        this.mLlWhole = findViewById(R.id.ll_task_manage_whole_page);
        this.mLlTaskInfo = findViewById(R.id.ll_task_manage_task_info);
        this.mIvCoverPic = findViewById(R.id.iv_task_manage_book_image);
        this.mTvBookName = findViewById(R.id.tv_task_manage_book_name);
        this.mTvNewNum = findViewById(R.id.tv_task_manage_new_word_num);
        this.mTvReviewNum = findViewById(R.id.tv_task_manage_review_word_num);
        this.mTvTotalLearn = findViewById(R.id.tv_task_manage_total_learn);
        this.mTvTotalMax = findViewById(R.id.tv_task_manage_total_max);
        this.mEtNewWordNum = findViewById(R.id.et_task_new);
        this.mEtReviewWordNum = findViewById(R.id.et_task_review);
        this.mBtnConfirm = findViewById(R.id.btn_task_confirm);
        this.mBtnSelectBook = findViewById(R.id.btn_task_manage_no_book_select);
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

    /**
     * Define what to do when Click on the Button in this page
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //if the "confirm task" button is clicked
            case R.id.btn_task_confirm:
                //set the task information
                if (setTask()){
                    //if task is set successfully, we jump back to the TaskCheckActivity
                    Intent intent = new Intent(TaskManageActivity.this, TaskCheckActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.btn_task_manage_no_book_select:
                //go to my book shelf
                Intent intent = new Intent(TaskManageActivity.this, MyBooksActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * Setting the task for specific user and book
     * (every single user and a book he owns can be set to a task)
     * @return whether the task has been set successfully
     */
    private boolean setTask(){
        //get the numbers from editTexts
        String strNew = this.mEtNewWordNum.getText().toString();
        String strReview = this.mEtReviewWordNum.getText().toString();

        int newWordsNum = Integer.parseInt(strNew);
        int reviewWordsNum = Integer.parseInt(strReview);

        //get the current leaning book
        int selectedBookId = MainActivity.currentUser.getSelectedBookId();
        Book selectedBook = LitePal.where("id = ?", String.valueOf(selectedBookId)).findFirst(Book.class);

        /*
            check the availability of these two numbers
            newWordsNum and reviewWordsNum should <= min(word number of the book, 100)
         */
        int wordCount = selectedBook.getWordNumber();
        int min = Math.min(wordCount, 100);
        String toastText;
        if (wordCount <= 100){
            toastText = "can not greater than word count in this book";
        }else{
            toastText = "can not greater than 100";
        }
        boolean newNumOK = false;
        boolean reviewNumOK = false;
        if (newWordsNum > min){
            Toast.makeText(this, "daily new words " + toastText, Toast.LENGTH_SHORT).show();
            newNumOK = false;
        }else{
            newNumOK = true;
        }

        if (reviewWordsNum > min){
            Toast.makeText(this, "daily reviewing words " + toastText, Toast.LENGTH_SHORT).show();
            reviewNumOK = false;
        }else{
            reviewNumOK = true;
        }

        //only when both the numbers are available, the task can be set
        if (newNumOK && reviewNumOK){
            //get the current task by user and book (this could be null)
            Task currentTask = LitePal.where("userId = ? and bookId = ?", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(selectedBookId)).findFirst(Task.class);

            //If the task already exists, we update(alter) it
            if (currentTask != null){
                //create a new object for helping to update
                Task taskForUpdate = new Task();
                taskForUpdate.setNewWordNum(newWordsNum);
                taskForUpdate.setReviewWordNum(reviewWordsNum);
                //update the currentTask according to the helping object
                taskForUpdate.update(currentTask.getId());
            }else{
                //if the task does not exist, we create it
                Task task = new Task();
                task.setUser(MainActivity.currentUser);
                task.setBook(selectedBook);
                task.setBookId(selectedBookId);
                task.setUserId(MainActivity.currentUser.getId());
                task.setNewWordNum(newWordsNum);
                task.setReviewWordNum(reviewWordsNum);
                task.save();
            }

            return true;

        }else{
            return false;
        }

    }


    /**
     * initialize the currentTask
     * @return true means here is a current task, false means no task yet
     */
    private boolean initCurrentTask(){
        /* if we are here, there must be a book */

        //get the task according to user and book
        this.currentTask = LitePal.where("userId = ? and bookId = ?", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(taskBook.getId())).findFirst(Task.class);
        //if there is not a task
        if(currentTask == null){
            return false;
        }

        return true;
    }
}