package com.example.kingofthewords;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kingofthewords.dbModels.Book;
import com.example.kingofthewords.dbModels.Task;
import com.example.kingofthewords.dbModels.Word;
import com.google.android.material.navigation.NavigationView;

import org.litepal.LitePal;

import java.util.List;

public class LearnDetailActivity extends AppCompatActivity implements View.OnClickListener {

    //the current word should be shown here
    private Word currentWord;
    private int currentWordId;

    //the current task (which the user is doing)
    private Task currentTask;
    private int currentTaskId;

    //the elements in this activity
    private Button mBtnNext;

    //the fragment in this activity
    private WordsDetailFragment mFragWordDetail;

    //determine whether the user come from review or new learning activity
    private Boolean isFromReview = false;   //default value is false

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
            Initialize the current data
            this should before the setContentView() method being called
         */
        //get the data from intent
        Intent intent = getIntent();
        this.currentWordId = intent.getIntExtra(LearnActivity.WORD_ID, -1);
        this.currentTaskId = intent.getIntExtra(LearnActivity.TASK_ID, -1);
        this.isFromReview = intent.getBooleanExtra("isFromReview", false);
        //initialize the current data
        initCurrentWord();
        initCurrentTask();

        //this should be called after initializing the current data
        setContentView(R.layout.activity_learn_detail);


        /*
            Define the toolbar  -----------------------------------------------------
         */
        //replace the default actionbar with the toolbar I have defined by myself
        Toolbar toolbar = findViewById(R.id.learn_detail_toolbar);
        setSupportActionBar(toolbar);

        //set the left top button
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
        }
        //-----------------------------------------------------------------------


        //initialize the fragment
        initFrag();

        //initialize the elements in this activity
        this.mBtnNext = findViewById(R.id.btn_learn_detail_next);

        //if the user come from review activity, this button should have text of "Back"
        if (isFromReview){
            this.mBtnNext.setText("Back");
        }

        //set the onclick listener for the buttons in this page
        this.mBtnNext.setOnClickListener(this);
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
            //if the home button is clicked
            case android.R.id.home:
                //go back to main activity
                Intent intent = new Intent(LearnDetailActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //when click on the "next" button
            case R.id.btn_learn_detail_next:
                onclickNext();
                break;
        }
    }

    /**
     *  Define what to do when clicking on the "next" button
     */
    private void onclickNext(){
        //if the user come from new learning task
        if (!isFromReview){
            //update the learning progress of this task
            this.currentTask.setLearnedWordNum(this.currentTask.getLearnedWordNum() + 1);
            this.currentTask.setCountTodayLearn(this.currentTask.getCountTodayLearn() + 1);
            this.currentTask.save();

            if (checkStop()){
                //if this is already the last word for today

                Toast.makeText(LearnDetailActivity.this, "task finished, go to get the reward", Toast.LENGTH_SHORT).show();
                //go to the finish activity
                Intent intent = new Intent(LearnDetailActivity.this, FinishLearnActivity.class);
                startActivity(intent);


            }else{
                //if still not reach the end of this task
                //go to the learn activity of next word
                Intent intent = new Intent(LearnDetailActivity.this, LearnActivity.class);
                startActivity(intent);

            }

        }else{
            //if the user come from the review task
            //back to the review activity
            Intent intent = new Intent(LearnDetailActivity.this, ReviewActivity.class);
            startActivity(intent);
        }


    }

    /**
     * Determine whether should we stop going to next word,
     * due to the number of the words have been learned today
     * reaches the daily newWordNum in this task.
     *
     * @return true for stop, false for do not stop
     */
    private boolean checkStop(){
        if (currentTask.getCountTodayLearn() >= currentTask.getNewWordNum()){
            //When the words learned today reach the max count in the task
            return true;

        }else{
            return false;

        }
    }

    /**
     * initialize the current word
     */
    private void initCurrentWord(){
        //initialize the current word
        this.currentWord = LitePal.where("id = ?", String.valueOf(this.currentWordId)).findFirst(Word.class);
    }

    /**
     * initialize the current task
     */
    private void initCurrentTask(){
        //initialize the current task
        this.currentTask = LitePal.where("id = ?", String.valueOf(this.currentTaskId)).findFirst(Task.class);
    }


    /**
     * initialize the fragment in this activity
     */
    private void initFrag(){
        //find the fragment in this activity
        this.mFragWordDetail = (WordsDetailFragment) getSupportFragmentManager().findFragmentById(R.id.word_detail_fragment);
        //refresh this fragment (set the values)
        this.mFragWordDetail.refresh(this.currentWord);
    }

    public Word getCurrentWord() {
        return currentWord;
    }
}