package Classes;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.toastabout.SecureChat.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * The account manager handles all things that have to do with the user's account
 * including Logging in, registering, managing friend list, JWT, etc.
 */
public class AccountManager {

    private String jwt;
    private String username;
    private String password;
    private JSONObject loginResponse, registerResponse, getMessageResponse, postMessageResponse;

    /**
     * Default Constructor (doesn't do anything)
     */
    public AccountManager(){ }

    /**
     * Log the user in
     * @param password
     * @param context
     */
    public void login(final String username,
                      final String password,
                      final Context context) {
        this.username = username;
        this.password = password;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the resposne from the server
                        try {
                            loginResponse = new JSONObject(response);
                            if (!loginResponse.getBoolean("error")) {
                                //if login is successful, send user to Inbox
                                jwt = loginResponse.getString("jwt");
                                Intent intent = new Intent("com.toastabout.SecureChat.InboxActivity");
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
     * Register a new user
     * @param username
     * @param password
     * @param email
     * @param context
     */
    public void registerUser(final String username,
                             final String password,
                             final String email,
                             final Context context) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response from the server

                        try {
                            registerResponse = new JSONObject(response);
                            //IF response is successful, change intent to login activity
                            if (!registerResponse.getBoolean("error")) {
                                try {
                                    Intent intent = new Intent("com.toastabout.SecureChat.LoginActivity");
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
    }

    /**
     * @return the JWT (might not even need this function)
     */
    public String getJWT() {
        return jwt;
    }

    /**
     * performs the same thing as login() but clarifies the purpose of it when
     * we just want to refresh the JWT
     * @param context
     */
    public void refreshJWT(Context context) {
        //log in with this.username and this.password
        login(this.username, this.password, context);
    }


}
