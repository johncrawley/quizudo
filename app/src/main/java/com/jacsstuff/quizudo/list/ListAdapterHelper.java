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
    private ListView list;
    private boolean isKeyboardDismissedOnDone = true;

    public ListAdapterHelper(Context context, ListView list, ListActionExecutor actionExecutor){
        this.context = context;
        this.list = list;
        this.actionExecutor = actionExecutor;
    }

    public void setupList(final List<String> items, int layoutRes){
        setupList(items, layoutRes, null);
    }

    public void dismissKeyboardOnDone(boolean isDismissed){
        this.isKeyboardDismissedOnDone = isDismissed;
    }

    public void setupList(final List<String> items, int layoutRes, View noResultsFoundView){
        if(list == null){
            return;
        }
        arrayAdapter = new ArrayAdapter<>(context, layoutRes, items);

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


    public void addToList(String name){
        if (listContains(name)) {
            return;
        }
        arrayAdapter.add(name);
    }


    private boolean listContains(String name) {
        for (int i = 0; i < arrayAdapter.getCount(); i++) {
            if (arrayAdapter.getItem(i).equals(name)) {
                return true;
            }
        }
        return false;
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
                    if(isKeyboardDismissedOnDone) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                    actionExecutor.onTextEntered(v.getText().toString());
                    editText.getText().clear();
                    return true;
                }
                return false;
            }
        });
    }


}
