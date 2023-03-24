package com.example.diplom2

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.widget.RelativeLayout
import android.widget.EditText
import android.annotation.SuppressLint
import android.os.Bundle
import com.example.diplom2.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.tasks.OnCompleteListener
import android.content.ContentValues
import android.view.LayoutInflater
import android.content.DialogInterface
import android.text.TextUtils
import com.google.android.material.snackbar.Snackbar
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.diplom2.models.User
import com.google.android.gms.tasks.OnFailureListener
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var  btnSignin: Button
    lateinit var btnRegister: Button
    lateinit var auth: FirebaseAuth
    lateinit  var db: FirebaseDatabase
    lateinit var users: DatabaseReference
    lateinit var root: RelativeLayout
    lateinit var email: EditText
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnSignin = findViewById(R.id.buttonSignIn)
        btnRegister = findViewById(R.id.buttonRegister)
        root = findViewById(R.id.root_allement)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        users = db.getReference("users")
        this.btnRegister.setOnClickListener({ showRegisterWindow() })
        btnSignin.setOnClickListener({ showSignInWindow() })

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(
                        ContentValues.TAG,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                println("токен$token")

//                         Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            })
    }

    private fun showSignInWindow() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Войти")
        dialog.setMessage("Введите данные для входа")
        val inflater = LayoutInflater.from(this)
        val signInWindow = inflater.inflate(R.layout.sign_in_window, null)
        dialog.setView(signInWindow)
        email = signInWindow.findViewById(R.id.emailField)
        val password = signInWindow.findViewById<EditText>(R.id.passField)
        dialog.setNegativeButton("Отменить") { dialogInterface, which -> dialogInterface.dismiss() }
        dialog.setPositiveButton(
            "Войти",
            DialogInterface.OnClickListener { dialogInterface, which ->
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(root, "Введите email", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                if (password.text.toString().length < 6) {
                    Snackbar.make(root, "Введите пароль больше 6 символов", Snackbar.LENGTH_SHORT)
                        .show()
                    return@OnClickListener
                }
                auth.signInWithEmailAndPassword(
                    email.getText().toString(),
                    password.text.toString()
                )
                    .addOnSuccessListener {
                        startActivity(Intent(this@MainActivity, Number::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Snackbar.make(
                            root,
                            "Ошибка авторизации. " + e.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
            })
        dialog.show()
    }

    private fun showRegisterWindow() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Регистрация")
        dialog.setMessage("Введите данные для регистрации")
        val inflater = LayoutInflater.from(this)
        val registerWindow = inflater.inflate(R.layout.register_window, null)
        dialog.setView(registerWindow)
        val email = registerWindow.findViewById<EditText>(R.id.emailField)
        val password = registerWindow.findViewById<EditText>(R.id.passField)
        val name = registerWindow.findViewById<EditText>(R.id.nameField)
        val phone = registerWindow.findViewById<EditText>(R.id.phoneField)
        val carNumber = registerWindow.findViewById<EditText>(R.id.carNumberField)
        dialog.setNegativeButton("Отменить") { dialogInterface, which -> dialogInterface.dismiss() }
        dialog.setPositiveButton(
            "Добавить",
            DialogInterface.OnClickListener { dialogInterface, which ->
                if (TextUtils.isEmpty(email.text.toString())) {
                    Snackbar.make(root, "Введите email", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                if (TextUtils.isEmpty(name.text.toString())) {
                    Snackbar.make(root, "Введите ваше имя", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                if (TextUtils.isEmpty(phone.text.toString())) {
                    Snackbar.make(root, "Введите ваш телефон", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                if (password.text.toString().length < 6) {
                    Snackbar.make(root, "Введите пароль больше 6 символов", Snackbar.LENGTH_SHORT)
                        .show()
                    return@OnClickListener
                }
                if (TextUtils.isEmpty(carNumber.text.toString())) {
                    Snackbar.make(root, "Введите номерной знак автомобиля", Snackbar.LENGTH_SHORT)
                        .show()
                    return@OnClickListener
                }
                //Регистрация пользователя
                auth.createUserWithEmailAndPassword(
                    email.text.toString(),
                    password.text.toString()
                )
                    .addOnSuccessListener {
                        val user = User(
                            name.text.toString(), email.text.toString(),
                            phone.text.toString(), carNumber.text.toString(), ArrayList()
                        )
                        users.child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .setValue(user)
                            .addOnSuccessListener {
                                Snackbar.make(
                                    root,
                                    "Пользователь добавлен",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                    }.addOnFailureListener { e ->
                        Snackbar.make(
                            root,
                            "Ошибка регистрации(возможно email уже занят). " + e.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
            })
        dialog.show()
    }
}