package mobile.com.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import io.realm.Realm;


public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                String address = smsMessage.getOriginatingAddress();
                Message.add(realm, address, smsMessage.getMessageBody().toString(),
                        smsMessage.getTimestampMillis(), false);

                new Preferences(context).add(address);
            }
            realm.commitTransaction();
        }
    }
}
