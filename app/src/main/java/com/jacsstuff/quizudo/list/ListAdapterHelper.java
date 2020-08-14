package com.jacsstuff.quizudo.list;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jacsstuff.quizudo.R;
import java.util.List;

public class ListAdapterHelper {

    private Context context;
    private ListActionExecutor actionExecutor;
    private SimpleItemArrayAdapter arrayAdapter;
    private ListView list;

    public ListAdapterHelper(Context context, ListView list, ListActionExecutor actionExecutor){
        this.context = context;
        this.list = list;
        this.actionExecutor = actionExecutor;
    }


    public boolean contains(String str){
        for(int i=0; i < arrayAdapter.getCount(); i++){
            SimpleListItem item = arrayAdapter.getItem(i);
            if(item == null){
                continue;
            }
            if(item.getName().equals(str)){
                return true;
            }
        }
        return false;
    }

    public void setupList(final List<SimpleListItem> items, int layoutRes, View noResultsFoundView){
        if(list == null){
            return;
        }
        arrayAdapter = new SimpleItemArrayAdapter(context, layoutRes, items);

        AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(position >= items.size()){
                    return false;
                }
                SimpleListItem item = items.get(position);
                actionExecutor.onLongClick(item);
                return true;
            }
        };

        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position >= items.size()){
                    return;
                }
                actionExecutor.onClick(items.get(position));
            }
        };


        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        list.setAdapter(arrayAdapter);
        list.setSelector(R.color.selectedListItemHidden);
        setupEmptyView(noResultsFoundView);
        list.setOnItemLongClickListener(longClickListener);
        list.setOnItemClickListener(clickListener);
    }


    private void setupEmptyView(View noResultsFoundView){
        if(noResultsFoundView == null){
            return;
        }
        list.setEmptyView(noResultsFoundView);
    }


    public void addToList(SimpleListItem item){
        if (contains(item)) {
            return;
        }
        arrayAdapter.add(item);
    }


    public boolean contains(SimpleListItem item) {
        for (int i = 0; i < arrayAdapter.getCount(); i++) {
            SimpleListItem item1 = arrayAdapter.getItem(i);
            if(item1 == null){
                continue;
            }
            String name = item1.getName();
            if (name.equals(item.getName())) {
                return true;
            }
        }
        return false;
    }


    public void setupKeyInput(final EditText editText){
        setupKeyInput(editText, true, false);
    }


    public void setupKeyInput(final EditText editText, final boolean isFieldClearedOnTextInput) {
        setupKeyInput(editText, isFieldClearedOnTextInput, true);
    }


    public void setupKeyInput(final EditText editText, final boolean isFieldClearedOnTextInput, final boolean isKeyboardDismissedOnDone) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm == null){
                        return false;
                    }
                    if(isKeyboardDismissedOnDone) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                    actionExecutor.onTextEntered(v.getId(), v.getText().toString());
                    if(isFieldClearedOnTextInput){
                          editText.getText().clear();
                    }
                    return true;
                }
                return false;
            }
        });
    }


}
