package com.example.kingofthewords;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.kingofthewords.dbModels.Book;
import com.google.android.material.navigation.NavigationView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MyBooksActivity extends AppCompatActivity {

    //the whole DrawerLayout in the 'My books' activity
    private DrawerLayout mDLMenu;

    //a list of book objects to be shown in this page, which are all the books the user has
    private List<Book> myBookList = new ArrayList<>();

    //the adapter for the recyclerView in this page
    private MyBookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

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
        MainActivity.setSideMenu(mNVMenu, MyBooksActivity.this, mDLMenu);

        //initialize the book list
        initMyBooks();

        //if there are no books in "my bookshelf", we show the text to tell the user
        TextView mTvNoBook = findViewById(R.id.my_books_no_book);
        if (myBookList.size() <= 0){
            mTvNoBook.setVisibility(View.VISIBLE);
        }

        //configure the recyclerView that shows 'my books' cards list
        RecyclerView mRvMyBookCards = findViewById(R.id.rv_my_book_list);
        //showing cards list in grid style
        mRvMyBookCards.setLayoutManager(new GridLayoutManager(this, 1));
        //set the adapter for this recyclerView
        bookAdapter = new MyBookAdapter(myBookList);
        mRvMyBookCards.setAdapter(bookAdapter);
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
     * This method is used to query the book information from the database
     * Then add book objects into the book list
     */
    private void initMyBooks(){
        //query a list of book objects from the database,
        //which the current user has.
        this.myBookList = MainActivity.currentUser.utilGetMyBooks();

//        System.out.println("-----------------------------------------book list");
//        for (Book b : myBookList){
//            System.out.println(b.getBookName());
//        }

        // *** MUST DO! ***
        //we must update the "MyBooks" of user, otherwise, LitePal will delete all the data the "book_user" table
        //I even do not know why!!! LitePal is so F**King weird!!!
        //This bug has consumed a lot of my time!!!
        MainActivity.currentUser.setMyBooks(this.myBookList);
    }
}