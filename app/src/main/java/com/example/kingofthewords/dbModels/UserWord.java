package com.example.kingofthewords.dbModels;

import org.litepal.crud.LitePalSupport;

/**
 * A table records the learned words of a user, especially familiarity of each word will be recorded
 */
public class UserWord extends LitePalSupport {

    private int userId; //id of the user
    private int wordId; //id of the word
    private int bookId; //id of the book of this word
    private int score = 0;  //how the user familiar with this word (the higher the less familiarity)
    private boolean isReviewed = false; //whether the word is reviewed

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isReviewed() {
        return isReviewed;
    }

    public void setReviewed(boolean reviewed) {
        isReviewed = reviewed;
    }
}
