package com.kinvey.bookshelf;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.store.DataStore;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.store.StoreType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.kinvey.java.store.BaseDataStore.MS;

public class CacheBenchmarkActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "Cache test";

    private EditText getItems;
    private EditText createItems;

    private Client client;
    private DataStore<Model> store;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_benchmark);

        createItems = (EditText) findViewById(R.id.create_items_et);

        findViewById(R.id.get_items_all).setOnClickListener(this);
        findViewById(R.id.get_items_all_with_query).setOnClickListener(this);
        findViewById(R.id.get_items_one).setOnClickListener(this);
        findViewById(R.id.create_items).setOnClickListener(this);

        client = ((App) getApplication()).getSharedClient();
        store = DataStore.collection(Model.COLLECTION, Model.class, StoreType.SYNC, client);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_items_one:
                getOneItem();
                break;
            case R.id.get_items_all:
                getItemsAll();
                break;
            case R.id.get_items_all_with_query:
                getItemsAllByQuery();
                break;
            case R.id.create_items:
                try {
                    createItems(Integer.parseInt(createItems.getText().toString()));
                } catch (IOException e) {
                    dismissProgress();
                    e.printStackTrace();
                }
                break;
        }
    }

    public void getOneItem() {
        showProgress("Finding one item");
        final long startTime = System.nanoTime();
        Query query = client.query().equals("firstName", "First_1000");
        store.find(query, new KinveyListCallback<Model>() {
            @Override
            public void onSuccess(List<Model> list) {
                long finishTime = System.nanoTime();
                System.out.println(TAG + " Get " + list.size() +" item(s) time: " + (finishTime - startTime)/MS);
                dismissProgress();
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.fillInStackTrace();
                dismissProgress();
            }
        });
    }

    public void getItemsAll() {
        showProgress("Finding all items");
        final long startTime = System.nanoTime();
        store.find(new KinveyListCallback<Model>() {
            @Override
            public void onSuccess(List<Model> list) {
                long finishTime = System.nanoTime();
                System.out.println(TAG + " Get " + list.size() +" item(s) time: " + (finishTime - startTime)/MS);
                dismissProgress();
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.fillInStackTrace();
                dismissProgress();
            }
        });
    }

    public void getItemsAllByQuery() {
        showProgress("Finding all items by query");
        final long startTime = System.nanoTime();
        Query query = client.query();
        store.find(query, new KinveyListCallback<Model>() {
            @Override
            public void onSuccess(List<Model> list) {
                long finishTime = System.nanoTime();
                System.out.println(TAG + " Get " + list.size() +" item(s) time: " + (finishTime - startTime)/MS);
                dismissProgress();
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.fillInStackTrace();
                dismissProgress();
            }
        });
    }

    public void createItems(int number) throws IOException {
        showProgress(getString(R.string.creating_items));
        if (number > 0) {
            List<Model> models = new ArrayList<>();
            Model model = null;
            for (int i = 0; i < number; i++) {
                model = new Model();
                model.age = 18;
                model.year = 2018;
                model.firstName = "First_" + i;
                model.secondName = "Second_" + i;
                model.thirdName = "Third_" + i;
                model.title = "Title";
                model.department = "Department";
                model.price = "Price";
                model.currency = "currency";
                model.company = "1";
                model.testField1 = "testField1";
                model.testField2 = "testField2";
                model.testField3 = "testField3";
                model.testField4 = "testField4";
                model.testField5 = "testField5";
                model.testField6 = "testField6";
                model.testField7 = "testField7";
                model.testField8 = "testField8";
                model.testField9 = "testField9";
                model.testField10 = "testField10";
                models.add(model);
            }
            System.out.println("Successful created: " + number + " items");
            store.save(models, new KinveyClientCallback<List<Model>>() {
                @Override
                public void onSuccess(List<Model> models) {
                    dismissProgress();
                    System.out.println("Successful saved");
                }

                @Override
                public void onFailure(Throwable throwable) {
                    dismissProgress();
                    System.out.println("Unsuccessful saved");
                    throwable.fillInStackTrace();
                }
            });
        }

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
}
