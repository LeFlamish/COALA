package com.example.smobileeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터 베이스
    private EditText mEtEmail, mEtPwd;
    private Button mBtnLogin, mBtnRegister, mBtnQuestion, mBtnFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        SharedPreferences preferences = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn && mFirebaseAuth.getCurrentUser() != null) {
            FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
            if (currentUser != null) {
                String userIdToken = currentUser.getUid();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userIdToken", userIdToken);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_register);
        mBtnQuestion = findViewById(R.id.btn_question);
        mBtnFind = findViewById(R.id.btn_find);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable the login button to prevent multiple clicks
                mBtnLogin.setEnabled(false);

                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                if (strEmail.length() == 0) {
                    Toast.makeText(LoginActivity.this, "아이디 입력 필요", Toast.LENGTH_SHORT).show();
                    mBtnLogin.setEnabled(true); // Re-enable the button
                    return;
                } else if (!strEmail.contains("@") || !strEmail.contains(".com")) {
                    Toast.makeText(LoginActivity.this, "이메일 양식 필수", Toast.LENGTH_SHORT).show();
                    mBtnLogin.setEnabled(true); // Re-enable the button
                    return;
                }
                if (strPwd.length() == 0) {
                    Toast.makeText(LoginActivity.this, "비밀번호 입력 필요", Toast.LENGTH_SHORT).show();
                    mBtnLogin.setEnabled(true); // Re-enable the button
                    return;
                } else if (strPwd.length() < 6) {
                    Toast.makeText(LoginActivity.this, "비밀번호는 6자리 이상", Toast.LENGTH_SHORT).show();
                    mBtnLogin.setEnabled(true); // Re-enable the button
                    return;
                }

                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                            if (currentUser != null && !currentUser.isEmailVerified()) {
                                // 사용자가 이메일로 로그인했지만 이메일이 인증되지 않은 경우
                                Toast.makeText(LoginActivity.this, "이메일을 인증해야 로그인할 수 있습니다.", Toast.LENGTH_SHORT).show();
                                mFirebaseAuth.signOut(); // 로그아웃 처리
                            } else {
                                // 로그인 성공
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                String userIdToken = currentUser.getUid();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userIdToken", userIdToken);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }

                        // Re-enable the login button regardless of login success or failure
                        mBtnLogin.setEnabled(true);
                    }
                });
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        mBtnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "아이디는 반드시 이메일 양식을 준수하며, 비밀번호는 반드시 6자리 이상 입력", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = mEtEmail.getText().toString();

                if (emailAddress.length() == 0) {
                    Toast.makeText(LoginActivity.this, "아이디 입력창에 이메일을 입력해주세요. 비밀번호 재설정 코드가 발송됩니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mFirebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("LoginActivity", "Email sent.");
                            // 비밀번호 재설정 이메일이 성공적으로 보내졌음을 사용자에게 알림
                            Toast.makeText(LoginActivity.this, "비밀번호 재설정 이메일이 전송되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 현재 액티비티 종료
        super.onBackPressed();

        // 어플리케이션 종료
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}