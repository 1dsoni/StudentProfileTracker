package com.projects.diwanshusoni.studentprofiletracker.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.diwanshusoni.studentprofiletracker.R;
import com.projects.diwanshusoni.studentprofiletracker.adapters.C_adapterGridviewChatter;
import com.projects.diwanshusoni.studentprofiletracker.helpers.Chatter;

import java.util.ArrayList;

public class ChimeraChatActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 1;

    public static String chatId = "";
    private static final String APP_TITLE = "Chimera Chat";
    private Button b_send;
    private EditText et_query;
    private TextView tv_result;
    private Chatter chatter ;

    private static final String SEPARATOR = "__::__";

    private static ArrayList<String> arrayList;
    private static C_adapterGridviewChatter adapterGridviewChatter;
    private ListView gridView;


    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chimera_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        link();
        setUpActionBar();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
        }

        connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        arrayList = new ArrayList<>();
        adapterGridviewChatter = new C_adapterGridviewChatter(ChimeraChatActivity.this, R.layout.chatdialog, arrayList);
        gridView.setAdapter(adapterGridviewChatter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                        && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

                    Toast.makeText(ChimeraChatActivity.this, "Internet not available ", Toast.LENGTH_SHORT).show();
                    return;
                }

                chatter = new Chatter(ChimeraChatActivity.this);
                if (et_query.getText().toString().trim().isEmpty()){
                    et_query.setError("cannot be emtpy");
                    return;
                }
                arrayList.add("user"+SEPARATOR+et_query.getText().toString().trim());
                chatter.setInput(et_query.getText().toString().trim(), arrayList, adapterGridviewChatter);
                adapterGridviewChatter.notifyDataSetChanged();
                try {
                    chatter.execute();
                    arrayList.add("bot"+SEPARATOR+chatter.get());
                    adapterGridviewChatter.notifyDataSetChanged();
                    et_query.setText(null);
                } catch (Exception e) {
                    Log.d("12345", "Exception "+e);
                    e.printStackTrace();
                }
            }
        });
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

    private void link() {
        //tv_result = (TextView) findViewById(R.id.tv_response);
        et_query = (EditText) findViewById(R.id.et_query);

        gridView = (ListView) findViewById(R.id.answer_grid);
    }

    private static void setTextv(String out){
        arrayList.add("bot"+out);
        adapterGridviewChatter.notifyDataSetChanged();
    }
    public static void setAdapterGridviewChatter(String out) {
        setTextv(out);
    }
}
