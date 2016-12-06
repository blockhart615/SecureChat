package com.toastabout.test_securechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.spongycastle.util.encoders.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import Encryption.RSACipher;

public class KeyExchange extends AppCompatActivity {

    private RSACipher rsaCipher;
    private ObjectMapper mapper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_exchange);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //gets token and username from previous activity
            username = extras.getString("username");
        }

        //creat rsaCipher object that will get the public key from a file
        try {
            rsaCipher = new RSACipher(KeyExchange.this);
        }
        catch (Exception e) {
            Log.d("RSACipher: ", e.getMessage());
        }

        //button handles scanning QR code to get friend's public key
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rsaCipher.receivePublicKey(KeyExchange.this);
            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.qr_code);
        System.out.println("Username: " + username);
        System.out.println("PublicKey: " + Base64.toBase64String(rsaCipher.getPublicKey().getEncoded()));
        rsaCipher.sendPublicKey(imageView, username, Base64.toBase64String(rsaCipher.getPublicKey().getEncoded()));

    }

    /**
     * Get result from scanning barcode
     * @param requestCode something
     * @param resultCode something
     * @param intent something
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        mapper = new ObjectMapper();

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {

            //recreate Map from JSON String
            try {
                Map<String,String> friend = mapper.readValue(scanResult.getContents(), new TypeReference<HashMap<String, String>>() {
                });
                //loop to get entry and key
                for (Map.Entry<String, String> entry : friend.entrySet()) {
                    //output for debugging purposes
                    System.out.println("Friend: " + entry.getKey());
                    System.out.println(entry.getKey() + "'s Public Key: " + entry.getValue());

                    //Have to convert string back into an RSA Public Key
                    byte[] decodedKey = Base64.decode(entry.getValue().getBytes());
                    try {
                        KeyFactory kf = KeyFactory.getInstance("RSA");
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
                        PublicKey friensPublicKey = kf.generatePublic(keySpec);

                        //add friend's username and key to keychain file
                        rsaCipher.addKeyToKeychain(entry.getKey(), friensPublicKey);
                        Toast.makeText(this, "Successfully added Friend's Key!", Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e) {
                        Log.d("KeyFactory: ", e.getMessage());
                    }
                }

            }
            catch (IOException e) {
                e.printStackTrace();
                Log.d("IOException: ", e.getMessage());
            }
        }
        // else continue with any other code you need in the method

    }


}
