package com.totalit.nbsz_server.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.totalit.nbsz_server.R;
import com.totalit.nbsz_server.business.domain.CollectSite;
import com.totalit.nbsz_server.business.domain.Counsellor;
import com.totalit.nbsz_server.business.domain.Donor;
import com.totalit.nbsz_server.business.util.AppUtil;

import java.util.List;

public class SelectCollectSiteActivity extends BaseActivity implements View.OnClickListener {

    private ListView collectSite;
    private Button save;
    private EditText search;
    private ArrayAdapter<CollectSite> collectSiteArrayAdapter;
    private Counsellor counsellor;
    private String donorNumber;
    private Donor item;
    private Donor holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_collect_site);
        Intent intent = getIntent();
        holder = (Donor) intent.getSerializableExtra("holder");
        counsellor = (Counsellor) intent.getSerializableExtra("counsellor");
        donorNumber = intent.getStringExtra("donorNumber");
        collectSite = (ListView) findViewById(R.id.collect_site);
        search = (EditText) findViewById(R.id.search);
        save = (Button) findViewById(R.id.btn_save);
        save.setOnClickListener(this);
        collectSiteArrayAdapter = new ArrayAdapter<>(this, R.layout.check_box_item, CollectSite.getAll());
        collectSite.setAdapter(collectSiteArrayAdapter);
        collectSite.setItemsCanFocus(false);
        collectSite.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        collectSiteArrayAdapter.notifyDataSetChanged();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = charSequence.toString();
                List<CollectSite> list = CollectSite.findByNameLike(name);
                collectSiteArrayAdapter.clear();
                collectSiteArrayAdapter.addAll(list);
                collectSiteArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        setSupportActionBar(createToolBar("NSBZ - Select Collect Site"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if(getCollectSite() != null){
            CollectSite selected = getCollectSite();
            selected.active = 1;
            selected.save();
            //Intent intent = new Intent(this, MainActivity.class);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("holder", holder);
            intent.putExtra("counsellor", counsellor);
            intent.putExtra("donorNumber", donorNumber);
            intent.putExtra("download", "yes");
            startActivity(intent);
            finish();
        }else{
            AppUtil.createShortNotification(this, "Please select a collect site before proceeding");
        }

    }

    public CollectSite getCollectSite(){
        CollectSite item = null;
        for(int i = 0; i < collectSite.getCount(); i++){
            if(collectSite.isItemChecked(i))
                item = collectSiteArrayAdapter.getItem(i);
        }
        return item;
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, SelectUserActivity.class);
        //intent.putExtra("donorNumber", donorNumber);
        startActivity(intent);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
