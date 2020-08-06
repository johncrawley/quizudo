package com.jacsstuff.quizudo.express.generatorsdetail;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QuestionTextMakerTest {

    private Context context;
    private String placeholder = "##";

    private QuestionTextMaker questionTextMaker;

    @Before
    public void setup(){

        questionTextMaker = new QuestionTextMaker(placeholder);
    }


    @Test
    public void canReplacePlaceholderWithSubject(){

        String questionTemplate = "What is the capital of ##?";
        String country = "France";
        String question = questionTextMaker.getQuestionText(questionTemplate, country);
        String expected = questionTemplate.replace(placeholder, country);
        assertEquals(expected, question);

    }

    @Test
    public void addsSubjectWhenNoPlaceHolderSpecified(){
        String template = "What is the capital of?";
        String country = "Germany";
        String expected = template.substring(0, template.length()-1) + " " + country + "?";
        String question = questionTextMaker.getQuestionText(template, country);
        assertEquals(expected, question);

    }
    @Test
    public void addsSubjectAndQuestMark(){
        String template = "What is the capital of";
        String country = "Italy";
        String expected = template + " " + country + "?";
        String question = questionTextMaker.getQuestionText(template, country);
        assertEquals(expected, question);
    }



    @Test
    public void addsSubjectWithFullStop(){
        String startingStr = "Name the capital of";
        String template = startingStr + ".";
        String country = "Spain";
        String expected = startingStr + " " + country + ".";
        String question = questionTextMaker.getQuestionText(template, country);
        assertEquals(expected, question);
    }

    @Test
    public void subsSubjectWithPlaceholderWithFullStop(){
        String startingStr = "is capital of ___.";
        String template = placeholder + " " + startingStr;
        String country = "Copenhagen";
        String expected = country + " " + startingStr;
        String question = questionTextMaker.getQuestionText(template, country);
        assertEquals(expected, question);
    }

    @Test
    public void emptySubjectReturnsEmptyText(){
        String startingStr = "What is ts capital of ##?";
        String template = placeholder + " " + startingStr;
        String country = "  ";
        String expected = "";
        String question = questionTextMaker.getQuestionText(template, country);
        assertEquals(expected, question);
    }

    @Test
    public void emptyTemplateReturnsSubjectWithQuestionMark(){
        String template = "   ";
        String sum = "2 + 2 =";
        String expected = sum + "?";
        String question = questionTextMaker.getQuestionText(template, sum);
        assertEquals(expected, question);
    }

}
