package com.example.diplom2;

import static com.example.diplom2.R.layout.list_item;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diplom2.models.Message;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ChatNew extends AppCompatActivity {
    private static final int SIGN_IN_CODE = 1;
    private static final int MAX_MESSAGE_LENGTH = 150;


    private RelativeLayout chatNew;
    private FirebaseListAdapter<Message> adapter;
    FirebaseListOptions<Message> options;
    private FloatingActionButton sendBtn;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("messagesByEmail");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_new);
        chatNew = findViewById(R.id.chat_new);
        sendBtn = findViewById(R.id.btnSend);
        displayAllMessages();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                EditText textField = findViewById(R.id.message_field);
                if (textField.equals("")) {
                    Toast.makeText(getApplicationContext(), "Введите сообщение!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (textField.length() == MAX_MESSAGE_LENGTH) {
                    Toast.makeText(getApplicationContext(), "Длина сообщения до 150 символов!", Toast.LENGTH_SHORT).show();
                    return;
                }
                myRef.push().setValue(
                        new Message(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(),
                                textField.getText().toString())
                );
                textField.setText("");
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void displayAllMessages() {
        Log.d(ChatNew.class.getSimpleName(), "displayAllMessages");
        options =
                new FirebaseListOptions.Builder<Message>()
                        .setQuery(myRef, Message.class)
                        .setLayout(list_item)
                        .build();
        ListView listOfMsg = findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Message model, int position) {
                Log.d(ChatNew.class.getSimpleName(), "getMessgaeFromFB: " + model.getUserName());
                TextView msgUser, msgTime, msgText;
                msgUser = v.findViewById(R.id.message_user);
                msgTime = v.findViewById(R.id.message_time);
                msgText = v.findViewById(R.id.message_text);
                msgUser.setText(model.getUserName());
                msgText.setText(model.getTextMessage());
                msgTime.setText(DateFormat.format("dd-mm-yyyy HH:mm:ss", model.getMessageTime()));
            }
        };
        listOfMsg.setAdapter(adapter);
    }

    public void goNumber(View view) {
        Intent intent = new Intent(this, Number.class);
        startActivity(intent);
    }
}