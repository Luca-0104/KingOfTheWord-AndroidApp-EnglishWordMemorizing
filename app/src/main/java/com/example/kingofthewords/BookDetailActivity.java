package com.example.kingofthewords;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kingofthewords.dbModels.Book;
import com.example.kingofthewords.dbModels.User;
import com.example.kingofthewords.dbModels.Word;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    //constant strings for getting the data from 'intent', which are working as tags
    public static final String BOOK_ID = "book_id";

    //an object of current book
    private Book currentBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        //get the book information and data from 'intent'
        Intent intent = getIntent();
        int currentBookId = intent.getIntExtra(BOOK_ID, -1);
        //initialize the currentBook object
        if (currentBookId != -1 && LitePal.where("id = ?", String.valueOf(currentBookId)).findFirst(Book.class) != null){
            currentBook = LitePal.where("id = ?", String.valueOf(currentBookId)).findFirst(Book.class);
        }else{
            //impossible to get here, I just put a toast here.
            Toast.makeText(BookDetailActivity.this, "Book info not fund! You may need to refresh.", Toast.LENGTH_SHORT).show();
        }

        String bookName = currentBook.getBookName();
        String bookCoverPicture = currentBook.getCoverPicture();

        //find each element in the book details layout (find views)
        Toolbar toolbar = findViewById(R.id.toolbar);   //notice that, this toolbar is the one in the book detail layout file
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);  //the collapsing title bar in the detail layout file
        ImageView mIVBookCover = findViewById(R.id.book_image_view);
        ImageView bookInfoImage = findViewById(R.id.book_info_image);
        TextView bookInfoName = findViewById(R.id.book_info_name);
        TextView bookInfoPrice = findViewById(R.id.book_info_price);
        TextView bookInfoWordCount = findViewById(R.id.book_info_word_count);
        RecyclerView bookInfoRvPreviewWords = findViewById(R.id.book_info_rv_word_preview);


        //set the action bar same as the one in main activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            //the 'back' button on the actionbar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //give the values to each element we have defined before in this layout file
        collapsingToolbar.setTitle(bookName);
        Glide.with(this).load(bookCoverPicture).into(mIVBookCover);
        Glide.with(this).load(bookCoverPicture).into(bookInfoImage);
        bookInfoName.setText(bookName);
        String wordCount = String.valueOf(currentBook.getWordNumber()) + " words";
        bookInfoPrice.setText(String.valueOf(currentBook.getPrice()));
        bookInfoWordCount.setText(wordCount);

        //initialize the recyclerView for showing the preview words
        bookInfoRvPreviewWords.setLayoutManager(new LinearLayoutManager(BookDetailActivity.this));
        //we will show 15 preview words for each book
        List<Word> wordList = currentBook.getWords();

        //we just show first 30 words as a preview of each word book
        if(wordList.size() > 30){
            wordList = wordList.subList(0, 30);
        }

        //set the adapter for this recyclerView
        bookInfoRvPreviewWords.setAdapter(new WordAdapter(BookDetailActivity.this, wordList));
        //set the divider line for each item in this recyclerView
        class MyDecoration extends RecyclerView.ItemDecoration{
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                //get a divider line at the bottom of each item
                outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.dividerHeight));
            }
        }
//        bookInfoRvPreviewWords.addItemDecoration(new MyDecoration());



        //When the purchasing button is clicked
        View mFabPurchase = findViewById(R.id.fab_purchase_book);
        mFabPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the purchase method
                purchaseBook();
            }
        });
    }


    /**
     * This is a method for defining what should been done
     * when the purchase button of a specific book is clicked.
     */
    private void purchaseBook(){
        /* The whole logical structure */
        //a dialog to confirm the payment
            //if yes
                //check if the user already own this book
                    //if yes
                        //Toast
                    //else
                        //check the balance of the user account
                            //if enough
                                //deduct W coins
                                //add the book into the user's book list
                                //update db (save)
                            //else
                                //Toast
            //if no
                //nothing happens

        /* The code */
        //a dialog to confirm the payment
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        dialogBuilder.setTitle("Confirm to pay for this book?");
        dialogBuilder.setMessage("This book costs " + currentBook.getPrice() + " 'W coins'");
        dialogBuilder.setCancelable(false); //we won't let the user quit by 'back' button
        //when click on the 'yes' button
        dialogBuilder.setPositiveButton("I'm sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //query all the books the current user owns
                List<Book> myBooks = MainActivity.currentUser.utilGetMyBooks();
                List<Integer> bookIds = new ArrayList<>();
                for(Book b : myBooks){
                    bookIds.add(b.getId());
                }
                //check if the user already own this book
                if(bookIds.contains(currentBook.getId())){
                    Toast.makeText(BookDetailActivity.this, "You already own this book, do not need to buy it again.", Toast.LENGTH_SHORT).show();

                }else{
                    //if the user do not own it yet
                    //we check the balance of the user account
                    int userBalance = MainActivity.currentUser.getBalance();
                    if (userBalance >= currentBook.getPrice()){
                        //deduct the W coins of the uer
                        MainActivity.currentUser.setBalance(MainActivity.currentUser.getBalance() - currentBook.getPrice());
                        //add the book to the user's bookshelf
//                        MainActivity.currentUser.getMyBooks().add(currentBook);
                        List<Book> books = MainActivity.currentUser.utilGetMyBooks();
                        books.add(currentBook);
                        MainActivity.currentUser.setMyBooks(books);

                        //submit to the database
                        MainActivity.currentUser.save();

                        Toast.makeText(BookDetailActivity.this, "Purchase successfully! Get start with it!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(BookDetailActivity.this, "You do not have enough W coins.", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(BookDetailActivity.this, "Payment canceled", Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.show();

    }


    /**
     * define what should be done when the back button 'HomeAsUp' is clicked.
     * we let it go back to the last activity
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //call the finish() method to close the current activity and go back to the last activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * For generating a long text consists of the book name,
     * this is used for testing, and for filling the whole page.
     *
     * @return
     */
    private String generateBookContent(String bookName){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 500; i++){
            stringBuilder.append(bookName);
        }
        return stringBuilder.toString();
    }

}