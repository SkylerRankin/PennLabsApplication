package com.skyle.foodbuildings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BuildingsFragment extends Fragment implements View.OnClickListener {

    public BuildingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle save) {
        super.onCreate(save);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle save) {
        final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_buildings, container, false);

        //set the click listener
        ((Button) linearLayout.findViewById(R.id.search_button)).setOnClickListener(this);
        ((EditText) linearLayout.findViewById(R.id.search_input)).addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            //if the user presses enter, simulate a button click. But if they delete all the text, then do not check if there is a new line character
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    ((Button) linearLayout.findViewById(R.id.search_button)).performClick();
                } else if (s.toString().substring(s.length()-1, s.length()).equals("\n")) {
                    if (s.toString().length() > 0) {
                        ((EditText) linearLayout.findViewById(R.id.search_input)).setText(s.toString().substring(0, s.length() - 1));
                    } else {
                        ((EditText) linearLayout.findViewById(R.id.search_input)).setText("");
                    }
                    ((Button) linearLayout.findViewById(R.id.search_button)).performClick();
                }
            }
        });
        return linearLayout;
    }

    //If there is text, then start the activity to show the building. If not text is typed, then remind that there has to be input
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_button) {
            String query = ((EditText) getActivity().findViewById(R.id.search_input)).getText().toString();
            if (query == null || query.length() == 0) {
                Toast.makeText(getActivity(), "Please enter a building name", Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(getActivity(), BuildingActivity.class);
                i.putExtra(BuildingActivity.QUERY, query);
                getActivity().startActivity(i);
            }
        }
    }
}
