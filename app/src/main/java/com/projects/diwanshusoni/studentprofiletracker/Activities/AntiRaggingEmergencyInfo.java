package com.projects.diwanshusoni.studentprofiletracker.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.projects.diwanshusoni.studentprofiletracker.R;

public class AntiRaggingEmergencyInfo extends AppCompatActivity {

    private Button b_submit, b_sos;
    private EditText et_num1, et_num2;

    //fire
    private String userId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_ragging_emergency_info);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        link();
        listenerSetup();
        setUpActionBar();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void listenerSetup() {
        b_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num1, num2;
                num1 = et_num1.getText().toString();
                num2 = et_num2.getText().toString();
                int valid =0;
                if (num1.length() != 10){
                    valid++;
                    toaster("Error Invalid Number : "+num1);
                }

                if (num2.length() != 10){
                    valid++;
                    toaster("Error Invalid Number : "+num2);
                }

                if (valid>0){
                    return;
                }
            }
        });

        b_sos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(AntiRaggingEmergencyInfo.this, "help pressed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void toaster(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    private void link() {
        b_submit = (Button) findViewById(R.id.btn_submit_antiragging_id);
        b_sos = (Button) findViewById(R.id.btn_sos_antiragging_id);
        et_num1 = (EditText) findViewById(R.id.et_emer1_antiragging_id);
        et_num2 = (EditText) findViewById(R.id.et_emer2_antiragging_id);
    }
}
