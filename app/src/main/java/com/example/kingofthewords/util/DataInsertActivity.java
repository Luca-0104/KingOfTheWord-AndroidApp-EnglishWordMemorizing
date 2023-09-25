package com.example.kingofthewords.util;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.kingofthewords.R;

import org.litepal.LitePal;

/**
 * This activity is only used to insert the data into the database, no layout appearance.
 * To insert the data, we should set this activity as Launching activity first.
 */
public class DataInsertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_insert);

        //create the database and tables (if this is the first time on this phone, database will be created)
        LitePal.getDatabase();

        /*
            This is for creating the database and inserting data, which should be executed only once.
            We should let this be commented when running
        */
        ReadJsonIntoDB.WriteDB();

        /*
        // for testing the database

        Book book = LitePal.findFirst(Book.class);
        List<Word> words = book.getWords();
        System.out.println(words == null ? "---0---":"===1===");
        System.out.println("---------------------" + words.get(0).getWord());
        System.out.println("---------------------" + words.size());
        System.out.println("---------------------" + book.getWordNumber());
        System.out.println("---------------------" + book.getBookName());
        for (Word w : words){
            System.out.println(w.getWord());
        }

        Word word = LitePal.findFirst(Word.class);
        List<Sentence> sentences = word.getSentences();
        System.out.println("===========" + sentences.size());
        System.out.println(sentences.get(0).getSentenceCN());
        System.out.println(sentences.get(0).getSentenceEN());
        System.out.println();
        List<Translation> translations = word.getTranslations();
        System.out.println("===========" + translations.size());
        System.out.println(translations.get(0).getTransCN());
        System.out.println(translations.get(0).getPos());
        */

    }
}