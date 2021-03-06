package com.toastabout.SecureChat;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Classes.Constants;
import Crypto.*;

public class RequestHandler {

    private JSONObject getMessageResponse, postMessageResponse;
    private Context context;
    private AESCipher aesCipher;

    public RequestHandler(Context context) {
        this.context = context;
        aesCipher = new AESCipher(context);
    }

    /**
     * Get the URL for the GET parameter
     * @param username user making the request
     * @return GET parameters for request
     */
    private String getGetMessagesURL(String username) {
        return Constants.GET_MESSAGES_URL+"/?receiver="+username;
    }

    public JSONObject getGETresponse() {return getMessageResponse;}


    /**
     * Get the conversations from the server
     * @param username the user that is logged in
     * @param lv List View that is going to be updated
     * @param conversations An ArrayList of conversations. This is just a list of names.
     * @param context Context of the current activity
     */
    public void getConversations(String username,
                                 final ListView lv,
                                 final ArrayList<String> conversations,
                                 final Context context) {

        // This is the array adapter, it takes the context of the activity as a first parameter,
        // the type of list view as a second parameter and your array as a third parameter.
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, conversations);


        //STRING REQUEST TO GET MESSAGES FROM SERVER
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getGetMessagesURL(username),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // GET the response from the server
                        try {
                            getMessageResponse = new JSONObject(response);

                            //IF NO ERRORS, Get conversations
                            if (!getMessageResponse.getBoolean("error")) {
                                JSONObject JSONconvos = getMessageResponse.getJSONObject("conversations");
                                Iterator<String> iter = JSONconvos.keys();
                                while (iter.hasNext()) {
                                    String next = iter.next();
                                    if (!conversations.contains(next))
                                        conversations.add(next);
                                }
                                lv.setAdapter(arrayAdapter);
                            }
                            else {
                                //DISPLAY ERROR MESSAGE from server
                                Toast.makeText(context, getMessageResponse.getString("error_msg"), Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }//END ON_RESPONSE
                },//END RESPONSE LISTENER
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "That didn't work!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        //add response to queue to be sent to server
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


	/**
     * Get chat messages for a specific conversation
     * @param username username of user getting messages
     * @param friend name of person user is having conversation with
     * @param listView listview that is adapted with the messages
     * @param context context of the chat activity
     */
    public void getChat(final String username,
                        final String friend,
                        final ListView listView,
                        final Context context) {

        //STRING REQUEST TO GET MESSAGES FROM SERVER
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getGetMessagesURL(username),
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
                            JSONArray jsonConversation = JSONconvos.getJSONArray(friend);

                            //Loop thorugh JSONArray to get each message
                            for (int i = 0; i < jsonConversation.length(); i++) {
                                String JSONString = jsonConversation.getString(i);
                                messageObject = new JSONObject(JSONString);

                                //get data from JSON message
                                sender = messageObject.getString("sender");
//                                encryptedMessage = messageObject.getString("message");
                                message = messageObject.getString("message");
                                timeStamp = messageObject.getString("time_sent");

                                try {
//                                    message = aesCipher.decrypt(encryptedMessage);
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
     * POST request sends message to the server
     * @param message message to be sent
     * @param receiver recipient of the message
     * @param jwt the token that was received when user logged in
     * @param context the context of the current activity
     */
    public void sendMessage(final String message,
                            final String receiver,
                            final String jwt,
                            final Context context) {

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.POST_MESSAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the resposne from the server
                        try {
                            postMessageResponse = new JSONObject(response);
                            String responseMsg = postMessageResponse.getString("error_msg");

                            //if no error
                            if (!postMessageResponse.getBoolean("error")) {
                                Toast.makeText(context, responseMsg, Toast.LENGTH_SHORT).show();
                            }
                            //some error occurred
                            else {
                                Toast.makeText(context, responseMsg, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }//END ON_RESPPNSE
                },//END RESPONSE LISTENER
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "That didn't work!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected HashMap<String, String> getParams()
            {
                HashMap<String, String> params = new HashMap<>();
                //encrypt message before sending to server
                try {
//                    String encryptedMessage = aesCipher.encrypt(message, receiver);
//                    params.put("message-to-send", encryptedMessage);
                    params.put("message-to-send", message);
                }
                catch (Exception e) {
                    Log.d("Encryption Error: ", e.getMessage());
                }

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

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


}
