package com.example.nimblenurse;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.example.nimblenurse.R.layout.tasks_row;


/**
 * Fragment class for each nav menu item
 */
public class MenuFragment extends Fragment{
    private static final String ARG_TEXT = "arg_text";
    private static final String ARG_COLOR = "arg_color";

    //String[] patient={"john bloo","smith blah","jane blee"};
    //String[] room={"309","315","345"};
    // String[] tasklist={"Patient- dying", "Patient 2 needs water", "Patient 3 needs help turning on the tv"};
    //public static String[] tasklist={"High","Low","Medium"};
    //String[] newpatient;
    //String[] newroom;
    //String[] newtasklist;
    //ListView tlist;
    //CustomList adapter;

    List<String> patient;
    List<String> priority;
    List<String> roomno;
    String[] newpriority;
    String[] newpatient;
    String[] newroomno;
    public static String message;
    ListView tasks;
    CustomList task_adapter;



    private String mText;
    private int mColor;
    public String name;

    private View mContent;
    private TextView mTextView;
    public static String fragtext;
    public ImageView img1;

    public static Fragment newInstance(String text, int color) {
        Fragment frag = new MenuFragment();
        Bundle args = new Bundle();
        fragtext=text;
        args.putString(ARG_TEXT, text);
        args.putInt(ARG_COLOR, color);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;
        tasks = new ListView(getContext());

        if(fragtext.equals("Patients Fragment"))
        {
            view= inflater.inflate(R.layout.activity_menufragment, container, false);

        }

        if(fragtext.equals("Tasks Fragment")) {

            view = inflater.inflate(R.layout.tasks_list, container, false);

           // task_adapter = new CustomList(this, newpriority,newpatient,newroomno);
           // tasks=(ListView)view.findViewById(R.id.task_list);
            //tasks.setAdapter(task_adapter);

        }
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // retrieve text and color from bundle or savedInstanceState
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            mText = args.getString(ARG_TEXT);
            mColor = args.getInt(ARG_COLOR);
        } else {
            mText = savedInstanceState.getString(ARG_TEXT);
            mColor = savedInstanceState.getInt(ARG_COLOR);
        }

        // initialize views
        mContent = view.findViewById(R.id.fragment_content);

        //  mTextView = (TextView) view.findViewById(R.id.text);

        // set text and background color
        //  mTextView.setText(mText);
        mContent.setBackgroundColor(mColor);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_TEXT, mText);
        outState.putInt(ARG_COLOR, mColor);
        super.onSaveInstanceState(outState);
    }


}