package com.kamal.tolet.views.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kamal.tolet.R;
import com.kamal.tolet.services.MyNetworkReceiver;
import com.kamal.tolet.utils.Utility;
import com.kamal.tolet.utils.language.LocaleHelper;

public class AboutActivity extends AppCompatActivity {

    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mNetworkReceiver = new MyNetworkReceiver(this);
        //mProgress = Utility.showProgressDialog(AboutActivity.this, getResources().getString( R.string.rotate_progress), false);
        //Utility.dismissProgressDialog(mProgress);

        ((TextView) findViewById(R.id.credit_by)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utility.aboutMe(AboutActivity.this);
                return true;
            }
        });
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
