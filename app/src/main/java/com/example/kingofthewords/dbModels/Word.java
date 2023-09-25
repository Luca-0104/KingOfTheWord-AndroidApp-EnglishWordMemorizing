package com.example.kingofthewords.dbModels;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class Word extends LitePalSupport {
    private int id;         //the primary key
    private String wordId;  //indicates the corresponding word book and the rank in this book
    private String word;    //this word
    private String usPhone; //phonetic symbol of us pronunciation
    private String ukPhone; //phonetic symbol of uk pronunciation
    private String audioUS; //the http request for us pronunciation audio
    private String audioUK; //the http request for uk pronunciation audio
    private List<Sentence> sentences = new ArrayList<Sentence>();   //example sentences
    private List<Translation> translations = new ArrayList<Translation>();  //the translations of this word
    private String bookId; //the book id of the corresponding book
    private Book book;    //1 book --> n words, 1word --> 1book (the relationship with Book class)


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getUsPhone() {
        return usPhone;
    }

    public void setUsPhone(String usPhone) {
        this.usPhone = usPhone;
    }

    public String getUkPhone() {
        return ukPhone;
    }

    public void setUkPhone(String ukPhone) {
        this.ukPhone = ukPhone;
    }

    public String getAudioUS() {
        return audioUS;
    }

    public void setAudioUS(String audioUS) {
        this.audioUS = audioUS;
    }

    public String getAudioUK() {
        return audioUK;
    }

    public void setAudioUK(String audioUK) {
        this.audioUK = audioUK;
    }

    public List<Sentence> getSentences() {
        return LitePal.where("word_id = ?", String.valueOf(id)).find(Sentence.class);
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    public List<Translation> getTranslations() {
        return LitePal.where("word_id = ?", String.valueOf(id)).find(Translation.class);
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
