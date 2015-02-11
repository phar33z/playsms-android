package com.artivisi.android.playsms.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.User;
import com.artivisi.android.playsms.helper.MessageHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.service.impl.AndroidMasterServiceImpl;
import com.artivisi.android.playsms.ui.adapter.InboxAdapter;
import com.google.gson.Gson;

public class ComposeMessageActivity extends ActionBarActivity {

    private String username;
    private String token;
    private String to;
    private String msg;

    private EditText mMsgTo, mMsg;
    private ProgressBar sendingMsg;
    private AndroidMasterService service = new AndroidMasterServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Message");

        User u = getUserCookie(LoginActivity.KEY_USER, User.class);
        username = u.getUsername();
        token = u.getToken();
        mMsgTo = (EditText) findViewById(R.id.txt_msg_to);
        mMsg = (EditText) findViewById(R.id.txt_msg);
        sendingMsg = (ProgressBar) findViewById(R.id.sending_msg);
        sendingMsg.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            msg = mMsg.getText().toString();
            to = mMsgTo.getText().toString();
            new SendMessage().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class SendMessage extends AsyncTask<Void, Void, MessageHelper> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sendingMsg.setVisibility(View.VISIBLE);
        }

        @Override
        protected MessageHelper doInBackground(Void... params) {
            return service.sendMessage(username, token, to, msg);
        }

        @Override
        protected void onPostExecute(MessageHelper messageHelper) {
            super.onPostExecute(messageHelper);
            sendingMsg.setVisibility(View.INVISIBLE);
            if(messageHelper.getStatus() != null){
                if(messageHelper.getStatus().equals("ERR")){
                    Toast.makeText(getApplicationContext(), messageHelper.getErrorString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Message has been delivered", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected <T> T getUserCookie(String key, Class<T> a) {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS, Context.MODE_PRIVATE);

        if (sharedPreferences == null) {
            return null;
        }

        String data = sharedPreferences.getString(key, null);

        if (data == null) {
            return null;
        } else {
            Gson gson = new Gson();
            return gson.fromJson(data, a);
        }
    }
}