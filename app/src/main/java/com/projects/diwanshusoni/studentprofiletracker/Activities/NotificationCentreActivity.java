package com.projects.diwanshusoni.studentprofiletracker.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.diwanshusoni.studentprofiletracker.Pojos.PojoNotifications;
import com.projects.diwanshusoni.studentprofiletracker.R;
import com.projects.diwanshusoni.studentprofiletracker.adapters.C_AdapterGrid_NotifList;
import com.projects.diwanshusoni.studentprofiletracker.constants.constants;
import java.util.ArrayList;

public class NotificationCentreActivity extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<PojoNotifications> arrayList;
    private C_AdapterGrid_NotifList c_adapterGrid_notifList;

    private ProgressDialog dialog;
    private static final String PROGRESS_MESSAGE="fetching data \n Please Wait";

    //fire
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private PojoNotifications p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_centre);
        link();
        setUpActionBar();
        listenerSetup();
        intitFire();

        arrayList = new ArrayList<>();
        c_adapterGrid_notifList = new C_AdapterGrid_NotifList(NotificationCentreActivity.this, R.layout.notif_griditem_notif_list_layout, arrayList);
        gridView.setAdapter(c_adapterGrid_notifList);
        populateData();

    }

    private void intitFire() {
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(constants.COLLEGE_UID).child(constants.NOTIFICATIONS).getRef();
    }

    private void populateData() {
        showWaitDialog();
        arrayList.clear();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    arrayList.add(snapshot.getValue(PojoNotifications.class));
                    c_adapterGrid_notifList.notifyDataSetChanged();
                }
             //   Log.d("1234", "count ");
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NotificationCentreActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void showWaitDialog(){
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(PROGRESS_MESSAGE);
        dialog.show();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Notification Centre");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void listenerSetup() {
       gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Intent intent = new Intent(NotificationCentreActivity.this, NotificationItemInfoActivity.class);
               intent.putExtra("Myobj",arrayList.get(i));
               startActivity(intent);
           }
       });
    }

    private void fireIntent(Class gotoClass) {
        Intent intent = new Intent(NotificationCentreActivity.this, gotoClass);
        startActivity(intent);
    }

    private void link() {
        gridView = (GridView) findViewById(R.id.notification_list_grid_id);
    }

}
