package com.example.kingofthewords;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kingofthewords.dbModels.Task;
import com.example.kingofthewords.dbModels.UserWord;
import com.example.kingofthewords.dbModels.Word;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a fragment of a list of words that are reviewed
 */
public class ReviewedFragment extends Fragment {
    //a view inflated by this fragment
    private View view;

    //an reference to the ReviewActivity
    private ReviewActivity activity;

    //elements in this fragment
    private RecyclerView mRvReviewedWords;

    //the reference to the current task
    private Task currentTask;


    /**
     * The constructor
     * @param currentTask the reference to the current task
     */
    public ReviewedFragment(Task currentTask){
        this.currentTask = currentTask;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the whole page view with "words_reviewed_fragment"
        this.view = inflater.inflate(R.layout.words_reviewed_fragment, container, false);

        //get the object of the ReviewActivity
        activity = (ReviewActivity) getActivity();

        //initialize the elements
        mRvReviewedWords = view.findViewById(R.id.rv_reviewed_words);

        //initialize the word list
        initWordList();

        return view;
    }

    /**
     * initialize the recyclerView list of words has been reviewed
     */
    private void initWordList(){
        //set the layout manager
        mRvReviewedWords.setLayoutManager(new LinearLayoutManager(activity));

        //query a list already learned words of this user and this book from database, which have been reviewed
        //this list is sorted by word id
        List<UserWord> uwList = LitePal.where("userid = ? and bookid = ? and isReviewed = ?", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(currentTask.getBookId()), "1").order("wordid").find(UserWord.class);

        List<Word> wordList = new ArrayList<>();
        //query out all the words objects in uwList
        if (uwList != null){
            for (UserWord uw : uwList){
                Word word = LitePal.where("id = ?", String.valueOf(uw.getWordId())).findFirst(Word.class);
                wordList.add(word);
            }
        }

//        //get the daily review word number of this task
//        int num = currentTask.getReviewWordNum();
//        //if the list longer than this number, we get the sublist
//        if (wordList.size() > num){
//            wordList = wordList.subList(0, num);
//        }

        //set the adapter
        mRvReviewedWords.setAdapter(new WordAdapter(activity, wordList));
    }


}
