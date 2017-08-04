package com.toastabout.SecureChat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class ChatActivity extends AppCompatActivity {

    RequestHandler requester;
    String username, friend, jwt;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requester = new RequestHandler(ChatActivity.this);
        listView = (ListView) findViewById(R.id.list_view);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //get extras from previous activity
            friend = extras.getString("friend");
            jwt = extras.getString("jwt");
            username = extras.getString("username");

            //update toolbar title to friend's name
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Conversation With " + friend +":");
            }

            requester.getChat(username, friend, listView, ChatActivity.this);


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

    }//END onCreate

}//END ChatActivity
