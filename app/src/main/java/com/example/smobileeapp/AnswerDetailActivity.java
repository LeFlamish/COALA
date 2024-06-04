package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AnswerDetailActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String answerId;
    private String questionId;
    private String userIdToken;
    private int problemNum;

    private TextView answerDetailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("답변 상세 보기");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        Intent intent = getIntent();
        answerId = intent.getStringExtra("answerId");
        questionId = intent.getStringExtra("questionId");
        userIdToken = intent.getStringExtra("userIdToken");
        problemNum = intent.getIntExtra("problemNum", -1);

        if (answerId == null || questionId == null || userIdToken == null || problemNum == -1) {
            Toast.makeText(this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        answerDetailTextView = findViewById(R.id.answerDetailTextView);

        loadAnswerDetail();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_answer_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish(); // 뒤로가기 버튼 클릭 시 액티비티 종료
            return true;
        } else if (id == R.id.action_settings15) { // 답변 수정
            DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers").child(answerId);
            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Answer existingAnswer = dataSnapshot.getValue(Answer.class);
                        if (existingAnswer != null && existingAnswer.getUserIdToken().equals(userIdToken)) {
                            Intent it = new Intent(AnswerDetailActivity.this, EditAnswer.class);
                            it.putExtra("userIdToken", userIdToken);
                            it.putExtra("problemNum", problemNum);
                            it.putExtra("questionId", questionId);
                            it.putExtra("answerId", answerId);
                            startActivity(it);
                            finish();
                        } else {
                            Toast.makeText(AnswerDetailActivity.this, "작성자만 답변을 수정할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AnswerDetailActivity.this, "답변을 찾을 수 없습니다..", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AnswerDetailActivity.this, "Failed to load question data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } else if (id == R.id.action_settings16) { // 답변 삭제
            deleteAnswer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAnswerDetail();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AnswerDetailActivity.this, QuestionDetailActivity.class);
        intent.putExtra("userIdToken", userIdToken);
        intent.putExtra("problemNum", problemNum);
        intent.putExtra("questionId", questionId);
        startActivity(intent);
        finish();
    }

    private void deleteAnswer() {
        DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers").child(answerId);
        questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Answer answer = dataSnapshot.getValue(Answer.class);
                    if (answer != null && answer.getUserIdToken().equals(userIdToken)) {
                        questionRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AnswerDetailActivity.this, "답변이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent it = new Intent(AnswerDetailActivity.this, QuestionDetailActivity.class);
                                it.putExtra("userIdToken", userIdToken);
                                it.putExtra("questionId", questionId);
                                it.putExtra("problemNum", problemNum);
                                startActivity(it);
                                finish();
                            } else {
                                Toast.makeText(AnswerDetailActivity.this, "파이어베이스에서 답변 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(AnswerDetailActivity.this, "작성자만 답변을 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AnswerDetailActivity.this, "답변이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AnswerDetailActivity.this, "질문을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAnswerDetail() {
        DatabaseReference answerRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers").child(answerId);
        answerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Answer answer = dataSnapshot.getValue(Answer.class);
                    if (answer != null) {
                        answerDetailTextView.setText(answer.getAnswerText()); // Answer 클래스에 getAnswerText 메소드가 있다고 가정
                    }
                } else {
                    Toast.makeText(AnswerDetailActivity.this, "답변을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AnswerDetailActivity.this, "Failed to load answer detail: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}