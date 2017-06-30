package com.example.nimblenurse;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class Patient_Detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        Intent intent = getIntent();
        final String patient = bundle.getString("Patient");
        final String room = bundle.getString("Room");
        TextView tp = (TextView) findViewById(R.id.patient_name);
        TextView troom = (TextView) findViewById(R.id.room);
        ImageView image2 = (ImageView) findViewById((R.id.imageView2));
        tp.setText(patient);
        //Log.i("room",troom.getText().toString());
        troom.setText(room);
        image2.setImageResource(R.drawable.patients);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
