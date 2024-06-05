package com.example.smobileeapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditReplyActivity extends AppCompatActivity {

    private DatabaseReference replyRef;
    private EditText replyEditText;
    private Button updateButton;
    private String userIdToken;
    private int problemNum;
    private String questionId;
    private String answerId;
    private String replyIdToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reply);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("댓글 수정");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Retrieve data from intent
        userIdToken = getIntent().getStringExtra("userIdToken");
        problemNum = getIntent().getIntExtra("problemNum", -1);
        questionId = getIntent().getStringExtra("questionId");
        answerId = getIntent().getStringExtra("answerId");
        replyIdToken = getIntent().getStringExtra("replyIdToken");

        // Initialize Firebase database reference
        replyRef = FirebaseDatabase.getInstance().getReference()
                .child("QuestionBulletin")
                .child(String.valueOf(problemNum))
                .child(questionId)
                .child("answers")
                .child(answerId)
                .child("replies")
                .child(replyIdToken);

        // Initialize views
        replyEditText = findViewById(R.id.replyEditText);
        updateButton = findViewById(R.id.updateButton);

        // Load reply text into EditText
        loadReplyText();

        // Update button click listener
        updateButton.setOnClickListener(v -> updateReply());
    }

    private void loadReplyText() {
        replyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Reply reply = dataSnapshot.getValue(Reply.class);
                    if (reply != null) {
                        String replyText = reply.getReplyText();
                        replyEditText.setText(replyText);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditReplyActivity.this, "Failed to load reply text: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateReply() {
        String newReplyText = replyEditText.getText().toString().trim();
        if (TextUtils.isEmpty(newReplyText)) {
            Toast.makeText(this, "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the reply text
        replyRef.child("replyText").setValue(newReplyText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditReplyActivity.this, "댓글이 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditReplyActivity.this, "댓글 수정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
