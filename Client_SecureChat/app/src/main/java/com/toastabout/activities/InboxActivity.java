package com.toastabout.SecureChat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import Classes.Constants;
import Classes.Inbox;

public class InboxActivity extends AppCompatActivity {

	private RequestHandler requester;
	private String jwt, username, friend;
	private ListView lv;
	private ArrayList<String> conversations;
	private Intent ChatIntent;
	private Intent ExchangeIntent;
    private Inbox inbox;

    /**
     * Everything that happens when the activity is created
     */
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("Inbox");
		}

		requester = new RequestHandler(this);  // GET RID OF THIS SOON!!!
		ChatIntent = new Intent(Constants.CHAT_INTENT);
		ExchangeIntent = new Intent(Constants.KEY_EXCHANGE_INTENT);

		//FAB to create new conversation with a user
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				final AlertDialog.Builder builder = new AlertDialog.Builder(InboxActivity.this);
				builder.setTitle("Start new Conversation with: ");

				//text field that takes in the friend's name you want to start a new converastion with
				final EditText newChatInput = new EditText(InboxActivity.this);
				builder.setView(newChatInput);

				//button to start a new conversation
				builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!newChatInput.getText().toString().equals("")) {
							friend = newChatInput.getText().toString();

							//TODO Send conversation, instead of username and friend
							ChatIntent.putExtra("username", username);
							ChatIntent.putExtra("friend", friend);
							ChatIntent.putExtra("jwt", jwt);
							startActivity(ChatIntent);
						}
						else {
							Toast.makeText(InboxActivity.this, "You need to enter a username.", Toast.LENGTH_SHORT).show();
						}
					}
				});
				//button sends you to activity that exchanges keys
				builder.setNegativeButton("Exchange Keys", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ExchangeIntent.putExtra("username", username);
						startActivity(ExchangeIntent);
					}
				});

				builder.show();
			}
		});

		//ONLY CALL THIS IF RECEIVED JWT (I.E. Login successful)
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			//gets token and username from previous activity
			jwt = extras.getString("jwt");
			username = extras.getString("username");
			lv = (ListView) findViewById(R.id.list);
			conversations = new ArrayList<>();

            //look for inbox file for the given user
            File inboxFile = new File(InboxActivity.this.getFilesDir().getPath() + username + "_" + Constants.INBOX_FILENAME);
            if (inboxFile.exists()) {
                try {
                    //read the inbox from the file
                    ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(inboxFile));
                    Object inboxObj = inputStream.readObject();
                    if (inboxObj instanceof Inbox) {
                        inbox = (Inbox) inboxObj;
                    }
                }
                catch (Exception e) {
                    Log.w("Error Reading Inbox: ", e.getMessage());
                }
            }
            else {
                //if the file doesn't exist, create a new inbox
                inbox = new Inbox(username);
            }

            //TODO  then pull any new messages from the server. Should this be a thread that polls the server every so often?
            inbox.updateInbox(username, InboxActivity.this);
			//Get messages when screen loads
			requester.getConversations(username, lv, conversations, InboxActivity.this); //THIS SHOULD BE GONE SOON!

			//swipe down to refresh, I.E. get messages from the server.
			final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {

					requester.getConversations(username, lv, conversations, InboxActivity.this);
                    Log.w("GetConversations Object", requester.getGETresponse().toString());

					swipeRefreshLayout.setRefreshing(false);
				}
			});


			//Add on click listener to list view to get desired conversation
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

					//get name of friend from the list
					friend = (String) lv.getItemAtPosition(i);

                    //TODO pass conversation object to chat activity
					//fill intent Extras, and start new activity
					ChatIntent.putExtra("username", username);
					ChatIntent.putExtra("friend", friend);
					ChatIntent.putExtra("jwt", jwt);
					startActivity(ChatIntent);
				}
			});
		}
	}//END onCreate

    /**
     * Everything that happens when the activity is paused. In this case, the app should save
     * the user's inbox to a file
     */
    @Override
    protected void onPause() {
        super.onPause();
        File inboxFile = new File(InboxActivity.this.getFilesDir().getPath() + username + "_" + Constants.INBOX_FILENAME);
        inbox.writeInboxToFile(inboxFile);
    }
}//END InboxActivity
