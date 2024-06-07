package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuestionsBySearch extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String userIdToken;
    private String how;
    private int problemNum;
    private String problemTitle;
    private String difficulty;
    private String problemType;
    private QuestionAdapter questionAdapter;
    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_by_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("검색한 질문 목록");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        how = intent.getStringExtra("how");

        ListView listView = findViewById(R.id.question_list_view);
        questionList = new ArrayList<>();
        questionAdapter = new QuestionAdapter(this, questionList); // QuestionAdapter와 item_question 사용
        listView.setAdapter(questionAdapter);

        if (how.equals("ByNum")) {
            problemNum = getIntent().getIntExtra("problemNum", -1);
            getSupportActionBar().setTitle(problemNum + "번 질문 보기");
            loadQuestionsByNumber(problemNum);
        } else if (how.equals("ByTitle")) {
            problemTitle = getIntent().getStringExtra("problemTitle");
            getSupportActionBar().setTitle(problemTitle + " 제목 질문 보기");
            loadQuestionsByTitle(problemTitle);
        } else if (how.equals("ByDifficulty")) {
            difficulty = getIntent().getStringExtra("problemDifficulty");
            getSupportActionBar().setTitle("\"" + difficulty + "\" 난이도 질문 보기");
            loadQuestionsByDifficulty(difficulty);
        } else if (how.equals("musthaveall") || how.equals("atleastone")) {
            problemType = getIntent().getStringExtra("problemType");
            StringBuilder modifiedType = new StringBuilder(problemType);
            if (modifiedType.length() > 12) {
                modifiedType.replace(12, modifiedType.length(), "…");
            }
            getSupportActionBar().setTitle("\"" + modifiedType + "\"" + " 유형 질문 보기");
            loadQuestionsByType(problemType);
        } else if (how.equals("searchmyquestion")) {
            getSupportActionBar().setTitle("나의 질문 보기");
            loadMyQuestions(userIdToken);
        }
    }

    private void loadQuestionsByNumber(int problemNum) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("QuestionBulletin", "DataSnapshot: " + dataSnapshot.toString());

                questionList.clear(); // 기존 데이터 삭제

                for (DataSnapshot questionsSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionSnapshot : questionsSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null && String.valueOf(question.getProblemNum()).contains(String.valueOf(problemNum))) {
                            questionList.add(question);
                        }
                    }
                }

                Collections.sort(questionList, new Comparator<Question>() {
                    @Override
                    public int compare(Question q1, Question q2) {
                        return Long.compare(q2.getTimePosted(), q1.getTimePosted());
                    }
                });

                questionAdapter.notifyDataSetChanged(); // 어댑터 갱신
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(QuestionsBySearch.this, "질문을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestionsByTitle(String problemTitle) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("QuestionBulletin", "DataSnapshot: " + dataSnapshot.toString());

                questionList.clear();

                for (DataSnapshot questionsSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionSnapshot : questionsSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null && String.valueOf(question.getProblemTitle()).contains(problemTitle)) {
                            questionList.add(question);
                        }
                    }
                }

                Collections.sort(questionList, new Comparator<Question>() {
                    @Override
                    public int compare(Question q1, Question q2) {
                        return Long.compare(q2.getTimePosted(), q1.getTimePosted());
                    }
                });

                questionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(QuestionsBySearch.this, "질문을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestionsByDifficulty(String difficulty) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("QuestionBulletin", "DataSnapshot: " + dataSnapshot.toString());

                questionList.clear();

                for (DataSnapshot questionsSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionSnapshot : questionsSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null && question.getProblemTier() != null && question.getProblemTier().equals(difficulty)) {
                            questionList.add(question);
                        }
                    }
                }

                Collections.sort(questionList, new Comparator<Question>() {
                    @Override
                    public int compare(Question q1, Question q2) {
                        return Long.compare(q2.getTimePosted(), q1.getTimePosted());
                    }
                });

                questionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(QuestionsBySearch.this, "질문을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestionsByType(String problemType) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("QuestionBulletin", "DataSnapshot: " + dataSnapshot.toString());

                questionList.clear();

                for (DataSnapshot questionsSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionSnapshot : questionsSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null && isValidProblem(question)) {
                            questionList.add(question);
                        }
                    }
                }

                Collections.sort(questionList, new Comparator<Question>() {
                    @Override
                    public int compare(Question q1, Question q2) {
                        return Long.compare(q2.getTimePosted(), q1.getTimePosted());
                    }
                });

                questionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(QuestionsBySearch.this, "질문을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMyQuestions(String userIdToken) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin").child(userIdToken);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("QuestionBulletin", "DataSnapshot: " + dataSnapshot.toString());

                questionList.clear();

                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                    Question question = questionSnapshot.getValue(Question.class);
                    if (question != null) {
                        questionList.add(question);
                    }
                }

                Collections.sort(questionList, new Comparator<Question>() {
                    @Override
                    public int compare(Question q1, Question q2) {
                        return Long.compare(q2.getTimePosted(), q1.getTimePosted());
                    }
                });

                questionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(QuestionsBySearch.this, "질문을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidProblem(Question question) {
        if (how.equals("musthaveall")) {
            String[] keywords = problemType.split("\\s*,\\s*");
            for (String keyword : keywords) {
                if (!question.getProblemTitle().contains(keyword)) {
                    return false;
                }
            }
            return true;
        } else if (how.equals("atleastone")) {
            String[] keywords = problemType.split("\\s*,\\s*");
            for (String keyword : keywords) {
                if (question.getProblemTitle().contains(keyword)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

