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

    private NfcAdapter mNfcAdapter;

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

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String text = (String.valueOf(amount));
        NdefMessage msg = new NdefMessage(new NdefRecord[] { createMime(
                "application/no.hvl.dat153.nfc_project",
                text.getBytes()
        )});

        mNfcAdapter.setNdefPushMessage(msg, this);

        mNfcAdapter.setOnNdefPushCompleteCallback((NfcEvent nfcEvent) -> {
            //mNfcAdapter.disableForegroundDispatch(this);
            mNfcAdapter.setNdefPushMessage(null, this);

            this.runOnUiThread(() -> {
                appPref.edit().putInt("balance", balance - amount).apply();
                Toast.makeText(this, "Transfer complete!", Toast.LENGTH_LONG).show();
                finish();
            });
        }, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mNfcAdapter.setNdefPushMessage(null, this);
        mNfcAdapter.setOnNdefPushCompleteCallback(null, this);
    }
}
