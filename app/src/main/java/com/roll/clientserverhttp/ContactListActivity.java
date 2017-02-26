package com.roll.clientserverhttp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class ContactListActivity extends AppCompatActivity implements ContactAdapter.ViewClickListener {

    private ListView listView;
    private String login;
    private ArrayList<User> users = new ArrayList<>();
    private ContactAdapter adapter;
    private ProgressBar progressBarContact;
    private ContactsAsyncTask asyncTask;
    private TextView txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        listView = (ListView) findViewById(R.id.list_contact);
        txtEmpty = (TextView) findViewById(R.id.txt_empty);
        progressBarContact = (ProgressBar) findViewById(R.id.progress_contacts);

        SharedPreferences sharedPreferences = getSharedPreferences("AUTH", MODE_PRIVATE);
        login = sharedPreferences.getString("LOGIN", "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ("".equals(login)) {
            return;
        } else {
            new ContactsAsyncTask().execute(3000);
        }
    }

    @Override
    public void btnViewClick(View view, int position) {
        User user = (User) adapter.getItem(position);
        Intent intent = new Intent(ContactListActivity.this, ViewContactActivity.class);
        intent.putExtra("LOGIN", login);
        intent.putExtra("ID", user.getPhone());
        startActivity(intent);
    }

    private class ContactsAsyncTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarContact.setVisibility(View.VISIBLE);
            if (users.size() == 0) {
                txtEmpty.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(Integer... params) {
            String result = "OK";
            try {
                getUsers();
                Thread.sleep(params[0]);
                if (users.size() == 0) {
                    result = "ERROR";
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarContact.setVisibility(View.GONE);
            if ("OK".equals(s)) {
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
        SharedPreferences sharedPreferences = getSharedPreferences(login, MODE_PRIVATE);
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
            editor.remove("LOGIN");
            editor.commit();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (item.getItemId() == R.id.item_add) {
            Intent intent = new Intent(this, AddContactActivity.class);
            intent.putExtra("LOGIN", login);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}

