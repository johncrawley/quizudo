package com.jacsstuff.quizudo.adverts;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.main.MainController;

public class BannerAdBuilder {

    private InterstitialAd mInterstitialAd;
    private Context context;
    private MainController controller;


    public BannerAdBuilder(MainController mainController, Context context){
        this.context = context;
        this.controller = mainController;

    }

    public void setupInterstitialAd(){
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.ads_unit_interstitial_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                Log.i("MainActivity", "onAdClosed: entered. About to start new quiz");
                controller.startNewQuiz();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                controller.startNewQuiz();
            }
        });
        requestNewInterstitial();
    }


    private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(context.getString(R.string.test_device1_id))
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


}
