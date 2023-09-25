package com.example.kingofthewords.dbModels;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class Book extends LitePalSupport {
    private int id;     //primary key
    private String bookId;  //the book id for example, CET4_1, IELTS_3
    private String bookName;    //the name of this book
    private String coverPicture;    //the http request for the cover picture of this book
    private int wordNumber; //the number of the words contained in this book
    private int price = 50;      //how many W coins should be paid for this book
    private List<Word> words = new ArrayList<Word>();   //1 book --> n words (relationship with Word class)
    private List<User> users = new ArrayList<User>();   //the users who owns this book (1 book --> n users, 1 user --> n books)
    private List<Task> tasks = new ArrayList<Task>();   //relationship with Task class

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    public int getWordNumber() {
        return wordNumber;
    }

    public void setWordNumber(int wordNumber) {
        this.wordNumber = wordNumber;
    }

    public List<Word> getWords() {
        return LitePal.where("book_id = ?", String.valueOf(id)).find(Word.class);
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
