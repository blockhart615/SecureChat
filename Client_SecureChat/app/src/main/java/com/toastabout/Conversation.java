package Classes;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.toastabout.SecureChat.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import Crypto.AESCipher;

/**
 * Implements Conversations
 */
public class Conversation {

    private ArrayList<Message> conversation;
    private String user, recipient;
    private JSONObject registerResponse, getMessageResponse, postMessageResponse;
    private AESCipher aesCipher;

    /**
     * Constructor
     * @param user
     * @param recipient
     */
    public Conversation(String user, String recipient) {
        this.user = user;
        this.recipient = recipient;
        conversation = new ArrayList<>();
    }

    /**
     * @return the conversation (might not be necessary. could handle it in loadConversation())
     */
    public ArrayList<Message> getConversation() {
        return conversation;
    }

    /**
     * this class will find the correct conversation file and load it into memory
     */
    public void loadConversation(String recipient) {

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
                    String encryptedMessage = aesCipher.encrypt(message, receiver);
                    params.put("message-to-send", encryptedMessage);
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

    /**
     *
     * @param message
     */
    public void addMessage(Message message) {
        conversation.add(message);
    }

}
