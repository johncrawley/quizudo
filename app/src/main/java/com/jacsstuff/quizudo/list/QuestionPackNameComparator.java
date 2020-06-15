package com.jacsstuff.quizudo.list;

import com.jacsstuff.quizudo.model.QuestionPackOverview;

import java.util.Comparator;

/**
 * Created by John on 04/02/2017.
 * Used for sorting the lists of question packs by name
 */
public class QuestionPackNameComparator implements Comparator<QuestionPackOverview>  {

    public int compare(QuestionPackOverview qp1, QuestionPackOverview qp2){
        String name1 = qp1.getName();
        String name2 = qp2.getName();
        return name1.compareToIgnoreCase(name2);
    }

}
