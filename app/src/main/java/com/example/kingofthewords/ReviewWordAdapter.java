package com.example.kingofthewords;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kingofthewords.dbModels.Book;
import com.example.kingofthewords.dbModels.Task;
import com.example.kingofthewords.dbModels.Translation;
import com.example.kingofthewords.dbModels.UserWord;
import com.example.kingofthewords.dbModels.Word;

import org.litepal.LitePal;

import java.util.List;

public class ReviewWordAdapter extends RecyclerView.Adapter<ReviewWordAdapter.WordViewHolder> {
    //the context uses this adapter, which here should be the instance of ReviewActivity
    private Context mContext;
    //an Array of Words that should be shown in the recyclerview
    private List<Word> mWordList;

    //the constructor
    public ReviewWordAdapter(Context context, List<Word> wordList){
        this.mContext = context;
        this.mWordList = wordList;
    }

    public ReviewWordAdapter.WordViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (mContext == null){
            mContext = viewGroup.getContext();
        }
        //inflate the 'mContext' with 'review_word_item'
        return new ReviewWordAdapter.WordViewHolder(LayoutInflater.from(mContext).inflate(R.layout.review_word_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ReviewWordAdapter.WordViewHolder wordViewHolder, int i) {
        /*
            define the value of each view element in the holder
         */
        //get each word object by id
        Word word = mWordList.get(i);

        //define values of views
        wordViewHolder.mTvWord.setText(word.getWord());
        String phone = "/" + word.getUsPhone() + "/";
        wordViewHolder.mTvPhone.setText(phone);

        //get the translate object of this word
        Translation translation = word.getTranslations().get(0);
        //define the values of the views
        wordViewHolder.mTvPop.setText(translation.getPos() + ".");
        wordViewHolder.mTvTrans.setText(translation.getTransCN());

        //get the url for requesting the audio
        //the part of the url links of audios
        final String AUDIO_URL = "https://dict.youdao.com/dictvoice?audio=";
        String audioUS = word.getAudioUS();
        String url = AUDIO_URL + audioUS;

        //set onclick listener of the audio button
        wordViewHolder.mBtnAudioUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioUS != null){
                    //start a new thread to play the audio
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MediaPlayer player = MediaPlayer.create(mContext, Uri.parse(url));
                            player.start();
                        }
                    }).start();
                }
            }
        });

        //set onclick listener of the cover
        wordViewHolder.mVCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make it invisible when clicking on it
                wordViewHolder.mVCover.setVisibility(View.INVISIBLE);
            }
        });

        //set onclick listener of the whole view (a single world item)
        wordViewHolder.mLlContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* jump to the word detail activity */
                Intent intent = new Intent(mContext, LearnDetailActivity.class);
                intent.putExtra(LearnActivity.WORD_ID, word.getId());
                //get and pass the current task id
                int selectedBookId = MainActivity.currentUser.getSelectedBookId();
                Task currentTask = LitePal.where("userId = ? and bookId = ?", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(selectedBookId)).findFirst(Task.class);
                intent.putExtra(LearnActivity.TASK_ID, currentTask.getId());
                //pass the signal that says the user comes from "reviewing"
                intent.putExtra("isFromReview", true);
                mContext.startActivity(intent);

                /* mark the word as reviewed */
                //update the status of current user-word relation
                UserWord userWordForUpdate = new UserWord();
                userWordForUpdate.setReviewed(true);
                userWordForUpdate.updateAll("userId = ? and wordId = ?", String.valueOf(MainActivity.currentUser.getId()), String.valueOf(word.getId()));

                /* update the review task progress */
                Task taskForUpdate = new Task();
                taskForUpdate.setReviewedWordNum(currentTask.getReviewedWordNum() + 1);
                taskForUpdate.setCountTodayReview(currentTask.getCountTodayReview() + 1);
                taskForUpdate.updateAll("id = ?", String.valueOf(currentTask.getId()));

            }
        });



    }

    @Override
    public int getItemCount() {
        return mWordList.size();
    }

    //an inner class for declaring and holding the views in this recyclerView
    class WordViewHolder extends RecyclerView.ViewHolder{

        //the views be held
        LinearLayout mLlContent;    //whole view (a single world item)
        TextView mTvWord;   //the word spelling
        TextView mTvPhone;  //the word phonetic
        TextView mTvPop;    //the word property
        TextView mTvTrans;  //the Chinese translation
        Button mBtnAudioUS; //the us audio
        View mVCover;       //the cover of the translation

        public WordViewHolder(View itemView) {
            super(itemView);
            //initialize the views that are held in this class
            this.mLlContent = itemView.findViewById(R.id.ll_review_word_content);
            this.mTvWord = itemView.findViewById(R.id.review_word_item_word);
            this.mTvPhone = itemView.findViewById(R.id.review_word_item_phone);
            this.mTvPop = itemView.findViewById(R.id.review_word_item_pop);
            this.mTvTrans = itemView.findViewById(R.id.review_word_item_trans);
            this.mBtnAudioUS = itemView.findViewById(R.id.btn_review_word_item_us_audio);
            this.mVCover = itemView.findViewById(R.id.review_word_item_cover);
        }
    }
}
