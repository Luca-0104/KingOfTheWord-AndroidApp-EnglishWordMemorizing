package com.example.kingofthewords;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class FinishLearnActivity extends AppCompatActivity {

    private Button mBtnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_learn);

        /*
            Define the toolbar  -----------------------------------------------------
         */
        //replace the default actionbar with the toolbar I have defined by myself
        Toolbar toolbar = findViewById(R.id.learn_finish_toolbar);
        setSupportActionBar(toolbar);

        //set the left top button
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
        }
        //-----------------------------------------------------------------------

        //initialize the buttons
        mBtnFinish = findViewById(R.id.btn_learn_finish);

        //set the click listener for buttons
        mBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go back to the MainActivity
                Intent intent = new Intent(FinishLearnActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

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
                Intent intent = new Intent(FinishLearnActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

}