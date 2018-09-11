package com.jacsstuff.quizudo.download;

import java.util.List;

public interface DownloadQuestionsController {


    void retrieveQuestionPacks(String url, List<String> questionPackNames);
    void retrieveQuestionPackNames();
}
