package com.example.kingofthewords;

import android.content.Context;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kingofthewords.dbModels.Translation;
import com.example.kingofthewords.dbModels.Word;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    //the context uses this adapter, which here should be the instance of BookDetailsActivity
    private Context mContext;
    //an Array of Words that should be shown in the recyclerview
    private List<Word> mWordList;

    //the constructor
    public WordAdapter(Context context, List<Word> wordList){
        this.mContext = context;
        this.mWordList = wordList;
    }

    public WordViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (mContext == null){
            mContext = viewGroup.getContext();
        }
        //inflate the 'mContext' with 'word_item'
        return new WordViewHolder(LayoutInflater.from(mContext).inflate(R.layout.word_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(WordAdapter.WordViewHolder wordViewHolder, int i) {
        /*
            define the value of each view element in the holder
         */
        //get each word object by id
        Word word = mWordList.get(i);

        //define values of views
        wordViewHolder.mTvWord.setText(word.getWord());
        String phone = "[" + word.getUkPhone() + "]";
        wordViewHolder.mTvPhone.setText(phone);

        //get the translate object of this word
        Translation translation = word.getTranslations().get(0);
        //define the values of the views
        wordViewHolder.mTvPop.setText(translation.getPos());
        wordViewHolder.mTvTrans.setText(translation.getTransCN());

    }

    @Override
    public int getItemCount() {
        return mWordList.size();
    }

    //an inner class for declaring and holding the views in this recyclerView
    class WordViewHolder extends RecyclerView.ViewHolder{

        //the views be held
        TextView mTvWord;   //the word spelling
        TextView mTvPhone;  //the word phonetic
        TextView mTvPop;    //the word property
        TextView mTvTrans;  //the Chinese translation

        public WordViewHolder(View itemView) {
            super(itemView);
            //initialize the views that are held in this class
            this.mTvWord = itemView.findViewById(R.id.word_item_word);
            this.mTvPhone = itemView.findViewById(R.id.word_item_phone);
            this.mTvPop = itemView.findViewById(R.id.word_item_pop);
            this.mTvTrans = itemView.findViewById(R.id.word_item_trans);
        }
    }

}
