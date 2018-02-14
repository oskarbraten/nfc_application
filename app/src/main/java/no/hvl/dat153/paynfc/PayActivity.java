package no.hvl.dat153.paynfc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import static android.nfc.NdefRecord.createMime;


public class PayActivity extends Activity {

    private NfcAdapter nfcAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        SharedPreferences appPref = getSharedPreferences("app_preferences", MODE_PRIVATE);
        final int balance = appPref.getInt("balance", 100);

        Intent intent = getIntent();

        Integer amount = intent.getExtras().getInt("amount", 0);

        TextView amountView = findViewById(R.id.amountView);
        amountView.setText(String.valueOf(amount));

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String text = (String.valueOf(amount));
        NdefMessage msg = new NdefMessage(new NdefRecord[] { createMime(
                "application/no.hvl.dat153.nfc_project",
                text.getBytes()
        )});

        nfcAdapter.setNdefPushMessage(msg, this);

        nfcAdapter.setOnNdefPushCompleteCallback((NfcEvent nfcEvent) -> {
            //nfcAdapter.disableForegroundDispatch(this);
            nfcAdapter.setNdefPushMessage(null, this);

            appPref.edit().putInt("balance", balance - amount).apply();

            this.runOnUiThread(() -> {
                int newBalance = appPref.getInt("balance", 100);
                if (newBalance == (balance - amount)) {
                    Toast.makeText(this, "Transfer of " + amount + " tokens complete!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
                finish();
            });
        }, this);
    }
}
