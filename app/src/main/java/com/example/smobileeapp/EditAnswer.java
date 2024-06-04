package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditAnswer extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String userIdToken;
    private String questionId;
    private int problemNum;
    private String answerId;
    private EditText answerEditText;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_answer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("답변 수정");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        questionId = intent.getStringExtra("questionId");
        problemNum = intent.getIntExtra("problemNum", -1);
        answerId = intent.getStringExtra("answerId");

        if (userIdToken == null || questionId == null || problemNum == -1 || answerId == null) {
            Toast.makeText(this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        answerEditText = findViewById(R.id.answerEditText);
        updateButton = findViewById(R.id.updateButton);

        // Retrieve the current answer text and populate the EditText
        loadAnswerText();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAnswer();
            }
        });
    }

    private void loadAnswerText() {
        DatabaseReference answerRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum))
                .child(questionId).child("answers").child(answerId);

        answerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Answer answer = dataSnapshot.getValue(Answer.class);
                    if (answer != null) {
                        String answerText = answer.getAnswerText();
                        answerEditText.setText(answerText);
                    }
                } else {
                    Toast.makeText(EditAnswer.this, "답변이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditAnswer.this, "Failed to load answer detail: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAnswer() {
        String newAnswerText = answerEditText.getText().toString().trim();
        if (TextUtils.isEmpty(newAnswerText)) {
            Toast.makeText(this, "답변을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference answerRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum))
                .child(questionId).child("answers").child(answerId);

        answerRef.child("answerText").setValue(newAnswerText, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(EditAnswer.this, "답변이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(EditAnswer.this, AnswerDetailActivity.class);
                    it.putExtra("userIdToken", userIdToken);
                    it.putExtra("problemNum", problemNum);
                    it.putExtra("questionId", questionId);
                    it.putExtra("answerId", answerId);
                    startActivity(it);
                    finish(); // Finish the activity after successful update
                } else {
                    Toast.makeText(EditAnswer.this, "Failed to update answer: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditAnswer.this, AnswerDetailActivity.class);
        intent.putExtra("userIdToken", userIdToken);
        intent.putExtra("problemNum", problemNum);
        intent.putExtra("questionId", questionId);
        intent.putExtra("answerId", answerId);
        startActivity(intent);
        finish();
    }
}