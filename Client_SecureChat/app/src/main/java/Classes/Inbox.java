package Classes;


import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.toastabout.SecureChat.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import Crypto.AESCipher;

/**
 * This class represents the user's inbox.
 * It will hold a list of conversations
 */
public class Inbox {

    private JSONObject getMessageResponse, postMessageResponse;
    private Context context;
    private AESCipher aesCipher;

    //A list of conversations for this user
    private ArrayList<Conversation> conversations;

    public Inbox() {

    }
    /**
     * Loads conversations that are stored locally in files
     */
    public void loadConversations() {

    }

    /**
     * Updates the conversations in the inbox with new messages from the server if there are any
     * @param username
     * @param lv
     * @param conversations
     * @param context
     */
    public void updateInbox(String username,
                            final ListView lv,
                            final ArrayList<String> conversations,
                            final Context context) {

//STRING REQUEST TO GET MESSAGES FROM SERVER
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.GET_URL + username,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        String sender, message, encryptedMessage, timeStamp;
                        JSONObject messageObject;
                        ArrayList<String> chatMessages = new ArrayList<>();

                        try {
                            //make JSON object from response and get conversations
                            JSONObject chatResponse = new JSONObject(response);
                            JSONObject JSONconvos = chatResponse.getJSONObject("conversations");

                            //TODO  Loop through each JSON Conversation and convert them into
                            //TODO  objects that we can use
                            JSONArray jsonConversation = JSONconvos.getJSONArray(friend);

                            //Loop thorugh JSONArray to get each message
                            for (int i = 0; i < jsonConversation.length(); i++) {
                                String JSONString = jsonConversation.getString(i);
                                messageObject = new JSONObject(JSONString);

                                //get data from JSON message
                                sender = messageObject.getString("sender");
                                encryptedMessage = messageObject.getString("message");
                                timeStamp = messageObject.getString("time_sent");

                                try {
                                    message = aesCipher.decrypt(encryptedMessage);
                                    String messageString = sender + ":\n" + message + "\n" + timeStamp;
                                    //add message to arraylist if it isn't already in the list
                                    if (!chatMessages.contains(messageString)){
                                        chatMessages.add(messageString);
                                    }
                                }
                                catch (Exception e){
                                    Log.d("Decryption error: ", e.getMessage());
                                }


                            }

                            //make array adapter for listView of messages and set adapter to list view
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, chatMessages);
                            listView.setAdapter(arrayAdapter);

                            listView.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Select the last row so it will scroll into view...
                                    listView.setSelection(arrayAdapter.getCount() - 1);
                                }
                            });

                        }
                        catch (JSONException e) {
                            Log.w("JSONException: ", e.getMessage());
                        }

                    }//END ON_RESPONSE
                },//END RESPONSE LISTENER
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w("VolleyError", error.getMessage());
                    }
                }
        );

        //add response to queue to be sent to server
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * This method parses a successful JSON response and populates the Inbox's
     * conversations member.
     * @param response
     */
    private void parseResponse(JSONObject response) {


        Iterator<String> iter = response.keys();
        while (iter.hasNext()) {
            String next = iter.next();
            if (!conversations.contains(next))
                conversations.add(next);
        }
    }

}
