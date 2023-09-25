package com.example.kingofthewords;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kingofthewords.dbModels.Book;
import com.example.kingofthewords.dbModels.User;

import java.util.List;

/**
 * This Adapter is for the book card list in the "My Books"
 */
public class MyBookAdapter extends RecyclerView.Adapter<MyBookAdapter.BookViewHolder> {

    private Context mContext;   //the context that contains this recyclerview
    private List<Book> mBookList;   //a list of book objects, which should be shown in this recyclerview

    public MyBookAdapter(List<Book> bookList){
        this.mBookList = bookList;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (mContext == null){
            mContext = viewGroup.getContext();
        }

        //inflate our 'my books' page with mybook_items
        View myBooksRecyclerView = LayoutInflater.from(mContext).inflate(R.layout.mybook_item, viewGroup, false);
        //then get the view holder of this view
        BookViewHolder holder = new BookViewHolder(myBooksRecyclerView);

        //set the clicking events of each book card
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the position of the clicked book card
                int position = holder.getAdapterPosition();
                //get the book object from the list using the position we got
                Book book = mBookList.get(position);
                //pass the book information (id) into the next activity, which is BookDetailsActivity (by using intent)
                Intent intent = new Intent(mContext, BookDetailActivity.class);
                intent.putExtra(BookDetailActivity.BOOK_ID, book.getId());
                mContext.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(MyBookAdapter.BookViewHolder bookViewHolder, int i) {
        Book book = mBookList.get(i);
        //set the book name on the card
        bookViewHolder.bookName.setText(book.getBookName());
        //set the book picture of the card
        //we will load it from internet using Glide
        Glide.with(mContext).load(book.getCoverPicture()).into(bookViewHolder.bookImage);
        //set the click listener on the 'select' button
        bookViewHolder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Book " + book.getId() + " is selected", Toast.LENGTH_SHORT).show();

                List<Book> myBooks = MainActivity.currentUser.getMyBooks();
                /*
                System.out.println("--------------------before--------------------");
                for (Book b : myBooks){
                    System.out.println(b.getId() + "  ");
                }
                System.out.println();
                */

                //set this book as the selected one of the current user
                MainActivity.currentUser.setSelectedBookId(book.getId());
                MainActivity.currentUser.save();

                //go to main activity
                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);

                /*
                myBooks = MainActivity.currentUser.getMyBooks();
                System.out.println("--------------------after--------------------");
                for (Book b : myBooks){
                    System.out.println(b.getId() + "  ");
                }
                 */

            }
        });
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }


    /**
     * This is the Book View Holder for book cards in "My Books"
     */
    static class BookViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;      //the whole card
        ImageView bookImage;    //the image of the cover of this book (on the card)
        TextView bookName;      //the name of this book (on the card)
        Button btnSelect;       //the button for selecting this book to learn

        public BookViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            bookImage = itemView.findViewById(R.id.iv_book_image);
            bookName = itemView.findViewById(R.id.tv_book_name);
            btnSelect = itemView.findViewById(R.id.btn_learn_book);
        }
    }

}
