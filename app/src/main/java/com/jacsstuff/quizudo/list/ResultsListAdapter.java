package com.jacsstuff.quizudo.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacsstuff.quizudo.model.QuestionResult;
import com.jacsstuff.quizudo.R;

import java.util.List;

/**
 * Created by John on 17/09/2016.
 */
public class ResultsListAdapter extends ArrayAdapter<QuestionResult>{

    private Context context;
    private List<QuestionResult> items;

    public ResultsListAdapter(Context context, int textViewResourceId, List<QuestionResult> items){
        super(context, textViewResourceId, items);
        this.context = context;
        this.items = items;
    }


    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if(view == null){
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.result_row,null);
        }

        QuestionResult questionResult = items.get(position);
        if(questionResult == null || questionResult.getQuestionResultText().isEmpty() || questionResult.getCorrectAnswer().isEmpty()){
            return view;
        }

        TextView questionText = view.findViewById(R.id.resultQuestionText);
        TextView correctAnswerText = view.findViewById(R.id.resultCorrectAnswerText);
        ImageView imageView = view.findViewById(R.id.resultAnsweredCorrectlyImage);


        if(questionText != null){
            questionText.setText(questionResult.getQuestionResultText());
        }
        if(correctAnswerText != null){
            String correctAnswer = questionResult.getCorrectAnswer();

            if(correctAnswer != null && !correctAnswer.isEmpty()){
                correctAnswerText.setText(correctAnswer);
            }
        }
        if(imageView != null){

            if(questionResult.wasAnsweredCorrectly()){
                imageView.setImageResource(R.drawable.correct_icon);
                imageView.setContentDescription(context.getResources().getString(R.string.correct_answer_icon_description));
            }
            else{
                imageView.setImageResource(R.drawable.incorrect_icon);
                imageView.setContentDescription(context.getResources().getString(R.string.incorrect_answer_icon_description));
            }
        }
        return view;
    }
}
