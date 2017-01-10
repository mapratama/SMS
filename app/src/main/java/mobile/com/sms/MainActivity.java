package mobile.com.sms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private Result[] results;
    private final int TOP_MESSAGE = 10, REQUEST_PERMISSION_SMS = 123;
    private TextView ignorePermissionTextView;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        preferences = new Preferences(this);

        ignorePermissionTextView = (TextView) findViewById(R.id.ignore_permission);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                    REQUEST_PERMISSION_SMS);
        else setupMessageData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.removeAllChangeListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            setupMessageData();
            return;
        }

        ignorePermissionTextView.setVisibility(View.VISIBLE);
    }

    private void setupMessageData() {
        getMessages("inbox");
        getMessages("sent");

        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm element) {
                setupUI();
            }
        });

        setupUI();
    }

    private void getMessages(String type) {
        boolean isSent;
        if (type.equals("sent")) isSent = true;
        else isSent = false;

        Uri uriSms = Uri.parse("content://sms/" + type);
        Cursor cursor = getContentResolver().query(uriSms, new String[]{"address", "body", "date"},
                null, null, null);
        cursor.moveToFirst();

        realm.beginTransaction();
        while (cursor.moveToNext())
            Message.add(realm, cursor.getString(0), cursor.getString(1), Long.parseLong(cursor.getString(2)), isSent);
        realm.commitTransaction();
    }

    private void setupUI() {
        ignorePermissionTextView.setVisibility(View.GONE);

        HashMap<String, Integer> unreadList = preferences.getUnreadList();
        Set<String> unreadAdresses = unreadList.keySet();

        RealmResults<Message> addressList = realm.where(Message.class).distinct("address");
        results = new Result[addressList.size()];
        for (int i = 0; i < addressList.size(); i++) {
            String address = addressList.get(i).getAddress();
            RealmResults<Message> messages = realm.where(Message.class).equalTo("address", address).findAllSorted("time");

            int totalUnread;
            if (unreadAdresses.contains(address)) totalUnread = unreadList.get(address);
            else totalUnread = 0;

            results[i] = new Result(messages.last(), messages.size(), totalUnread);
        }
        Arrays.sort(results);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setAdapter(new MessageViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int totalUnread = preferences.getTotalUnread();
        if (totalUnread > 0) {
            RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.activity_main);
            Snackbar.make(mainLayout, "You have " + totalUnread + " messages unread", Snackbar.LENGTH_LONG).show();
        }
    }

    class MessageViewAdapter extends RecyclerView.Adapter<MessageViewAdapter.MessageViewHolder> {

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final MessageViewHolder holder, final int position) {
            Result result = results[position];
            Message message = result.getMessage();

            holder.addressTextView.setText(message.getAddress());
            holder.messageTextView.setText(message.getMessage());
            holder.totalMessageTextView.setText(String.valueOf(result.getTotalMessage()));
            holder.timeTextView.setText(DateTimeUtils.getRelativeTimeSpanString(message.getTime()));

            int totalUnread = result.getTotalUnread();
            if (totalUnread > 0) {
                holder.unreadLayout.setVisibility(View.VISIBLE);
                holder.totalUnreadTextView.setText(String.valueOf(totalUnread));
            }
            else holder.unreadLayout.setVisibility(View.GONE);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            int length = results.length;
            if (length > TOP_MESSAGE) return TOP_MESSAGE;
            return length;
        }

        class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView addressTextView, messageTextView, timeTextView,
                    totalMessageTextView, totalUnreadTextView;
            public RelativeLayout unreadLayout;

            public MessageViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                addressTextView = (TextView) view.findViewById(R.id.address);
                messageTextView = (TextView) view.findViewById(R.id.message);
                timeTextView = (TextView) view.findViewById(R.id.time);
                totalMessageTextView = (TextView) view.findViewById(R.id.total_message);
                totalUnreadTextView = (TextView) view.findViewById(R.id.total_unread);
                unreadLayout = (RelativeLayout) view.findViewById(R.id.unread_layout);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                intent.putExtra("address", addressTextView.getText().toString());
                startActivity(intent);
            }
        }
    }
}

