package com.example.smobileeapp;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private ImageButton goodhelpButton;
    private boolean isGoodHelp = false;
    private Question currentQuestion;

    private ValueEventListener questionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeViews();
        setupToolbar();
        retrieveIntentExtras();

        if (userIdToken == null || questionId == null || problemNum == -1) {
            Toast.makeText(this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupListeners();
        displayQuestion();
        loadAnswers();
        checkAndDeleteAnswers();
        checkUserGoodHelpQuestion();
    }

    private void initializeViews() {
        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("질문 상세 보기");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back button
        }

        answerEditText = findViewById(R.id.answerEditText);
        answersListView = findViewById(R.id.answersListView);
        problemNumTextView = findViewById(R.id.problemNum);
        problemTitleTextView = findViewById(R.id.problemTitle);
        problemDifficultyTextView = findViewById(R.id.problemDifficulty);
        problemTypeTextView = findViewById(R.id.problemType);
        questionTitleTextView = findViewById(R.id.questionTitle);
        questionTextView = findViewById(R.id.questionText);
        goodhelpButton = findViewById(R.id.goodhelpButton);

        answerList = new ArrayList<>();
        answerAdapter = new AnswerAdapter(this, answerList);
        answersListView.setAdapter(answerAdapter);
    }

    private void setupToolbar() {
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("질문 상세 보기");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back button
        }
    }

    private void retrieveIntentExtras() {
        // Retrieve intent extras
        Intent intent = getIntent();
        userIdToken = mAuth.getCurrentUser().getUid();
        questionId = intent.getStringExtra("questionId");
        problemNum = intent.getIntExtra("problemNum", -1);
    }

    private void setupListeners() {
        // Setup listeners
        goodhelpButton.setOnClickListener(v -> toggleGoodHelp());

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

    private void displayQuestion() {
        DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);

        questionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentQuestion = dataSnapshot.getValue(Question.class);
                    if (currentQuestion != null) {
                        String questionTitle = currentQuestion.getQuestionTitle();
                        String questionText = currentQuestion.getQuestionText();
                        int problemNum = currentQuestion.getProblemNum();
                        String problemTitle = currentQuestion.getProblemTitle();
                        String problemDifficulty = currentQuestion.getProblemTier();
                        String problemType = currentQuestion.getProblemType();
                        problemNumTextView.setText(String.valueOf(problemNum));
                        problemTitleTextView.setText(problemTitle);
                        problemDifficultyTextView.setText(problemDifficulty);
                        problemTypeTextView.setText(problemType);
                        questionTitleTextView.setText(questionTitle);
                        questionTextView.setText(questionText);
                    }
                } else {
                    // Handle case where question does not exist
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

        answersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                answerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Answer answer = snapshot.getValue(Answer.class);
                    if (answer != null) {
                        answerList.add(answer);
                    }
                }

                // likeCount 내림차순으로 정렬 (같으면 원래 순서 유지)
                Collections.sort(answerList, new Comparator<Answer>() {
                    @Override
                    public int compare(Answer a1, Answer a2) {
                        // likeCount 비교 (내림차순)
                        return Integer.compare(a2.getLikeCount(), a1.getLikeCount());
                    }
                });

                answerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionDetailActivity.this, "Failed to load answers: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                            continue;
                        }
                        snapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    answerList.clear();
                                    loadAnswers();
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

    private void toggleGoodHelp() {
        if (currentQuestion == null) {
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference goodHelpRef = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("goodHelpsByUsers")
                .child(currentUserId);

        goodHelpRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    goodHelpRef.removeValue();
                    decrementGoodHelpCount();
                    isGoodHelp = false;
                    updateGoodHelpButton(isGoodHelp);
                } else {
                    goodHelpRef.setValue(true);
                    incrementGoodHelpCount();
                    isGoodHelp = true;
                    updateGoodHelpButton(isGoodHelp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionDetailActivity.this, "좋아요 상태를 변경하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void incrementGoodHelpCount() {
        DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);

        questionRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Question question = mutableData.getValue(Question.class);
                if (question == null) {
                    return Transaction.success(mutableData);
                }

                int currentGoodHelpCount = question.getGoodHelpCount();
                question.setGoodHelpCount(currentGoodHelpCount + 1);

                mutableData.setValue(question);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Toast.makeText(QuestionDetailActivity.this, "추천 수를 증가시키는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void decrementGoodHelpCount() {
        DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);

        questionRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Question question = mutableData.getValue(Question.class);
                if (question == null) {
                    return Transaction.success(mutableData);
                }

                int currentGoodHelpCount = question.getGoodHelpCount();
                if (currentGoodHelpCount > 0) {
                    question.setGoodHelpCount(currentGoodHelpCount - 1);
                }

                mutableData.setValue(question);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Toast.makeText(QuestionDetailActivity.this, "추천 수를 감소시키는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateGoodHelpButton(boolean isGoodHelp) {
        this.isGoodHelp = isGoodHelp;
        if (isGoodHelp) {
            goodhelpButton.setImageResource(R.drawable.thumbs_up);
        } else {
            goodhelpButton.setImageResource(R.drawable.thumbs_up_line);
        }
    }

    private void checkUserGoodHelpQuestion() {
        DatabaseReference goodHelpRef = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("goodHelpsByUsers")
                .child(userIdToken);

        goodHelpRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isGoodHelp = true;
                } else {
                    isGoodHelp = false;
                }
                updateGoodHelpButton(isGoodHelp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionDetailActivity.this, "추천 상태를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        // 모든 답변 필드를 확인하고 delete가 true인 경우 삭제를 수행합니다.
        checkAndDeleteAnswers();
        displayQuestion();
    }

    public void submitAnswer(View view) {
        String answerText = answerEditText.getText().toString().trim();
        if (TextUtils.isEmpty(answerText)) {
            Toast.makeText(this, "답변을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable the submit button to prevent multiple submissions
        view.setEnabled(false);

        String answerId = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers").push().getKey();
        if (answerId == null) {
            Toast.makeText(this, "답변을 제출하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            // Re-enable the submit button in case of failure
            view.setEnabled(true);
            return;
        }

        Answer answer = new Answer(answerId, answerText, userIdToken);
        mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers").child(answerId).setValue(answer)
                .addOnSuccessListener(aVoid -> {
                    // 답변이 제출되면 해당 질문의 answerCount를 증가시킵니다.
                    incrementAnswerCount(); // answerCount를 증가시키는 메서드 호출
                    Toast.makeText(this, "답변이 제출되었습니다.", Toast.LENGTH_SHORT).show();
                    answerEditText.setText("");
                    loadAnswers();
                    // Re-enable the submit button after successful submission
                    view.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "답변을 제출하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    // Re-enable the submit button in case of failure
                    view.setEnabled(true);
                });
    }


    // Other methods like submitAnswer(), editQuestion(), deleteQuestion(), etc. are omitted for brevity

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_question_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_settings13) {
            DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);
            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Question existingQuestion = dataSnapshot.getValue(Question.class);
                        if (existingQuestion != null && existingQuestion.getUserIdToken().equals(userIdToken)) {
                            showEditConfirmationDialog();
                        } else {
                            Toast.makeText(QuestionDetailActivity.this, "작성자만 질문을 수정할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(QuestionDetailActivity.this, "질문을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(QuestionDetailActivity.this, "Failed to load question data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } else if (id == R.id.action_settings14) {
            DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);
            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Question existingQuestion = dataSnapshot.getValue(Question.class);
                        if (existingQuestion != null && existingQuestion.getUserIdToken().equals(userIdToken)) {
                            showDeleteConfirmationDialog();
                        } else {
                            Toast.makeText(QuestionDetailActivity.this, "작성자만 질문을 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(QuestionDetailActivity.this, "질문을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(QuestionDetailActivity.this, "Failed to load question data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("질문 삭제");
        builder.setMessage("정말로 이 질문을 삭제하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteQuestion(); // Call deleteAnswer method if user clicks Yes
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing if user clicks No
            }
        });
        builder.show();
    }

    private void showEditConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("질문 수정");
        builder.setMessage("정말로 이 질문을 수정하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                editQuestion();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing if user clicks No
            }
        });
        builder.show();
    }

    private void editQuestion() {
        if (currentQuestion == null) {
            Toast.makeText(this, "질문을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Current Question: " + currentQuestion);

        String currentUserIdToken = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "Current User ID Token: " + currentUserIdToken);

        if (TextUtils.isEmpty(currentUserIdToken)) {
            Toast.makeText(this, "사용자 정보를 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String questionUserIdToken = currentQuestion.getUserIdToken();
        Log.d(TAG, "Question User ID Token: " + questionUserIdToken);

        if (TextUtils.isEmpty(questionUserIdToken)) {
            Toast.makeText(this, "질문 작성자 정보를 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent editIntent = new Intent(QuestionDetailActivity.this, EditQuestion.class);
        editIntent.putExtra("questionId", questionId);
        editIntent.putExtra("problemNum", problemNum);
        startActivity(editIntent);
    }

    private void deleteQuestion() {
        if (currentQuestion == null) {
            Toast.makeText(this, "질문을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Current Question: " + currentQuestion);

        String currentUserIdToken = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "Current User ID Token: " + currentUserIdToken);

        if (TextUtils.isEmpty(currentUserIdToken)) {
            Toast.makeText(this, "사용자 정보를 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String questionUserIdToken = currentQuestion.getUserIdToken();
        Log.d(TAG, "Question User ID Token: " + questionUserIdToken);

        if (TextUtils.isEmpty(questionUserIdToken)) {
            Toast.makeText(this, "질문 작성자 정보를 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);
        questionRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(QuestionDetailActivity.this, "질문이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(QuestionDetailActivity.this, "질문 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show());
    }
}