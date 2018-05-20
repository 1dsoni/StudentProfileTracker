package com.projects.diwanshusoni.studentprofiletracker.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.projects.diwanshusoni.studentprofiletracker.R;
import com.projects.diwanshusoni.studentprofiletracker.helpers.ValidationHelper;

public class LoginActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String PROGRESS_MESSAGE = "Please Wait \n logging you in.";
    private EditText et_email, et_password;
    private TextInputLayout til_email, til_password;
    private Button signin_btn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     //   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        init();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        listener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void listener() {
        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean v = true;
                if((et_email.getText().toString()).isEmpty()){
                    til_email.setError("Please check again");
                    v = false;
                }
                if((et_password.getText().toString()).isEmpty()){
                    til_password.setError("Please check again");
                    v = false;
                }

                if(!v){
                    return;
                }
                showWaitDialog();
                firebaseAuth.signInWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    dialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Cannot Signin", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    dialog.dismiss();
                                    //start activity here
                                }
                            }
                        });
            }
        });
    }

    private void showWaitDialog(){
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(PROGRESS_MESSAGE);
        dialog.show();
    }

    private void init() {
        et_email = (EditText) findViewById(R.id.email_edit_text);
        et_password = (EditText) findViewById(R.id.pass_edit_text);
        til_email = (TextInputLayout) findViewById(R.id.email_input_text_layout);
        til_password = (TextInputLayout) findViewById(R.id.pass_text_input_layout);
        signin_btn = (Button) findViewById(R.id.signin_btn);
    }
}
