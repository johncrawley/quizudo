package com.jacsstuff.quizudo.express.generators;

import com.jacsstuff.quizudo.express.questionset.ChunkEntity;
import com.jacsstuff.quizudo.express.questionset.QuestionSetEntity;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileParserTest {

    private final String DELIMITER = ",,";


    @Test
    public void canParseFileToAGenerator(){
        FileParser fileParser = new FileParser();

        List<String> lines = new ArrayList<>();
        String expectedGeneratorName = "test1";
        String expectedSet1Name = "set1";
        String expectedQuestion1Template = "What is the capital of";
        List<String[]> chunkStrings = new ArrayList<>();
        chunkStrings.add(new String[]{"France", "Paris"});
        chunkStrings.add(new String[]{"Germany", "Berlin"});
        chunkStrings.add(new String[]{"Ireland", "Dublin"});
        lines.add("**Generator: " + expectedGeneratorName);
        lines.add("**Set: " + expectedSet1Name);
        lines.add("**Question: " + expectedQuestion1Template);
        for(String[] chunkString : chunkStrings){
            lines.add(chunkString[0] + DELIMITER + chunkString[1]);
        }

        for(String line: lines){
            fileParser.parse(line);
        }
        fileParser.finish();

        GeneratorEntity generatorEntity = fileParser.getGeneratorEntity();
        assertEquals(expectedGeneratorName, generatorEntity.getGeneratorName());

        List<QuestionSetEntity> questionSetEntities = generatorEntity.getQuestionSetEntities();
        assertEquals(1, questionSetEntities.size());

        QuestionSetEntity qset = questionSetEntities.get(0);
        assertEquals(expectedSet1Name, qset.getName());
        assertEquals(expectedQuestion1Template, qset.getQuestionTemplate());

        List<ChunkEntity> chunkEntities = qset.getChunkEntities();
        assertEquals(chunkStrings.size(), chunkEntities.size());
        for(int i = 0; i < chunkStrings.size(); i++){
            assertEquals(chunkStrings.get(i)[0], chunkEntities.get(i).getQuestionSubject());
            assertEquals(chunkStrings.get(i)[1], chunkEntities.get(i).getAnswer());
        }
    }



}
