package com.toastabout.test_securechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class InboxActivity extends AppCompatActivity {

	ServerRequest requester = new ServerRequest();
    String jwt, username, friend;
    ListView lv;
    ArrayList<String> conversations;
	ArrayAdapter<String> arrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		//ONLY CALL THIS IF RECEIVED JWT
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			//gets token and username from previous activity
			jwt = extras.getString("jwt");
			username = extras.getString("username");
			lv = (ListView) findViewById(R.id.list);
			conversations = new ArrayList<>();



			//Get messages when screen loads
			requester.getConversations(username, lv, conversations, InboxActivity.this);
//            updateConversations();


			//swipe down to refresh, I.E. get messages from the server.
			final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					requester.getConversations(username, lv, conversations, InboxActivity.this);
                    Log.w("GetConversations Object", requester.getGetMessageResponse().toString());
//                    updateConversations();
					swipeRefreshLayout.setRefreshing(false);
				}
			});


			//Add on click listener to list view to get desired conversation
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					friend = (String) lv.getItemAtPosition(i);

					Intent intent = new Intent("com.toastabout.test_securechat.ChatActivity");
					try {
                        //convert json messages to string so they can be passed to next activity
						JSONArray messages = requester.getGetMessageResponse().getJSONObject("conversations").getJSONArray(friend);
						intent.putExtra("messages", messages.toString());
					}
					catch (JSONException e) {
						Toast.makeText(InboxActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					}

					intent.putExtra("friend", friend);
					intent.putExtra("jwt", jwt);
                    intent.putExtra("conversations", conversations);
					startActivity(intent);
				}
			});
		}
	}//END onCreate


//    public void updateConversations() {
//        // This is the array adapter, it takes the context of the activity as a first parameter,
//        // the type of list view as a second parameter and your array as a third parameter.
//        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, conversations);
//
//        try {
//            JSONObject GETresponse = requester.getGetMessageResponse();
//            if (GETresponse.getBoolean("error")) {
//                Toast.makeText(this, GETresponse.getString("error_msg"), Toast.LENGTH_LONG).show();
//            }
//            else {
//                JSONObject JSONconvos = GETresponse.getJSONObject("conversations");
//                Iterator<String> iter = JSONconvos.keys();
//                while (iter.hasNext()) {
//                    String next = iter.next();
//                    if (!conversations.contains(next))
//                        conversations.add(next);
//                }
//                lv.setAdapter(arrayAdapter);
//            }
//
//        }
//        catch (JSONException e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//
//    }//END refreshLayout()



}//END InboxActivity
