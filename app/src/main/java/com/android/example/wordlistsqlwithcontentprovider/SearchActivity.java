package com.android.example.wordlistsqlwithcontentprovider;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by bryon on 3/11/18.
 */

public class SearchActivity extends AppCompatActivity {

    private TextView mTextView;
    private EditText mEditWordView;
    private WordListOpenHelper mDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mTextView = findViewById(R.id.search_result);
        mEditWordView = findViewById(R.id.search_word);
        mDB = new WordListOpenHelper(this);
    }

    public void showResult(View view) {
        String word = String.valueOf(mEditWordView.getText());
        mEditWordView.setText("");
        mTextView.setText(Html.fromHtml(getString(R.string.search_result_prefix) + " <b>" + word + "</b>:<br/><br/>"));
        Cursor cursor = mDB.search(word);
        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            int index;
            String result;
            do {
                index = cursor.getColumnIndex(WordListOpenHelper.KEY_WORD);
                result = cursor.getString(index);
                mTextView.append(result + "\n");
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
