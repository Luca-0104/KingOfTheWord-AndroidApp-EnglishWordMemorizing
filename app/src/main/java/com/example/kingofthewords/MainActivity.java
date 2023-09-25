package com.example.kingofthewords;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kingofthewords.dbModels.Book;
import com.example.kingofthewords.dbModels.Task;
import com.example.kingofthewords.dbModels.User;
import com.example.kingofthewords.dbModels.UserWord;
import com.example.kingofthewords.dbModels.Word;
import com.example.kingofthewords.services.MyNotification;
import com.example.kingofthewords.services.ProgressCleaner;
import com.google.android.material.navigation.NavigationView;

import org.litepal.LitePal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //the tag for operating the sharedPreference file
    public static final String USER_ID = "user_id";

    //an object of the current user (This can be accessed in all the activities)
    public static User currentUser;
    //the task that is doing (might be null)
    private Task currentTask;
    //the book in this task
    private Book taskBook;

    //the whole DrawerLayout in the main activity
    private DrawerLayout mDLMenu;

    //we will use the shared preference to read out the user id of the current user.
    private static SharedPreferences pref;

    //the elements in this page
    private LinearLayout mLlHasBook;
    private LinearLayout mLlNoBook;
    private Button mBtnLearnNew;
    private Button mBtnReview;
    private Button mBtnStart;
    private ImageView mIvCoverPic;
    private TextView mTvBookName;
    private TextView mTvToLearn;
    private TextView mTvToReview;
    private TextView mTvPercentage;
    private RelativeLayout mRlProBar;
    private TextView mTvProBar;
    private TextView mTvProBarBackground;
    private RecyclerView mRvNotFamiliar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the shared preference file
        pref = getSharedPreferences("data", MODE_PRIVATE);
        //retrieve the current user id from the shared preference file
        int currentUserId = pref.getInt(USER_ID, -1);
        //initialize the current user object by using the the id we got
        if (currentUserId != -1 && LitePal.where("id = ?", String.valueOf(currentUserId)).findFirst(User.class) != null){
            currentUser = LitePal.where("id = ?", String.valueOf(currentUserId)).findFirst(User.class);

        }else{
            //impossible to get here, I just put a toast here.
            Toast.makeText(MainActivity.this, "User info not fund! You may need to login again.", Toast.LENGTH_SHORT).show();
        }

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

        /*
            setting the navigationView inside the side menu
        */
        NavigationView mNVMenu = findViewById(R.id.nv_side_menu);
        setSideMenu(mNVMenu, MainActivity.this, mDLMenu);

        //initialize the elements on this page
        initElements();

        //initialize the selectedBook
        if (initSelectedBook()){ //if there is a selected book

            //if there are task set on this book
            if (isTasked(taskBook)){
                //set values on elements
                setElementsValues(true);

                //set the width of the progress bar
                setTaskProgressBar();

                //initialize the recyclerView of not familiar words
                initWordList();

            //if there are no tasks on this book yet
            }else{
                //set values on elements
                setElementsValues(false);

                //invisible the progress bar
                mRlProBar.setVisibility(View.INVISIBLE);

            }

            //set click listener for buttons
            this.mBtnLearnNew.setOnClickListener(this);
            this.mBtnReview.setOnClickListener(this);

        }else{  //if there is no task yet
            //set click listener for buttons
            this.mBtnStart.setOnClickListener(this);

            //set the visibility
            setElementsInvisible();
        }

        //initialize and start the daily progress resting service
