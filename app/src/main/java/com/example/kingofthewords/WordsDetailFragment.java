package com.example.kingofthewords;

import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kingofthewords.dbModels.Sentence;
import com.example.kingofthewords.dbModels.Translation;
import com.example.kingofthewords.dbModels.Word;

import java.util.List;

/**
 * This is a fragment of detailed word information, which is contained in the LearnDetailActivity
 */
public class WordsDetailFragment extends Fragment implements View.OnClickListener {

    //a view inflated by this fragment
    private View view;

    //elements in this fragment
    private TextView mTvWord;
    private TextView mTvUSPhone;
    private TextView mTvUKPhone;
    private RecyclerView mRvTrans;
    private RecyclerView mRvEg;
    private Button mBtnAudioUS;
    private Button mBtnAudioUK;

    //Lists for recyclerViews
    private List<Translation> transList;
    private List<Sentence> egList;

    //the part of the url links of audios
    private static final String AUDIO_URL = "https://dict.youdao.com/dictvoice?audio=";
    private String audioUS;
    private String audioUK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the whole page view with "words_detail_fragment"
        this.view = inflater.inflate(R.layout.words_detail_fragment, container, false);

        //get the object of the LearDetailActivity
        LearnDetailActivity activity = (LearnDetailActivity) getActivity();

        //initialize the elements
        mTvWord = view.findViewById(R.id.tv_detail_frag_word);
        mTvUSPhone = view.findViewById(R.id.tv_detail_frag_us_phone);
        mTvUKPhone = view.findViewById(R.id.tv_detail_frag_uk_phone);
        mRvTrans = view.findViewById(R.id.rv_detail_frag_trans);
        mRvEg = view.findViewById(R.id.rv_detail_frag_eg);
        mBtnAudioUS = view.findViewById(R.id.btn_detail_frag_us_audio);
        mBtnAudioUK = view.findViewById(R.id.btn_detail_frag_uk_audio);

        //set the onclick listener for buttons
        mBtnAudioUS.setOnClickListener(this);
        mBtnAudioUK.setOnClickListener(this);

        //we need to initialize the lists here, before creating the adapters
        Word currentWord = activity.getCurrentWord();
        transList = currentWord.getTranslations();
        egList = currentWord.getSentences();

        //set adapters, layout managers, decorations for recyclerViews
        mRvTrans.setLayoutManager(new LinearLayoutManager(activity));
        mRvTrans.setAdapter(new TransAdapter(activity, transList));
        mRvEg.setLayoutManager(new LinearLayoutManager(activity));
        mRvEg.setAdapter(new EgAdapter(activity, egList));


        //return the view of this this whole page
        return view;
    }

    /**
     *  Refresh the detailed word information when the current learning word is changed
     */
    public void refresh(Word word){
        mTvWord.setText(word.getWord());  //update the word

        String usPhone = "/" + word.getUsPhone() + "/";
        String ukPhone = "/" + word.getUkPhone() + "/";
        mTvUSPhone.setText(usPhone); //update the US phonetic symbol
        mTvUKPhone.setText(ukPhone); //update the UK phonetic symbol

        //update the translation
        transList = word.getTranslations();

        //update the example sentences
        egList = word.getSentences();

        //update the audio link
        audioUS = word.getAudioUS();
        audioUK = word.getAudioUK();

    }

    /**
     * Define what to do when a button is clicked
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //if the us audio is clicked
            case R.id.btn_detail_frag_us_audio:
                //if there is a audio resource, we play it
                if (audioUS != null && audioUS != ""){
                    playAudio(AUDIO_URL + audioUS);
                }
                break;

            //if the uk audio is clicked
            case R.id.btn_detail_frag_uk_audio:
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
        MediaPlayer player = new MediaPlayer().create(getActivity(), Uri.parse(url));
        player.start();
    }


}
