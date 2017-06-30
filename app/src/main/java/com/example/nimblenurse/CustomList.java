package com.example.nimblenurse;

/**
 * Created by Sarah on 4/11/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[]  patient;
    private final String[] priority;
    private final String[] roomno;

    /*public CustomList(AdapterView.OnItemLongClickListener context,
                      String[] priority, String[] patient, String[] roomno) {
        super((Context) context, R.layout.tasks_row, priority);
        this.context = (Activity) context;
        this.patient = patient;
        this.roomno = roomno;
        this.priority=priority;

    }
    */
    public CustomList(Activity context,
                      String[] priority,String[] patient, String[] roomno) {
        super(context, R.layout.tasks_row, priority);
        this.context = context;
        this.patient = patient;
        this.roomno = roomno;
        this.priority=priority;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.i("View","view");
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.tasks_row, null, true);
        TextView txtpatient = (TextView) rowView.findViewById(R.id.tpatient_name);
        TextView txtroom = (TextView) rowView.findViewById(R.id.troom_no);
        TextView txttask = (TextView) rowView.findViewById(R.id.t_description);
        Log.i("View2","afterfindingids");
        if(priority[position].equals("low"))
         rowView.setBackgroundColor(Color.GREEN);
        // rowView.setBackgroundColor(Color.RED);

        if(priority[position].equals("med"))
            rowView.setBackgroundColor(Color.YELLOW);


        if(priority[position].equals("high"))
            rowView.setBackgroundColor(Color.RED);
        Log.i("View3","generating row view");

        txtpatient.setText(patient[position]);
        txtroom.setText(roomno[position]);
        txttask.setText(priority[position]);
        Log.i("return","returning");
        return rowView;
    }
}