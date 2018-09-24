package com.skyle.foodbuildings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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

        //set the click listeners
        ((Button) linearLayout.findViewById(R.id.search_button)).setOnClickListener(this);
        ((ListView) linearLayout.findViewById(R.id.recents_list)).setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.recent_search_view, R.id.recents_item, RecentSearchesData.getArray()));
        ((ListView) linearLayout.findViewById(R.id.recents_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //Use the same logic as when the search button is clicked; start a new intent and pass the title of the building
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String query = RecentSearchesData.data.get(position);
                RecentSearchesData.data.add(0, query);
                if (RecentSearchesData.data.size() > 10) {
                    RecentSearchesData.data = RecentSearchesData.data.subList(0, 9);
                }
                Intent i = new Intent(getActivity(), BuildingActivity.class);
                i.putExtra(BuildingActivity.QUERY, query);
                getActivity().startActivity(i);
            }
        });
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
                RecentSearchesData.data.add(0, query);
                if (RecentSearchesData.data.size() > 10) {
                    RecentSearchesData.data = RecentSearchesData.data.subList(0, 9);
                }
                Intent i = new Intent(getActivity(), BuildingActivity.class);
                i.putExtra(BuildingActivity.QUERY, query);
                getActivity().startActivity(i);
            }
        }
    }

    //when you leave the fragment to see the BuildingActivity and then return, update the recently searched list with the new information, since onCreate isn't necessarily called again
    @Override
    public void onResume() {
        super.onResume();
        ((ListView) getActivity().findViewById(R.id.recents_list)).setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.recent_search_view, R.id.recents_item, RecentSearchesData.getArray()));
    }
}
