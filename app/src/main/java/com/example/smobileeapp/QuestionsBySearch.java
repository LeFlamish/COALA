package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class QuestionsBySearch extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private String userIdToken;
    private String how;
    private int problemNum;
    private String problemTitle;
    private String difficulty;
    private String problemType;

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

        LinearLayout layout = findViewById(R.id.questions);

        if (how.equals("ByNum")) {
            problemNum = getIntent().getIntExtra("problemNum", -1);
            loadQuestionsByNumber(problemNum, layout);
        } else if (how.equals("ByTitle")) {
            problemTitle = getIntent().getStringExtra("problemTitle");
            loadQuestionsByTitle(problemTitle, layout);
        } else if (how.equals("ByDifficulty")) {
            difficulty = getIntent().getStringExtra("problemDifficulty");
            loadQuestionsByDifficulty(difficulty, layout);
        } else if (how.equals("musthaveall") || how.equals("atleastone") ) {
            problemType = getIntent().getStringExtra("problemType");
            loadQuestionsByType(problemType, layout);
        }
    }

    private void loadQuestionsByNumber(int problemNum, LinearLayout layout) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("QuestionBulletin", "DataSnapshot: " + dataSnapshot.toString());

                layout.removeAllViews(); // 기존 뷰 삭제

                List<Question> questionList = new ArrayList<>();

                for (DataSnapshot questionsSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionSnapshot : questionsSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null && String.valueOf(question.getProblemNum()).contains(String.valueOf(problemNum))) {
                            Integer problemNum = questionSnapshot.child("problemNum").getValue(Integer.class);
                            Long timePosted = questionSnapshot.child("timePosted").getValue(Long.class);

                            // null 체크 후 로그 추가
                            if (problemNum == null || timePosted == null) {
                                Log.e("QuestionBulletin", "problemNum or timePosted is null for questionId: " + question.getQuestionId());
                                continue; // 다음 질문으로 넘어감
                            }

                            questionList.add(question);
                        }
                    }
                }

                // timePosted 값을 기준으로 내림차순 정렬
                Collections.sort(questionList, new Comparator<Question>() {
                    @Override
                    public int compare(Question q1, Question q2) {
                        return Long.compare(q2.getTimePosted(), q1.getTimePosted());
                    }
                });

                // 정렬된 질문을 화면에 표시하는 코드
                for (Question question : questionList) {
                    addQuestionToLayout(question, layout);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 가져오기가 취소될 때 호출됨
                Toast.makeText(QuestionsBySearch.this, "질문을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestionsByTitle(String problemTitle, LinearLayout layout) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("QuestionBulletin", "DataSnapshot: " + dataSnapshot.toString());

                layout.removeAllViews(); // 기존 뷰 삭제

                List<Question> questionList = new ArrayList<>();

                for (DataSnapshot questionsSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionSnapshot : questionsSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null && String.valueOf(question.getProblemTitle()).contains(problemTitle)) {
                            Integer problemNum = questionSnapshot.child("problemNum").getValue(Integer.class);
                            Long timePosted = questionSnapshot.child("timePosted").getValue(Long.class);

                            // null 체크 후 로그 추가
                            if (problemNum == null || timePosted == null) {
                                Log.e("QuestionBulletin", "problemNum or timePosted is null for questionId: " + question.getQuestionId());
                                continue; // 다음 질문으로 넘어감
                            }

                            questionList.add(question);
                        }
                    }
                }

                // timePosted 값을 기준으로 내림차순 정렬
                Collections.sort(questionList, new Comparator<Question>() {
                    @Override
                    public int compare(Question q1, Question q2) {
                        return Long.compare(q2.getTimePosted(), q1.getTimePosted());
                    }
                });

                // 정렬된 질문을 화면에 표시하는 코드
                for (Question question : questionList) {
                    addQuestionToLayout(question, layout);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 가져오기가 취소될 때 호출됨
                Toast.makeText(QuestionsBySearch.this, "질문을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestionsByDifficulty(String difficulty, LinearLayout layout) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("QuestionBulletin", "DataSnapshot: " + dataSnapshot.toString());

                layout.removeAllViews(); // 기존 뷰 삭제

                List<Question> questionList = new ArrayList<>();

                for (DataSnapshot questionsSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionSnapshot : questionsSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null && question.getProblemTier() != null && question.getProblemTier().equals(difficulty)) {
                            Integer problemNum = questionSnapshot.child("problemNum").getValue(Integer.class);
                            Long timePosted = questionSnapshot.child("timePosted").getValue(Long.class);

                            // null 체크 후 로그 추가
                            if (problemNum == null || timePosted == null) {
                                Log.e("QuestionBulletin", "problemNum or timePosted is null for questionId: " + question.getQuestionId());
                                continue; // 다음 질문으로 넘어감
                            }

                            questionList.add(question);
                        }
                    }
                }

                // timePosted 값을 기준으로 내림차순 정렬
                Collections.sort(questionList, new Comparator<Question>() {
                    @Override
                    public int compare(Question q1, Question q2) {
                        return Long.compare(q2.getTimePosted(), q1.getTimePosted());
                    }
                });

                // 정렬된 질문을 화면에 표시하는 코드
                for (Question question : questionList) {
                    addQuestionToLayout(question, layout);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 가져오기가 취소될 때 호출됨
                Toast.makeText(QuestionsBySearch.this, "질문을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestionsByType(String problemType, LinearLayout layout) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("QuestionBulletin", "DataSnapshot: " + dataSnapshot.toString());

                layout.removeAllViews(); // 기존 뷰 삭제

                List<Question> questionList = new ArrayList<>();

                for (DataSnapshot questionsSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionSnapshot : questionsSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null && isValidProblem(question)) {
                            Integer problemNum = questionSnapshot.child("problemNum").getValue(Integer.class);
                            Long timePosted = questionSnapshot.child("timePosted").getValue(Long.class);

                            // null 체크 후 로그 추가
                            if (problemNum == null || timePosted == null) {
                                Log.e("QuestionBulletin", "problemNum or timePosted is null for questionId: " + question.getQuestionId());
                                continue; // 다음 질문으로 넘어감
                            }

                            questionList.add(question);
                        }
                    }
                }

                // timePosted 값을 기준으로 내림차순 정렬
                Collections.sort(questionList, new Comparator<Question>() {
                    @Override
                    public int compare(Question q1, Question q2) {
                        return Long.compare(q2.getTimePosted(), q1.getTimePosted());
                    }
                });

                // 정렬된 질문을 화면에 표시하는 코드
                for (Question question : questionList) {
                    addQuestionToLayout(question, layout);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 가져오기가 취소될 때 호출됨
                Toast.makeText(QuestionsBySearch.this, "질문을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidProblem(Question question) {
        if (how.equals("musthaveall")) {
            return containsAllTypes(question.getProblemType(), problemType);
        } else if (how.equals("atleastone")) {
            return containsAnyType(question.getProblemType(), problemType);
        }
        return false;
    }

    private boolean containsAllTypes(String problemType, String type) {
        String[] typeWords = type.split(", ");
        for (String word : typeWords) {
            if (!problemType.contains(word)) {
                return false;
            }
        }
        return true;
    }

    private boolean containsAnyType(String problemType, String type) {
        String[] typeWords = type.split(", ");
        for (String word : typeWords) {
            if (problemType.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private void addQuestionToLayout(Question question, LinearLayout layout) {
        LinearLayout layout_item = new LinearLayout(QuestionsBySearch.this);
        layout_item.setOrientation(LinearLayout.VERTICAL);
        layout_item.setPadding(20, 10, 20, 10);
        layout_item.setId(question.getProblemNum());

        // questionId를 뷰에 첨부
        layout_item.setTag(R.id.tag_question_id, question.getQuestionId());

        TextView tv_problemNum = new TextView(QuestionsBySearch.this);
        tv_problemNum.setText(String.valueOf(question.getProblemNum()));
        tv_problemNum.setTextSize(30);
        // 문제의 난이도에 따라 색상 변경
        tv_problemNum.setBackgroundColor(getColorForDifficulty(question.getProblemTier()));
        layout_item.addView(tv_problemNum);

        TextView tv_problemTitle = new TextView(QuestionsBySearch.this);
        tv_problemTitle.setText("문제 제목 : " + question.getProblemTitle());
        tv_problemTitle.setTextColor(ContextCompat.getColor(QuestionsBySearch.this, android.R.color.black));
        layout_item.addView(tv_problemTitle);

        TextView tv_questionTitle = new TextView(QuestionsBySearch.this);
        tv_questionTitle.setText("질문 제목 : " + question.getQuestionTitle());
        tv_questionTitle.setTextColor(ContextCompat.getColor(QuestionsBySearch.this, android.R.color.black));
        layout_item.addView(tv_questionTitle);

        layout_item.setOnClickListener(QuestionsBySearch.this);

        layout.addView(layout_item);
    }

    @Override
    public void onClick(View view) {
        LinearLayout layout_item = (LinearLayout) view;
        Integer problemNumInteger = (Integer) layout_item.getId();
        int problemNum = problemNumInteger != null ? problemNumInteger.intValue() : -1; // 기본값은 -1로 설정하거나 다른 적절한 값으로 설정
        String questionId = (String) layout_item.getTag(R.id.tag_question_id); // Get the questionId
        Intent it = new Intent(this, QuestionDetailActivity.class);

        it.putExtra("userIdToken", userIdToken);
        it.putExtra("problemNum", problemNum); // 문제 번호를 인텐트에 추가
        it.putExtra("questionId", questionId); // Add questionId to the intent
        startActivity(it);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 뒤로가기 버튼 클릭 시 액티비티 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getColorForDifficulty(String difficulty) {
        if (difficulty.contains("골드")) {
            return ContextCompat.getColor(this, R.color.gold);
        } else if (difficulty.contains("실버")) {
            return ContextCompat.getColor(this, R.color.silver);
        } else if (difficulty.contains("브론즈")) {
            return ContextCompat.getColor(this, R.color.bronze);
        } else if (difficulty.contains("플래티넘")) {
            return ContextCompat.getColor(this, R.color.platinum);
        } else {
            return ContextCompat.getColor(this, R.color.default_color);
        }
    }
}