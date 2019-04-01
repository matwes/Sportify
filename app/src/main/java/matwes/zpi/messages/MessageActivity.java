package matwes.zpi.messages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.Common;
import matwes.zpi.GetMethodAPI;
import matwes.zpi.R;
import matwes.zpi.RequestAPI;
import matwes.zpi.domain.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MessageActivity extends AppCompatActivity implements AsyncTaskCompleteListener<String> {
    private long eventId;
    private String userId;
    private ArrayList<Message> messages;
    private MessageListAdapter adapter;
    private EditText messageBox;
    private Timer timer;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messages = new ArrayList<>();
        Intent intent = getIntent();
        eventId = intent.getLongExtra("eventId", -1);

        userId = Common.getCurrentUserId(this);

        recyclerView = findViewById(R.id.messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new MessageListAdapter(messages, userId);
        recyclerView.setAdapter(adapter);
        messageListener();

        messageBox = findViewById(R.id.messageBox);
        Button sendButton = findViewById(R.id.btnSendMessage);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Objects.equals(messageBox.getText().toString(), "")) {
                    new RequestAPI(getApplicationContext(), "POST", getJsonToPostMessage(), new AsyncTaskCompleteListener<String>() {
                        @Override
                        public void onTaskComplete(String result) {
                            getMessages();
                        }
                    }, false).execute(getLink());
                }
            }
        });
    }

    @Override
    public void onTaskComplete(String result) {
        String json;
        try {
            json = new JSONObject(result).getJSONArray("content").toString();

            ArrayList<Message> temp = Message.jsonMessagesToList(json);
            if (temp.size() != messages.size()) {
                messages.clear();
                messages.addAll(temp);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(temp.size() - 1);
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    void getMessages() {
        if (Common.isOnline(this)) {
            new GetMethodAPI(this, this, false).execute(getLink());
        }
    }

    String getJsonToPostMessage() {
        JSONObject json = new JSONObject();
        try {
            json.put("author_id", userId);
            json.put("message", messageBox.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageBox.setText("");
        return json.toString();
    }

    void messageListener() {
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getMessages();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 5000);
    }

    @Override
    public void onStop() {
        super.onPause();
        timer.cancel();
        timer.purge();
    }

    private String getLink() {
        return String.format(Common.URL + "/events/%d/messages?size=99", eventId);
    }
}
