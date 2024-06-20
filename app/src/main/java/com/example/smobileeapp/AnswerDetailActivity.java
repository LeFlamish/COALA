package com.example.smobileeapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
import androidx.core.content.ContextCompat;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerDetailActivity extends AppCompatActivity {

    private static final String TAG = "AnswerDetailActivity";

    private DatabaseReference mDatabase;
    private String answerId;
    private String questionId;
    private String userIdToken;
    private int problemNum;

    private TextView answerDetailTextView;
    private EditText replyEditText;
    private ListView repliesListView;
    private List<Reply> repliesList;
    private ReplyAdapter replyAdapter;
    private ImageButton likeButton;
    private boolean isLiked = false;
    private Map<String, Boolean> likesByUsers;
    private Answer currentAnswer; // 현재 답변 객체, 초기화 필요

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        likesByUsers = new HashMap<>();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("답변 상세 보기");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        replyEditText = findViewById(R.id.replyEditText);
        repliesListView = findViewById(R.id.repliesListView);

        repliesList = new ArrayList<>();
        replyAdapter = new ReplyAdapter(this, repliesList);
        repliesListView.setAdapter(replyAdapter);

        Intent intent = getIntent();
        answerId = intent.getStringExtra("answerId");
        questionId = intent.getStringExtra("questionId");
        userIdToken = intent.getStringExtra("userIdToken");
        problemNum = intent.getIntExtra("problemNum", -1);

        Log.d(TAG, "onCreate: answerId=" + answerId + ", questionId=" + questionId + ", userIdToken=" + userIdToken + ", problemNum=" + problemNum);

        if (answerId == null || questionId == null || userIdToken == null || problemNum == -1) {
            Toast.makeText(this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        answerDetailTextView = findViewById(R.id.answerDetailTextView);

        // 초기에 답변 및 답글 로드
        loadAnswerDetail();
        loadReplies();

        likeButton = findViewById(R.id.likeButton);

        // 좋아요 버튼 초기 상태 설정
        checkUserLikedAnswer();

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike(); // 좋아요 버튼 클릭 시 좋아요 토글
            }
        });

        repliesListView.setOnItemClickListener((parent, view, position, id) -> {
            Reply selectedReply = repliesList.get(position);
            Intent replyDetailIntent = new Intent(AnswerDetailActivity.this, ReplyDetailActivity.class);
            replyDetailIntent.putExtra("replyIdToken", selectedReply.getReplyIdToken());
            replyDetailIntent.putExtra("questionId", questionId);
            replyDetailIntent.putExtra("userIdToken", userIdToken);
            replyDetailIntent.putExtra("problemNum", problemNum);
            replyDetailIntent.putExtra("answerId", answerId);
            startActivity(replyDetailIntent);
        });
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
          onBackPressed();
            return true;
        } else if (id == R.id.action_settings15) {
            DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers").child(answerId);
            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Answer existingAnswer = dataSnapshot.getValue(Answer.class);
                        if (existingAnswer != null && existingAnswer.getUserIdToken().equals(userIdToken)) {
                            showEditConfirmationDialog();
                        } else {
                            Toast.makeText(AnswerDetailActivity.this, "작성자만 답변을 수정할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AnswerDetailActivity.this, "답변을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AnswerDetailActivity.this, "Failed to load question data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } else if (id == R.id.action_settings16) {
            DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).child("answers").child(answerId);
            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Answer existingAnswer = dataSnapshot.getValue(Answer.class);
                        if (existingAnswer != null && existingAnswer.getUserIdToken().equals(userIdToken)) {
                            showDeleteConfirmationDialog();
                        } else {
                            Toast.makeText(AnswerDetailActivity.this, "작성자만 답변을 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AnswerDetailActivity.this, "답변을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AnswerDetailActivity.this, "Failed to load question data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("답변 삭제");
        builder.setMessage("정말로 이 답변을 삭제하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteAnswer(); // Call deleteAnswer method if user clicks Yes
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
        builder.setTitle("답변 수정");
        builder.setMessage("정말로 이 답변을 수정하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(AnswerDetailActivity.this, EditAnswer.class);
                it.putExtra("userIdToken", userIdToken);
                it.putExtra("problemNum", problemNum);
                it.putExtra("questionId", questionId);
                it.putExtra("answerId", answerId);
                startActivity(it);
                finish();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing if user clicks No
            }
        });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAnswerDetail();
        loadReplies();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d("555","back555");
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d("444","back444");
        super.onBackPressed();
        Intent intent = new Intent(AnswerDetailActivity.this, QuestionDetailActivity.class);
        intent.putExtra("userIdToken", userIdToken);
        intent.putExtra("problemNum", problemNum);
        intent.putExtra("questionId", questionId);
        startActivity(intent);
        finish();
    }

    private void deleteAnswer() {
        DatabaseReference answerRef = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId);

        DatabaseReference replyRef = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId)
                .child("replies");

        // 먼저 모든 댓글을 삭제합니다.
        replyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue(); // 각 댓글에 대해 삭제 진행
                }

                decrementAnswerCount();

                // replyCount 필드를 null로 설정하여 삭제합니다.
                answerRef.child("replyCount").setValue(null).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // replyCount 필드가 성공적으로 삭제된 후에 답변을 삭제합니다.
                        answerRef.removeValue().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                // 삭제가 성공하면 해당 필드들도 함께 삭제합니다.
                                answerRef.child("problemNum").removeValue(); // problemNum 필드 삭제
                                // deleted 필드를 true로 설정하여 삭제된 상태를 나타냅니다.
                                answerRef.child("deleted").setValue(true).addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        Toast.makeText(AnswerDetailActivity.this, "답변이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        // 삭제 후에는 질문 상세 화면으로 이동합니다.
                                        Intent intent = new Intent(AnswerDetailActivity.this, QuestionDetailActivity.class);
                                        intent.putExtra("userIdToken", userIdToken);
                                        intent.putExtra("problemNum", problemNum);
                                        intent.putExtra("questionId", questionId);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(AnswerDetailActivity.this, "답변 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AnswerDetailActivity.this, "Failed to load replies: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void decrementAnswerCount() {
        DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);
        questionRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Question question = mutableData.getValue(Question.class);
                if (question == null) {
                    return Transaction.success(mutableData);
                }

                // 기존 answerCount를 감소시킵니다.
                int newAnswerCount = Math.max(question.getAnswerCount() - 1, 0); // 최소값은 0입니다.
                question.setAnswerCount(newAnswerCount);

                // 업데이트된 Question 객체를 저장합니다.
                mutableData.setValue(question);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.e(TAG, "decrementAnswerCount:onComplete: ", databaseError.toException());
                } else {
                    Log.d(TAG, "decrementAnswerCount:onComplete: Answer count decremented successfully");
                }
            }
        });
    }

    private void loadAnswerDetail() {
        DatabaseReference answerRef = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId);

        answerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentAnswer = dataSnapshot.getValue(Answer.class);
                    if (currentAnswer != null) {
                        // 답변 텍스트를 표시
                        answerDetailTextView.setText(currentAnswer.getAnswerText());
                        answerDetailTextView.setTextColor(getResources().getColor(android.R.color.black));

                        // 좋아요 버튼 상태 설정
                        checkUserLikedAnswer();
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

    public void submitReply(View view) {
        String replyText = replyEditText.getText().toString().trim();
        if (TextUtils.isEmpty(replyText)) {
            Toast.makeText(this, "답글을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 버튼을 비활성화합니다.
        view.setEnabled(false);

        long currentTime = System.currentTimeMillis();

        String replyId = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId)
                .child("replies")
                .push().getKey();

        if (replyId == null) {
            Toast.makeText(this, "답글을 제출하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            // 버튼을 다시 활성화합니다.
            view.setEnabled(true);
            return;
        }

        // Create a new Reply object with replyIdToken
        Reply reply = new Reply(replyText, currentTime, userIdToken, replyId);

        // Save the Reply object to the database
        mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId)
                .child("replies")
                .child(replyId)
                .setValue(reply)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "답글이 제출되었습니다.", Toast.LENGTH_SHORT).show();
                        replyEditText.setText("");
                        // 답글 제출 후에는 loadReplies()를 호출하여 새로운 답글을 가져오도록 수정
                        loadReplies();
                    } else {
                        Toast.makeText(this, "답글을 제출하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                    // 버튼을 다시 활성화합니다.
                    view.setEnabled(true);
                });
    }

    private void loadReplies() {
        DatabaseReference replyRef = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId)
                .child("replies");

        replyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                repliesList.clear(); // Clear existing data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Reply reply = snapshot.getValue(Reply.class);
                    if (reply != null) {
                        repliesList.add(reply);
                    }
                }
                replyAdapter.notifyDataSetChanged();
                // 댓글이 추가될 때마다 답변의 댓글 개수 필드를 업데이트
                updateReplyCount(repliesList.size());
                Log.d(TAG, "loadReplies:onDataChange: repliesList size=" + repliesList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "loadReplies:onCancelled", databaseError.toException());
                Toast.makeText(AnswerDetailActivity.this, "Failed to load replies.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateReplyCount(int count) {
        mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId)
                .child("replyCount")
                .setValue(count)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "updateReplyCount: Reply count updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "updateReplyCount: Failed to update Reply count", e));
    }

    private void toggleLike() {
        if (currentAnswer == null) {
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference likesRef = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId)
                .child("likesByUsers")
                .child(currentUserId);

        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User already liked, so remove the like
                    likesRef.removeValue();
                    decrementLikeCount(); // Decrease like count in database
                    isLiked = false;
                    updateLikeButton(isLiked); // Update like button UI
                } else {
                    // User has not liked, so add the like
                    likesRef.setValue(true);
                    incrementLikeCount(); // Increase like count in database
                    isLiked = true;
                    updateLikeButton(isLiked); // Update like button UI
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AnswerDetailActivity.this, "Failed to change like status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void incrementLikeCount() {
        DatabaseReference answerRef = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId);

        answerRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Answer answer = mutableData.getValue(Answer.class);
                if (answer == null) {
                    return Transaction.success(mutableData);
                }

                // Increase the like count
                int newLikeCount = answer.getLikeCount() + 1;
                answer.setLikeCount(newLikeCount);

                // Update the answer object in the database
                mutableData.setValue(answer);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.e(TAG, "incrementLikeCount:onComplete: ", databaseError.toException());
                } else {
                    Log.d(TAG, "incrementLikeCount:onComplete: Like count incremented successfully");
                }
            }
        });
    }

    private void decrementLikeCount() {
        DatabaseReference answerRef = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId);

        answerRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Answer answer = mutableData.getValue(Answer.class);
                if (answer == null) {
                    return Transaction.success(mutableData);
                }

                // Decrease the like count
                int newLikeCount = Math.max(answer.getLikeCount() - 1, 0); // Ensure like count doesn't go below zero
                answer.setLikeCount(newLikeCount);

                // Update the answer object in the database
                mutableData.setValue(answer);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.e(TAG, "decrementLikeCount:onComplete: ", databaseError.toException());
                } else {
                    Log.d(TAG, "decrementLikeCount:onComplete: Like count decremented successfully");
                }
            }
        });
    }

    private void checkUserLikedAnswer() {
        if (currentAnswer == null) {
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference likesRef = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId)
                .child("likesByUsers")
                .child(currentUserId);

        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isLiked = dataSnapshot.exists();
                updateLikeButton(isLiked); // 좋아요 버튼 이미지 업데이트
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AnswerDetailActivity.this, "좋아요 상태를 확인하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLikeButton(boolean liked) {
        if (liked) {
            likeButton.setImageResource(R.drawable.redheart); // 좋아요 이미지
        } else {
            likeButton.setImageResource(R.drawable.lineheart); // 좋아요가 안 눌러진 이미지
        }
    }
}
