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
import com.kamal.tolet.models.PostAd;
import com.kamal.tolet.services.MyNetworkReceiver;
import com.kamal.tolet.session.SharedPrefManager;
import com.kamal.tolet.utils.Utility;
import com.kamal.tolet.utils.language.LocaleHelper;
import com.kamal.tolet.viewmodels.PostAdViewModel;
import com.kamal.tolet.views.adapters.PostsAdapter;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;
    private RecyclerView mRecyclerView;
    private PostsAdapter mAdapter;
    private ArrayList<PostAd> mArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        mNetworkReceiver = new MyNetworkReceiver(this);
        mProgress = Utility.showProgressDialog(FavoriteActivity.this, getResources().getString( R.string.progress), true);

        //SharedPrefManager.getInstance(FavoriteActivity.this).deleteFavoriteItems();
        ArrayList<String> arrayList = SharedPrefManager.getInstance(FavoriteActivity.this).getFavoriteItems();

        mRecyclerView = (RecyclerView) findViewById(R.id.favorite_recycler_view);

        //Receive the data and observe the data from view model
        PostAdViewModel mPostAdViewModel = ViewModelProviders.of(this).get(PostAdViewModel.class); //Initialize view model

        if (arrayList != null) {
            for (int i=0; i<arrayList.size(); i++) {
                mPostAdViewModel.getPostAdById(arrayList.get(i)).observe(this, new Observer<PostAd>() {
                    @Override
                    public void onChanged(PostAd model) {
                        if (model != null) {
                            mArrayList.add(model);
                            mAdapter.notifyDataSetChanged();
                            Utility.dismissProgressDialog(mProgress);
                        } else {
                            alertMessage();
                        }
                    }
                });
            }
        } else {
            alertMessage();
        }

        initRecyclerView();
    }

    private void alertMessage() {
        Utility.dismissProgressDialog(mProgress);
        Utility.alertDialog(FavoriteActivity.this, getResources().getString(R.string.msg_no_data));
    }

    private void initRecyclerView() {
        mAdapter = new PostsAdapter(this, mArrayList); //mUserViewModel.getUsers().getValue()
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
