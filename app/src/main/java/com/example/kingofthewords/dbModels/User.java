package com.example.kingofthewords.dbModels;

import android.database.Cursor;

import com.example.kingofthewords.MainActivity;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * The data table of our users
 */
public class User extends LitePalSupport {

    private int id; //primary key
    private String username;
    private String password;
    private String email;
    private String gender;
    private int balance = 500;    //the number of W coins this user has (initialized with 500 coins)
    private int selectedBookId = -1;     //the id of the current learning book
    private String notifyTime = "17:00:00"; //the time for daily notification, here is a default value

    private List<Book> myBooks = new ArrayList<Book>(); //the word books this user owns (1 book --> n users, 1 user --> n books)
    private List<Task> myTasks = new ArrayList<Task>(); //the daily tasks the user has set

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public List<Book> getMyBooks() {
        return myBooks;
    }

    public void setMyBooks(List<Book> myBooks) {
        this.myBooks = myBooks;
    }

    public List<Task> getMyTasks() {
        return LitePal.where("user_id = ?", String.valueOf(id)).find(Task.class);
    }

    public void setMyTasks(List<Task> myTasks) {
        this.myTasks = myTasks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSelectedBookId() {
        return selectedBookId;
    }

    public void setSelectedBookId(int selectedBookId) {
        this.selectedBookId = selectedBookId;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    /* ----------- util methods---------- */

    public List<Book> utilGetMyBooks(){
        /*
         * LitePal cannot query in the "many to many" cases!!!
         * So I use the cursor instead
         * */
        List<Book> tempBookList = new ArrayList<>();
        Cursor cursor = LitePal.findBySQL("select * from book_user where user_id = ?", String.valueOf(this.id));
        if (cursor.moveToFirst()){
            do{
                int book_id = cursor.getInt(cursor.getColumnIndex("book_id"));
                Book book = LitePal.where("id = ?", String.valueOf(book_id)).findFirst(Book.class);
                tempBookList.add(book);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return tempBookList;
    }
}
