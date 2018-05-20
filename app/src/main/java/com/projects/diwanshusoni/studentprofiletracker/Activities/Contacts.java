package com.projects.diwanshusoni.studentprofiletracker.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.diwanshusoni.studentprofiletracker.Pojos.PojoStudent;
import com.projects.diwanshusoni.studentprofiletracker.R;
import com.projects.diwanshusoni.studentprofiletracker.constants.SharedPrefConstants;
import com.projects.diwanshusoni.studentprofiletracker.constants.constants;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

// to store emergency contacts to firebase
public class Contacts extends AppCompatActivity {

    EditText text_contact1, text_contact2;
    Button submit;
    String contact1, contact2;

    DatabaseReference databaseReference;
    ConnectivityManager connectivityManager;

    private SharedPreferences sharedPreferences;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        sharedPreferences = getSharedPreferences("campusBuddy_antiRagging", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("ADDED_FRIEND_CONTACT", false)) {
            startActivity(new Intent(Contacts.this, AntiRagging.class));
            finish();
        }
        //change path to, student's emergency contacts

        databaseReference = FirebaseDatabase.getInstance().getReference(constants.COLLEGE_UID).child(constants.USERS).getRef().child(constants.STUDENTS).getRef();

        submit = (Button) findViewById(R.id.Submit_button);
        text_contact1 = (EditText) findViewById(R.id.emergen_contact1);
        text_contact2 = (EditText) findViewById(R.id.emergen_contact2);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
        }

        connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact1 = text_contact1.getText().toString().trim();
                contact2 = text_contact2.getText().toString().trim();

                //to check device is connected to internet because without internet data would not be upated to  firbase
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                        && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

                    Toast.makeText(Contacts.this, "Internet not available to update contact list", Toast.LENGTH_SHORT).show();
                    return;
                }

                //checking validity of mobile no
                if (contact1.length() != 10 || contact2.length() != 10 || (!TextUtils.isDigitsOnly(contact1)) || (!TextUtils.isDigitsOnly(contact2))) {
                    Toast.makeText(Contacts.this, "INVALID MOBILE NO.", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences.Editor editor = getSharedPreferences(SharedPrefConstants.SHARED_PREF_CONTACTS_NAME, MODE_PRIVATE).edit();
                editor.putString("f1",contact1);
                editor.putString("f2",contact2);
                editor.commit();

                //to store emergency contacts in contact map
                final HashMap<String, String> contact_map = new HashMap<String, String>();
                contact_map.put("contact1", contact1);
                contact_map.put("contact2", contact2);

                databaseReference.getRef().child(constants.STUDENTS_ROLL)
                        .child(getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getString(SharedPrefConstants.STU_ROLLNO, null))
                        .child(constants.FRIEND_CONTACT).getRef()
                        .setValue(contact_map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Contacts.this, "Error Updating Contacts", Toast.LENGTH_SHORT).show();
                                } else {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("ADDED_FRIEND_CONTACT", true);
                                    editor.commit();
                                    startActivity(new Intent(Contacts.this, AntiRagging.class));
                                    finish();
                                    Toast.makeText(Contacts.this, "Done", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                };
        });
    }
}
