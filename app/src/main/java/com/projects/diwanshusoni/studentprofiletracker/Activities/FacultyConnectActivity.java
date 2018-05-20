package com.projects.diwanshusoni.studentprofiletracker.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.diwanshusoni.studentprofiletracker.Pojos.PojoFaculty;
import com.projects.diwanshusoni.studentprofiletracker.Pojos.PojoNotifications;
import com.projects.diwanshusoni.studentprofiletracker.R;
import com.projects.diwanshusoni.studentprofiletracker.adapters.C_AdapterListview_FacultyConnect;
import com.projects.diwanshusoni.studentprofiletracker.constants.SharedPrefConstants;
import com.projects.diwanshusoni.studentprofiletracker.constants.constants;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FacultyConnectActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private static final String PROGRESS_MESSAGE="fetching data \n Please Wait";

    private ListView listView;
    private C_AdapterListview_FacultyConnect c_adapterListview_facultyConnect;
    private ArrayList<PojoFaculty> arrayList ;

    private PojoFaculty p;

    //fire
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private AlertDialog dialogg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_connect);
        link();
        setUpActionBar();
        listenerSetup();
        intitFire();
        FacultyDownloader facultyDownloader = new FacultyDownloader();
        ProgressDialog progressDialog = new ProgressDialog(this);
        Context context = FacultyConnectActivity.this;
        Object[] objects = new Object[10];
        objects[0] = progressDialog;
        facultyDownloader.doInBackground(objects);
        arrayList = new ArrayList<>();
        c_adapterListview_facultyConnect = new C_AdapterListview_FacultyConnect(FacultyConnectActivity.this, R.layout.facultyconnect_listviewitem_layout, arrayList);
        listView.setAdapter(c_adapterListview_facultyConnect);
       // populateDataInBackground();
        populateData();
    }



    private void listenerSetup() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FacultyConnectActivity.this);
                final CharSequence sequence[] = {"Mobile Number","Email Address", "Cancel"};
                builder.setItems(sequence, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (sequence[i] == "Mobile Number"){
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+arrayList.get(position).getMobileNumber()));
                            startActivity(intent);
                        }
                        else if (sequence[i] == "Email Address"){
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri data = Uri.parse("mailto:"+arrayList.get(position).getEmail()+"?subject=" + "Test Subject" + "&body=" + "To\n"+arrayList.get(position).getName()+"\n.<body of mail>."+"\nYours Sincerely,\n"+getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getString(SharedPrefConstants.STU_NAME, null));
                            intent.setData(data);
                            startActivity(intent);
                        }
                        else if (sequence[i] == "Cancel"){
                            dialogg.dismiss();
                        }
                    }
                });
                dialogg = builder.create();
                dialogg.show();
            }
        });
    }

    private void intitFire() {
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(constants.COLLEGE_UID).child(constants.USERS).getRef().child(constants.FACULTY).getRef().child(constants.FACULTY_UID).getRef();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Faculty Connect");
    }

    private void showWaitDialog(){
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(PROGRESS_MESSAGE);
        dialog.show();
    }

    private void populateData() {
        final SharedPreferences.Editor editor = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).edit();
        arrayList.clear();
        showWaitDialog();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PojoFaculty pp = snapshot.getValue(PojoFaculty.class);
                    if ( ! getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getBoolean(SharedPrefConstants.FAC_PROFILE_OFFLINE, false)){
                        editor.clear();
                        editor.putBoolean(SharedPrefConstants.FAC_PROFILE_OFFLINE, true);
                        editor.putString(SharedPrefConstants.FAC_NAME, pp.getName());
                        editor.putString(SharedPrefConstants.FAC_ADDRESS, pp.getAddress());
                        editor.putString(SharedPrefConstants.FAC_BRANCH, pp.getBranch());
                        editor.putString(SharedPrefConstants.FAC_MOBILE_NO, pp.getMobileNumber());
                        editor.putString(SharedPrefConstants.FAC_EMAIL, pp.getEmail());
                    }
                    //editor.putString(SharedPrefConstants.STU_SEM, pp.getSemester());
                    //editor.putString(SharedPrefConstants.STU_ROLLNO, pp.getRollNo());
                    arrayList.add(pp);
                }
                dialog.dismiss();
                c_adapterListview_facultyConnect.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FacultyConnectActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                 dialog.dismiss();
            }
        });
    }

    private void populateDataInBackground() {
       // showWaitDialog();
        arrayList.clear();
        try {
            arrayList = (ArrayList<PojoFaculty>) (new FacultyDownloader().execute(arrayList, databaseReference, c_adapterListview_facultyConnect, this)).get();
            Log.d("1234", " arr");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        c_adapterListview_facultyConnect.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void link() {
        listView = (ListView) findViewById(R.id.listview_facultylist_id);
    }
}

class FacultyDownloader extends AsyncTask<Object, Long , Object>{

    private ArrayList<PojoFaculty> arrayList;
    private PojoFaculty p;
    private C_AdapterListview_FacultyConnect c_adapterListview_facultyConnect;
    private Context context;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        Toast.makeText(context, "Getting List oF Faculties", Toast.LENGTH_SHORT).show();
        Log.d("1234", "onPreExecute: ");
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

      //  Toast.makeText(context, "List oF Faculties Gathered", Toast.LENGTH_SHORT).show();
        Log.d("1234", "onpostExecute: ");
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        arrayList =(ArrayList<PojoFaculty>) objects[0];
        DatabaseReference databaseReference = (DatabaseReference) objects[1];
        c_adapterListview_facultyConnect = (C_AdapterListview_FacultyConnect) objects[2];
        context = (Context) objects[3];
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PojoFaculty p2 = snapshot.getValue(PojoFaculty.class);
                    p = new PojoFaculty();
                    Log.d("1234", "count "+snapshot.getChildrenCount());
                    p.setUID(p2.getUID());
                    p.setName(p2.getName());
                    p.setEmail(p2.getEmail());
                    p.setMobileNumber(p2.getMobileNumber());
                    p.setBranch(p2.getBranch());
                    p.setDesignation(p2.getDesignation());
                    arrayList.add(p2);
                }
                c_adapterListview_facultyConnect.notifyDataSetChanged();
                //   Log.d("1234", "count ");
                //dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                // dialog.dismiss();
            }
        });
        return arrayList;
    }
}