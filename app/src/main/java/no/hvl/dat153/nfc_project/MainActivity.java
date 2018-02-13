package no.hvl.dat153.nfc_project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "This device does not support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "You need to enable NFC to send/receive payments.", Toast.LENGTH_LONG).show();
        }

        Button paymentBtn = findViewById(R.id.paymentBtn);
        paymentBtn.setOnClickListener((View v) -> {
            startActivity(new Intent(this, PayActivity.class));
        });


        SharedPreferences appPref = getSharedPreferences("AppPref", MODE_PRIVATE);
        float blance = appPref.getFloat("blance", Float.NEGATIVE_INFINITY);
        if (blance == Float.NEGATIVE_INFINITY) {
            appPref.edit().putFloat("blance", (float) 100.0);
            appPref.edit().apply();
        }
    }


}
