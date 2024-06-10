package com.example.smobileeapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuestionDetailActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
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
    private TextView problemTitleTextView;
    private TextView problemNumTextView;
    private TextView problemDifficultyTextView;
    private TextView problemTypeTextView;

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

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        userIdToken = mAuth.getCurrentUser().getUid();
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

        problemNumTextView = findViewById(R.id.problemNum);
        problemTitleTextView = findViewById(R.id.problemTitle);
        problemDifficultyTextView = findViewById(R.id.problemDifficulty);
        problemTypeTextView = findViewById(R.id.problemType);
        questionTitleTextView = findViewById(R.id.questionTitle);
        questionTextView = findViewById(R.id.questionText);


        displayQuestion();
        loadAnswers();
        checkAndDeleteAnswers();

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

        Answer answer = new Answer(answerId, answerText, userIdToken);
        mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers").child(answerId).setValue(answer)
                .addOnSuccessListener(aVoid -> {
                    // 답변이 제출되면 해당 질문의 answerCount를 증가시킵니다.
                    incrementAnswerCount(); // answerCount를 증가시키는 메서드 호출
                    Toast.makeText(this, "답변이 제출되었습니다.", Toast.LENGTH_SHORT).show();
                    answerEditText.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(this, "답변을 제출하는 데 실패했습니다.", Toast.LENGTH_SHORT).show());
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
                        int problemNum = question.getProblemNum();
                        String problemTitle = question.getProblemTitle();
                        String problemDifficulty = question.getProblemTier();
                        String problemType = question.getProblemType();
                        problemNumTextView.setText(String.valueOf(problemNum));
                        problemTitleTextView.setText(problemTitle);
                        problemDifficultyTextView.setText(problemDifficulty);
                        problemTypeTextView.setText(problemType);
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
                String answerId = dataSnapshot.getKey();
                if (answerId == null) {
                    return; // answerId가 null인 경우 메서드를 종료
                }
                for (int i = 0; i < answerList.size(); i++) {
                    Answer answer = answerList.get(i);
                    if (answer != null && answer.getAnswerId() != null && answer.getAnswerId().equals(answerId)) {
                        answerList.remove(i);
                        answerAdapter.notifyDataSetChanged();
                        break;
                    }
                }
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
        DatabaseReference answersRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers");

        // Delete all answers first
        answersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }

                // After all answers are deleted, remove the question
                questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Question question = dataSnapshot.getValue(Question.class);
                            if (question != null && question.getUserIdToken().equals(userIdToken)) {
                                questionRef.removeValue().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Set 'deleted' field to true indicating question is deleted
                                        questionRef.child("deleted").setValue(true).addOnCompleteListener(deletionTask -> {
                                            if (deletionTask.isSuccessful()) {
                                                Toast.makeText(QuestionDetailActivity.this, "질문과 답변이 모두 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(QuestionDetailActivity.this, "질문 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(QuestionDetailActivity.this, "질문 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionDetailActivity.this, "답변을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 모든 답변 필드를 확인하고 delete가 true인 경우 삭제를 수행합니다.
        checkAndDeleteAnswers();
    }

    private void checkAndDeleteAnswers() {
        DatabaseReference answersRef = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers");

        answersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Answer answer = snapshot.getValue(Answer.class);
                    if (answer != null && answer.isDeleted()) {
                        String answerId = snapshot.getKey();
                        if (answerId == null) {
                            continue; // answerId가 null인 경우 루프를 계속
                        }
                        snapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    for (int i = 0; i < answerList.size(); i++) {
                                        Answer currentAnswer = answerList.get(i);
                                        if (currentAnswer != null && currentAnswer.getAnswerId() != null && currentAnswer.getAnswerId().equals(answerId)) {
                                            answerList.remove(i);
                                            answerAdapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }
                                    Toast.makeText(QuestionDetailActivity.this, "답변이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(QuestionDetailActivity.this, "답변 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionDetailActivity.this, "Failed to check answers: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void incrementAnswerCount() {
        DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);
        questionRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Question question = mutableData.getValue(Question.class);
                if (question == null) {
                    return Transaction.success(mutableData);
                }

                // 기존 answerCount를 증가시킵니다.
                question.setAnswerCount(question.getAnswerCount() + 1);

                // 업데이트된 Question 객체를 저장합니다.
                mutableData.setValue(question);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.e(TAG, "incrementAnswerCount:onComplete: ", databaseError.toException());
                } else {
                    Log.d(TAG, "incrementAnswerCount:onComplete: Answer count incremented successfully");
                }
            }
        });
    }
}