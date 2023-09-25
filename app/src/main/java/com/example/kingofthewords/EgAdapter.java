package com.example.kingofthewords;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kingofthewords.dbModels.Sentence;

import java.util.List;

/**
 * This is an adapter for word example sentences recyclerView in the WordsDetailFragment
 */
public class EgAdapter extends RecyclerView.Adapter<EgAdapter.EgViewHolder> {

    //the context uses this adapter, which here should be the instance of LearnDetailActivity
    private Context mContext;
    //an Array of example sentence objects that should be shown in the recyclerview
    private List<Sentence> mEgList;

    //the constructor
    public EgAdapter(Context context,  List<Sentence> egList){
        this.mContext = context;
        this.mEgList = egList;
    }

    public EgViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (mContext == null){
            mContext = viewGroup.getContext();
        }
        //inflate the 'mContext' with 'eg_item'
        return new EgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.eg_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(EgViewHolder holder, int i) {
        /*
            define the value of each view element in the holder
         */
        //get each Sentence object by id
        Sentence sentence = mEgList.get(i);

        //set the value of English sentence
        holder.mTvEN.setText(sentence.getSentenceEN());

        //set the value of Chinese translation
        holder.mTvCN.setText(sentence.getSentenceCN());
    }

    @Override
    public int getItemCount() {
        return mEgList.size();
    }

    //an inner class for declaring and holding the views in this recyclerView
    class EgViewHolder extends RecyclerView.ViewHolder{

        //the views be held
        TextView mTvEN;     //English example sentence
        TextView mTvCN;     //Chinese translation

        public EgViewHolder(View itemView) {
            super(itemView);
            //initialize the views that are held in this class
            this.mTvEN = itemView.findViewById(R.id.tv_eg_item_EN);
            this.mTvCN = itemView.findViewById(R.id.tv_eg_item_CN);
        }
    }
}
