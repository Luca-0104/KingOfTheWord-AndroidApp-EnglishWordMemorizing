package com.example.kingofthewords;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
 * This is a fragment of a list of words that should be reviewed
 */
public class ToReviewFragment extends Fragment {

    //a view inflated by this fragment
    private View view;

    //elements in this fragment
    private RecyclerView mRvToReviewWords;

    //an reference to the ReviewActivity
    private ReviewActivity activity;

    //the reference to the current task
    private Task currentTask;

    /**
     * The constructor
     * @param currentTask the reference to the current task
     */
    public ToReviewFragment(Task currentTask){
        this.currentTask = currentTask;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the whole page view with "words_to_review_fragment"
        this.view = inflater.inflate(R.layout.words_to_review_fragment, container, false);

        //get the object of the ReviewActivity
        activity = (ReviewActivity) getActivity();


        //initialize the elements
        mRvToReviewWords = view.findViewById(R.id.rv_to_review_words);

        //initialize the word list
        initWordList();

        return view;
    }

    /**
     * initialize the recyclerView list of words need to be reviewed today
     */
    private void initWordList(){
        //set the layout manager
        mRvToReviewWords.setLayoutManager(new LinearLayoutManager(activity));

        //query a list already learned words of this user and this book from database, which should be reviewed
        //this list is sorted by word id
        List<UserWord> uwList = LitePal.where("userid = ? and bookid = ? and isReviewed = ?", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(currentTask.getBookId()), "0").order("wordid").find(UserWord.class);

        List<Word> wordList = new ArrayList<>();
        //query out all the words objects in uwList
        if (uwList != null){
            for (UserWord uw : uwList){
                Word word = LitePal.where("id = ?", String.valueOf(uw.getWordId())).findFirst(Word.class);
                wordList.add(word);
            }
        }

        //get the daily review word number of this task
        int num = currentTask.getReviewWordNum();
        //if the list longer than this number, we get the sublist
        if (wordList.size() > num){
            wordList = wordList.subList(0, num);
        }

        //set the adapter
        mRvToReviewWords.setAdapter(new ReviewWordAdapter(activity, wordList));

    }
}
