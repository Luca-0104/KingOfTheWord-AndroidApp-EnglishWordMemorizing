package com.example.kingofthewords;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kingofthewords.dbModels.Sentence;
import com.example.kingofthewords.dbModels.Translation;
import com.example.kingofthewords.dbModels.Word;

import java.util.List;

/**
 * This is a fragment of basic word information, which is contained in the LearnActivity
 */
public class WordsContentFragment extends Fragment implements View.OnClickListener {

    //a view inflated by this fragment
    private View view;

    //elements in this fragment
    private LinearLayout mLlHint;
    private TextView mTvWord;
    private TextView mTvHint;
    private TextView mTvUSPhone;
    private TextView mTvUKPone;
    private Button mBtnAudioUS;
    private Button mBtnAudioUK;

    //the part of the url links of audios
    private static final String AUDIO_URL = "https://dict.youdao.com/dictvoice?audio=";
    private String audioUS;
    private String audioUK;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the whole page view with "words_content_fragment"
        this.view = inflater.inflate(R.layout.words_content_fragment, container, false);

        //initialize the elements
        mTvWord = view.findViewById(R.id.tv_content_frag_word);
        mTvHint = view.findViewById(R.id.tv_content_frag_hint);
        mLlHint = view.findViewById(R.id.ll_content_frag_hint);
        mTvUSPhone = view.findViewById(R.id.tv_content_frag_us_phone);
        mTvUKPone = view.findViewById(R.id.tv_content_frag_uk_phone);
        mBtnAudioUS = view.findViewById(R.id.btn_content_frag_us_audio);
        mBtnAudioUK = view.findViewById(R.id.btn_content_frag_uk_audio);

        //set the onclick listener for buttons
        mBtnAudioUS.setOnClickListener(this);
        mBtnAudioUK.setOnClickListener(this);



        //return the view of this this whole page
        return view;
    }

    /**
     *  Refresh the detailed word information when the current learning word is changed
     */
    public void refresh(Word word){
        //update the word
        mTvWord.setText(word.getWord());

        //update the hint
        List<Sentence> sentencesList = word.getSentences();
        //if this word has example sentences
        if (sentencesList.size() > 0){
            Sentence sentence = sentencesList.get(0);
            if (sentence != null){
                mTvHint.setText(sentence.getSentenceEN());
            }

        }else{
            //if there are no sentences
            mTvHint.setText("No example sentence...");
        }


        //update the phonetic symbols
        String usPhone = "/" + word.getUsPhone() + "/";
        String ukPhone = "/" + word.getUkPhone() + "/";
        mTvUSPhone.setText(usPhone);
        mTvUKPone.setText(ukPhone);

        //update the audio link
        audioUS = word.getAudioUS();
        audioUK = word.getAudioUK();


    }

    /**
     *  To show the hint, make the hint visible
     */
    public void showHint(){
        //make the LinearLayout of the hint visible
        this.mLlHint.setVisibility(View.VISIBLE);
    }

    /**
     * Define what to do when a button is clicked
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //if the us audio is clicked
            case R.id.btn_content_frag_us_audio:
                //if there is a audio resource, we play it
                if (audioUS != null && audioUS != ""){
                    playAudio(AUDIO_URL + audioUS);
                }
                break;

                //if the uk audio is clicked
            case R.id.btn_content_frag_uk_audio:
                //if there is a audio resource, we play it
                if (audioUK != null && audioUK != ""){
                    playAudio(AUDIO_URL + audioUK);
                }
                break;
        }
    }

    /**
     *  This method is for playing the audio
     * @param url The http url of the audio resource
     */
    private void playAudio(String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer player = MediaPlayer.create(getActivity(), Uri.parse(url));
                player.start();
            }
        }).start();

    }
}
