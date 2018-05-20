package com.projects.diwanshusoni.studentprofiletracker.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projects.diwanshusoni.studentprofiletracker.Pojos.PojoFaculty;
import com.projects.diwanshusoni.studentprofiletracker.R;

import java.util.ArrayList;

/**
 * Created by Diwanshu Soni on 17-09-2017.
 */

public class C_AdapterListview_FacultyConnect extends ArrayAdapter {

    private Context context;
    private int resource;
    private ArrayList<PojoFaculty> arrayList;

    public C_AdapterListview_FacultyConnect(@NonNull Context context, @LayoutRes int resource, ArrayList arrayList) {
        super(context, resource, arrayList);
        this.context = context;
        this.resource = resource;
        this.arrayList = arrayList;
        Log.d("1234", "C_AdapterListview_FacultyConnect: "+arrayList.size());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resource, null);

        Log.d("1234", "Connect: ");

        TextView tv_name, tv_designation, tv_branch, tv_letter;
        tv_name = view.findViewById(R.id.tv_name_faccultyconnect_listitem_id);
        tv_designation = view.findViewById(R.id.tv_designation_faccultyconnect_listitem_id);
        tv_branch = view.findViewById(R.id.tv_subjects_faccultyconnect_listitem_id);
       // tv_letter = view.findViewById(R.id.tv_profilepic_faccultyconnect_listitem_id);

        //tv_letter.setText(arrayList.get(position).getName().toString().toUpperCase().charAt(0));
        tv_name.setText(arrayList.get(position).getName().toString());
        tv_designation.setText(arrayList.get(position).getDesignation().toString());
        tv_branch.setText(arrayList.get(position).getBranch().toString());

        return view;
    }
}
