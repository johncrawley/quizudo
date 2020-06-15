package com.jacsstuff.quizudo.utils;

import android.util.Log;

import com.jacsstuff.quizudo.model.QuestionPackDbEntity;
import com.jacsstuff.quizudo.model.Question;
import com.jacsstuff.quizudo.model.QuestionPackOverview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 30/08/2016.
 *
 * Parses questions and question packs in json and back
 */
public class JSONParser {

    private static final String TOPICS_DELIMITER = ",";
    private final String QUESTION_TEXT = "questionText";
    private final String TOPICS = "topics";
    private final String CORRECT_ANSWERS = "correctAnswers";
    private final String ANSWER_CHOICES = "answerChoiceViews";

    private final String NAME = "name";
    private final String QUESTIONS = "questions";
    private final String QUESTION_PACKS = "questionPacks";
    private final String DESCRIPTION = "description";
    private final String AUTHOR = "author";
    private final String DATE = "date";
    private final String VERSION = "version";
    private final String NUMBER_OF_QUESTIONS = "numberOfQuestions";
    private final String ANSWER_POOL_NAME = "answerPool";

    private final String TRIVIA = "trivia";

    public void log(String msg){
        Log.i("parseFile()", msg);
    }

    public QuestionPackDbEntity parseFile(String filepath){

        // try(InputStream inputStream = new FileInputStream(filepath);
        // JsonReader reader = Json.createReader(inputStream)){

        QuestionPackDbEntity questionPack = new QuestionPackDbEntity();
        try{
            InputStream inputStream = new FileInputStream(filepath);
            StringBuilder str = new StringBuilder();
            int input;
            while((input = inputStream.read()) != -1){
                str.append((char)input);
            }
            inputStream.close();
            questionPack = parseString(str.toString());

        }

        catch(IOException e){
            System.out.println(e.getMessage());

        }
        return questionPack;
    }

    private QuestionPackDbEntity parseString(String input){

        QuestionPackDbEntity questionPack = new QuestionPackDbEntity();
        Question question;

        try{
            JSONObject quizObject = new JSONObject(input);
            JSONArray results = quizObject.getJSONArray(QUESTIONS);

            questionPack.setName(quizObject.getString(NAME));
            questionPack.setDescription(quizObject.getString(DESCRIPTION));

            for(int i=0; i<results.length(); i++){
                JSONObject result = results.getJSONObject(i);
                question = new Question();
                question.setTopics(parseValues(result, TOPICS));
                question.setAnswerChoices(parseValues(result, ANSWER_CHOICES));
                question.setCorrectAnswer(parseValues(result, CORRECT_ANSWERS).get(0));
                question.setTrivia(result.getString(TRIVIA));
                question.setQuestionText(result.getString(QUESTION_TEXT));
                questionPack.addQuestion(question);
            }
        }catch(JSONException e){
            Log.i("JSON EXCEPTION","Oh well. Unable to read JSON string: " + input);
        }
        return questionPack;
    }

