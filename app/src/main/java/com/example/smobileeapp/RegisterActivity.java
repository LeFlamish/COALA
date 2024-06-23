package com.example.smobileeapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터 베이스

    private EditText mEtEmail, mEtPwd;
    private Button mBtnRegister, mBtnQuestion;
    private CardView mCardLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mBtnRegister = findViewById(R.id.btn_register);
        mBtnQuestion = findViewById(R.id.btn_question);
        mCardLogin = findViewById(R.id.card_login);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 처리 시작
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                if (strEmail.length() == 0) {
                    mEtEmail.setError("아이디 입력 필요");
                    //Toast.makeText(RegisterActivity.this, "아이디 입력 필요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                    mEtEmail.setError("이메일 양식 필수");
                    //Toast.makeText(RegisterActivity.this, "이메일 양식 필수", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (strPwd.length() == 0) {
                    mEtEmail.setError("비밀번호 입력 필요");
                    //Toast.makeText(RegisterActivity.this, "비밀번호 입력 필요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (strPwd.length() < 6) {
                    mEtEmail.setError("비밀번호는 6자리 이상");
                    //Toast.makeText(RegisterActivity.this, "비밀번호는 6자리 이상", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            sendEmailVerification(firebaseUser); // 이메일 인증 보내기

                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                String userEmail = firebaseUser.getEmail();
                                String userPassword = strPwd;

                                UserAccount newUser = new UserAccount(userId, userEmail, userPassword);

                                mDatabaseRef.child("users").child(userId).setValue(newUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> databaseTask) {
                                                if (databaseTask.isSuccessful()) {
                                                    //Toast.makeText(RegisterActivity.this, "회원가입 및 데이터베이스 추가 완료", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "회원가입은 성공했지만 데이터베이스 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException) exception).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        Toast.makeText(RegisterActivity.this, "이미 가입된 이메일입니다.", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(RegisterActivity.this, "회원가입에 실패했습니다: " + errorCode, Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        mCardLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 버튼 클릭 시 실행할 코드
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        mBtnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(android.R.id.content),
                        "아이디는 반드시 이메일 양식을 준수하며, 비밀번호는 반드시 6자리 이상 입력",
                        Snackbar.LENGTH_LONG).show();

                // Toast.makeText(RegisterActivity.this, "아이디는 반드시 이메일 양식을 준수하며, 비밀번호는 반드시 6자리 이상 입력", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(android.R.id.content),
                                    "이메일 인증을 위한 링크가 " + user.getEmail() + " 으로 전송됨.",
                                    Snackbar.LENGTH_LONG).show();


                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "이메일 인증 메일 전송에 실패했습니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}