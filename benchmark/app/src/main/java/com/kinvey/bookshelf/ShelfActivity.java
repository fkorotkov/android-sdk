package com.kinvey.bookshelf;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.model.User;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.store.UserStore;
import com.kinvey.android.sync.KinveyPullCallback;
import com.kinvey.android.sync.KinveyPullResponse;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.store.StoreType;

import java.io.IOException;

public class ShelfActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final String TAG = "Performance Test: ";
    private final int LIMIT = 10000; //10k items
    private final int MS = 1000000; // to convert nanoseconds to milliseconds

    private Client client;
    private DataStore<HierarchyCache> store;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        client = ((App) getApplication()).getSharedClient();
        store = DataStore.collection(HierarchyCache.COLLECTION, HierarchyCache.class, StoreType.SYNC, client);
    }

    @Override
    protected void onStop() {
        dismissProgress();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shelf, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_pull10k:
                pull(0, 10000);
                break;
            case R.id.action_pull50k:
                pull(0, 50000);
                break;
            case R.id.action_pull100k:
                pull(0, 100000);
                break;
            case R.id.action_login:
                login();
                break;
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pull(final int skip, final int items) {
        showProgress(getResources().getString(R.string.progress_pull));
        final long startTime = System.nanoTime();

        Query query = client.query().setLimit(LIMIT);
        if (skip > 0) {
            query.setSkip(skip);
        }
        store.pull(query, new KinveyPullCallback<HierarchyCache>() {
            @Override
            public void onSuccess(KinveyPullResponse kinveyPullResponse) {
                long finishTime = System.nanoTime();
                System.out.println(TAG + " pull 10k items time: " + (finishTime - startTime)/MS);

                dismissProgress();
                Toast.makeText(ShelfActivity.this, R.string.toast_pull_completed, Toast.LENGTH_LONG).show();
                if (items > 10000 && skip <= items - 20000) {
                    pull(skip + LIMIT, items);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                dismissProgress();
                Toast.makeText(ShelfActivity.this, R.string.toast_pull_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login(){
        showProgress(getResources().getString(R.string.progress_login));
        try {
            UserStore.login(Constants.USER_NAME, Constants.USER_PASSWORD, client, new KinveyClientCallback<User>() {
                @Override
                public void onSuccess(User result) {
                    //successfully logged in
                    dismissProgress();
                    Toast.makeText(ShelfActivity.this, R.string.toast_sign_in_completed, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Throwable error) {
                    dismissProgress();
                    Toast.makeText(ShelfActivity.this, R.string.toast_can_not_login, Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            dismissProgress();
            Toast.makeText(ShelfActivity.this, R.string.toast_unsuccessful, Toast.LENGTH_LONG).show();
        }
    }

    private void logout() {
        showProgress(getResources().getString(R.string.progress_logout));
        UserStore.logout(client, new KinveyClientCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dismissProgress();
                Toast.makeText(ShelfActivity.this, R.string.toast_logout_completed, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                dismissProgress();
                Toast.makeText(ShelfActivity.this, R.string.toast_logout_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
