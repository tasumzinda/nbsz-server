package com.totalit.nbsz_server.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.totalit.nbsz_server.R;
import com.totalit.nbsz_server.business.domain.Counsellor;
import com.totalit.nbsz_server.business.domain.Donor;

public class DownloadDataActivity extends BaseActivity implements View.OnClickListener{

    private Button save;
    private Counsellor counsellor;
    private String donorNumber;
    private Donor item;
    private Donor holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_data);
        Intent intent = getIntent();
        holder = (Donor) intent.getSerializableExtra("holder");
        counsellor = (Counsellor) intent.getSerializableExtra("counsellor");
        donorNumber = intent.getStringExtra("donorNumber");
        save = (Button) findViewById(R.id.btn_save);
        save.setOnClickListener(this);
        setSupportActionBar(createToolBar("NBSZ"));
        if(savedInstanceState == null){
            syncAppData();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("holder", holder);
        intent.putExtra("counsellor", counsellor);
        intent.putExtra("donorNumber", donorNumber);
        startActivity(intent);
        finish();
    }
}
