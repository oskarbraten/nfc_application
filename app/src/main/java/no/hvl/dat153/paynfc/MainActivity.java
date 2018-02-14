package no.hvl.dat153.paynfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MainActivity extends Activity {

    public static String MIME_TYPE = "application/no.hvl.dat153.nfc_project";
    private NfcAdapter nfcAdapter;
    private TextView balanceLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences appPref = getSharedPreferences("app_preferences", MODE_PRIVATE);

        final int currentBalance = appPref.getInt("balance", 100);

        balanceLabel = findViewById(R.id.balanceLabel);
        balanceLabel.setText(Integer.toString(currentBalance));


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        nfcAdapter.setNdefPushMessage(null, this);

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
            EditText amountField = findViewById(R.id.amountField);


            int amount = Integer.parseInt(amountField.getText().toString());

            if (amount < 0) {
                Toast.makeText(this, "Invalid amount.", Toast.LENGTH_SHORT).show();
            } else if (currentBalance - amount < 0) {
                Toast.makeText(this, "Not enough funds.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, PayActivity.class);
                intent.putExtra("amount", amount);

                startActivity(intent);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

//    @Override
//    public void onNewIntent(Intent intent) {
//        // onResume gets called after this to handle the intent
//        setIntent(intent);
//    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present

        Log.d("TESAT: ", new String(msg.getRecords()[0].getPayload()));
    }
}
