package com.jacsstuff.quizudo.creator;

import android.content.Context;

import com.jacsstuff.quizudo.answerPool.AnswerPoolDBManager;
import com.jacsstuff.quizudo.db.DBWriter;
import com.jacsstuff.quizudo.list.SimpleListItem;
import com.jacsstuff.quizudo.model.QuestionPackDbEntity;
import com.jacsstuff.quizudo.validation.Validator;

import java.util.ArrayList;
import java.util.List;

public class QuizWriterControllerImpl implements QuizWriterController {

    private final QuestionPackItem questionPackItem;
    private final List<QuestionItem> questionItems;
    private final DBWriter dbWriter;
    private final QuizWriterView view;
    private final Validator validator;
    private int currentPage = 1; // page = 1 means the questionPack UI elements are shown
                                // page = >2 means the question UI elements are shown
                                // so page = 2 should correspond to questionItems.get(0);
    private int currentQuestion = 0;
    private int currentQuestionIndex = -1;
    private final String NONE_OPTION;
    private final int FIRST_PAGE = 1;


    QuizWriterControllerImpl(QuizWriterView view, Context context, String noneOption){
        dbWriter = new DBWriter(context);
        questionItems = new ArrayList<>();
        questionPackItem = new QuestionPackItem();
        this.view = view;
        validator = new Validator();
        view.setAnswerPoolChoices(getAnswerPoolNames(context));
        NONE_OPTION = noneOption;
    }

    private List<String> getAnswerPoolNames(Context context){
        AnswerPoolDBManager answerPoolDBManager = new AnswerPoolDBManager(context);
        List<SimpleListItem> items = answerPoolDBManager.getAnswerPools();
        List<String> answers = new ArrayList<>();
        for(SimpleListItem item: items){
            answers.add(item.getName());
        }
        return answers;
    }

    public void saveQuestionPack(){
        questionPackItem.setAuthor("User");
        copyDataFromViewToCurrentQuestionItem();
        copyQuestionPackInfoFromView();
        QuestionPackItemConverter converter = new QuestionPackItemConverter(NONE_OPTION);

        QuestionPackDbEntity questionPackDbEntity = converter.convertToQuestionPackDbDetail(questionPackItem, questionItems);
        validator.validate(questionPackDbEntity);

        if(questionPackDbEntity.hasNoQuestions()){
            view.displayNothingToSaveMessage();
            return;
        }
        if(dbWriter.saveQuestionPackRecord(questionPackDbEntity) != -1){
            view.displaySaveSuccessMessage(questionPackDbEntity.getNumberOfQuestions());
            return;
        }
        view.displaySaveFailMessage();
    }


    public void loadFirstPage(){
        copyDataFromViewToCurrentQuestionItem();
        view.showQuestionPackScreen();
        resetPageVars();
        view.setPageNumber(this.currentPage);
        view.disablePreviousButton();
        view.disableFirstButton();
        view.enableLastButton();
        view.setFirstPageTitle();
    }


    public void loadLastPage(){
        view.disableLastButton();
        view.enablePreviousButton();
        if(currentPage == FIRST_PAGE){
            view.showQuestionScreen();
            view.enableFirstButton();
            view.enableNextButton();
        }
        else{
            copyDataFromViewToCurrentQuestionItem();
        }

        if(questionItems.size() == 0){
            createQuestionItem();
        }
        setPageVarsToCurrentMax();
        copyDataFromCurrentQuestionItemToView();
        view.setPageNumber(this.currentPage);
        view.setQuestionNumber(this.currentQuestion);
        setAnswerPoolSpinnerStatus();

    }


    public void loadNextPage(){
        handleLoadSecondPage();
        handleSecondLastPage();
        incrementPageVars();

        if(currentQuestionIndex > 0) {
            copyDataFromViewToPreviousQuestionItem();
        }

        if(isOnNewQuestionIndex()) {
            createQuestionItem();
        }
        if(currentPage -1 != FIRST_PAGE) {
            copyDataFromCurrentQuestionItemToView();
        }
        setAnswerPoolSpinnerStatus();
        view.setQuestionNumber(this.currentQuestion);
    }


    private void handleLoadSecondPage(){
        if(currentPage == FIRST_PAGE){
            view.showQuestionScreen();
            view.enableFirstButton();
            view.enablePreviousButton();
        }
    }


    private void handleSecondLastPage(){
        if(isLastPage()){
            view.disableLastButton();
        }
    }


