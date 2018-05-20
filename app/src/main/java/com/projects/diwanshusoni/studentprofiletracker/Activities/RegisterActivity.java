package com.projects.diwanshusoni.studentprofiletracker.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.projects.diwanshusoni.studentprofiletracker.Pojos.UserInfoPojo;
import com.projects.diwanshusoni.studentprofiletracker.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private Button b_register;
    private CircleImageView circleImageView;
    private EditText et_name, et_email, et_mobileNumber, et_collegeName;
    private String imageUrl;
    private Uri imageuri1 = null;

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage fireStorage;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initFire();
        link();
        listenerSetup();
        setUpActionBar();
    }

    private void initFire() {

    }

    private void listenerSetup() {
        b_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerProcess();
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });
    }

    private void registerProcess() {
        String email, mobileNumber, collegeId;

        email = et_email.getText().toString();
        mobileNumber = et_mobileNumber.getText().toString();
        collegeId = et_collegeName.getText().toString();
        int valid = 0;
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            toaster("Error Invalid Email");
            valid++;
        }

        if (mobileNumber.length() != 10){
            toaster("Error Invalid Mobile Number");
            valid++;
        }

        if (valid>0){
            return;
        }

        if (imageuri1 != null){
            sendToStorage();
        }

        UserInfoPojo pojo = new UserInfoPojo();
        pojo.setEmail(email);
        pojo.setCollegeId(collegeId);
        pojo.setPhone(mobileNumber);
        pojo.setImageUrl(imageUrl);

    }

    private void sendToStorage() {

    }

    private void toaster(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void link() {
        b_register = (Button) findViewById(R.id.btn_register_registeractivity_id);
        circleImageView = (CircleImageView) findViewById(R.id.circle_iv_profilepic_registeractivity_id);
        et_name = (EditText) findViewById(R.id.et_username_registeractivity_id);
        et_email = (EditText) findViewById(R.id.et_email_registeractivity_id);
        et_mobileNumber = (EditText) findViewById(R.id.et_mobilenumber_registeractivity_id);
        et_collegeName = (EditText) findViewById(R.id.et_collegeid_registeractivity_id);
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
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageuri1 = result.getUri();
                circleImageView.setImageURI(imageuri1);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
