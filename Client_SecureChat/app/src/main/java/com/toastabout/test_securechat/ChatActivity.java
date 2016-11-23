package com.toastabout.test_securechat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String friend = extras.getString("friend");
            String messages = extras.getString("messages");
            String sender = "", receiver = "", message = "", timeStamp = "";
            JSONObject messageObject = null;

            toolbar.setTitle(friend);
            TextView messageTxt = (TextView) findViewById(R.id.messages);
//            messageTxt.setText(messages);

            //convert string messages into JSON objects
            try {
                JSONArray JSONmessages = new JSONArray(messages);
                for (int i = 0; i < JSONmessages.length(); i++) {
                    String JSONString = JSONmessages.getString(i);
                    messageObject = new JSONObject(JSONString);
                    sender = messageObject.getString("sender");
                    message = messageObject.getString("message");
                    timeStamp = messageObject.getString("time_sent");

                    messageTxt.append(sender + ":\n" + message + "\n" + timeStamp + "\n\n");
                }
            }
            catch (JSONException e) {
                messageTxt.setText(e.getMessage());
                e.printStackTrace();
            }

        }



    }

}
