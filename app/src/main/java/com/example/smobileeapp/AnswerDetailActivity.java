package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("답변 상세 보기");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        loadReplies(); // 이 부분을 추가합니다.

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
            finish();
            return true;
        } else if (id == R.id.action_settings15) {
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
            deleteAnswer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAnswerDetail();
        loadReplies();
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
        DatabaseReference answerRef = mDatabase.child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId);

        answerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Answer answer = dataSnapshot.getValue(Answer.class);
                    if (answer != null) {
                        // 답변 텍스트를 표시
                        answerDetailTextView.setText(answer.getAnswerText());
                        answerDetailTextView.setTextColor(getResources().getColor(android.R.color.black));

                        // 답변에 대한 답글 목록 로드
                        loadReplies();
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
}