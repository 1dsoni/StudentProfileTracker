package com.projects.diwanshusoni.studentprofiletracker.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projects.diwanshusoni.studentprofiletracker.R;
import com.projects.diwanshusoni.studentprofiletracker.constants.SharedPrefConstants;
import com.squareup.picasso.Picasso;

public class StudentProfileActivity extends AppCompatActivity {

    private static final String APP_TITLE = "Student Profile";

    private ImageView iv_profilePic;
    private Button b_myInfo, b_courses, b_notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        link();
        listenerSetup();
        setUpActionBar();
        Picasso.with(this).load(getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getString(SharedPrefConstants.STU_PIC, null)).into(iv_profilePic);

    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(APP_TITLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void listenerSetup() {
        b_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentProfileActivity.this, NotificationCentreActivity.class);
                startActivity(intent);
            }
        });

        b_myInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout nameLayout, emailLayout, rollno_layout, branch_layout, sem_layout, contact_layout, address_layout;
                TextView name_student, email_student, rollno_student, branch_student, sem_student, contact_student, address_student;
                nameLayout = (LinearLayout) findViewById(R.id.student_name_layout);
                name_student = (TextView) findViewById(R.id.name_student);
                name_student.setText(getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getString(SharedPrefConstants.STU_NAME, null));
                emailLayout = (LinearLayout) findViewById(R.id.student_email_layout);
                email_student = (TextView) findViewById(R.id.email_student);
                email_student.setText(getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getString(SharedPrefConstants.STU_EMAIL, null));
                rollno_layout = (LinearLayout) findViewById(R.id.student_rollno_layout);
                rollno_student = (TextView) findViewById(R.id.rollno_student);
                rollno_student.setText(getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getString(SharedPrefConstants.STU_ROLLNO, null));
                branch_layout = (LinearLayout) findViewById(R.id.student_branch_layout);
                branch_student = (TextView) findViewById(R.id.branch_student);
                branch_student.setText(getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getString(SharedPrefConstants.STU_BRANCH, null));
                sem_layout = (LinearLayout) findViewById(R.id.student_sem_layout);
                sem_student = (TextView) findViewById(R.id.sem_student);
                sem_student.setText(getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getString(SharedPrefConstants.STU_SEM, null));
                contact_layout = (LinearLayout) findViewById(R.id.student_contact_layout);
                contact_student = (TextView) findViewById(R.id.contact_student);
                contact_student.setText(getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getString(SharedPrefConstants.STU_MOBILE_NO, null));
                address_layout = (LinearLayout) findViewById(R.id.student_address_layout);
                address_student = (TextView) findViewById(R.id.address_student);
                address_student.setText(getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, MODE_PRIVATE).getString(SharedPrefConstants.STU_ADDRESS, null));
                nameLayout.setVisibility(View.VISIBLE);
                emailLayout.setVisibility(View.VISIBLE);
                rollno_layout.setVisibility(View.VISIBLE);
                branch_layout.setVisibility(View.VISIBLE);
                sem_layout.setVisibility(View.VISIBLE);
                contact_layout.setVisibility(View.VISIBLE);
                address_layout.setVisibility(View.VISIBLE);
                b_courses.setVisibility(View.GONE);
                b_notification.setVisibility(View.GONE);
                b_myInfo.setVisibility(View.GONE);
            }
        });
        b_courses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void link() {
        iv_profilePic = (ImageView) findViewById(R.id.iv_student_pic_student_profile_id);
        b_myInfo = (Button) findViewById(R.id.btn_myinfo_student_profile_id);
        b_courses = (Button) findViewById(R.id.btn_mycourses_student_profile_id);
        b_notification = (Button) findViewById(R.id.btn_campusnotifications_student_profile_id);
    }
}
