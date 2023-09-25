package com.example.kingofthewords;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.kingofthewords.dbModels.Book;
import com.example.kingofthewords.dbModels.Task;
import com.example.kingofthewords.dbModels.User;
import com.example.kingofthewords.dbModels.UserWord;
import com.example.kingofthewords.dbModels.Word;

import org.litepal.LitePal;

import java.util.List;

public class LearnActivity extends AppCompatActivity implements View.OnClickListener {

    //the current word should be shown here
    private Word currentWord;
    //the current task (which the user is doing), this can be initialized with currentWord
    private Task currentTask;
    //an object of UserWor class, which can record the familiarity score of the current user and the word
    private UserWord uw;

    //the elements in this activity
    private Button mBtnKnow;
    private Button mBtnHint;

    //the fragment in this activity
    private WordsContentFragment mFragWordContent;

    //count how many time the hint button been clicked
    private int hintCount;

    //the tag for intent extra data
    public static final String WORD_ID = "current_word_id";
    public static final String TASK_ID = "current_task_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        /*
            Define the toolbar  -----------------------------------------------------
         */
        //replace the default actionbar with the toolbar I have defined by myself
        Toolbar toolbar = findViewById(R.id.learn_toolbar);
        setSupportActionBar(toolbar);

        //set the left top button
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
        }
        //-----------------------------------------------------------------------

        //update the currentWord
        initCurrentWord();
        //initialize the UserWord to record familiarity
        initUserWord();

        //initialize the fragment
        initFrag();

        //initializing the hint count every time come to this activity
        this.hintCount = 0;

        //initialize the elements in this activity
        this.mBtnKnow = findViewById(R.id.btn_learn_know);
        this.mBtnHint = findViewById(R.id.btn_learn_hint);

        //set the onclick listener for the buttons in this page
        this.mBtnKnow.setOnClickListener(this);
        this.mBtnHint.setOnClickListener(this);


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
                Intent intent = new Intent(LearnActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }


    /**
     * Define what to do when clicking on the buttons
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //when the "know" button is clicked
            case R.id.btn_learn_know:
                onClickKnow();
                break;

            //when the "hint" button is clicked
            case R.id.btn_learn_hint:
                onClickHint();
                break;
        }
    }

    /**
     * Define what to do when "know" button is clicked
     *
     */
    private void onClickKnow(){
        //Every time being clicked, we should go to LearnDetailActivity
        goDetail();
    }

    /**
     * Define what to do when "hint" button is clicked
     */
    private void onClickHint(){
        //update familiarity score (+1)
        increaseFamiliarityScore(MainActivity.currentUser, currentWord, 1);

        //update display
        if (this.hintCount == 0){
            //First time clicking, we show the hint
            this.mFragWordContent.showHint();

        }else if (this.hintCount == 1){
            //Second time clicking, we go to the LearnDetailActivity
            goDetail();
        }

        //update the count
        this.hintCount += 1;
    }


    /**
     * calculate and record the familiarity of the user with currentWord
     * @param u The user
     * @param w The word
     * @param i How much score to be added to the familiar score
     */
    private void increaseFamiliarityScore(User u, Word w, int i){
        //query the UserWord object from db
        UserWord uwTempt = LitePal.where("userid = ? and wordid = ?", String.valueOf(u.getId()), String.valueOf(w.getId())).findFirst(UserWord.class);
        //update the score by i
        uwTempt.setScore(uwTempt.getScore() + i);
        //submit to the database
        uwTempt.save();
    }

    /**
     * Go to the LearnDetailActivity
     */
    private void goDetail(){
        Intent intent = new Intent(LearnActivity.this, LearnDetailActivity.class);

        //pass the id of current word and task to the detail activity
        intent.putExtra(WORD_ID, currentWord.getId());
        intent.putExtra(TASK_ID, currentTask.getId());

        startActivity(intent);
    }

    /**
     *  initialize the current learning word
     */
    private void initCurrentWord(){
        //It is weird that, current user might be lost
        //so we need to redefine it when it becomes null
        MainActivity.updateCurrentUser();

        //if we are here, there should already been a task, so do not need to check
        int selectedBookId = MainActivity.currentUser.getSelectedBookId();
        Book selectedBook = LitePal.where("id = ?", String.valueOf(selectedBookId)).findFirst(Book.class);
        List<Word> words = selectedBook.getWords();
        this.currentTask = LitePal.where("userId = ? and bookId = ?", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(selectedBookId)).findFirst(Task.class);

        //get the id of the first word in this book
        int beginId = words.get(0).getId();
        //calculate the id of current word
        int currentWordId = beginId + this.currentTask.getLearnedWordNum();

        //initialize the current word
        this.currentWord = LitePal.where("id = ?", String.valueOf(currentWordId)).findFirst(Word.class);
    }

    /**
     *  initialize the fragment in this activity, which can contain basic information of different words every time
     */
    private void initFrag(){
        //find the fragment in this activity
        this.mFragWordContent = (WordsContentFragment) getSupportFragmentManager().findFragmentById(R.id.word_content_fragment);
        //refresh this fragment (set the values)
        this.mFragWordContent.refresh(this.currentWord);
    }

    /**
     * initialize the UserWord object, which can record the familiarity score of the current user and the word
     */
    private void initUserWord(){
        //query the object from db first
        UserWord uwTempt = LitePal.where("userid = ? and wordid = ?", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(currentWord.getId())).findFirst(UserWord.class);

        //check if the uer and this word are already recorded in the database
        if (uwTempt != null){
            //if so, we just update the instance variable uw of this class
            this.uw = uwTempt;
        }else{
            //if not, we create an UserWord object to init the uw
            uw = new UserWord();
            //set the initial values
            uw.setUserId(MainActivity.currentUser.getId());
            uw.setWordId(currentWord.getId());
//            Book taskBook = LitePal.where("id = ?", String.valueOf(MainActivity.currentUser.getSelectedBookId())).findFirst(Book.class);
            uw.setBookId(currentTask.getBookId());
            uw.setScore(0);
            //submit to the database
            uw.save();
        }


    }


}