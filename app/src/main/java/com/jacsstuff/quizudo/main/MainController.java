package com.jacsstuff.quizudo.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.jacsstuff.quizudo.configure.CreateQuizActivity;
import com.jacsstuff.quizudo.utils.Utils;

/**
 * Created by John on 27/06/2016.
 * Performing some logic for the main activity
 */
public class MainController {

    private Context context;

    public MainController(Context context){

        this.context = context;
        setupAdsIfEnabled();
    }

    public void startNewQuiz(){
        Log.i("new Quiz", "Beginning new quiz");
        Intent intent = new Intent(context, CreateQuizActivity.class);
        context.startActivity(intent);
    }

    private void setupAdsIfEnabled(){

        if (Utils.isFreeVersion(context)) {
            //setupBannerAd();
           // setupInterstitialAd();
        }
    }


}
