package com.jacsstuff.quizudo.list;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jacsstuff.quizudo.R;
import java.util.List;

public class ListAdapterHelper {


    private Context context;
    private ListActionExecutor actionExecutor;
    private ArrayAdapter<String> arrayAdapter;

    public ListAdapterHelper(Context context, ListActionExecutor actionExecutor){
        this.context = context;
        this.actionExecutor = actionExecutor;
    }


    public void setupList(ListView list, final List<String> items, View noResultsFoundView){
        if(list == null){
            return;
        }
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, items);

        AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = items.get(position);
                actionExecutor.onLongClick(item);
                return true;
            }
        };

        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actionExecutor.onClick(items.get(position));
            }
        };

        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        list.setAdapter(arrayAdapter);
        list.setSelector(R.color.selectedListItemHidden);
        list.setEmptyView(noResultsFoundView);
        list.setOnItemLongClickListener(longClickListener);
        list.setOnItemClickListener(clickListener);
    }

    public void addToList(String name){
        arrayAdapter.add(name);
    }

    public void setupKeyInput(final EditText editText) {

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm == null){
                        return false;
                    }
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    actionExecutor.onTextEntered(v.getText().toString());
                    editText.getText().clear();
                    return true;
                }
                return false;
            }
        });
    }


}
