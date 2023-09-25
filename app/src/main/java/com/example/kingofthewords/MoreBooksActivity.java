package com.example.kingofthewords;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kingofthewords.dbModels.Book;
import com.example.kingofthewords.dbModels.Task;
import com.google.android.material.navigation.NavigationView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MoreBooksActivity extends AppCompatActivity {

    //the whole DrawerLayout in the 'more books' activity
    private DrawerLayout mDLMenu;

    //a list of book objects to be shown in this page
    private List<Book> bookList = new ArrayList<>();

    //the adapter for the recyclerView in this page
    private BookAdapter bookAdapter;

    //this element is for the functionality of swiping down to refresh
    private SwipeRefreshLayout mSrlSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_books);

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
        MainActivity.setSideMenu(mNVMenu, MoreBooksActivity.this, mDLMenu);


        //initialize our book list
        initBooks();

        //configure our recyclerView that shows 'more books' cards list
        RecyclerView mRvBookCards = findViewById(R.id.rv_book_list);
        //showing cards list in grid style
        mRvBookCards.setLayoutManager(new GridLayoutManager(this, 2));
        //set the adapter for this recyclerView
        bookAdapter = new BookAdapter(bookList);
        mRvBookCards.setAdapter(bookAdapter);

        //to deal with the action of 'swiping down to refresh' of our book list recyclerview
        mSrlSwipeRefresh = findViewById(R.id.swipe_refresh);
        mSrlSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.purple_200)); //set the color of the progress bar
        mSrlSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //when refreshing, we load the book cards again
                refreshBooks();
            }
        });


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
     * This method is used to reload the book cards information
     */
    private void refreshBooks(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //reload the book list
                        initBooks();
                        //tell the adapter, book list has been refreshed
                        bookAdapter.notifyDataSetChanged();
                        //end the refreshing and hide the progress bar
                        mSrlSwipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    /**
     * This method is used to query the book information from the database
     * Then add book objects into the book list
     */
    private void initBooks(){
        //query a list of book objects from the database
        this.bookList = LitePal.findAll(Book.class);
    }
}