    public List<QuestionPackOverview> parseQuestionPackOverviews(String input){
        List<QuestionPackOverview> questionPackOverviews = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(input);
            for(int i=0; i< array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");
                String author = obj.getString("author");
                int numberOfQuestions = obj.getInt("numberOfQuestions");
                if(name.isEmpty()){
                    continue;
                }
                questionPackOverviews.add(new QuestionPackOverview(name, author, numberOfQuestions,i));

            }
        }catch(JSONException e){
             e.printStackTrace();
        }
        return questionPackOverviews;
    }

    public Question parseQuestion(String json){
        Question question = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            question = new Question();

            question.setQuestionText(jsonObject.getString(QUESTION_TEXT));
            question.setCorrectAnswer(parseValues(jsonObject, CORRECT_ANSWERS).get(0));
            question.setAnswerChoices(parseValues(jsonObject, ANSWER_CHOICES));
            question.setTrivia(jsonObject.getString(TRIVIA));
            question.setTopics(parseValues(jsonObject, TOPICS));

        }catch (JSONException e){
            Log.i("JSON Exception", "Unable to parse question from : " +  json);
        }
        return question;
    }


    private Question parseQuestion(JSONObject jsonObject){
        Question question = null;
        try {
            question = new Question();

            question.setQuestionText(jsonObject.getString(QUESTION_TEXT));
            question.setCorrectAnswer(parseValues(jsonObject, CORRECT_ANSWERS).get(0));
            question.setAnswerChoices(parseValues(jsonObject, ANSWER_CHOICES));
            question.setTrivia(jsonObject.getString(TRIVIA));
            question.setTopics(parseValues(jsonObject, TOPICS));
            question.setAnswerPoolName(jsonObject.getString(ANSWER_POOL_NAME));

        }catch (JSONException e){
            Log.i("JSON Exception", "Unable to parse question from : " + jsonObject.toString());
        }
        return question;
    }

    private String getTopicsFromQuestion(JSONObject question){

        List <String> topicsList = parseValues(question, TOPICS);
        StringBuilder topics = new StringBuilder();
        for(String topic : topicsList){
            topics.append(topic);
            topics.append(TOPICS_DELIMITER);
        }
        return topics.toString();
    }

    public List<QuestionPackDbEntity> parseQuestionPacksFromJson(String data){

        List<QuestionPackDbEntity> questionPackDbEntities = new ArrayList<>();
        try {
            JSONObject quizObject = new JSONObject(data);

            JSONArray questionPacks = quizObject.getJSONArray(QUESTION_PACKS);
            for( int i = 0; i< questionPacks.length(); i ++){
                Log.i("json parser", "data: " + data);

                JSONObject qpObject = questionPacks.getJSONObject(i);
                QuestionPackDbEntity questionPackDbEntity = new QuestionPackDbEntity(
                        qpObject.getString(NAME),
                        qpObject.getString(AUTHOR),
                        qpObject.getString(DESCRIPTION),
                        qpObject.getString(DATE),
                        System.currentTimeMillis(),
                        qpObject.getInt(NUMBER_OF_QUESTIONS),
                        qpObject.getInt(VERSION));

                JSONArray questions = qpObject.getJSONArray(QUESTIONS);

                for(int j = 0; j < questions.length(); j++){
                    questionPackDbEntity.addQuestion(parseQuestion(questions.getJSONObject(j)));
                }
                questionPackDbEntities.add(questionPackDbEntity);
            }
        }catch(JSONException e){
            Log.i("JSON EXCEPTION","Unable to read JSON string: " + data);
            e.printStackTrace();
        }
        return questionPackDbEntities;
    }

    public String generateJsonQuestionString(String questionText, String correctAnswer, String trivia, String topics, String... incorrectAnswers){
        String jsonString = "";
        JSONObject obj = new JSONObject();
        JSONArray correctAnswers = new JSONArray();
        JSONArray answerChoices = new JSONArray();
        try {
            correctAnswers.put(correctAnswer);
            for(String incorrectAnswer : incorrectAnswers){
                answerChoices.put(incorrectAnswer);
            }

            obj.put(CORRECT_ANSWERS, correctAnswers);
            obj.put(ANSWER_CHOICES, answerChoices);
            obj.put(QUESTION_TEXT, questionText);
            obj.put(TRIVIA, trivia);
            obj.put(TOPICS, topics);
            jsonString = obj.toString();

        }catch(JSONException e){
            Log.i("JSONPARSER", "problem when creating JsonString from question data: " + e.getMessage());
            e.printStackTrace();
        }
        return jsonString;
    }

    private List<String> parseValues(JSONObject result, String arrayName){

        List<String> valuesList = new ArrayList<>();
        try {
            JSONArray jsonValueArray = result.getJSONArray(arrayName);
            if(jsonValueArray !=null){
                for(int i=0; i<jsonValueArray.length();i++){
                    String value = jsonValueArray.getString(i).trim();

                    value = value.trim();
                    if (value.startsWith("\"")) {
                        value = value.substring(1);
                    }
                    if (value.endsWith("\"")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    if (!value.equals("")) {
                        valuesList.add(value);
                    }
                }
            }

        }catch(JSONException e){
            Log.i("JSONEXCEPTION:", " Unable to parseFile json list: " + arrayName);
        }
        return valuesList;
    }



}
