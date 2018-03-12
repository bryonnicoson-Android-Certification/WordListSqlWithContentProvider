package com.android.example.wordlistsqlwithcontentprovider;

/**
 * Created by bryon on 3/10/18.
 */

public class WordItem {

    private int mId;
    private String mWord;

    public WordItem(){}

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmWord() {
        return mWord;
    }

    public void setmWord(String mWord) {
        this.mWord = mWord;
    }
}
