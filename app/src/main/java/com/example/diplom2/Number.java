package com.example.diplom2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.diplom2.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Number extends AppCompatActivity {
    private EditText userNameFileld;
    ListView listView;
    TextView textHello;
    private ArrayAdapter<String> adapter;
    private List<String> listData;
    //Создание переменных для БД
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    //Переменная для картинки
    String image;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_number);
        listView = findViewById(R.id.resultTextView);
        userNameFileld = findViewById(R.id.userNameField);
        textHello = findViewById(R.id.textHello);

        //Для заполнения списка
        listData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.name_item, R.id.user_name, listData);
        listView.setAdapter(adapter);
        //setContentView(R.layout.activity_number);

        //Вход в БД
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("users");

        getDataFromDB();
        getNameFromDB();

        //Взятие картинки из бд
        image = String.valueOf(db.getReference("image"));
    }

    public void goNumber(View view) {
        String email = userNameFileld.getText().toString();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listData.size() > 0) {
                    listData.clear();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if (user.getEmail().equals(email)) {
                        listData.add(user.getCarNumber());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        users.addValueEventListener(listener);
    }

    public void goAdd(View view) {
        Intent intent = new Intent(this, Add.class);
        startActivity(intent);
    }

    public void goPhotos(View view) {
        Intent intent = new Intent(this, MyPhotos.class);
        startActivity(intent);
    }

    public void goChats(View view) {
        Intent intent = new Intent(this, ChatNew.class);
        startActivity(intent);
    }

    public void goComment(View view) {
        Intent intent = new Intent(this, SearchCommentUser.class);
        startActivity(intent);
    }

    private void getDataFromDB() {
        //Добавление номера по почте в список
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listData.size() > 0) {
                    listData.clear();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if (Objects.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(), user.getEmail()))
                        listData.add(user.getCarNumber());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        users.addValueEventListener(listener);
    }

    private void getNameFromDB() {
        //Добавление номера по почте в список
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if (Objects.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(), user.getEmail()))
                        textHello.setText("Добро пожаловать," + user.getName() + " , выберите, что хотите сделать в приложении:");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        users.addValueEventListener(listener);
    }

}