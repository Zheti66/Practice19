package com.example.practice19;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignActivity extends AppCompatActivity {

    private AppPreferences appPreferences;
    private EditText LoginTextView, PasswordTextView;
    private EditText NewLoginTextView, NewPasswordTextView, NewAgeTextView, NewNameTextView, NewStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Init();
        CheckSignInfo();
    }
    private void CheckSignInfo() {
        appPreferences = new AppPreferences(this);
        String savedLogin = appPreferences.getLogin();
        String savedPassword = appPreferences.getPassword();

        LoginTextView.setText(savedLogin);
        PasswordTextView.setText(savedPassword);

        // Если данные есть, автоматически вызываем вход
        if (Transform.StringNoNull(savedLogin) && Transform.StringNoNull(savedPassword)) {
            SignIn();
        }
    }
    public void OnSignInClick(View view) {
        SignIn();
    }
    private void Init() {
        TabHost tabHost = findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tabSignIn);
        tabSpec.setIndicator("Вход");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tabSignUp);
        tabSpec.setIndicator("Регистрация");
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        LoginTextView = findViewById(R.id.LoginTextView);
        PasswordTextView = findViewById(R.id.PasswordTextView);

        NewLoginTextView = findViewById(R.id.NewLoginTextView);
        NewPasswordTextView = findViewById(R.id.NewPasswordTextView);
        NewAgeTextView = findViewById(R.id.NewAgeTextView);
        NewNameTextView = findViewById(R.id.NewNameTextView);
        NewStateTextView = findViewById(R.id.NewStateTextView);
    }

    private String getNewLogin() { return NewLoginTextView.getText().toString(); }
    private String getNewPassword() { return NewPasswordTextView.getText().toString(); }
    private String getNewName() { return NewNameTextView.getText().toString(); }
    private String getNewState() { return NewStateTextView.getText().toString(); }
    private int getNewAge() { return Transform.parseIntOrDefault(NewAgeTextView.getText().toString(), 0); }

    private void goNext(String profileId) {
        UserStaticInfo.profileId = profileId;

        // Сохраняем данные для автовхода
        AppPreferences appPreferences = new AppPreferences(this);
        appPreferences.saveLoginAndPassword(LoginTextView.getText().toString(), PasswordTextView.getText().toString());

        // Переходим к экрану загрузки
        Intent intent = new Intent(this, LoadedUserDataActivity.class);
        startActivity(intent);

        // Закрываем экран входа, чтобы по кнопке "Назад" нельзя было вернуться
        finish();
    }

    // Проверка на пустоту с анимацией
    private boolean EditTextNoNullWithAnimation(EditText animationTextView) {
        boolean NoNullText = Transform.StringNoNull(animationTextView.getText().toString());
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.error_edit);
        if (!NoNullText) {
            animationTextView.startAnimation(animation);
        }
        return NoNullText;
    }

    public void SignIn() {
        // Используем &, чтобы проверить оба поля
        if (EditTextNoNullWithAnimation(PasswordTextView) & EditTextNoNullWithAnimation(LoginTextView)) {
            String login = LoginTextView.getText().toString();
            String password = PasswordTextView.getText().toString();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(UserStaticInfo.USERS_SIGN_IN_INFO);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(login).exists()) {
                        Object value = dataSnapshot.child(login).child(UserStaticInfo.PASSWORD).getValue();
                        if (value != null && value.toString().equals(password)) {
                            String profId = dataSnapshot.child(login).child(UserStaticInfo.PROFILE_ID).getValue().toString();

                            // СОХРАНЯЕМ ЛОГИН И ПАРОЛЬ
                            appPreferences.saveLoginAndPassword(login, password);

                            goNext(profId);
                        } else {
                            CantSignIn();
                        }
                    } else {
                        CantSignIn();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    public void SignUp(View view) {
        // Проверяем все поля на пустоту
        if (EditTextNoNullWithAnimation(NewLoginTextView) & EditTextNoNullWithAnimation(NewPasswordTextView) &
                EditTextNoNullWithAnimation(NewNameTextView) & EditTextNoNullWithAnimation(NewStateTextView)) {

            FirebaseDatabase database1 = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database1.getReference(UserStaticInfo.USERS_SIGN_IN_INFO).child(getNewLogin());

            // Проверяем, существует ли уже такой логин
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.child(UserStaticInfo.PASSWORD).exists()) {
                        // Если логина нет, регистрируем
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String id = database.getReference(UserStaticInfo.USERS_PROFILE_INFO).push().getKey();
                        String login = getNewLogin();

                        if (id != null) {
                            database.getReference(UserStaticInfo.USERS_SIGN_IN_INFO).child(login).child(UserStaticInfo.PASSWORD).setValue(getNewPassword());
                            database.getReference(UserStaticInfo.USERS_SIGN_IN_INFO).child(login).child(UserStaticInfo.PROFILE_ID).setValue(id);

                            database.getReference(UserStaticInfo.USERS_PROFILE_INFO).child(id).child("Age").setValue(getNewAge());
                            database.getReference(UserStaticInfo.USERS_PROFILE_INFO).child(id).child("Name").setValue(getNewName());
                            database.getReference(UserStaticInfo.USERS_PROFILE_INFO).child(id).child("State").setValue(getNewState());

                            goNext(id);
                        }
                    } else {
                        // Если логин занят
                        Toast.makeText(SignActivity.this, getResources().getText(R.string.UserExistMessage), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        } else {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) v.vibrate(500);
            Toast.makeText(this, getResources().getString(R.string.NullParametersMessage), Toast.LENGTH_SHORT).show();
        }
    }

    private void CantSignIn() {
        Toast.makeText(this, getResources().getString(R.string.CantSignInMessage), Toast.LENGTH_SHORT).show();
    }
}