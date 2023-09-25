package com.example.kingofthewords.dbModels;

import org.litepal.crud.LitePalSupport;

/**
 * For each user and a single book he owns, there should be daily task scheme.
 */
public class Task extends LitePalSupport {

    private int id; //primary key
    private Book book;  //which word book is this task for
    private User user;  //which user owns this task
    private int bookId; //the id of the book
    private int userId; //the id of the user
    private int newWordNum;//the number of the new words should be learned everyday.
    private int reviewWordNum;//the number of the already-learned worlds should be reviewed everyday.
    private int learnedWordNum = 0; //the total number of the already-learned words in this book, this can also be used as a flag to save the progress in a book
    private int reviewedWordNum = 0; //the total number of the already-reviewed words in this book, this can also be used as a flag to save the progress in a book
    private int countTodayLearn = 0; //the number of the word have been learned today, should be set to 0 every day
    private int countTodayReview = 0;   //the number of the word have been reviewed today, should be set to 0 every day
    private boolean learnRewardGet = false; //whether the user has gotten the daily reward of learning new words
    private boolean reviewRewardGet = false; //whether the user has gotten the daily reward of reviewing words


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNewWordNum() {
        return newWordNum;
    }

    public void setNewWordNum(int newWordNum) {
        this.newWordNum = newWordNum;
    }

    public int getReviewWordNum() {
        return reviewWordNum;
    }

    public void setReviewWordNum(int reviewWordNum) {
        this.reviewWordNum = reviewWordNum;
    }

    public int getLearnedWordNum() {
        return learnedWordNum;
    }

    public void setLearnedWordNum(int learnedWordNum) {
        this.learnedWordNum = learnedWordNum;
    }

    public int getReviewedWordNum() {
        return reviewedWordNum;
    }

    public void setReviewedWordNum(int reviewedWordNum) {
        this.reviewedWordNum = reviewedWordNum;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCountTodayLearn() {
        return countTodayLearn;
    }

    public void setCountTodayLearn(int countTodayLearn) {
        this.countTodayLearn = countTodayLearn;
    }

    public int getCountTodayReview() {
        return countTodayReview;
    }

    public void setCountTodayReview(int countTodayReview) {
        this.countTodayReview = countTodayReview;
    }

    public boolean isLearnRewardGet() {
        return learnRewardGet;
    }

    public void setLearnRewardGet(boolean learnRewardGet) {
        this.learnRewardGet = learnRewardGet;
    }

    public boolean isReviewRewardGet() {
        return reviewRewardGet;
    }

    public void setReviewRewardGet(boolean reviewRewardGet) {
        this.reviewRewardGet = reviewRewardGet;
    }
}
