package com.jacsstuff.quizudo.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

import java.util.logging.LogManager;

/**
 * Created by John on 16/12/2016.
 * Used for the trivia in the answer dialogue activity.
 */
public class LimitedHeightScrollView extends ScrollView {

    public static int WITHOUT_MAX_HEIGHT_VALUE = 1000;

    private int maxHeight = WITHOUT_MAX_HEIGHT_VALUE;

    public LimitedHeightScrollView(Context context) {
        super(context);
    }

    public LimitedHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LimitedHeightScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
           // if (maxHeight != WITHOUT_MAX_HEIGHT_VALUE
           //         && heightSize > maxHeight) {
           //     heightSize = maxHeight;
            //}
            if(heightSize > maxHeight){
                heightSize = maxHeight;
            }
            Log.i("limitedScrollView", "height size = " + heightSize);
            if(heightSize < -1 || heightSize > 5000){
                heightSize = 1000;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
            getLayoutParams().height = heightSize;


        } catch (Exception e) {
            Log.e("onMesure", "Error forcing height", e);
        } finally {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }
}