package com.simrat.myapplication.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simrat.myapplication.R;
import com.simrat.myapplication.model.NavDrawerItem;

import java.util.Collections;
import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {

    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater layoutInflater;
    private Context context;
    TypedArray icons;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.data = data;
        icons = context.getResources().obtainTypedArray(R.array.nav_drawer_icons);

    }
    public void delete(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public NavigationDrawerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NavigationDrawerAdapter.MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.drawerListIcon.setImageResource(icons.getResourceId(position, -1));
//        if(current.getTitle() == "Home")
//            holder.drawerListIcon.setImageResource(R.drawable.home_icon);
//        else if(current.getTitle() == "My Rides")
//            holder.drawerListIcon.setImageResource(R.drawable.car_icon);
//        else if(current.getTitle() == "Profile")
//            holder.drawerListIcon.setImageResource(R.drawable.account_icon);
//        else if(current.getTitle() == "Settings")
//            holder.drawerListIcon.setImageResource(R.drawable.settings_icon);
//        else holder.drawerListIcon.setImageResource(R.drawable.logout_icon);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView drawerListIcon;

        public MyViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            drawerListIcon = (ImageView) itemView.findViewById(R.id.drawer_list_icon);;
        }
    }
}
