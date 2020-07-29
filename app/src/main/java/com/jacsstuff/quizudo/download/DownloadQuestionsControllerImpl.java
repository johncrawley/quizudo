package com.jacsstuff.quizudo.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.jacsstuff.quizudo.utils.JSONParser;
import com.jacsstuff.quizudo.db.DBWriter;
import com.jacsstuff.quizudo.model.QuestionPackDbEntity;
import com.jacsstuff.quizudo.model.QuestionPackOverview;
import com.jacsstuff.quizudo.validation.Validator;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DownloadQuestionsControllerImpl implements DownloadQuestionsController {

    private DBWriter dbWriter;
    private Handler handler;
    private ExecutorService executorService;
    private final int QUESTION_PACKS_DOWNLOADED_COUNT = 0;
    private final int ERROR = 8;
    private final int QUESTION_PACKS_OVERVIEW_LIST = 2;
    private Downloader jsonDownloader;
    private JSONParser jsonParser;
    private  Validator validator;
    private DownloadQuestionsView view;


    DownloadQuestionsControllerImpl(Context context, DownloadQuestionsView view){

        executorService = Executors.newSingleThreadExecutor();
        dbWriter = new DBWriter(context);
        jsonDownloader = new DownloaderImpl();
        jsonParser = new JSONParser();
        validator = new Validator();
        this.view = view;
        setupHandler();
    }

    private String getQueryString(String request, final List<String> args){
        StringBuilder queryStr = new StringBuilder();
        queryStr.append(request);

        if(args != null){
            String delimiter = "";
            for(String arg : args) {
                if(arg.trim().isEmpty()){
                    continue;
                }
                queryStr.append(delimiter);
                delimiter = ",";
                queryStr.append(arg);
            }
        }
        return queryStr.toString();
    }


    private String createRequestUrl(String pathname, String request, final List<String> args){
        Log.i("createRequestUrl", "pathname: " + pathname + " request: " + request);

        try {
            URL url = new URL(pathname + getQueryString(request, args));
            URI t = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            Log.i("URL TRY "  , t.toString());
            return t.toString();

        }catch (MalformedURLException  |URISyntaxException e){
            Log.i("Exception ", e.getMessage());
            return "";
        }

    }

    @Override
    public void retrieveQuestionPacks(final String url, final List<String> questionPackNames) {
            view.displayDownloadingDialog();
            executorService.execute(new Runnable(){
                public void run() {
                    Message msg;
                    String requestUrl = createRequestUrl(url, "getQuestionPacks?names=", questionPackNames);
                    Log.i("DownloadQuestionsCntrl", "requestUrl :" +  requestUrl);
                    Response response = jsonDownloader.getResponse(requestUrl);

                    if(response.codeEquals(Response.Code.SUCCESS)){
                        int successCount = saveQuestionPacks(response);
                        msg = handler.obtainMessage(QUESTION_PACKS_DOWNLOADED_COUNT, successCount);
                    }
                    else{
                        msg = handler.obtainMessage(ERROR, response.getCode());
                    }
                    msg.sendToTarget();
                }
            });
    }

    @Override
    public void retrieveQuestionPackNames(){
        view.displayDownloadingDialog();
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                String requestUrl = createRequestUrl(view.getUrl(), "listQuestionPacks", null);
                Log.i("DownloadContrl", "request url: " + requestUrl);
                Response response = jsonDownloader.getResponse(requestUrl);
                Message msg;
                if(response.codeEquals(Response.Code.SUCCESS)) {
                    List<QuestionPackOverview> qpOverviews = jsonParser.parseQuestionPackOverviews(response.getBody());
                    msg = handler.obtainMessage(QUESTION_PACKS_OVERVIEW_LIST, qpOverviews);
                }
                else{
                    msg = handler.obtainMessage(ERROR, response.getCode());
                }
                msg.sendToTarget();
            }
        });
    }


    private int saveQuestionPacks(Response response){
        List<QuestionPackDbEntity> questionPacks = jsonParser.parseQuestionPacksFromJson(response.getBody());

        int successCount = 0;
        for (QuestionPackDbEntity qp : questionPacks) {
            validator.validate(qp);
            if(qp.hasNoQuestions()){
                continue;
            }
            if (dbWriter.saveQuestionPackRecord(qp) != -1) {
                successCount++;
            }
        }
        return successCount;
    }



    // tells the view what to do once the running thread has finished executing
    private void setupHandler(){
        handler = new Handler(Looper.getMainLooper()){

            public void handleMessage(Message inputMessage){
                if(inputMessage.what == ERROR){
                    Response.Code errorCode = (Response.Code)inputMessage.obj;
                    switch(errorCode){
                        case NOT_FOUND:
                            view.displayNotFoundMessage();
                            break;
                        case SERVER_NOT_FOUND:
                            view.displayServerNotFoundMessage();
                            break;
                        case SERVER_ERROR:
                            view.displayServerErrorMessage();
                            break;
                        case OTHER_ERROR:
                            view.displayCatchAllErrorMessage();
                    }
                }
                else if(inputMessage.what == QUESTION_PACKS_OVERVIEW_LIST){
                    @SuppressWarnings("unchecked")
                    List<QuestionPackOverview> qpOverviews = (List<QuestionPackOverview>)inputMessage.obj;
                    view.updateQuestionPackList(qpOverviews);
                }
                else if(inputMessage.what == QUESTION_PACKS_DOWNLOADED_COUNT){
                    view.displayQuestionPacksDownloadedMessage((int)inputMessage.obj);
                }
            }
        };
    }
}
