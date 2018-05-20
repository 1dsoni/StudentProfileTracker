package com.projects.diwanshusoni.studentprofiletracker.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projects.diwanshusoni.studentprofiletracker.R;

import java.util.ArrayList;

/**
 * Created by Diwanshu Soni on 15-09-2017.
 */

public class C_adapterGridviewChatter extends ArrayAdapter {
    private Context context;
    private int resource;
    private ArrayList<String> arrayList;
    private static final String SEPARATOR = "__::__";
    private TextView tv_reply_bot, tv_query_user;

    public C_adapterGridviewChatter(@NonNull Context context, @LayoutRes int resource, ArrayList<String> arrayList) {
        super(context, resource, arrayList);
        this.context = context;
        this.resource = resource;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resource, null);

        tv_reply_bot= view.findViewById(R.id.tv_bot_messsage_id);
        tv_query_user = view.findViewById(R.id.tv_user_messsage_id);

        String[] data = arrayList.get(position).split(SEPARATOR);

        if (data[0].equals("user")){
            tv_query_user.setVisibility(View.VISIBLE);
            tv_query_user.setText(data[1]);
        }else {
            tv_reply_bot.setVisibility(View.VISIBLE);
            tv_reply_bot.setText(data[1]);
        }

        return view;
    }
}
