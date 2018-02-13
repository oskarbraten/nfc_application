package no.hvl.dat153.nfc_project;

import android.app.Activity;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.Toast;

import static android.nfc.NdefRecord.createMime;


public class PayActivity extends Activity {

    private NfcAdapter mNfcAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String text = ("kos på os");
        NdefMessage msg = new NdefMessage(new NdefRecord[] { createMime(
                "application/no.hvl.dat153.nfc_project",
                text.getBytes()
        )});

        mNfcAdapter.setNdefPushMessage(msg, this);

        mNfcAdapter.setOnNdefPushCompleteCallback((NfcEvent nfcEvent) -> {
            finish();
        }, this);

        SharedPreferences appPref = getSharedPreferences("AppPref",MODE_PRIVATE);
        float blance = appPref.getFloat("blance", Float.NEGATIVE_INFINITY);

        Toast.makeText(this, "*****  " + blance, Toast.LENGTH_LONG).show();

    }
}
