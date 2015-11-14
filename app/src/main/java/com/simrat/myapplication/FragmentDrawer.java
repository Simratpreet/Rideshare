package com.simrat.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simrat.myapplication.adapter.NavigationDrawerAdapter;
import com.simrat.myapplication.data.RideshareDbHelper;
import com.simrat.myapplication.model.NavDrawerItem;
import com.simrat.myapplication.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;




public class FragmentDrawer extends Fragment {

    private String DEBUG_TAG = this.getClass().getName().toString();
    private LinearLayout navHeader;
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private static String[] titles = null;
    private View containerView;
    private FragmentDrawerListener drawerListener;
    static SharedPreferences sharedPreferences;
    static ImageView profilePic;
    private TextView personName;
    private RideshareDbHelper dbHelper;

    public FragmentDrawer(){

    }

    public void setDrawerListener(FragmentDrawerListener listener){
        this.drawerListener = listener;
    }

    public static List<NavDrawerItem> getData(){
        List<NavDrawerItem> data = new ArrayList<>();

        for(int i = 0; i < titles.length; i++){
            NavDrawerItem item = new NavDrawerItem();
            item.setTitle(titles[i]);
            data.add(item);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        dbHelper = new RideshareDbHelper(getContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawer_list);
        profilePic = (ImageView) layout.findViewById(R.id.profile_pic);
        navHeader = (LinearLayout) layout.findViewById(R.id.nav_header_container);
        personName = (TextView) layout.findViewById(R.id.person_name);
        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.container_body, new ProfileFragment()).commit();
            }
        });

        personName.setText(sharedPreferences.getString("Name", "").toUpperCase());
        String token = sharedPreferences.getString("AuthToken", "");
        Log.d(DEBUG_TAG, token);
        User user = dbHelper.getUser(token);

        String path = getContext().getFilesDir() + "/" + token + "dp.jpg";
        File file = new File(path);

        if(file.exists()){
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                profilePic.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
        else{
            path = getContext().getFilesDir() + "/" + token + "dp.png";
            file = new File(path);
            if(file.exists()){
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    profilePic.setImageBitmap(bitmap);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }

        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return layout;
    }
    public static void setProfilePic(){
        String profilePicBitmap = sharedPreferences.getString("ProfilePic", "");
        byte[] decodedPic = Base64.decode(profilePicBitmap, Base64.DEFAULT);
        profilePic.setImageBitmap(BitmapFactory.decodeByteArray(decodedPic, 0, decodedPic.length));
    }

    @TargetApi(11)
    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }
}