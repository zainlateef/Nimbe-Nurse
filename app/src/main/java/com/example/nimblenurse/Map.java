package com.example.nimblenurse;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.util.Log;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.eddystone.Eddystone;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import java.util.ArrayList;
import android.support.annotation.NonNull;
import android.support.annotation.ColorRes;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttCallback;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static android.content.Intent.ACTION_VIEW;
import static android.text.TextUtils.isDigitsOnly;
import static com.example.nimblenurse.R.id.container;


public class Map extends AppCompatActivity {

    private static final String SELECTED_ITEM = "arg_selected_item";
    private BottomNavigationView mBottomNav;
    private int mSelectedItem;
    private BeaconManager beaconManager;
    private String scanId;
    private final String TAG = "My beacon: ";
    private boolean BeaconFound = false;
    private boolean AlreadyEntered = false;
    private MqttAndroidClient client;
    private String ClientID;
    //private BottomBar mBottomBar;

    private String main_topic = "nurse1";
    private String ServerUri = "tcp://192.168.0.11:1883";


    private String purple_beacon = "ecee698d84f0";
    private String mint_beacon = "21d3402d3588";
    private String blue_beacon = "225d70f53451";
    private boolean purple_found = false;
    private boolean mint_found = false;
    private boolean blue_found = false;
    private boolean purple_already = false;
    private boolean mint_already = false;
    private boolean blue_already = false;
    private String room1 = "0";
    private String room2 = "0";
    private String room3 = "0";


    private String map_url = "http://192.168.0.11:8080";
    private String stream_url= "http://www.twitch.tv/twflem";



    List<String> patient;
    List<String> priority;
    List<String> roomno;
    String[] newpriority;
    String[] newpatient;
    String[] newroomno;
    String bpm;

    FrameLayout frame;

    //WebView map;
    /*TextView room1;
    TextView room2;
    TextView room3;*/
    TableLayout tl;
    List<String> roomValues;

    //Drawable myDrawable;

    WebView stream;

    ListView tasks;
    List<String> myTasks;
    CustomList task_adapter;

    ListView patients;
    List<String> myPatients;
    ArrayAdapter<String> patient_adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        priority=new ArrayList<String>();
        patient=new ArrayList<String>();
        roomno=new ArrayList<String>();


        roomValues = new ArrayList<String>();
        tasks = new ListView(this);
        myTasks = new ArrayList<String>();

        patients = new ListView(this);
        myPatients = new ArrayList<String>();
        myPatients.add("Patient: Martha Lingleton, Pulse: OVER 9000");
        patient_adapter = new ArrayAdapter<String>(this, R.layout.da_items, myPatients);
        patients.setAdapter(patient_adapter);
        tl = new TableLayout(this);

        stream = new WebView(this);
        stream.getSettings().setJavaScriptEnabled(true);

