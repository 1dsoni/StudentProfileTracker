package com.projects.diwanshusoni.studentprofiletracker.Activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.diwanshusoni.studentprofiletracker.Pojos.PojoFaculty;
import com.projects.diwanshusoni.studentprofiletracker.R;
import com.projects.diwanshusoni.studentprofiletracker.constants.SharedPrefConstants;
import com.projects.diwanshusoni.studentprofiletracker.constants.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class AntiRagging extends AppCompatActivity implements ChildEventListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

private GoogleApiClient mGoogleApiClient;
private Location mLocation;                 // to store current location
private LocationManager mLocationManager;

private LocationRequest mLocationRequest;
private com.google.android.gms.location.LocationListener listener;
private long UPDATE_INTERVAL = 500;
private long FASTEST_INTERVAL = 00;

private LocationManager locationManager;

private DatabaseReference databaseReference, d2;
private HashMap<String, String> contactList = new HashMap<>();      //list of contacts to send ragging alert message
private HashMap<String, LatLng> locationList = new HashMap<>();     //list of location of campus's various locations are stored

private static final int PERMISSION_REQUEST_SEND_SMS_CODE = 1;

private Button help;
private Set set;
private String w, messageLOC,message;
private Iterator iter;
    private ArrayList<String> ssss, ssss2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_ragging);
        help = (Button) findViewById(R.id.help_button);
        ssss = new ArrayList<>();
        ssss2 = new ArrayList<>();
        String[] ff = getSharedPreferences(SharedPrefConstants.SHARED_PREF_CONTACTS_NAME, MODE_PRIVATE).getString("AllContacts", null).split("_");
        final String ff2 = getSharedPreferences(SharedPrefConstants.SHARED_PREF_CONTACTS_NAME, MODE_PRIVATE).getString("f1", null);
        String ff22 = getSharedPreferences(SharedPrefConstants.SHARED_PREF_CONTACTS_NAME, MODE_PRIVATE).getString("f2", null);

        if (ff != null || ff22 !=null){
            ssss2.add(ff2);
            ssss2.add(ff22);
        }

        for (String s : ff){
            Log.d("12345", " list number s "+s);
            ssss.add(s);
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //to get friend contacts and authorities
        //while using this in main app set databaseReference to logged in student's emergency contact list
        d2 = FirebaseDatabase.getInstance().getReference(constants.COLLEGE_UID).child(constants.USERS).getRef().child(constants.FACULTY).getRef().child(constants.FACULTY_UID).getRef();

        databaseReference = FirebaseDatabase.getInstance().getReference(constants.COLLEGE_UID).child(constants.USERS).getRef().child(constants.STUDENTS).getRef().child(constants.FRIEND_CONTACT).getRef();
        //while using this in main app set databaseReference to concerened faculty contact no
        databaseReference.addChildEventListener(this);

        //checking permission to send_sms
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS_CODE);
        }

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                if (contactList.isEmpty()) {
                    Toast.makeText(AntiRagging.this, "Emergency Contact List is empty", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                SmsManager smsManager = SmsManager.getDefault();

                if (ssss2.size() == 0 ){
                    Toast.makeText(AntiRagging.this, "frind contacts not found ", Toast.LENGTH_SHORT).show();
                }
                if (ssss.size() == 0 ){
                    Toast.makeText(AntiRagging.this, "faculty contacts not found ", Toast.LENGTH_SHORT).show();
                }
                // if location not available then, only help request will be sent
                if (messageLOC != null)
                    message = "RAGGING ALERT!\nStudent :"+getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getString(SharedPrefConstants.STU_NAME, null)+" at " + "https://www.google.co.in/maps/search/?api=1&query=" + messageLOC + " need your help.";
                else
                    message = "RAGGING ALERT!\nStudent : "+getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getString(SharedPrefConstants.STU_NAME, null)+" need your help";

                //get contact list to send help request
                /*set = contactList.keySet();
                iter = set.iterator();*/
                for (String s : ssss){
                    if (s.length()==10)
                    smsManager.sendTextMessage(s, null, message, null, null);
                }
                for (String ss : ssss2){
                    try{

                        if (ss.length()==10)
                            smsManager.sendTextMessage(ss, null, message, null, null);
                    }
                    catch (Exception e){

                    }
                }

                /*while (iter.hasNext()) {
                    w = iter.next().toString();
                    // to send help request
                    smsManager.sendTextMessage(contactList.get(w), null, message, null, null);
                }*/
                Toast.makeText(AntiRagging.this, "Help request sent", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AntiRagging.this, RecordAudioActivity.class);
                intent.putExtra("StartRecording", true);
                startActivity(intent);
            }
        });
    }

    // methods override for contact list for child to be emergency contact or authorities
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        contactList.put(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        contactList.put(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        contactList.remove(dataSnapshot.getKey());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }
    // above 4 methods are methods of ChildEventListener interface

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(AntiRagging.this, "Error in fetching emergency conatacts", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {// when location updated to  update location to be sent in message
        messageLOC = location.getLatitude()+","+location.getLongitude();
       // mLastLocation = location;
    }

    @Override
    public void onConnected(Bundle bundle) {        // called when device is connected to location provider like gps
        // check permission to access fine and coarse location is granted or not
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){                  //location not set so location update is requested
            startLocationUpdates();
        }
        if (mLocation != null) {                //storing latitude and longitude to be sent with message
            messageLOC = mLocation.getLatitude()+","+mLocation.getLongitude();
        } else {                                //location is not available due to some error
            messageLOC = null;
//            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
//        Log.d("MainAcitivityLog", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.d("MainActivityLog", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {      // to resume location fetch when activity is resumed
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {       // stop to fetch location when activity is not in use
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {                  //to show alert when gps is off

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("SETTINGS");
        builder.setMessage("Enable GPS Provider!\nGo to System Setting?");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {       //opens settings to open gps
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
}
