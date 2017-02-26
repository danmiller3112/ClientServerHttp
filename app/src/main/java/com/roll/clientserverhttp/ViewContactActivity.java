package com.roll.clientserverhttp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.roll.clientserverhttp.entities.User;

public class ViewContactActivity extends AppCompatActivity {

    private EditText nameView, emailView, phoneView, descView;
    private ProgressBar progressView;
    private String id, login, jsonUser;
    private String name, email, phone, desc;
    private MenuItem editItem, saveItem;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        nameView = (EditText) findViewById(R.id.view_name);
        emailView = (EditText) findViewById(R.id.view_email);
        phoneView = (EditText) findViewById(R.id.view_phone);
        descView = (EditText) findViewById(R.id.view_desc);
        progressView = (ProgressBar) findViewById(R.id.progress_view);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            id = intent.getExtras().getString("ID", "");
            login = intent.getExtras().getString("LOGIN", "");
        }

        SharedPreferences sPref = getSharedPreferences(login, MODE_PRIVATE);
        jsonUser = sPref.getString(id, "");
        Gson gson = new Gson();
        user = gson.fromJson(jsonUser, User.class);

        nameView.setText(user.getName());
        emailView.setText(user.getEmail());
        phoneView.setText(user.getPhone());
        descView.setText(user.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        editItem = menu.findItem(R.id.item_edit);
        saveItem = menu.findItem(R.id.item_save);
        saveItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_edit:
                editItem.setVisible(false);
                saveItem.setVisible(true);
                nameView.setEnabled(true);
                emailView.setEnabled(true);
                phoneView.setEnabled(true);
                descView.setEnabled(true);
                break;
            case R.id.item_save:
                phone = String.valueOf(phoneView.getText());
                if ("".equals(phone)) {
                    Toast.makeText(ViewContactActivity.this, "Phone number is EMPTY!!!", Toast.LENGTH_LONG).show();
                } else {
                    editItem.setVisible(true);
                    saveItem.setVisible(false);
                    name = String.valueOf(nameView.getText());
                    email = String.valueOf(emailView.getText());
                    desc = String.valueOf(descView.getText());
                    new SaveAsynkTask().execute(3000);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class SaveAsynkTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressView.setVisibility(View.VISIBLE);
            nameView.setEnabled(false);
            emailView.setEnabled(false);
            phoneView.setEnabled(false);
            descView.setEnabled(false);
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                User newUser = new User(name, email, phone, desc);
                SharedPreferences sPref = getSharedPreferences(login, MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.remove(id);
                editor.putString(phone, new Gson().toJson(newUser));
                editor.commit();
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "OK";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressView.setVisibility(View.INVISIBLE);
            Toast.makeText(ViewContactActivity.this, "Save OK!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
