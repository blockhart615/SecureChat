package com.toastabout.test_securechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity {

    ServerRequest requester = new ServerRequest();
    String friend, jwt;
    String sender, message, timeStamp;
    JSONObject messageObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //get extras from previous activity
            friend = extras.getString("friend");
            String chatMessages = extras.getString("messages");
            jwt = extras.getString("jwt");


            toolbar.setTitle(friend);
            final TextView conversation = (TextView) findViewById(R.id.messages);

            //convert string messages into JSON objects Build out messages on app
            try {
                JSONArray JSONmessages = new JSONArray(chatMessages);
                for (int i = 0; i < JSONmessages.length(); i++) {
                    String JSONString = JSONmessages.getString(i);
                    messageObject = new JSONObject(JSONString);
                    sender = messageObject.getString("sender");
                    message = messageObject.getString("message");
                    timeStamp = messageObject.getString("time_sent");

                    conversation.append(sender + ":\n" + message + "\n" + timeStamp + "\n\n");
                }
            }
            catch (JSONException e) {
                conversation.setText(e.getMessage());
                e.printStackTrace();
            }


            //Click listener for SEND button to send a message
            Button sendBtn = (Button) findViewById(R.id.send);
            sendBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    EditText messageTxt = (EditText) findViewById(R.id.message_txt);
                    String messageString = messageTxt.getText().toString();
                    if (messageString.isEmpty()) {
                        Toast.makeText(ChatActivity.this, "Message is empty, write some text!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        requester.sendMessage(messageString, friend, jwt, ChatActivity.this);
                        messageTxt.setText("");
                    }
                }
            });

        }//END if Bundle Extras


        final ScrollView scrollview = ((ScrollView) findViewById(R.id.scroll_view));
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.scrollTo(0, scrollview.getBottom());
            }
        });


    }//END onCreate

}//END ChatActivity
