package com.toastabout.test_securechat;

import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class ServerRequest {
    // Server user login url
    private final String LOGIN_URL = "https://toastabout.com/SecureChat/login.php";
    private final String REGISTER_URL = "https://toastabout.com/SecureChat/register.php";
    private final String GET_MESSAGES_URL = "https://toastabout.com/SecureChat/GetMessage.php";
    private final String POST_MESSAGE_URL = "https://toastabout.com/SecureChat/SendMessage.php";

    private JSONObject loginResponse, registerResponse, getMessageResponse, postMessageResponse;

    /**
     * Get the URL for the GET parameter
     * @param username
     * @return
     */
    private String getGetMessagesURL(String username) {
        return GET_MESSAGES_URL+"/?receiver="+username;
    }

//    public JSONObject getLoginResponse() {return loginResponse;}
//    public JSONObject getRegisterResponse() {return registerResponse;}
    public JSONObject getGetMessageResponse() {return getMessageResponse;}
//    public JSONObject getPostMessageResponse() {return postMessageResponse;}

    /**
     * Send request to server to log in
     * @param username username of user who wishes to log in
     * @param password the user's password
     * @param context context of the current Activity, to allow changing intents.
     */
    public void login(final String username,
                      final String password,
                      final Context context) {

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the resposne from the server
                        try {
                            loginResponse = new JSONObject(response);
                            if (!loginResponse.getBoolean("error")) {
                                //if login is successful, send user to Inbox
                                String jwt = loginResponse.getString("jwt");
                                Intent intent = new Intent("com.toastabout.test_securechat.InboxActivity");
                                intent.putExtra("jwt", jwt);
                                intent.putExtra("username", username);
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, loginResponse.getString("error_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }//END ON_RESPONSE
                },//END RESPONSE LISTENER
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "That didn't work!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    /**
     * Registers a user in the database
     * @param username desired username of the user
     * @param password desire password of the user
     * @param email User's email address
     * @param context the Context of the current activity
     */
    public void registerUser(final String username,
                             final String password,
                             final String email,
                             final Context context) {

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response from the server

                        try {
                            registerResponse = new JSONObject(response);
                                //IF response is successful, change intent to login activity
                                if (!registerResponse.getBoolean("error")) {
                                    try {
                                        Intent intent = new Intent("com.toastabout.test_securechat.LoginActivity");
                                        intent.putExtra("username", registerResponse.getString("username"));
                                        context.startActivity(intent);
                                    } catch (Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, registerResponse.getString("error_msg"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        catch (JSONException e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }//END ON_RESPPONSE
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
                params.put("username", username);
                params.put("password", password);
                params.put("email", email);
                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }//END registerUser


    /**
     * Get the conversations from the server
     * @param username the user that is logged in
     * @param lv List View that is going to be updated
     * @param conversations An ArrayList of conversations
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
                            JSONObject JSONconvos = getMessageResponse.getJSONObject("conversations");
                            Iterator<String> iter = JSONconvos.keys();
                            while (iter.hasNext()) {
                                String next = iter.next();
                                if (!conversations.contains(next))
                                    conversations.add(next);
                            }
                            lv.setAdapter(arrayAdapter);


                            //IF NO ERRORS, DO THIS!
                            if (!getMessageResponse.getBoolean("error")) {

                            }
                            //DISPLAY ERROR MESSAGE
                            else {
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_MESSAGE_URL,
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

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


}
