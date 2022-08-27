package com.kamal.tolet.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;


import com.kamal.tolet.R;
import com.kamal.tolet.models.User;
import com.kamal.tolet.services.MyNetworkReceiver;
import com.kamal.tolet.session.SharedPrefManager;
import com.kamal.tolet.utils.Utility;
import com.kamal.tolet.utils.language.LocaleHelper;
import com.kamal.tolet.viewmodels.NotificationViewModel;
import com.kamal.tolet.views.adapters.NotificationAdapter;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;
    private RecyclerView mRecyclerView;
    private NotificationAdapter mAdapter;
    //private ArrayList<User> mArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mNetworkReceiver = new MyNetworkReceiver(this);
        User mUser = SharedPrefManager.getInstance(NotificationActivity.this).getUser();
        mProgress = Utility.showProgressDialog(NotificationActivity.this, getResources().getString( R.string.progress), true);

        mRecyclerView = (RecyclerView) findViewById(R.id.notification_recycler_view);

        //Receive the data and observe the data from view model
        NotificationViewModel mViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class); //Initialize view model
        mViewModel.getNotifications(mUser.getUserAuthId()).observe(this, new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                if (users != null) {
                    //mArrayList.addAll(users);
                    initRecyclerView(users);
                    mAdapter.notifyDataSetChanged();
                    Utility.dismissProgressDialog(mProgress);
                } else {
                    Utility.dismissProgressDialog(mProgress);
                    Utility.alertDialog(NotificationActivity.this, getResources().getString(R.string.msg_no_data));
                }
            }
        });

        //initRecyclerView();
    }

    private void initRecyclerView(ArrayList<User> mArrayList) {
        mAdapter = new NotificationAdapter(this, mArrayList); //mUserViewModel.getUsers().getValue()
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
    }

    //===============================================| Language Change
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    //===============================================| onStart(), onPause(), onResume(), onStop()
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
