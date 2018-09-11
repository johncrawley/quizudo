package com.jacsstuff.quizudo.list;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.jacsstuff.quizudo.model.QuestionPackOverview;
import com.jacsstuff.quizudo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by John on 27/12/2016.
 *
 * This class groups together a list used to display question pack items,
 *  the textView that is shown when no question pack items are in the list,
 *  and the button that performs the action on the selected question pack items.
 *
 * The action performed by the button will change per activity, so there is a public method
 *  that allows the caller to specify an OnClickListener for it.
 *
 */
public class QuestionPackList {

    protected ListView listView; //the list that contains  the question pack items
    Button buttonView; // the button that initiates the action to be performed on the selected list items
    protected Context context;
    private Map <Integer, Boolean> isIdSelectedMap;
    private Map <String, Boolean> isNameSelectedMap; // for use with downloading question packs based on name
    protected Activity activity;
    private ArrayAdapter <QuestionPackOverview> arrayAdapter;

    public QuestionPackList(Context context, Activity activity, ListView listView, Button buttonView, TextView noItemsTextView){
        this.context = context;
        this.activity = activity;
        isIdSelectedMap = new HashMap<>();
        isNameSelectedMap = new HashMap<>();
        this.listView = listView;
        this.buttonView = buttonView;
        this.listView.setEmptyView(noItemsTextView);
    }


    public void disableButton(){
        buttonView.setEnabled(false);
    }


    public void setButtonOnClick(View.OnClickListener listener){
        buttonView.setOnClickListener(listener);
    }

    public void initializeList(final List<QuestionPackOverview> questionPackOverviews){
        log("Entered InitializeList()-v1");
        setupViews(questionPackOverviews.isEmpty());
        arrayAdapter = new QuestionPackListAdapter(context, android.R.layout.simple_list_item_checked, questionPackOverviews);
        setListViewOptions(arrayAdapter);
        setupListClickListener();
        disableButton();
        arrayAdapter.notifyDataSetChanged();
    }
    public void initializeList(){
        log("Entered InitializeList()-v1");
        List <QuestionPackOverview> questionPackOverviews = new ArrayList<>();
        setupViews(false);
        arrayAdapter = new QuestionPackListAdapter(context, android.R.layout.simple_list_item_checked, questionPackOverviews);
        setListViewOptions(arrayAdapter);
        setupListClickListener();
        disableButton();
        arrayAdapter.notifyDataSetChanged();
    }

    public void updateList(final List<QuestionPackOverview> questionPackOverviews){

        arrayAdapter.clear();
        arrayAdapter.addAll(questionPackOverviews);
       arrayAdapter.notifyDataSetChanged();
    }

    void setupViews(boolean isListEmpty){
        if(isListEmpty){
            setupViewsForEmptyList(); }
    }


    // what happens when there are no items in the list: we display a "nothing found" message instead.
    private void setupViewsForEmptyList(){
        buttonView.setVisibility(View.GONE);
    }

    void setListViewOptions(ArrayAdapter arrayAdapter){
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(arrayAdapter);
        listView.setSelector(R.color.selectedListItemHidden);
    }


    protected void setupListClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                ViewGroup layout = (ViewGroup)view;
                if(layout == null){return;}
                CheckedTextView checkedTextItem = (CheckedTextView)layout.getChildAt(1);
                if(checkedTextItem == null){
                    return;
                }
                checkedTextItem.toggle();
                int questionPackId = (int)view.getTag(R.id.VIEW_TAG_ID);
                String name = (String)view.getTag(R.id.VIEW_TAG_NAME);
                isNameSelectedMap.put(name, checkedTextItem.isChecked());

                isIdSelectedMap.put(questionPackId, checkedTextItem.isChecked());
                setButtonEnabledState();
            }
        });
    }

    private void setButtonEnabledState(){
        if(!isSomethingSelected(isIdSelectedMap)){
            buttonView.setEnabled(false);
        }
        else{
            buttonView.setEnabled(true);
        }


    }

    private static boolean isSomethingSelected(Map<Integer, Boolean> itemsMap){

        for(int key: itemsMap.keySet()){
            if(itemsMap.get(key)){
                return true;
            }
        }
        return false;
    }

    public Set<Integer> getSelectedIds(){
        Set <Integer> selectedIds = new HashSet<>();
        for(int id : isIdSelectedMap.keySet()){
            if(isIdSelectedMap.get(id)){
                selectedIds.add(id);
            }
        }
        return selectedIds;
    }


    public List<String> getSelectedNames(){
        List<String> selectedNames = new ArrayList<>();
        for(String name: isNameSelectedMap.keySet()){
            if(isNameSelectedMap.get(name)){
              selectedNames.add(name);
            }
        }
        return selectedNames;
    }


    private void log(String msg){
        Log.i("QuestionPackList", msg);
    }


}
