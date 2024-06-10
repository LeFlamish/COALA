package com.example.smobileeapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReplyDetailActivity extends AppCompatActivity {
    private String userIdToken;
    private int problemNum;
    private String questionId;
    private String replyIdToken;
    private String answerId;
    private String replyUserIdToken; // 댓글 작성자의 사용자 ID를 저장할 변수 추가
    private TextView replyDetailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("댓글 상세 보기");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        problemNum = intent.getIntExtra("problemNum", -1);
        questionId = intent.getStringExtra("questionId");
        replyIdToken = intent.getStringExtra("replyIdToken");
        answerId = intent.getStringExtra("answerId");

        replyDetailTextView = findViewById(R.id.replyDetailTextView);

        loadReplyDetail();
    }

    private void loadReplyDetail() {
        DatabaseReference replyRef = FirebaseDatabase.getInstance().getReference()
                .child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId)
                .child("replies")
                .child(replyIdToken);

        replyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Reply reply = dataSnapshot.getValue(Reply.class);
                    if (reply != null) {
                        replyDetailTextView.setText(reply.getReplyText());
                        replyUserIdToken = reply.getUserIdToken(); // 댓글 작성자의 사용자 ID 저장
                    }
                } else {
                    // 댓글이 없는 경우에 대한 처리
                    replyDetailTextView.setText("댓글이 존재하지 않습니다.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터베이스 읽기 작업이 취소된 경우에 대한 처리
                replyDetailTextView.setText("데이터베이스에서 댓글을 읽어오는 데 실패했습니다.");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reply_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings17) {
            editReply();
            return true;
        } else if (id == R.id.action_settings18) {
            deleteReply();
            return true;
        } else if (id == android.R.id.home) {
            finish(); // 뒤로가기 버튼 클릭 시 액티비티 종료
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // 댓글 수정 기능 추가
    private void editReply() {
        // 현재 로그인된 사용자의 아이디 가져오기
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 댓글을 작성한 사용자의 아이디와 현재 로그인된 사용자의 아이디 비교
        if (replyUserIdToken != null && replyUserIdToken.equals(currentUserID)) {
            // 수정 화면으로 이동
            Intent intent = new Intent(ReplyDetailActivity.this, EditReplyActivity.class);
            intent.putExtra("userIdToken", userIdToken);
            intent.putExtra("problemNum", problemNum);
            intent.putExtra("questionId", questionId);
            intent.putExtra("answerId", answerId);
            intent.putExtra("replyIdToken", replyIdToken);
            startActivity(intent);
        } else {
            // 현재 로그인된 사용자가 댓글 작성자가 아닌 경우
            Toast.makeText(ReplyDetailActivity.this, "본인이 작성한 댓글만 수정할 수 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 댓글 삭제 기능 추가
    private void deleteReply() {
        DatabaseReference replyRef = FirebaseDatabase.getInstance().getReference()
                .child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId)
                .child("replies")
                .child(replyIdToken);

        // 현재 로그인된 사용자의 아이디 가져오기
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 댓글을 작성한 사용자의 아이디와 현재 로그인된 사용자의 아이디 비교
        replyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Reply reply = dataSnapshot.getValue(Reply.class);
                    if (reply != null) {
                        String replyUserID = reply.getUserIdToken();
                        // 댓글을 작성한 사용자의 아이디와 현재 로그인된 사용자의 아이디가 같으면 삭제
                        if (replyUserID.equals(currentUserID)) {
                            replyRef.removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ReplyDetailActivity.this, "댓글 삭제에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                    finish(); // 현재 액티비티 종료
                                } else {
                                    // 댓글 삭제 실패
                                    Toast.makeText(ReplyDetailActivity.this, "댓글 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // 현재 로그인된 사용자가 댓글 작성자가 아닌 경우
                            Toast.makeText(ReplyDetailActivity.this, "본인이 작성한 댓글만 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // 댓글이 존재하지 않는 경우
                    Toast.makeText(ReplyDetailActivity.this, "댓글이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터베이스 읽기 작업이 취소된 경우에 대한 처리
                Toast.makeText(ReplyDetailActivity.this, "데이터베이스에서 댓글을 읽어오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}