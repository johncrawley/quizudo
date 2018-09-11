package com.jacsstuff.quizudo.download;

import com.jacsstuff.quizudo.model.QuestionPackOverview;

import java.util.List;

public interface DownloadQuestionsView {

    void displayDownloadingDialog();
    void displayQuestionPacksDownloadedMessage(int number);
    void displayServerNotFoundMessage();
    void displayNotFoundMessage();
    void updateQuestionPackList(List<QuestionPackOverview> questionPackOverviews);
    void displayServerErrorMessage();
    void displayCatchAllErrorMessage();
    String getUrl();

}
