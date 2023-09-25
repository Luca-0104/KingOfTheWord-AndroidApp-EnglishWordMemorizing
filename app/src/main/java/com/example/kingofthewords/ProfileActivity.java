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

import com.google.android.material.navigation.NavigationView;

public class ProfileActivity extends AppCompatActivity {

    //the whole DrawerLayout in the 'My Profile' activity
    private DrawerLayout mDLMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        MainActivity.setSideMenu(mNVMenu, ProfileActivity.this, mDLMenu);
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

}