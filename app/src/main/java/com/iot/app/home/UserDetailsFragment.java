package com.iot.app.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v11.GenericAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.iot.app.R;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsFragment extends HBFragment {
    public SwipeRefreshLayout swipeLayout;
    String TAG = this.getClass().getSimpleName();
    Toolbar aUDToolbar;
    PrimePreference rhPreference;
    ImageView aUDBackIcon;
    View progressView;
    RecyclerView recyclerView;
    UserDetailsAdapter mAdapter;
    List<UserDetailsAdapterDataList> dataLists;

    public static UserDetailsFragment newInstance() {
        return new UserDetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_user_detail, container, false);
        aUDToolbar = view.findViewById(R.id.account_user_detail_toolbar);
        getAct().setSupportActionBar(aUDToolbar);
        aUDToolbar.setTitle(R.string.user_details);
        progressView = view.findViewById(R.id.progressView);
        aUDBackIcon = view.findViewById(R.id.account_user_detail_backIcon);
        aUDBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction;
                transaction = ((HomeActivity) getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.account_frame, new MainAccountFragment());
                transaction.commit();
                ((HomeActivity) getActivity()).bottomNavigationView.setVisibility(View.VISIBLE);

            }
        });
        rhPreference = new PrimePreference(getActivity().getApplicationContext());
        recyclerView = view.findViewById(R.id.account_recyclerView);


        recyclerView = view.findViewById(R.id.account_recyclerView);
        dataLists = new ArrayList<>();
//        getAct().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getAct().getSupportActionBar().setDisplayShowHomeEnabled(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new UserDetailsAdapter(getActivity(), dataLists);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        swipeLayout = view.findViewById(R.id.swipe_container);
        progressView = view.findViewById(R.id.progressView);
        addItem();
        progressView.setVisibility(View.GONE);
        recyclerView.setAdapter(mAdapter);
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));
        super.onViewCreated(view, savedInstanceState);
    }

    private void addItem() {

        dataLists.clear();
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                email = FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                role = "User",
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dataLists.add(0, new UserDetailsAdapterDataList(R.drawable.ic_user_id, getString(R.string.user_id), uid));
        dataLists.add(1, new UserDetailsAdapterDataList(R.drawable.ic_name, getString(R.string.name), name));
        dataLists.add(2, new UserDetailsAdapterDataList(R.drawable.ic_email, getString(R.string.email), email));
        dataLists.add(3, new UserDetailsAdapterDataList(R.drawable.ic_role, getString(R.string.role), role));
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onStop() {
        Log.e("PRAMFrag", TAG + " onStop ");

        super.onStop();
        setUserVisibleHint(false);
    }

    class UserDetailsAdapter extends GenericAdapter<UserDetailsAdapterDataList> {

        public UserDetailsAdapter(@NonNull Context context, List<UserDetailsAdapterDataList> list) {
            super(context, list);
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new UserDetailsVH(inflater.inflate(R.layout.view_users_details_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Log.e("Adapter", " onBindViewHolder " + position);
            String data = dataList.get(position).getData();
            String head = dataList.get(position).getHeading();
            int imgId = dataList.get(position).getIconId();
            Log.e("Adapter", " onBindViewHolder " + data);

            UserDetailsVH hold = (UserDetailsVH) holder;

            hold.data.setText(data);
            hold.head.setText(head);
            hold.icon.setImageResource(imgId);
            Log.e("AdapLog", "in the On Item View");
        }

        @Override
        public int getItemCount() {
//        Log.e("Adapter", " Account "+dataList.size());
            return dataList.size();
        }

        class UserDetailsVH extends ItemViewHolder {
            TextView head, data;
            ImageView icon;

            UserDetailsVH(View itemView) {
                super(itemView);
                head = findById(R.id.user_head);
                data = findById(R.id.user_data);
                icon = findById(R.id.icon_set);
            }
        }
    }
}
