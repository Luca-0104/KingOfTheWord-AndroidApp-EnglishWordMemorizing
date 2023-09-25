package com.example.kingofthewords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.kingofthewords.dbModels.Task;
import com.example.kingofthewords.dbModels.UserWord;
import com.example.kingofthewords.dbModels.Word;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.litepal.LitePal;

public class ReviewActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //the current word should be shown here
    private Word currentWord;
    //the current task (which the user is doing), this can be initialized with currentWord
    private Task currentTask;
    //an object of UserWor class, which can record the familiarity score of the current user and the word
    private UserWord uw;

    //the nav view to choose word list
    private BottomNavigationView mBnvNav;

    //the fragments in this activity
    private ToReviewFragment mFragToReview;
    private ReviewedFragment mFragReviewed;


    //the tag for intent extra data
    public static final String WORD_ID = "current_word_id";
    public static final String TASK_ID = "current_task_id";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        /*
            Define the toolbar  -----------------------------------------------------
         */
        //replace the default actionbar with the toolbar I have defined by myself
        Toolbar toolbar = findViewById(R.id.review_toolbar);
        setSupportActionBar(toolbar);

        //set the left top button
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
        }
        //-----------------------------------------------------------------------



        //find views
        mBnvNav = findViewById(R.id.review_nav);
        mBnvNav.setOnNavigationItemSelectedListener(this);

        //initialize the current task
        int selectedBookId = MainActivity.currentUser.getSelectedBookId();
        this.currentTask = LitePal.where("userId = ? and bookId = ?", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(selectedBookId)).findFirst(Task.class);

        //initialize the fragments
        initFrags();

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
                Intent intent = new Intent(ReviewActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //if the "to review" nav item is selected
            case R.id.item_to_review:
                //change current fragment from mFragReviewed to mFragToReview
                changeFrag(mFragReviewed, mFragToReview);
                break;

            //if the "reviewed" nav item is selected
            case R.id.item_item_reviewed:
                //change current fragment from mFragToReview to mFragReviewed
                changeFrag(mFragToReview, mFragReviewed);
                break;
        }
        return true;
    }

    /**
     * Initialize the fragments
     */
    private void initFrags(){
        mFragToReview = new ToReviewFragment(currentTask);
        mFragReviewed = new ReviewedFragment(currentTask);

        //show "to review" fragment
        getSupportFragmentManager().beginTransaction().add(R.id.review_words_layout, mFragToReview).show(mFragToReview).commit();
    }

    /**
     * Change the current fragment from frag 1 to frag 2
     */
    private void changeFrag(Fragment frag1, Fragment frag2){
        //get fragment transaction obj
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //hide the former fragment
        transaction.hide(frag1);

        //if the frag2 is not added to the transaction, we add it now
        if (!frag2.isAdded()) {
            transaction.add(R.id.review_words_layout, frag2);
        }

        //show frag2 on the screen
        transaction.show(frag2).commit();
    }
}