package com.example.kingofthewords;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kingofthewords.dbModels.Translation;

import java.util.List;

/**
 * This is an adapter for word translation recyclerView in the WordsDetailFragment
 */
public class TransAdapter extends RecyclerView.Adapter<TransAdapter.TransViewHolder> {

    //the context uses this adapter, which here should be the instance of LearnDetailActivity
    private Context mContext;
    //an Array of Translations that should be shown in the recyclerview
    private List<Translation> mTransList;

    //the constructor
    public TransAdapter(Context context,  List<Translation> transList){
        this.mContext = context;
        this.mTransList = transList;
    }

    public TransViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (mContext == null){
            mContext = viewGroup.getContext();
        }
        //inflate the 'mContext' with 'trans_item'
        return new TransViewHolder(LayoutInflater.from(mContext).inflate(R.layout.trans_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(TransViewHolder holder, int i) {
        /*
            define the value of each view element in the holder
         */
        //get each Translation object by id
        Translation trans = mTransList.get(i);

        //set the value of pos
        String pos = trans.getPos() + ".";
        holder.mTvPos.setText(pos);

        //set the value of Chinese translation
        holder.mTvTrans.setText(trans.getTransCN());

    }

    @Override
    public int getItemCount() {
        return mTransList.size();
    }

    //an inner class for declaring and holding the views in this recyclerView
    class TransViewHolder extends RecyclerView.ViewHolder{

        //the views be held
        TextView mTvPos;    //the "part of speech" of this word
        TextView mTvTrans;  //the Chinese translation

        public TransViewHolder(View itemView) {
            super(itemView);
            //initialize the views that are held in this class
            this.mTvPos = itemView.findViewById(R.id.tv_trans_item_pos);
            this.mTvTrans = itemView.findViewById(R.id.tv_trans_item_trans);
        }
    }

}
