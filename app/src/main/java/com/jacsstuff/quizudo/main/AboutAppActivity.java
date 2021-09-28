package com.jacsstuff.quizudo.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jacsstuff.quizudo.R;

public class AboutAppActivity extends Activity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        TextView createdByText = findViewById(R.id.createdBy);
        createdByText.setText(getResources().getString(R.string.about_app_created_by));
        View layout = findViewById(R.id.about_dialog);
        layout.setOnClickListener(this);
    }
    public void onClick(View view){
        finish();
    }
}
