package com.jacsstuff.quizudo.list;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by John on 29/12/2016.
 *
 * Simple version of the Question Pack List that only displays the strings and returns selected strings.
 * It also creates display versions of the names (replacing underscores with spaces).
 */
public class DownloadQuestionPackList extends QuestionPackList{


    // maybe put the simple list stuff into a new class
    private String[] questionPackNames;
    private Set<String> selectedItems;
    private Map<String,String> displayNamesMap;

    public DownloadQuestionPackList(Context context, Activity a, ListView listView, Button buttonView, TextView noItemsFoundText){
        super(context, a, listView, buttonView, noItemsFoundText);
        selectedItems = new HashSet<>();
    }


    public void initializeSimpleList(final String[] questionPackNames){
        setupViews(questionPackNames.length > 0);
        setupDisplayNamesMap(questionPackNames);
        this.questionPackNames = getDisplayNames();
        selectedItems.clear();
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_checked, this.questionPackNames);
        setListViewOptions(arrayAdapter);
        setupListClickListener();
        disableButton();
    }

    @Override
    protected void setupListClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String questionPackName = questionPackNames[position];
                if(selectedItems.contains(questionPackName)){
                    selectedItems.remove(questionPackName);
                }
                else{
                    selectedItems.add(questionPackName);
                }
                if(selectedItems.isEmpty()){
                    buttonView.setEnabled(false);
                }
                else{
                    buttonView.setEnabled(true);
                }
            }
        });
    }



    public Set<String> getSelectedStrings(){
        return getQuestionPackRequestNames(selectedItems);

    }


    // We have a displayNames map so that we can display the display names, but when it comes
    // to making the request, the actual name (with underscores) is used.
    private void setupDisplayNamesMap(String[] questionPackNames){

        displayNamesMap = new HashMap<>();
        for(String name : questionPackNames){
            String displayName = name.replaceAll("_", " ");
            displayNamesMap.put(displayName, name);
        }

    }

    private String[] getDisplayNames(){
        Set <String> displayNamesSet = displayNamesMap.keySet();
        String[] displayNamesArray =new String[displayNamesSet.size()];
        int index = 0;
        for(String displayName : displayNamesSet){
            displayNamesArray[index] = displayName;
            index++;
        }
        return displayNamesArray;
    }

    private void log(String msg){
        Log.i("DownloadQP_LIST", msg);
    }

    private Set<String> getQuestionPackRequestNames(Set <String> selectedDisplayNames){
        HashSet<String> requestNames= new HashSet<>();
        StringBuilder str = new StringBuilder("Display Names: ");
        StringBuilder str2 = new StringBuilder("Request Names: ");

        for(String displayName : selectedDisplayNames){
            str.append(displayName);
            str.append(",");
            requestNames.add(displayNamesMap.get(displayName));

        }
        log(str.toString());
        for(String requestName: requestNames){
            str2.append(requestName);
            str2.append(",");
        }
        log(str2.toString());

        log("display Names Map: ");
        for(String key:displayNamesMap.keySet()){
            log(key + " ==> " + displayNamesMap.get(key));
        }

        return requestNames;
    }

}