        setContentView(R.layout.activity_map);
        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);

        mBottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectFragment(item);
                        return true;
                    }
                });
        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
        } else {
            selectedItem = mBottomNav.getMenu().getItem(0);
        }
        selectFragment(selectedItem);
        //Allows use of Estimote's SDK. We're using Trenton's particular access key.
        EstimoteSDK.initialize(getApplicationContext(), "trenton-fleming-s-your-own-i9l", "be9707bd2c9c2f8943909f1b47a8fa38");
        beaconManager = new BeaconManager(getApplicationContext());

        //This allows the service to listen for eddystone packets every second.
        beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
            @Override public void onEddystonesFound(List<Eddystone> eddystones) {
                String instance = " ";

                //Wouldn't want to continuously try to suscribe to the same topic over and over.


                for (Eddystone e : eddystones) {
                    instance = e.instance;

                    if (instance.equals(purple_beacon)) {
                        purple_found = true;
                    }

                    if (instance.equals(mint_beacon)) {
                        mint_found = true;
                    }

                    if (instance.equals(blue_beacon)) {
                        blue_found = true;
                    }

                }

                if((!blue_found && !mint_found && !purple_found) || eddystones.isEmpty())
                    BeaconFound = false;
                else
                    BeaconFound = true;

                if(BeaconFound && !AlreadyEntered) {
                    do_Enter(main_topic);
                    AlreadyEntered = true;
                }

                else if(!BeaconFound && AlreadyEntered){
                    do_Exit(main_topic);
                    BeaconFound = false;
                    AlreadyEntered = false;
                }

                if(purple_found && !purple_already) {
                    do_Enter("1");
                    purple_already = true;
                }
                else if(!purple_found && purple_already) {
                    do_Exit("1");
                    purple_already = false;
                }

                if(mint_found && !mint_already) {
                    do_Enter("2");
                    mint_already = true;
                }
                else if(!mint_found && mint_already) {
                    do_Exit("2");
                    mint_already = false;
                }

                if(blue_found && !blue_already) {
                    do_Enter("3");
                    blue_already = true;
                }
                else if(!blue_found && blue_already) {
                    do_Exit("3");
                    blue_already = false;
                }

                purple_found = false;
                mint_found = false;
                blue_found = false;

            }
        });

        //Starts scanning for eddystones
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                scanId = beaconManager.startEddystoneScanning();
            }
        });

        // The Mqtt client that handles sending and recieving packets
        ClientID = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), ServerUri,
                        ClientID);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {}
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                String payload = message.toString();
                //.substring(0,3);
                Log.d(TAG, topic);

                //patient name
                //room number
                //room number
                //priority

                if(payload.contains(".") || isDigitsOnly(payload)){
                    Log.d(TAG, "modify patients");
                    updatePatient("Martha Lingleton\t\tRoom:309\t\t\tBPM:" + payload);
                } else if (payload.contains("-")) {
                    Log.d(TAG, payload);
                    updateMap(message.toString().substring(0,5));

                }
                else if(payload.contains("_")){
                    populateTask(payload);
                }


            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
    }

   /* public void sendNotification(View view) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);

//Create the intent that’ll fire when the user taps the notification//

        Intent intent = new Intent(Map.this,Map.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);

        mBuilder.setSmallIcon(R.drawable.ic_stat_name);
        mBuilder.setContentTitle("My notification");
        mBuilder.setContentText("Hello World!");

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001, mBuilder.build());
    }

*/

    private void populateTask(String message) {


        Fragment frag=null;
        Log.i("Checking", "Entered populate task");
       /* if(message.equals("pop") && !(priority.isEmpty()))
        {
            task_adapter = new CustomList(this, newpriority,newpatient,newroomno);
            tasks=(ListView)findViewById(R.id.task_list);
            tasks.setAdapter(task_adapter);

        }*/
        //Log.i("payload", message);
        Toast.makeText(getApplicationContext(), "Waiting for tasks!", Toast.LENGTH_SHORT).show();
            String[] split = message.split("_");
            //Log.i("Test","testing ");
            // Log.i("priority", split[0]);
            //Log.i("patient", split[1]);
            //Log.i("roomno", split[2]);
            priority.add(split[0]);
            patient.add(split[1]);
            roomno.add(split[2]);
            newpatient = patient.toArray(new String[0]);
            newpriority = priority.toArray(new String[0]);
            newroomno = roomno.toArray(new String[0]);
            task_adapter = new CustomList(this, newpriority, newpatient, newroomno);
            tasks = (ListView) findViewById(R.id.task_list);
            tasks.setAdapter(task_adapter);


        tasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(Map.this, Patient_Detail.class);
                intent.putExtra("Patient", patient.get(position));
                intent.putExtra("Room", roomno.get(position));
                startActivity(intent);

            }
        });

        tasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                Toast.makeText(getApplicationContext(), "You have checked up on " + patient.get(pos)+" in "+roomno.get(pos), Toast.LENGTH_SHORT).show();
                //Log.i("click", list.get(pos));
                priority.remove(pos);
                patient.remove(pos);
                roomno.remove(pos);
                //Log.i("click", list.get(pos));
                newpriority = priority.toArray(new String[0]);
                newpatient = patient.toArray(new String[0]);
                //List<String> list2 = new LinkedList<String>(Arrays.asList(room));
                // list2.remove(pos);
                newroomno = roomno.toArray(new String[0]);

                task_adapter = new CustomList(Map.this, newpriority,newpatient,newroomno);
                tasks=(ListView)findViewById(R.id.task_list);
                tasks.setAdapter(task_adapter);



                return true;
            }
        });


    }

    private void updatePatient(String temp){

        Log.i("update",temp);
        myPatients.set(0, temp);
        patients=(ListView)findViewById(R.id.patientList);
        patient_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myPatients);
        patients.setAdapter(patient_adapter);
       // Martha Lingleton		Room:309			BPM:" + payload);
            bpm= temp.substring(temp.indexOf("B")+3);
        patients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // name=patient[position];
                // Log.i("onlick","click");
                Intent intent = new Intent(Map.this, Patient_Detail.class);
                //  Log.i("onlick2","click2");
                intent.putExtra("Patient", "Martha Lingleton");
                intent.putExtra("Room", "Room:309");
                //intent.putExtra("BPM", bpm);
                // Log.i("onlick2",patient.get(position));

                startActivity(intent);

            }
        });

    }

    private void updateMap(String payload) {
        tl.removeAllViews();
        String[] split = payload.split("-");
        room1 = split[0];
        room2 = split[1];
        room3 = split[2];
        TableRow row1 = new TableRow(this);
        TextView tv1 = new TextView(this);
        View v1 = new View(this);
        v1.setLayoutParams(new TableRow.LayoutParams(120,120));
        v1.setBackgroundColor(Color.BLUE);
        tv1.setText("\nRoom 1: Nurses Nearby: " + room1);
        row1.addView(v1);
        row1.addView(tv1);

        //row1.setBackgroundColor(Color.BLUE);
        TableRow row2 = new TableRow(this);
        TextView tv2 = new TextView(this);
        View v2 = new View(this);
        v2.setLayoutParams(new TableRow.LayoutParams(120,120));
        v2.setBackgroundColor(Color.GREEN);
        tv2.setText("\nRoom 2: Nurses Nearby: " + room2);
        row2.addView(v2);
        row2.addView(tv2);
        //row2.setBackgroundColor(Color.GREEN);
        TableRow row3 = new TableRow(this);
        TextView tv3 = new TextView(this);
        View v3 = new View(this);
        v3.setLayoutParams(new TableRow.LayoutParams(120,120));
        v3.setBackgroundColor(Color.BLUE);
        tv3.setText("\nRoom 3: Nurses Nearby: " + room3);
        row3.addView(v3);
        row3.addView(tv3);
        //row3.setBackgroundColor(Color.BLUE);
        tl.addView(row1);
        tl.addView(row2);
        tl.addView(row3);
        //map.setBackgroundDrawable(myDrawable);
    }

    protected void onResume() {
        super.onResume();

        // We need permissions to scan for eddystone
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                scanId = beaconManager.startEddystoneScanning();
            }
        });

    }

    protected void do_Enter(final String topic){

        Log.d(TAG, "User Entered");

        // Attempt to suscribe to the current building's topic
        int qos = 2;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Subscribed: " + topic);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d(TAG, "Did not suscribe to topic: " + topic);

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    protected void do_Exit(final String topic) {
        Log.d(TAG, "User exited");

        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Unsubscribed from: " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d(TAG, "Did not unsubscribe from:" + topic);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    private void selectFragment(MenuItem item) {
        Fragment frag = null;

        FrameLayout frame = (FrameLayout) findViewById(container);
        frame.removeAllViews();

        switch (item.getItemId()) {
            case R.id.Patients: {
                Log.d(TAG, "Patients");
                frag = MenuFragment.newInstance("Patients Fragment",
                        getColorFromRes(R.color.color_patients));
                //frame.addView(patients);
                break;
            }

            case R.id.Tasks: {
                Log.d(TAG, "Tasks");
                frag = MenuFragment.newInstance("Tasks Fragment",
                        getColorFromRes(R.color.color_tasks));
                Toast.makeText(getApplicationContext(), "Waiting for tasks!", Toast.LENGTH_SHORT).show();
                //frame.addView(tasks);
                break;
            }

            case R.id.Map: {
                Log.d(TAG, "Map");
                frame.addView(tl);
                break;
            }

            case R.id.Stream: {
                Log.d(TAG, "Stream");
                frame.addView(stream);
                stream.loadUrl(stream_url);
                break;
            }

        }

        // update selected item
        mSelectedItem = item.getItemId();

        // uncheck the other items.
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
        }

        // ----------------------------  updateToolbarText(item.getTitle());-------------------

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(container, frag, frag.getTag());
            ft.commit();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        MenuItem homeItem = mBottomNav.getMenu().getItem(0);
        if (mSelectedItem != homeItem.getItemId()) {
            // select home item
            selectFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }
    private int getColorFromRes(@ColorRes int resId) {
        return ContextCompat.getColor(this, resId);
    }
}