    public void loadPreviousPage(){
        if(currentPage == FIRST_PAGE){
            return;
        }
        copyDataFromViewToCurrentQuestionItem();
        decrementPageVars();
        view.setPageNumber(this.currentPage);
        view.enableLastButton();
        if(currentPage == FIRST_PAGE){
            handleFirstPageLoad();
            return;
        }
        copyDataFromCurrentQuestionItemToView();
        view.setQuestionNumber(this.currentQuestion);
        setAnswerPoolSpinnerStatus();
    }


    private void handleFirstPageLoad(){
        view.showQuestionPackScreen();
        view.enableNextButton();
        view.disablePreviousButton();
        view.disableFirstButton();
        view.setFirstPageTitle();
    }


    private boolean isOnNewQuestionIndex() {
        return questionItems.size() <= currentQuestionIndex;
    }


    private void createQuestionItem() {
        final int MAX_ANSWER_CHOICES = 6;
        QuestionItem currentQuestionItem = new QuestionItem(MAX_ANSWER_CHOICES);
        questionItems.add(currentQuestionItem);
    }


    private void copyDataFromViewToCurrentQuestionItem() {
        if(currentQuestionIndex < 0){
            return;
        }
        QuestionItem questionItem = questionItems.get(currentQuestionIndex);
        copyDataFromViewToQuestionItem(questionItem);
    }


    private void copyDataFromViewToPreviousQuestionItem() {
        QuestionItem questionItem = questionItems.get(currentQuestionIndex-1);
        copyDataFromViewToQuestionItem(questionItem);
    }


    private void setAnswerPoolSpinnerStatus(){
        if(view.isUsingDefaultAnswerPool()){
            view.disableAnswerPoolSelection();
        }
        else{
            view.enableAnswerPoolSelection();
        }
        view.setPageNumber(this.currentPage);
    }


    private void copyDataFromViewToQuestionItem(QuestionItem questionItem){
        questionItem.setAnswerChoices(view.getAnswerChoices());
        questionItem.setChosenAnswerPool(view.getAnswerPool());
        questionItem.setTopics(view.getTopics());
        questionItem.setTrivia(view.getTriviaText());
        questionItem.setQuestion(view.getQuestionText());
        questionItem.setCorrectAnswer(view.getCorrectAnswerText());
        questionItem.setUsesDefaultAnswerPool(view.isUsingDefaultAnswerPool());
    }


    private void copyDataFromCurrentQuestionItemToView() {
        if(currentQuestionIndex < 0){
            return;
        }
        QuestionItem questionItem = questionItems.get(currentQuestionIndex);
        view.setQuestionText(questionItem.getQuestion());
        view.setAnswerChoices(questionItem.getAnswerChoices());
        view.setTriviaText(questionItem.getTrivia());
        view.setAnswerPool(deriveCurrentAnswerPoolChoice(questionItem));
        view.setTopics(questionItem.getTopics());
        view.setCorrectAnswerText(questionItem.getCorrectAnswer());
        view.setUsesDefaultAnswerPool(questionItem.isUsingDefaultAnswerPool());
    }


    private String deriveCurrentAnswerPoolChoice(QuestionItem questionItem){
        String answerPoolChoice = questionItem.getChosenAnswerPool();
        if(answerPoolChoice.isEmpty()){
            answerPoolChoice = NONE_OPTION;
        }
        return answerPoolChoice;
    }


    private void copyQuestionPackInfoFromView(){
        questionPackItem.setDefaultAnswerPool(view.getDefaultAnswerPoolName());
        questionPackItem.setDefaultTopics(view.getDefaultTopics());
        questionPackItem.setQuestionPackName(view.getQuestionPackName());
        questionPackItem.setDescription(view.getDescription());
    }


    private void incrementPageVars(){
        currentPage++;
        currentQuestion++;
        currentQuestionIndex++;
    }


    private void decrementPageVars(){
        currentPage--;
        currentQuestion--;
        currentQuestionIndex--;
    }


    private void resetPageVars(){
        currentPage = 1;
        currentQuestion = 0;
        currentQuestionIndex = -1;
    }


    private void setPageVarsToCurrentMax(){

        currentPage = questionItems.size() + 1;
        currentQuestion = questionItems.size();
        currentQuestionIndex = questionItems.size() -1;
    }


    private boolean isLastPage(){

        return currentPage == questionItems.size();
    }

}
