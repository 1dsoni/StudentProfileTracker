package com.projects.diwanshusoni.studentprofiletracker.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.diwanshusoni.studentprofiletracker.Pojos.PojoNotifications;
import com.projects.diwanshusoni.studentprofiletracker.R;
import com.squareup.picasso.Picasso;

public class NotificationItemInfoActivity extends AppCompatActivity {

    private EditText et_desc;
    private TextView et_links;
    private ImageView iv_image;

    private PojoNotifications pojoNotifications;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_item_info);
        pojoNotifications = (PojoNotifications) getIntent().getSerializableExtra("Myobj");
        link();
        setUpActionBar();
        setUpData();
    }
    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Custom Header");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setUpData() {
        et_desc.setText(pojoNotifications.getDescriptions());
        et_links.setText(pojoNotifications.getUsefulLinks());
        if (pojoNotifications.getImageUrl() != null){
            iv_image.setVisibility(View.VISIBLE);
            Picasso.with(this).load(pojoNotifications.getImageUrl()).into(iv_image);
        }
       // iv_image.setVisibility(View.VISIBLE);
       // iv_image.setBackground(getResources().getDrawable(R.drawable.picc));
    }


    private void link() {
        et_desc = (EditText) findViewById(R.id.et_description_notif_id);
        et_links = (TextView) findViewById(R.id.et_useful_links_notif_id);
        iv_image = (ImageView) findViewById(R.id.iv_image_notif_id);
        gridView = (GridView) findViewById(R.id.notification_list_grid_id);
    }
}
