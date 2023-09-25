package com.example.kingofthewords.dbModels;

import org.litepal.crud.LitePalSupport;

/**
 * The example sentences of words
 * 1 word --> n Sentences
 * 1 sentence --> 1 word
 */
public class Sentence extends LitePalSupport {

    private String sentenceEN;  //example sentences
    private String sentenceCN;  //Chinese translation
    private Word word;  //the corresponding word of this sentence

    public String getSentenceEN() {
        return sentenceEN;
    }

    public void setSentenceEN(String sentenceEN) {
        this.sentenceEN = sentenceEN;
    }

    public String getSentenceCN() {
        return sentenceCN;
    }

    public void setSentenceCN(String sentenceCN) {
        this.sentenceCN = sentenceCN;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }
}
