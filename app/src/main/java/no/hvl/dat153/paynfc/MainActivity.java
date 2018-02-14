package no.hvl.dat153.paynfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static String MIME_TYPE = "application/no.hvl.dat153.nfc_project";
    private NfcAdapter nfcAdapter;
    private TextView balanceLabel;
    private EditText amountField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountField = findViewById(R.id.amountField);
        balanceLabel = findViewById(R.id.balanceLabel);

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

            String amountString = amountField.getText().toString();

            if (amountString.isEmpty()) {
                Toast.makeText(this, "Please enter an amount.", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(amountString);
            final int currentBalance = getSharedPreferences("app_preferences", MODE_PRIVATE).getInt("balance", 100);

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

        Button addCurrencyBtn = findViewById(R.id.addCurrencyBtn);
        addCurrencyBtn.setOnClickListener((View v) -> {
            startActivity(new Intent(this, AddGrunkerActivity.class));
        });

        // Check to see that the Activity started due to an Android Beam
        // and that the intent has not been parsed before
        if (savedInstanceState == null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    private void processIntent(Intent intent) {
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage message = (NdefMessage) rawMessages[0];

        SharedPreferences prefs = getSharedPreferences("app_preferences", MODE_PRIVATE);

        // record 0 contains the MIME type, record 1 is the AAR, if present
        final int amount = Integer.parseInt(new String(message.getRecords()[0].getPayload()));

        // abort transfer if amount is negative.
        if (amount < 0) {
            return;
        }

        final int currentBalance = prefs.getInt("balance", 100);

        final int newBalance = currentBalance + amount;

        // update preferences, and label.
        prefs.edit().putInt("balance", newBalance).apply();
        balanceLabel.setText(String.valueOf(newBalance));

        Toast.makeText(this, "Received " + String.valueOf(amount), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        // update balanceLabel
        final int currentBalance = getSharedPreferences("app_preferences", MODE_PRIVATE).getInt("balance", 100);
        balanceLabel.setText(String.valueOf(currentBalance));

        // clear amountField.
        amountField.setText("");
        amountField.clearFocus();

        // set message to null to avoid resending NDEF payload.
        nfcAdapter.setNdefPushMessage(null, this);

        startForegroundDispatch(this, nfcAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        stopForegroundDispatch(this, nfcAdapter);
    }

    public static void startForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TYPE);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
    }
}
