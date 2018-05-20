package com.projects.diwanshusoni.studentprofiletracker.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.diwanshusoni.studentprofiletracker.Pojos.PojoNotifications;
import com.projects.diwanshusoni.studentprofiletracker.R;
import com.projects.diwanshusoni.studentprofiletracker.helpers.TimeFormat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Diwanshu Soni on 16-09-2017.
 *
 * here the notif centre items list item layout is created
 */

public class C_AdapterGrid_NotifList extends ArrayAdapter {

    private Context context;
    private int res;
    private ArrayList<PojoNotifications> arrayList;

    public C_AdapterGrid_NotifList(@NonNull Context context, @LayoutRes int resource, ArrayList arrayList) {
        super(context, resource, arrayList);
        this.context = context;
        res = resource;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(res, null);

        ImageView iv;
        TextView tv_head, tv_validity, tv_dated, tv_type;
        tv_dated = view.findViewById(R.id.sub_count_grid_item_id);
        tv_head = view.findViewById(R.id.sub_name_grid_item_id);
        tv_validity = view.findViewById(R.id.sub_date_grid_item_id);
        tv_type = view.findViewById(R.id.notificationtype_grid_item_id);
        iv = view.findViewById(R.id.notification_icon_image_id);

        PojoNotifications pojo = arrayList.get(position);

        tv_dated.setText(""+(pojo.getPostedOn()));
        tv_head.setText(pojo.getHeading());
        tv_validity.setText(""+ (pojo.getValidUpto()));
        tv_type.setText(pojo.getType());
        if (pojo.getImageUrl() != null){
            Picasso.with(context).load(pojo.getImageUrl())
                    .resize(250, 250)
                    .into(iv);
        }
        return view;
    }
}
