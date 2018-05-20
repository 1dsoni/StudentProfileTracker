package com.projects.diwanshusoni.studentprofiletracker.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.diwanshusoni.studentprofiletracker.Pojos.PojoFaculty;
import com.projects.diwanshusoni.studentprofiletracker.Pojos.PojoStudent;
import com.projects.diwanshusoni.studentprofiletracker.R;
import com.projects.diwanshusoni.studentprofiletracker.adapters.C_AdapterListview_FacultyConnect;
import com.projects.diwanshusoni.studentprofiletracker.constants.SharedPrefConstants;
import com.projects.diwanshusoni.studentprofiletracker.constants.constants;
import com.projects.diwanshusoni.studentprofiletracker.helpers.Chatter;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //firebase security
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;


    private ProgressDialog dialog;
    private AlertDialog alertDialog;
    private AlertDialog dialogImagePreview;
    private static final int FINISH =1;
    private static final int DONT_FINISH =0;

    private TextView tv_nameNavHead, tv_emailNavHead;
    private CircleImageView iv_profileNavHead;

    private ImageView iv_imagePreview;

    private String imageURL = "NULL";

    //image
    private static final int GALLERY_REQUEST = 1;
    private Uri imageuri1 = null;

    private Button b_ragging, b_noti, b_fac, b_stu;


    private StorageReference fireStorageRef;
    private FirebaseStorage fireStorage;

    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        link();
        initFirebase();
        new FacultyDetailsDownloader().execute(FirebaseDatabase.getInstance().getReference().child(constants.COLLEGE_UID).child(constants.USERS).child(constants.FACULTY).child(constants.FACULTY_UID), getApplicationContext());

        preferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE);
        if (!preferences.getBoolean(SharedPrefConstants.STU_PROFILE_OFFLINE, false)){
            Log.d("12345", "data saved 1");
            dataOfUserFromFb();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               fireIntent(ChimeraChatActivity.class, DONT_FINISH);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        navigationView.setNavigationItemSelectedListener(this);

        View nav_header = LayoutInflater.from(this).inflate(R.layout.nav_header_user_dashboard, null);
        tv_emailNavHead = nav_header.findViewById(R.id.tv_email_nav_header_id);
        tv_nameNavHead = nav_header.findViewById(R.id.tv_name_nav_header_id);
        iv_profileNavHead = nav_header.findViewById(R.id.iv_profilepic_nav_header_id);
        if (preferences.getBoolean(SharedPrefConstants.STU_PROFILE_OFFLINE, false)){
            Log.d("12345", "data saved 2");
            tv_emailNavHead.setText(preferences.getString(SharedPrefConstants.STU_EMAIL, null));
            tv_nameNavHead.setText(preferences.getString(SharedPrefConstants.STU_NAME, null));
            Picasso.with(this).load(preferences.getString(SharedPrefConstants.STU_PIC, null)).into(iv_profileNavHead);
        }

        navigationView.addHeaderView(nav_header);
        listenerSetup();
    }

    private void dataOfUserFromFb() {
        databaseReference.getRef().child(constants.STUDENTS_EMAIL).getRef().child(auth.getCurrentUser().getEmail().replace(".", "_")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("1234", " why "+dataSnapshot.getValue());
               databaseReference.child(constants.STUDENTS_ROLL).getRef().child(dataSnapshot.getValue(String.class)).getRef()
                       .addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                               PojoStudent pp = dataSnapshot.getValue(PojoStudent.class);
                               Log.d("1234", "Dowanloading data");
                               SharedPreferences preferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE);
                               SharedPreferences.Editor editor = preferences.edit();
                               editor.clear();
                               editor.putString(SharedPrefConstants.STU_NAME, pp.getName());
                               editor.putString(SharedPrefConstants.STU_ADDRESS, pp.getAddress());
                               editor.putString(SharedPrefConstants.STU_BRANCH, pp.getBranch());
                               editor.putString(SharedPrefConstants.STU_MOBILE_NO, pp.getMobileNumber());
                               editor.putString(SharedPrefConstants.STU_EMAIL, pp.getEmail());
                               editor.putString(SharedPrefConstants.STU_SEM, pp.getSemester());
                               editor.putString(SharedPrefConstants.STU_ROLLNO, pp.getRollNo());
                               if (pp.getImageUrl()!= null){
                                   editor.putString(SharedPrefConstants.STU_PIC, pp.getImageUrl());
                               }
                               editor.putBoolean(SharedPrefConstants.STU_PROFILE_OFFLINE, true);
                               editor.commit();
                               Log.d("1234", "Dowanloaded data");


                               if (preferences.getBoolean(SharedPrefConstants.STU_PROFILE_OFFLINE, false)){
                                   Log.d("12345", "data saved 2");
                                   tv_emailNavHead.setText(preferences.getString(SharedPrefConstants.STU_EMAIL, null));
                                   tv_nameNavHead.setText(preferences.getString(SharedPrefConstants.STU_NAME, null));
                                   if (pp.getImageUrl() != null)
                                   Picasso.with(UserDashboardActivity.this).load(preferences.getString(SharedPrefConstants.STU_PIC, null)).into(iv_profileNavHead);
                               }
                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void sendPicOfUserFromStorageToFb() {
        PojoStudent ptemp = new PojoStudent();
        databaseReference.getRef().child(constants.STUDENTS_EMAIL).getRef().child(auth.getCurrentUser().getEmail().replace(".", "_")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                databaseReference.child(constants.STUDENTS_ROLL).getRef().child(dataSnapshot.getValue(String.class)).getRef().child("imageUrl").getRef()
                        .setValue(imageURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(UserDashboardActivity.this, "Check Connection", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            dataOfUserFromFb();
                            Log.d("12345", "6");
                            Toast.makeText(UserDashboardActivity.this, "Done", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addImage() {
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageuri = data.getData();
            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageuri1 = result.getUri();
                iv_profileNavHead.setImageURI(imageuri1);
                iv_imagePreview.setImageURI(imageuri1);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void listenerSetup() {
        iv_profileNavHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("12345", "1");
                addImageProcess();
            }
        });
        b_fac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fireIntent(FacultyConnectActivity.class, DONT_FINISH);
            }
        });

        b_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fireIntent(NotificationCentreActivity.class, DONT_FINISH);
            }
        });

        b_ragging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fireIntent(Contacts.class, DONT_FINISH);
            }
        });

        b_stu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fireIntent(StudentProfileActivity.class, DONT_FINISH);
            }
        });
    }

    private void link() {
        b_fac = (Button) findViewById(R.id.b_facultyconnent_dsh_id);
        b_stu = (Button) findViewById(R.id.b_studentprofile_dsh_id);
        b_noti = (Button) findViewById(R.id.b_notifications_dsh_id);
        b_ragging = (Button) findViewById(R.id.b_ragging_dsh_id);
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(constants.COLLEGE_UID).getRef().child(constants.USERS).getRef().child(constants.STUDENTS).getRef();

        fireStorage = FirebaseStorage.getInstance();
        fireStorageRef = fireStorage.getReference().child(constants.COLLEGE_UID).child(constants.USERS).child(constants.STUDENTS).child(auth.getCurrentUser().getUid());
    }

    private void addImageProcess() {

        Log.d("12345", "2");
        createImagePreviewDialog();
        addImage();
    }

    private void freezeUntillAddQue(String message) {
        dialog = new ProgressDialog(UserDashboardActivity.this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(message);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }
    private void sendPiture(){
        freezeUntillAddQue("Please Wait\nUploading Image");
        fireStorageRef.child(imageuri1.getLastPathSegment()).putFile(imageuri1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    imageURL = ""+task.getResult().getDownloadUrl().toString();

                    Log.d("12345", "5");
                    sendPicOfUserFromStorageToFb();
                    dialog.dismiss();
                }else {
                    Toast.makeText(UserDashboardActivity.this, "Image Not Uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void createImagePreviewDialog() {

        Log.d("12345", "3");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.showselected_image_alert_layout, null);

        iv_imagePreview = (ImageView) view.findViewById(R.id.image_preview_alert_id);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogImagePreview.dismiss();
                imageuri1=null;
            }
        });
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (imageuri1 == null){
                    Toast.makeText(UserDashboardActivity.this, "No Image Selected !", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    Log.d("12345", "4");
                    sendPiture();
                    Toast.makeText(UserDashboardActivity.this, "BOLO TARA RARA", Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setView(view);
        dialogImagePreview = builder.create();
        dialogImagePreview.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_logout){
            logoutAction();
            return true;
        }
        else if (id == R.id.action_exit){
            exitProcess();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void exitProcess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserDashboardActivity.this);
        builder.setMessage(getResources().getString(R.string.alert_exit_activity_user_dashboard));

        builder.setPositiveButton(getResources().getString(R.string.exittext_alert_activity_user_dashboard), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                auth.signOut();
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                alertDialog.dismiss();
                fireIntent( LoginActivity.class, FINISH);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.canceltext_alert_activity_user_dashboard), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void fireIntent( Class gotoClass, int whatToDo) {
        Intent intent = new Intent(UserDashboardActivity.this, gotoClass);
        startActivity(intent);
        if (whatToDo == 1){
            finish();
        }else {

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_studentprofiletracker) {
            fireIntent(StudentProfileActivity.class, DONT_FINISH);
        }
        else if (id == R.id.nav_notificationalert) {
            fireIntent(NotificationCentreActivity.class, DONT_FINISH);
        }
        else if (id == R.id.nav_raggingalert) {
            fireIntent(Contacts.class, DONT_FINISH);
        }
        else if (id == R.id.nav_facultyconnect) {
            fireIntent(FacultyConnectActivity.class, DONT_FINISH);
        }
        else if (id == R.id.nav_chatter) {
            fireIntent(ChimeraChatActivity.class, DONT_FINISH);
        }
        else if (id == R.id.nav_logout){
            auth.signOut();
            exitProcess();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


class FacultyDetailsDownloader extends AsyncTask<Object, Long , Object> {

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
       // arrayList =(ArrayList<PojoFaculty>) objects[0];
        DatabaseReference databaseReference = (DatabaseReference) objects[0];
        context = (Context) objects[1];
        SharedPreferences preferences = context.getSharedPreferences(SharedPrefConstants.SHARED_PREF_CONTACTS_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String ss = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PojoFaculty p2 = snapshot.getValue(PojoFaculty.class);
                    ss = ss +"_"+ p2.getMobileNumber();
                }
                editor.putString("AllContacts", ss);
                editor.commit();
               // c_adapterListview_facultyConnect.notifyDataSetChanged();
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
