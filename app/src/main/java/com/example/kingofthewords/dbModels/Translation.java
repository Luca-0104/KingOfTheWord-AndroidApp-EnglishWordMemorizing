package com.example.kingofthewords.dbModels;

import org.litepal.crud.LitePalSupport;

/**
 * The translations of words
 * 1 word --> n translations
 * 1 translation --> 1 word
 */
public class Translation extends LitePalSupport {

    private String transCN; //the Chinese translation of this word
    private String pos; //the part of speech of this word in this translation
    private Word word;  //the corresponding word of this piece of translation

    public String getTransCN() {
        return transCN;
    }

    public void setTransCN(String transCN) {
        this.transCN = transCN;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }
}
