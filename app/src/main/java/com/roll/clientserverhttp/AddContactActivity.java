package com.roll.clientserverhttp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.roll.clientserverhttp.entities.User;

public class AddContactActivity extends AppCompatActivity {

    private EditText inputName, inputEmail, inputPhone, inputDesc;
    private String login, phone;
    private String jsonUser;
    private MenuItem addConItem;
    private ProgressBar progressBarSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputDesc = (EditText) findViewById(R.id.input_desc);
        progressBarSave = (ProgressBar) findViewById(R.id.progress_save);

        SharedPreferences sharedPreferences = getSharedPreferences("AUTH", MODE_PRIVATE);
        login = sharedPreferences.getString("LOGIN", "");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        addConItem = (MenuItem) findViewById(R.id.item_add_contact);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_add_contact) {
            phone = String.valueOf(inputPhone.getText());
            String email = String.valueOf(inputEmail.getText());
            String desc = String.valueOf(inputDesc.getText());
            String name = String.valueOf(inputName.getText());
            if ("".equals(phone) || "".equals(name)) {
                Toast.makeText(AddContactActivity.this, "Name or phone is empty", Toast.LENGTH_SHORT).show();
            } else {
                Gson gson = new Gson();
                jsonUser = gson.toJson(new User(name, email, phone, desc));
                Log.d("jsonUser", jsonUser);
                new SaveContactAsyncTask().execute(3000);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class SaveContactAsyncTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarSave.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences(login, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(phone, jsonUser);
                editor.commit();
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "ERROR ADD_CONTACT";
            }
            return "OK";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarSave.setVisibility(View.GONE);
            if ("OK".equals(s)) {
                Toast.makeText(AddContactActivity.this, "Add contact OK!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
