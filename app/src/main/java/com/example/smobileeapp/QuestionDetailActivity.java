package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuestionDetailActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String userIdToken;
    private String questionId;
    private int problemNum;

    private EditText answerEditText;
    private ListView answersListView;
    private AnswerAdapter answerAdapter;
    private List<Answer> answerList;
    private TextView questionTitleTextView;
    private TextView questionTextView;

    private ChildEventListener answersListener;
    private ValueEventListener questionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("질문 상세 보기");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        questionId = intent.getStringExtra("questionId");
        problemNum = intent.getIntExtra("problemNum", -1);

        if (userIdToken == null || questionId == null || problemNum == -1) {
            Toast.makeText(this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        answerEditText = findViewById(R.id.answerEditText);
        answersListView = findViewById(R.id.answersListView);

        answerList = new ArrayList<>();
        answerAdapter = new AnswerAdapter(this, answerList);
        answersListView.setAdapter(answerAdapter);

        questionTitleTextView = findViewById(R.id.questionTitle);
        questionTextView = findViewById(R.id.questionText);

        displayQuestion();
        loadAnswers();

        answersListView.setOnItemClickListener((parent, view, position, id) -> {
            Answer selectedAnswer = answerList.get(position);
            Intent answerDetailIntent = new Intent(QuestionDetailActivity.this, AnswerDetailActivity.class);
            answerDetailIntent.putExtra("answerId", selectedAnswer.getAnswerId());
            answerDetailIntent.putExtra("questionId", questionId);
            answerDetailIntent.putExtra("userIdToken", userIdToken);
            answerDetailIntent.putExtra("problemNum", problemNum);
            startActivity(answerDetailIntent);
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_question_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            finish(); // 뒤로가기 버튼 클릭 시 액티비티 종료
            return true;
        } else if (id == R.id.action_settings13) {
            DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);
            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Question existingQuestion = dataSnapshot.getValue(Question.class);
                        if (existingQuestion != null && existingQuestion.getUserIdToken().equals(userIdToken)) {
                            Intent it = new Intent(QuestionDetailActivity.this, EditQuestion.class);
                            it.putExtra("userIdToken", userIdToken);
                            it.putExtra("problemNum", problemNum);
                            it.putExtra("questionId", questionId);
                            startActivity(it);
                        } else {
                            Toast.makeText(QuestionDetailActivity.this, "작성자만 질문을 수정할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(QuestionDetailActivity.this, "질문을 찾을 수 없습니다..", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(QuestionDetailActivity.this, "Failed to load question data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } else if (id == R.id.action_settings14) {
            deleteQuestion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submitAnswer(View view) {
        String answerText = answerEditText.getText().toString().trim();
        if (TextUtils.isEmpty(answerText)) {
            Toast.makeText(this, "답변을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String answerId = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers").push().getKey();
        if (answerId == null) {
            Toast.makeText(this, "답변을 제출하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Answer answer = new Answer(answerId, answerText, userIdToken, problemNum);
        mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers").child(answerId).setValue(answer);
        answerEditText.setText("");
    }

    private void displayQuestion() {
        DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);

        questionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Question question = dataSnapshot.getValue(Question.class);
                    if (question != null) {
                        String questionTitle = question.getQuestionTitle();
                        String questionText = question.getQuestionText();
                        questionTitleTextView.setText(questionTitle);
                        questionTextView.setText(questionText);
                    }
                } else {
                    //Toast.makeText(QuestionDetailActivity.this, "질문이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionDetailActivity.this, "질문을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        };

        questionRef.addValueEventListener(questionListener);
    }

    private void loadAnswers() {
        DatabaseReference answersRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers");

        answersListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Answer answer = dataSnapshot.getValue(Answer.class);
                if (answer != null) {
                    answerList.add(answer);
                    answerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Update the UI if an answer is changed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Update the UI if an answer is removed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Update the UI if an answer is moved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionDetailActivity.this, "답변을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        };

        answersRef.addChildEventListener(answersListener);
    }

    private void deleteQuestion() {
        DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);
        questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Question question = dataSnapshot.getValue(Question.class);
                    if (question != null && question.getUserIdToken().equals(userIdToken)) {
                        questionRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(QuestionDetailActivity.this, "질문이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                                finish();
                            } else {
                                Toast.makeText(QuestionDetailActivity.this, "파이어베이스에서 질문 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(QuestionDetailActivity.this, "작성자만 질문을 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QuestionDetailActivity.this, "질문이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionDetailActivity.this, "질문을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove listeners to prevent memory leaks
        if (questionListener != null) {
            DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);
            questionRef.removeEventListener(questionListener);
        }
        if (answersListener != null) {
            DatabaseReference answersRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers");
            answersRef.removeEventListener(answersListener);
        }
    }

}