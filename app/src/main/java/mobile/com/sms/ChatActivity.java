package mobile.com.sms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;


public class ChatActivity extends AppCompatActivity {

    private RealmResults<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String address = getIntent().getStringExtra("address");
        new Preferences(this).delete(address);

        messages = Realm.getDefaultInstance().where(Message.class)
                .equalTo("address", address).findAllSorted("time");

        getSupportActionBar().setTitle(address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setAdapter(new ChatAdapter());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }


    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

        @Override
        public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ChatViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final ChatViewHolder holder, final int position) {
            Message message = messages.get(position);
            String messageString = message.getMessage();
            String time = DateTimeUtils.getRelativeTimeSpanString(message.getTime());

            if (message.isSent()) {
                holder.sendArea.setVisibility(View.VISIBLE);
                holder.receiveArea.setVisibility(View.GONE);
                holder.sendText.setText(messageString);
                holder.sendTimeText.setText(time);
            }
            else {
                holder.sendArea.setVisibility(View.GONE);
                holder.receiveArea.setVisibility(View.VISIBLE);
                holder.receiveText.setText(messageString);
                holder.receiveTimeText.setText(time);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        class ChatViewHolder extends RecyclerView.ViewHolder {
            public TextView sendText, receiveText, sendTimeText, receiveTimeText;
            public RelativeLayout sendArea, receiveArea;

            public ChatViewHolder(View item) {
                super(item);
                sendText = (TextView) item.findViewById(R.id.send);
                receiveText = (TextView) item.findViewById(R.id.receive);
                sendArea = (RelativeLayout) item.findViewById(R.id.send_area);
                receiveArea = (RelativeLayout) item.findViewById(R.id.receive_area);
                sendTimeText = (TextView) item.findViewById(R.id.send_time);
                receiveTimeText = (TextView) item.findViewById(R.id.receive_time);
            }
        }
    }
}
