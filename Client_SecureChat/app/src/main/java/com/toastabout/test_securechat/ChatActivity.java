package com.toastabout.test_securechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.fullScroll(View.FOCUS_DOWN);



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //get extras from previous activity
            final String friend = extras.getString("friend");
            String messages = extras.getString("messages");
            final String jwt = extras.getString("jwt");


            String sender, message, timeStamp;
            JSONObject messageObject;
            toolbar.setTitle(friend);
            TextView conversation = (TextView) findViewById(R.id.messages);
            //convert string messages into JSON objects Build out messages on app
            try {
                JSONArray JSONmessages = new JSONArray(messages);
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
                        sendMessage(messageString, friend, jwt);
                        messageTxt.setText("");

                    }
                }
            });

        }

    }



    public void sendMessage(final String message, final String receiver, final String jwt) {
        Routes URL = new Routes();


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.getPostMessagesURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the resposne from the server
                        try {
                            JSONObject JSONresponse = new JSONObject(response);
                            String responseMsg = JSONresponse.getString("error_msg");

                            //if no error
                            if (!JSONresponse.getBoolean("error")) {
                                Toast.makeText(ChatActivity.this, responseMsg, Toast.LENGTH_SHORT).show();
                            }
                            //some error occurred
                            else {
                                Toast.makeText(ChatActivity.this, responseMsg, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }//END ON_RESPPNSE
                },//END RESPONSE LISTENER
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChatActivity.this, "That didn't work!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected HashMap<String, String> getParams()
            {
                HashMap<String, String> params = new HashMap<>();
                params.put("message-to-send", message);
                params.put("recipient", receiver);
                return params;
            }
            @Override
            public HashMap<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

}