//        startProgressRestingService();
    }

    /**
     * Initializing all the elements
     */
    private void initElements(){
        mBtnLearnNew = findViewById(R.id.btn_main_learn_new);
        mBtnReview = findViewById(R.id.btn_main_review);
        mBtnStart= findViewById(R.id.btn_main_no_selected_book_start);
        mLlHasBook = findViewById(R.id.ll_main_has_selected_book);
        mLlNoBook = findViewById(R.id.ll_main_no_selected_book);
        mIvCoverPic = findViewById(R.id.iv_main_book_image);
        mTvBookName = findViewById(R.id.tv_main_book_name);
        mTvToLearn = findViewById(R.id.tv_main_to_learn);
        mTvToReview = findViewById(R.id.tv_main_to_review);
        mTvPercentage = findViewById(R.id.tv_main_per);
        mRlProBar = findViewById(R.id.rl_main_progress_bar_module);
        mTvProBar = findViewById(R.id.tv_main_progress_bar);
        mTvProBarBackground = findViewById(R.id.tv_main_progress_bar_back_under);
        mRvNotFamiliar = findViewById(R.id.rv_main_not_familiar);
    }

    /**
     * Set values on elements
     * @param isTasked whether there are tasks on selected book
     */
    private void setElementsValues(Boolean isTasked){
        //set the book cover picture
        Glide.with(this).load(taskBook.getCoverPicture()).into(mIvCoverPic);

        //set the book name
        mTvBookName.setText(taskBook.getBookName());

        //set the data in the "daily task reminder"
        if (isTasked){
            /*
                if there are tasks, we set the task data
             */
            mTvToLearn.setText(String.valueOf(currentTask.getNewWordNum() - currentTask.getCountTodayLearn()));
            mTvToReview.setText(String.valueOf(currentTask.getReviewWordNum() - currentTask.getCountTodayReview()));

            //calculated the percentage of finished task on today
            float per = ((float) (currentTask.getCountTodayLearn() + currentTask.getCountTodayReview())) / ((float) (currentTask.getNewWordNum() + currentTask.getReviewWordNum()));
            //we will keep two digits after dot
            DecimalFormat df = new DecimalFormat("0.00");
            String perStr = df.format(per*100) + "%";
            if (perStr.equals("100.00%")){
                //just reduce the wastage of screen space
                perStr = "100%";
            }
            mTvPercentage.setText(perStr);

        }else{
            /*
                if there are no tasks, we set the default data
             */
            mTvToLearn.setText("0");
            mTvToReview.setText("0");
            mTvPercentage.setText("0%");

        }
    }

    /**
     * initialize the SelectedBook
     * @return true means here is a selected book, false means no selected book
     */
    private boolean initSelectedBook(){
        //get the id of selected book
        int selectedBookId = currentUser.getSelectedBookId();
        //if there is not a selected book yet
        if(selectedBookId < 0){
            return false;

        //if there is a selected book
        }else{
            //initialize the book of this task
            this.taskBook = LitePal.where("id = ?", String.valueOf(selectedBookId)).findFirst(Book.class);
            return true;
        }
    }

    /**
     * Check if there are tasks set on a specific book
     * This method also init the currentTask
     *
     * @param book The book to be checked
     * @return true for having tasks, false for not
     */
    private boolean isTasked(Book book){
        //get the task according to user and book
        this.currentTask = LitePal.where("userId = ? and bookId = ?", String.valueOf(currentUser.getId()), String.valueOf(book.getId())).findFirst(Task.class);
        //if there is not a task
        if(currentTask == null){
            return false;
        }

        return true;
    }

    /**
     * set some elements invisible, and show only a text and a button for indicating
     */
    private void setElementsInvisible(){
        mLlNoBook.setVisibility(View.VISIBLE);
        mLlHasBook.setVisibility(View.INVISIBLE);
    }

    /**
     * calculate and set the length of the progress bar
     */
    private void setTaskProgressBar(){
        //get the total length of track (the length of the background bar)
        int totalLength = mTvProBarBackground.getWidth();

        //do not know why getWidth always return 0!!! so we define it in pixel temporarily
        totalLength = 730;

        /*
            calculate how much percentage have the already-learned-words taken up.
            (Total number of already-learned-words) / (the word count of the book)
         */
        float per = ((float) currentTask.getLearnedWordNum()) / ((float) taskBook.getWordNumber());


        //first, calculate the float of length
        //then, parse it to integer
        //this can minimize the losing of accuracy, which can be very tiny
        float progressLengthFloat = totalLength * per;
        int progressLengthInt = (int) progressLengthFloat;

        //set the length of progressbar
        mTvProBar.setWidth(progressLengthInt);

    }

    /**
     * initialize the recyclerView list of not familiar words
     */
    private void initWordList(){
        //set the layout manager
        mRvNotFamiliar.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        //query a list of not familiar words of this user and this book from database
        List<UserWord> uwList = LitePal.where("userid = ? and bookid = ? and score = 2", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(taskBook.getId())).find(UserWord.class);
        List<Word> wordList = new ArrayList<>();
        //if there are some recorded words of this user in this book
        if (uwList != null){
            for (UserWord uw : uwList){
                Word word = LitePal.where("id = ?", String.valueOf(uw.getWordId())).findFirst(Word.class);
                wordList.add(word);
            }
        }
        //set the adapter
        mRvNotFamiliar.setAdapter(new WordAdapter(MainActivity.this, wordList));

    }



    /* -------------------------------- Public Util Methods for Every Activity -----------------------------------*/

    /**
     * This method is used to configure the side menu for very activity (both the header and selection panel)
     * @param context this method is called in which activity
     * @param mNVMenu the menu using the navigationView in this activity
     * @param mDLMenu the DrawerLayout in this activity
     */
    public static void setSideMenu(NavigationView mNVMenu, Context context, DrawerLayout mDLMenu){
        //setting the header information on the side menu
        View header = mNVMenu.inflateHeaderView(R.layout.side_menu_header);
        TextView headerEmail = header.findViewById(R.id.side_menu_header_email);
        TextView headerUsername = header.findViewById(R.id.side_menu_header_username);
        TextView headerBalance = header.findViewById(R.id.side_menu_header_balance);
        //initialize the user info in the side menu header
        headerUsername.setText(currentUser.getUsername());
        headerEmail.setText(currentUser.getEmail());
        headerBalance.setText(String.valueOf(currentUser.getBalance()));

        //setting the selections panel in the side menu
        //set the default selected (current) item
        if (context.getClass() == ProfileActivity.class) {
            mNVMenu.setCheckedItem(R.id.menu_profile);  //default selected one

        }else if(context.getClass() == MyBooksActivity.class){
            mNVMenu.setCheckedItem(R.id.menu_my_books);  //default selected one

        }else if(context.getClass() == MoreBooksActivity.class){
            mNVMenu.setCheckedItem(R.id.menu_more_books);  //default selected one

        }else if(context.getClass() == MainActivity.class){
            mNVMenu.setCheckedItem(R.id.menu_daily_task);  //default selected one

        }else if(context.getClass() == TaskCheckActivity.class){
            mNVMenu.setCheckedItem(R.id.menu_task_manage);  //default selected one

        }else if(context.getClass() == SetNotifyActivity.class){
            mNVMenu.setCheckedItem(R.id.menu_notify);  //default selected one

        }

        //set the selected listener for those menu items (selections)
        mNVMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //when selecting on those menu item
                Intent intent;
                switch (menuItem.getItemId()){
                    case R.id.menu_profile:
                        intent = new Intent(context, ProfileActivity.class);
                        context.startActivity(intent);
                        break;
                    case R.id.menu_my_books:
                        intent = new Intent(context, MyBooksActivity.class);
                        context.startActivity(intent);
                        break;
                    case R.id.menu_more_books:
                        intent = new Intent(context, MoreBooksActivity.class);
                        context.startActivity(intent);
                        break;
                    case R.id.menu_daily_task:
                        intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        break;
                    case R.id.menu_task_manage:
                        intent = new Intent(context, TaskCheckActivity.class);
                        context.startActivity(intent);
                        break;
                    case R.id.menu_notify:
                        intent = new Intent(context, SetNotifyActivity.class);
                        context.startActivity(intent);
                        break;
                    case R.id.menu_logout:
                        Toast.makeText(context, "You have logged out", Toast.LENGTH_SHORT).show();
                        intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        break;
                }

                mDLMenu.closeDrawers(); //close the menu
                return true;
            }
        });
    }

    /**
     * Query again the current user object from database.
     * This should be called after every time updating the data in currentUser object,
     * because the referenced object of current user cannot update automatically.
     */
    public static void updateCurrentUser(){
        if (currentUser == null){
            //retrieve the current user id from the shared preference file
            int currentUserId = pref.getInt(USER_ID, -1);
            //initialize the current user object by using the the id we got
            if (currentUserId != -1 && LitePal.where("id = ?", String.valueOf(currentUserId)).findFirst(User.class) != null){
                currentUser = LitePal.where("id = ?", String.valueOf(currentUserId)).findFirst(User.class);
            }
        }else{
            currentUser = LitePal.where("id = ?", String.valueOf(currentUser.getId())).findFirst(User.class);
        }
    }
    /* ----------------------------------------------------------------------------------------------------------- */


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
     * Define what to do when clicking on the buttons on this page.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //if the "learn new words" button is clicked
            case R.id.btn_main_learn_new:
                onclickLearn();
                break;

            //if the "review" button is clicked
            case R.id.btn_main_review:
                onclickReview();
                break;

            case R.id.btn_main_no_selected_book_start:
                //go to "my bookshelf"
                Intent intent = new Intent(MainActivity.this, MyBooksActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * Define what to do when "learn" button is clicked
     */
    private void onclickLearn(){
        int selectedBookId = MainActivity.currentUser.getSelectedBookId();
        //if the user has a selected book, they can go for study
        if (selectedBookId > 0){
            //if a task has already been set on selected book, go to the learn activity
            Task currentTask = LitePal.where("userId = ? and bookId = ?", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(selectedBookId)).findFirst(Task.class);
            if (currentTask != null){
                //check if the new learn task has been finished
                if(Integer.parseInt((String) mTvToLearn.getText()) <= 0){
                    //task has been finished
                    //dialog: you have finished today's new words learning task
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
                    dialogBuilder.setTitle("You have finished today's new words learning task");
                    dialogBuilder.setMessage("You may go for reviewing now~");
                    dialogBuilder.setCancelable(false); //we won't let the user quit by 'back' button
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //nothing to do
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //nothing to do
                        }
                    });
                    dialogBuilder.show();

                }else{
                    //task is not finished tet, we should go to LearnActivity
                    Intent intent = new Intent(MainActivity.this, LearnActivity.class);
                    startActivity(intent);
                }

            }else{
                //dialog: you should set the task for learning this book first
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
                dialogBuilder.setTitle("You should set the task for this book first");
                dialogBuilder.setMessage("Go to set the task?");
                dialogBuilder.setCancelable(false); //we won't let the user quit by 'back' button
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //transfer to taskManageActivity
                        Intent taskIntent = new Intent(MainActivity.this, TaskManageActivity.class);
                        startActivity(taskIntent);
                    }
                });
                dialogBuilder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //nothing to do
                    }
                });
                dialogBuilder.show();

            }

        }else{
            //otherwise, redirect the user to select a book (My book shelf)

            //dialog
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            dialogBuilder.setTitle("You have not selected any book");
            dialogBuilder.setMessage("Go to 'my bookshelf' and select a book to learn?");
            dialogBuilder.setCancelable(false); //we won't let the user quit by 'back' button
            dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //transfer the user to "my book shelf"
                    Intent intent = new Intent(MainActivity.this, MyBooksActivity.class);
                    startActivity(intent);
                }
            });
            dialogBuilder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //nothing to do
                }
            });
            dialogBuilder.show();

        }
    }

    /**
     * Define what to do when "review" button is clicked
     */
    private void onclickReview(){
        int selectedBookId = MainActivity.currentUser.getSelectedBookId();
        //if the user has a selected book, they can go for study
        if (selectedBookId > 0){
            //if a task has already been set on selected book, go to the review activity
            Task currentTask = LitePal.where("userId = ? and bookId = ?", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(selectedBookId)).findFirst(Task.class);
            if (currentTask != null){
                //check if the review task has been finished
                if(Integer.parseInt((String) mTvToReview.getText()) <= 0){
                    //task has been finished
                    //dialog: you have finished today's review task
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
                    dialogBuilder.setTitle("You have finished today's review task");
                    dialogBuilder.setMessage("Don't forget to learn more tomorrow~");
                    dialogBuilder.setCancelable(false); //we won't let the user quit by 'back' button
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //nothing to do
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //nothing to do
                        }
                    });
                    dialogBuilder.show();

                }else{
                    //task is not finished tet, we should go to ReviewActivity
                    Intent intent = new Intent(MainActivity.this, ReviewActivity.class);
                    startActivity(intent);
                }

            }else{
                //dialog: you should set the task for learning this book first
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
                dialogBuilder.setTitle("You should set the task for this book first");
                dialogBuilder.setMessage("Go to set the task?");
                dialogBuilder.setCancelable(false); //we won't let the user quit by 'back' button
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //transfer to taskManageActivity
                        Intent taskIntent = new Intent(MainActivity.this, TaskManageActivity.class);
                        startActivity(taskIntent);
                    }
                });
                dialogBuilder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //nothing to do
                    }
                });
                dialogBuilder.show();

            }

        }else{
            //otherwise, redirect the user to select a book (My book shelf)

            //dialog
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            dialogBuilder.setTitle("You have not selected any book");
            dialogBuilder.setMessage("Go to 'my bookshelf' and select a book to learn?");
            dialogBuilder.setCancelable(false); //we won't let the user quit by 'back' button
            dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //transfer the user to "my book shelf"
                    Intent intent = new Intent(MainActivity.this, MyBooksActivity.class);
                    startActivity(intent);
                }
            });
            dialogBuilder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //nothing to do
                }
            });
            dialogBuilder.show();

        }
    }

    /**
     * Start the notification service
     */
    private void startNotificationService(String notifyTime){
        Intent intent = new Intent(MainActivity.this, MyNotification.class);
        //pass the notify time to the MyNotification
        /*
            we have to do this because, the 'startService' always be executed first, do not know why.
            Therefore, it always be called before all the initializing operations in the 'onCreate' method of MainActivity.
            This is so weird!
            So that we 'currentUser' is not initialized if we use it in MyNotification.
            So that we have to pass the notify time to the MyNotification by intent.
        */
        intent.putExtra("notify_time", notifyTime);
        MainActivity.this.startService(intent);
    }

    /**
     * Start the progress resting service
     */
    private void startProgressRestingService(){
        Intent intent = new Intent(MainActivity.this, ProgressCleaner.class);
        MainActivity.this.startService(intent);
    }


}