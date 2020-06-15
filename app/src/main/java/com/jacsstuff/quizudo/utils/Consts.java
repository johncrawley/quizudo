package com.jacsstuff.quizudo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by John on 03/11/2016.
 *
 * Consts used in various classes for various purposes. What a description!
 */
public class Consts {


    public static final String ANSWER_CHOICE_DELIMITER = "~";
    public static final String TOPICS_DELIMITER = ",";

    public static final String FILENAME_EXTENSION = ".qzp";
    public static final String AUTHOR_DELIMITER = "__";
    public static final int    LOADING_DIALOG_TIMEOUT = 7000;

    public static final String NUMBER_OF_QUESTIONS_INTENT_EXTRA = "numberOfQuestions";
    public static final String DISPLAY_ANSWER_DIALOG_INTENT_EXTRA = "showAnswerDialog";
    public static final String SUBMIT_ANSWER_ON_TOUCH_INTENT_EXTRA = "submitAnswerOnTouch";
    public static final String QUIZ_SETTINGS_PREFERENCES = "quizSettings";

    public static final String DOWNLOAD_QUESTION_PACKS_PREFERENCE = "DownloadQuestionPacks";
    public static final String REMOVE_QUESTIONS_PREFERENCE = "RemoveQuestionPacks";
    public static final String PREVIOUS_NUMBER_OF_QUESTIONS_PREFERENCE = "previousNumberOfQuestions";
    public static final String PREVIOUS_SUBMIT_ANSWER_ON_TOUCH_PREFERENCE = "submitAnswerOnTouch";
    public static final String DOWNLOAD_URL_PREFERENCE = "downloadUrl";
    public static final String PREVIOUS_SHOW_ANSWER_PREFERENCE = "showAnswer";
    public static final String WERE_DEFAULT_QPS_ADDED_PREFERENCE = "wereDefaultQPsAdded";
    public static final String ANSWER_POOL_PREFERENCE = "AnswerPoolSettings";
    public static final String QUESTION_CREATOR_PREFERENCE = "QuestionPackCreator";
    public static final int DEFAULT_NUMBER_OF_QUESTIONS = 10;

    public static final String IS_ANSWER_CORRECT_INTENT_EXTRA = "isAnswerCorrect";
    public static final String CORRECT_ANSWER_INTENT_EXTRA = "correctAnswer";
    public static final String TRIVIA_INTENT_EXTRA = "trivia";

}