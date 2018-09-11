package com.jacsstuff.quizudo.options;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jacsstuff.quizudo.R;

public class DialogActivity extends Activity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        context = DialogActivity.this;
        TextView dialogText =   findViewById(R.id.dialogText);
        Button yesButton =      findViewById(R.id.yesButton);
        Button noButton  =      findViewById(R.id.noButton);

        final Intent intent = getIntent();

        String message = intent.getStringExtra(Dialog.MESSAGE.toString());
        dialogText.setText(message);

        noButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                finish();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                yesButtonAction(intent, context);
                finish();
            }
        });
    }
    public enum Dialog { MESSAGE, STRING_EXTRA1, STRING_EXTRA2, INT_EXTRA1}
    protected String getIntentText(Intent intent){
        String inputText = intent.getStringExtra(Dialog.STRING_EXTRA1.toString());

        if(inputText == null){
            inputText = "";
        }
        return inputText;
    }


    protected void yesButtonAction(Intent intent, Context context){

        String name = intent.getStringExtra(Dialog.STRING_EXTRA1.toString());
        if(name != null && !name.isEmpty()){
        }
        finish();
    }
}
