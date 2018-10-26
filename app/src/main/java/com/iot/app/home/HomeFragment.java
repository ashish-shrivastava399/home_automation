package com.iot.app.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v11.GenericAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iot.app.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends HBFragment {
    public Toolbar toolbar;
    public ViewPager viewPager;
    public SwipeRefreshLayout swipeLayout;
    String TAG = this.getClass().getSimpleName();
    PrimePreference rhPreference;
    ImageView aUDBackIcon;
    View progressView;
    RecyclerView recyclerView;
    List<HomeDataItems> dataLists;
    RQPreference rqPreference;
    Toolbar aUDToolbar;
    HomeAdapter mAdapter;

    private SearchView.OnQueryTextListener OQTListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    public static HBFragment newInstance() {
        Log.e("Home Instance", "HomeFragment newInstance");
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, TAG + " onActivityCreated");
        View view = inflater.inflate(R.layout.frag_h_home, container, false);
        toolbar = view.findViewById(R.id.account_user_detail_toolbar);
        getAct().setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.home);
        rqPreference = new RQPreference(getCont().getApplicationContext());
        viewPager = view.findViewById(R.id.view_pager);
        progressView = view.findViewById(R.id.progressView);
        rhPreference = new PrimePreference(getCont().getApplicationContext());
        recyclerView = view.findViewById(R.id.account_recyclerView);
        dataLists = new ArrayList<>();
        mAdapter = new HomeAdapter(getCont(), dataLists);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        swipeLayout = view.findViewById(R.id.swipe_container);
        progressView = view.findViewById(R.id.progressView);
        addItem();
        progressView.setVisibility(View.GONE);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    private void addItem() {
        dataLists.add(new HomeDataItems("Light", true, R.drawable.ic_bulb_on, R.drawable.ic_bulb_off));
        dataLists.add(new HomeDataItems("Fan", false, R.drawable.ic_fan_on, R.drawable.ic_fan_off));
        dataLists.add(new HomeDataItems("Smoke Detector", true, R.drawable.ic_smk_on, R.drawable.ic_smk_off));
        dataLists.add(new HomeDataItems("Pump", false, R.drawable.ic_pump_on, R.drawable.ic_pump_off));
        dataLists.add(new HomeDataItems("A.C.", true, R.drawable.ic_ac_on, R.drawable.ic_ac_off));
        mAdapter.notifyDataSetChanged();
    }

    class HomeAdapter extends GenericAdapter<HomeDataItems> {

        HomeAdapter(@NonNull Context context, List<HomeDataItems> list) {
            super(context, list);
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new HomeVH(inflater.inflate(R.layout.view_home_details_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            Log.e("Adapter", " onBindViewHolder " + position);
            String title = dataList.get(position).getTitle();
            final boolean state = dataList.get(position).getState();

            final HomeVH hold = (HomeVH) holder;

            if (state) hold.icon.setImageResource(dataList.get(position).getImgIDOn());
            else hold.icon.setImageResource(dataList.get(position).getImgIDOff());

            hold.title.setText(title);
            hold.switchCompat.setChecked(state);
            hold.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Toast.makeText(context, "Switch is " + isChecked, Toast.LENGTH_SHORT).show();
                    if (isChecked) hold.icon.setImageResource(dataList.get(position).getImgIDOn());
                    else hold.icon.setImageResource(dataList.get(position).getImgIDOff());
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class HomeVH extends ItemViewHolder {
            TextView title;
            SwitchCompat switchCompat;
            ImageView icon;

            HomeVH(View itemView) {
                super(itemView);
                title = findById(R.id.home_item_title);
                icon = findById(R.id.home_item_icon);
                switchCompat = findById(R.id.home_item_switch);
            }
        }
    }
}
