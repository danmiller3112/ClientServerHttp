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

public class LoginActivity extends AppCompatActivity {

    private EditText inputLogin, inputPass;
    private Button btnLogin;
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
        progressBarLogin = (ProgressBar) findViewById(R.id.progress_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_login) {
                    if ("".equals(String.valueOf(inputLogin.getText()))) {
                        inputLogin.setError("Login is empty!");
                        return;
                    }
                    if ("".equals(String.valueOf(inputPass.getText()))) {
                        inputPass.setError("Password is Empty");
                        return;
                    }

                    new LoginAsyncTask().execute(3000);
                }
            }
        });

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

    private class LoginAsyncTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnLogin.setEnabled(false);
            inputLogin.setEnabled(false);
            inputPass.setEnabled(false);
            progressBarLogin.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return resultAsynkTask[1];
            }
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
}
