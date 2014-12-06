package app.sample.digevo.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import app.sample.digevo.R;
import app.sample.digevo.adapters.CallAdapter;
import app.sample.digevo.network.entities.Call;

public class CallsListActivity extends ActionBarActivity {

    private ListView mCallsList;
    private CallAdapter mAdapter;
    private ArrayList<Call> mCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calls_list);

        mCalls = (ArrayList<Call>) getIntent().getExtras().getSerializable("calls");

        mCallsList = (ListView) findViewById(R.id.calls_list);
        mAdapter = new CallAdapter(CallsListActivity.this, 0, mCalls);
        mCallsList.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calls_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                CallsListActivity.this.finish();
                return true;
            case android.R.id.home:
                CallsListActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
