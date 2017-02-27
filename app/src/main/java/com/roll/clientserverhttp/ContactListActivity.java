package com.roll.clientserverhttp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.roll.clientserverhttp.adapters.ContactAdapter;
import com.roll.clientserverhttp.entities.User;
import com.roll.clientserverhttp.model.HttpProvider;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class ContactListActivity extends AppCompatActivity implements ContactAdapter.ViewClickListener {

    private ListView listView;
    private String token;
    private ArrayList<User> users = new ArrayList<>();
    private ContactAdapter adapter;
    private ProgressBar progressBarContact;
    private TextView txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        listView = (ListView) findViewById(R.id.list_contact);
        txtEmpty = (TextView) findViewById(R.id.txt_empty);
        progressBarContact = (ProgressBar) findViewById(R.id.progress_contacts);

        SharedPreferences sharedPreferences = getSharedPreferences("AUTH", MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        users.clear();
        new ContactsAsyncTask().execute();
    }

    @Override
    public void btnViewClick(View view, int position) {
        User user = (User) adapter.getItem(position);
        Intent intent = new Intent(ContactListActivity.this, ViewContactActivity.class);
        intent.putExtra("USER", new Gson().toJson(user));
        startActivity(intent);
    }

    private class ContactsAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarContact.setVisibility(View.VISIBLE);
            if (users.size() == 0) {
                txtEmpty.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "Get all contacts, OK!";

            Request request = new Request.Builder()
                    .header("Authorization", token)
                    .url(HttpProvider.BASE_URL + "/contactsarray")
                    .get()
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.setReadTimeout(15, TimeUnit.SECONDS);
            client.setConnectTimeout(15, TimeUnit.SECONDS);

            try {
                Response response = client.newCall(request).execute();
                if (response.code() < 400) {
                    String jsonResponse = response.body().string();
                    Log.d("Get all contacts", jsonResponse);

                    JSONArray jsonArray = new JSONArray(new JSONObject(jsonResponse).getString("contacts"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        users.add(new Gson().fromJson(jsonArray.getString(i), User.class));
                    }

                } else if (response.code() == 401) {
                    result = "Wrong authorization! empty token!";
                } else {
                    String jsonResponse = response.body().string();
                    Log.d("Get all contacts", jsonResponse);
                    result = "Server ERROR!";
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = "Connection ERROR!";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarContact.setVisibility(View.GONE);
            if ("Get all contacts, OK!".equals(s)) {
                if (users.size() != 0) {
                    txtEmpty.setVisibility(View.INVISIBLE);
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    initAdapter();
                }
            }
        }
    }

    private void getUsers() {
        Gson gson = new Gson();
        users.clear();
        SharedPreferences sharedPreferences = getSharedPreferences(token, MODE_PRIVATE);
        Collection<?> tempList = sharedPreferences.getAll().values();
        for (Object iterator : tempList) {
            users.add(gson.fromJson(String.valueOf(iterator), User.class));
            Collections.sort(users, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
        }

    }

    private void initAdapter() {
        adapter = new ContactAdapter(this, users, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) adapter.getItem(position);
                Toast.makeText(ContactListActivity.this, "Was clicket position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_logout) {
            SharedPreferences sPref = getSharedPreferences("AUTH", MODE_PRIVATE);
            SharedPreferences.Editor editor = sPref.edit();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (item.getItemId() == R.id.item_add) {
            Intent intent = new Intent(this, AddContactActivity.class);
            intent.putExtra("TOKEN", token);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}

