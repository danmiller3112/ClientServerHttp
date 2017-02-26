package com.roll.clientserverhttp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputLogin, inputPass;
    private Button btnLogin, btnRegister;
    private ProgressBar progressBarLogin;
    private String[] resultAsynkTask = {"Login OK", "Login ERROR!"};
    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputLogin = (EditText) findViewById(R.id.input_login);
        inputPass = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        progressBarLogin = (ProgressBar) findViewById(R.id.progress_login);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        checkLogin();
    }

    private void checkLogin() {

        SharedPreferences sharedPreferences = getSharedPreferences("AUTH", MODE_PRIVATE);
        login = sharedPreferences.getString("LOGIN", "");

        if ("".equals(login)) {
            return;
        }
        startContactList();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (chekFields()) {
                    new LoginAsyncTask().execute();
                }
                break;
            case R.id.btn_register:
                if (chekFields()) {
                    new RegisterAsyncTask().execute();
                }
                break;
        }
    }

    private boolean chekFields() {
        if ("".equals(String.valueOf(inputLogin.getText()))) {
            inputLogin.setError("Login is empty!");
            return false;
        }
        if ("".equals(String.valueOf(inputPass.getText()))) {
            inputPass.setError("Password is Empty");
            return false;
        }
        return true;
    }

    private class LoginAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnLogin.setEnabled(false);
            inputLogin.setEnabled(false);
            inputPass.setEnabled(false);
            progressBarLogin.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            return resultAsynkTask[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            btnLogin.setEnabled(true);
            progressBarLogin.setVisibility(View.GONE);
            inputLogin.setEnabled(false);
            inputPass.setEnabled(false);
            if (s.equals(resultAsynkTask[0])) {
                Toast.makeText(LoginActivity.this, "Logon OK!", Toast.LENGTH_SHORT).show();
                saveSP();
                startContactList();
                finish();
            }
        }
    }

    private void saveSP() {
        login = String.valueOf(inputLogin.getText());
        SharedPreferences sharedPreferences = getSharedPreferences("AUTH", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LOGIN", login);
        editor.putString("PASS", String.valueOf(inputPass.getText()));
        editor.commit();
    }

    private void startContactList() {
        Intent intent = new Intent("contact.list.dan");
        startActivity(intent);
    }

    private class RegisterAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnLogin.setEnabled(false);
            inputLogin.setEnabled(false);
            inputPass.setEnabled(false);
            progressBarLogin.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            return resultAsynkTask[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            btnLogin.setEnabled(true);
            progressBarLogin.setVisibility(View.GONE);
            inputLogin.setEnabled(false);
            inputPass.setEnabled(false);
            if (s.equals(resultAsynkTask[0])) {
                Toast.makeText(LoginActivity.this, "Logon OK!", Toast.LENGTH_SHORT).show();
                saveSP();
                startContactList();
                finish();
            }
        }
    }
}
