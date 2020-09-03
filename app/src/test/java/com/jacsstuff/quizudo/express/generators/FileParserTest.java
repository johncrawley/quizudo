package com.jacsstuff.quizudo.express.generators;

import com.jacsstuff.quizudo.express.questionset.ChunkEntity;
import com.jacsstuff.quizudo.express.questionset.QuestionSetEntity;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FileParserTest {

    private final String DELIMITER = ",,";
    private final String GENERATOR_TAG = "**Generator: ";
    private final String SET_TAG = "**Set: ";
    private final String QUESTION_TAG = "**Question: ";
    private FileParser fileParser;
    private List<List<String>> chunkSubjectsLists;
    private List<List<String>> chunkAnswersLists;
    private List<String> lines;
    private List<String> expectedSetNames;
    List<String> expectedQuestionTemplates;

    @Before
    public void init(){
        chunkSubjectsLists = new ArrayList<>();
        chunkAnswersLists = new ArrayList<>();
        fileParser = new FileParser();
        lines = new ArrayList<>();
    }


    @Test
    public void canParseFileToAGenerator(){
        String expectedGeneratorName = "test1";
        expectedSetNames = Arrays.asList("set1");
        expectedQuestionTemplates = Arrays.asList("What is the capital of");
        chunkSubjectsLists.add(Arrays.asList("France", "Germany", "Ireland"));
        chunkAnswersLists.add(Arrays.asList( "Paris", "Berlin", "Dublin"));
        List<String> lines = setupGoodInputLines(expectedGeneratorName);
        boolean result = parseLines(fileParser, lines);
        assertTrue(result);
        assertGeneratorEntity(fileParser, expectedGeneratorName);
    }


    @Test
    public void canParseMultipleQuestionSets(){
        String expectedGeneratorName = "test2";
        setupQuestionTemplatesAndChunkLists();
        lines = setupGoodInputLines(expectedGeneratorName);
        boolean result = parseLines(fileParser, lines);
        assertTrue(result);
        assertGeneratorEntity(fileParser, expectedGeneratorName);
    }


    @Test
    public void canParseMultipleQuestionSetsAndIgnoreBadChunksAndEmptyLines(){

        String expectedGeneratorName = "test3";
        expectedSetNames = Arrays.asList("set1", "set2", "set3");
        expectedQuestionTemplates = Arrays.asList("What is the capital of", "What is the longest river in", "What is the highest mountain in");

        chunkSubjectsLists.add(Arrays.asList("France", "Germany", "Ireland"));
        chunkSubjectsLists.add(Arrays.asList("Germany", "", "Ireland")); // 1 empty question subject here
        chunkSubjectsLists.add(Arrays.asList("","Italy", "Ireland"));        // 1 empty question subject here
        chunkAnswersLists.add(Arrays.asList( "Paris", "Berlin", "Dublin"));
        chunkAnswersLists.add(Arrays.asList("The Danube","The Rhine",  "The Shannon"));
        chunkAnswersLists.add(Arrays.asList("Teide", "",  "Carrauntoohil")); // 1 empty answer here
        lines = setupGoodInputLines(expectedGeneratorName);
        lines.add("");
        lines.add("");
        boolean result = parseLines(fileParser, lines);
        assertTrue(result);
        GeneratorEntity generatorEntity = fileParser.getGeneratorEntity();
        List<QuestionSetEntity> questionSetEntities = generatorEntity.getQuestionSetEntities();
        assertCorrectNumberOfChunks(3, questionSetEntities.get(0));
        assertCorrectNumberOfChunks(2, questionSetEntities.get(1));
        assertCorrectNumberOfChunks(1, questionSetEntities.get(2));
    }


    private void setupQuestionTemplatesAndChunkLists(){
        expectedSetNames = Arrays.asList("set1", "set2", "set3");
        expectedQuestionTemplates = Arrays.asList("What is the capital of", "What is the longest river in", "What is the highest mountain in");
        chunkSubjectsLists.add(Arrays.asList("France", "Germany", "Ireland"));
        chunkSubjectsLists.add(Arrays.asList("Germany", "France", "Ireland"));
        chunkSubjectsLists.add(Arrays.asList("Spain","Italy", "Ireland"));
        chunkAnswersLists.add(Arrays.asList( "Paris", "Berlin", "Dublin"));
        chunkAnswersLists.add(Arrays.asList("The Danube","The Rhine",  "The Shannon"));
        chunkAnswersLists.add(Arrays.asList("Teide", "Monte Bianco",  "Carrauntoohil"));

    }
    @Test
    public void wontParseWithBadGeneratorTag(){
        lines.add("prefix " + GENERATOR_TAG + "generatorName");
        lines.add(SET_TAG + "set1");
        lines.add(QUESTION_TAG + "What is the capital of");
        lines.add("Ireland,,Dublin");
        lines.add("Germany,,Berlin");
        assertFalseResultAndNullGenerator();
    }


    @Test
    public void wontParseIfMissingSetTag(){
        lines.add(GENERATOR_TAG + "generatorName");
        lines.add(QUESTION_TAG + "What is the capital of"); // missing set tag
        lines.add("Ireland,,Dublin");
        lines.add("Germany,,Berlin");
        parseLines(fileParser, lines);
        assertFalseResultAndNullGenerator();
    }


    @Test
    public void wontParseIfQuestionSetTagIsntFollowedByQuestionTag(){
        lines.add(GENERATOR_TAG + "generatorName");
        lines.add(SET_TAG + "set1");
        lines.add("Ireland,,Dublin"); // the parsing should not proceed because there is a missing question template here
        lines.add("Germany,,Berlin");
        parseLines(fileParser, lines);
        assertFalseResultAndNullGenerator();
    }

    @Test
    public void wontParseIfOneQuestionTagFollowsAnother(){
        lines.add(GENERATOR_TAG + "generatorName");
        lines.add(SET_TAG + "set1");
        lines.add(QUESTION_TAG + "What is the capital of");
        lines.add(QUESTION_TAG + "What is the highest mountain in"); // parsing shouldn't proceed, since there is a duplicate question template
        lines.add("Ireland,,Dublin");
        lines.add("Germany,,Berlin");
        parseLines(fileParser, lines);
        assertFalseResultAndNullGenerator();
    }


    @Test
    public void wontParseIfMoreThanOneGeneratorTag(){
        lines.add(GENERATOR_TAG + "generatorName");
        lines.add(GENERATOR_TAG + "anotherGeneratorName");
        lines.add(SET_TAG + "set1");
        lines.add(QUESTION_TAG + "What is the capital of");
        lines.add("Ireland,,Dublin");
        lines.add("Germany,,Berlin");
        parseLines(fileParser, lines);
        assertFalseResultAndNullGenerator();
    }


    private void assertFalseResultAndNullGenerator(){
        boolean result = parseLines(fileParser, lines);
        assertFalse(result);
        assertNull(fileParser.getGeneratorEntity());
    }


    private void assertCorrectNumberOfChunks(int expectedNumber, QuestionSetEntity questionSetEntity){
        List<ChunkEntity> chunkEntities = questionSetEntity.getChunkEntities();
        assertEquals(expectedNumber, chunkEntities.size());
    }


    private void assertGeneratorEntity(FileParser fileParser, String expectedGeneratorName){

        GeneratorEntity generatorEntity = fileParser.getGeneratorEntity();
        assertEquals(expectedGeneratorName, generatorEntity.getGeneratorName());

        List<QuestionSetEntity> questionSetEntities = generatorEntity.getQuestionSetEntities();
        assertEquals(expectedSetNames.size(), questionSetEntities.size());

        for(int i = 0 ; i< questionSetEntities.size(); i++) {
            assertQuestionSetEntity(questionSetEntities.get(i), i);
        }
    }

    private void assertQuestionSetEntity(QuestionSetEntity qset, int i){
        assertEquals(expectedSetNames.get(i), qset.getName());
        assertEquals(expectedQuestionTemplates.get(i), qset.getQuestionTemplate());

        List<String> chunkSubjects = chunkSubjectsLists.get(i);
        List<String> chunkAnswers = chunkAnswersLists.get(i);
        List<ChunkEntity> chunkEntities = qset.getChunkEntities();
        assertEquals(chunkSubjects.size(), chunkEntities.size());
        for(int j = 0; j < chunkEntities.size(); j++){
            assertEquals(chunkSubjects.get(j), chunkEntities.get(j).getQuestionSubject());
            assertEquals(chunkAnswers.get(j), chunkEntities.get(j).getAnswer());
        }
    }

    private boolean parseLines(FileParser parser, List<String> lines){
        for(String line: lines){
            if(!parser.parse(line)){
                break;
            }
        }
       return parser.finish();
    }


    private List<String> setupGoodInputLines(String expectedGeneratorName){
        List<String> lines = new ArrayList<>();
        lines.add(GENERATOR_TAG + expectedGeneratorName);
        for(int i=0; i< expectedSetNames.size(); i++){
            lines.add(SET_TAG + expectedSetNames.get(i));
            lines.add(QUESTION_TAG + expectedQuestionTemplates.get(i));
            List<String> chunkSubjects = chunkSubjectsLists.get(i);
            List<String> chunkAnswers = chunkAnswersLists.get(i);
            for(int j = 0; j < chunkSubjects.size(); j++){
                lines.add(chunkSubjects.get(j) + DELIMITER + chunkAnswers.get(j));
            }
        }
        return lines;
    }


}
