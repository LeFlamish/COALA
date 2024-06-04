package com.example.smobileeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuestionBulletinFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private String userIdToken;
    private Context context;

    public static QuestionBulletinFragment newInstance(String userIdToken) {
        QuestionBulletinFragment fragment = new QuestionBulletinFragment();
        Bundle args = new Bundle();
        args.putString("userIdToken", userIdToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userIdToken = getArguments().getString("userIdToken");
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userIdToken = currentUser.getUid();
        } else {
            // currentUser가 null일 경우 처리
            Log.e("QuestionBulletinFragment", "currentUser is null");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_bulletin, container, false);
        LinearLayout layout = view.findViewById(R.id.questions);

        // onViewCreated() 메서드 호출
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("QuestionBulletin", "DataSnapshot: " + dataSnapshot.toString());

                layout.removeAllViews(); // 기존 뷰 삭제

                List<Question> questionList = new ArrayList<>();

                for (DataSnapshot questionsSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionSnapshot : questionsSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null) {
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
                    LinearLayout layout_item = new LinearLayout(context);
                    layout_item.setOrientation(LinearLayout.VERTICAL);
                    layout_item.setPadding(20, 10, 20, 10);
                    layout_item.setId(question.getProblemNum());

                    // questionId를 뷰에 첨부
                    layout_item.setTag(R.id.tag_question_id, question.getQuestionId());

                    TextView tv_problemNum = new TextView(context);
                    tv_problemNum.setText(String.valueOf(question.getProblemNum()));
                    tv_problemNum.setTextSize(30);
                    // 문제의 난이도에 따라 색상 변경
                    tv_problemNum.setBackgroundColor(getColorForDifficulty(question.getProblemTier()));
                    layout_item.addView(tv_problemNum);

                    TextView tv_problemTitle = new TextView(context);
                    tv_problemTitle.setText("문제 제목 : " + question.getProblemTitle());
                    tv_problemTitle.setTextColor(ContextCompat.getColor(context, android.R.color.black)); // 수정 필요
                    layout_item.addView(tv_problemTitle);

                    TextView tv_questionTitle = new TextView(context);
                    tv_questionTitle.setText("질문 제목 : " + question.getQuestionTitle());
                    tv_questionTitle.setTextColor(ContextCompat.getColor(context, android.R.color.black)); // 수정 필요
                    layout_item.addView(tv_questionTitle);

                    layout_item.setOnClickListener(QuestionBulletinFragment.this);

                    layout.addView(layout_item);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // onCancelled 메서드 내부의 코드는 QuestionBulletin 클래스의 것과 동일
                // ...
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 여기에 기존 코드를 넣으십시오.
    }

    @Override
    public void onClick(View view) {
        LinearLayout layout_item = (LinearLayout) view;
        int problemNum = layout_item.getId();
        String questionId = (String) layout_item.getTag(R.id.tag_question_id); // Get the questionId

        Intent it = new Intent(context, QuestionDetailActivity.class); // getActivity()가 아닌 context 사용
        it.putExtra("userIdToken", userIdToken);
        it.putExtra("problemNum", problemNum); // 문제 번호를 인텐트에 추가
        it.putExtra("questionId", questionId); // Add questionId to the intent
        startActivity(it);
    }

    private int getColorForDifficulty(String difficulty) {
        if (difficulty != null) {
            if (difficulty.contains("골드")) {
                return ContextCompat.getColor(context, R.color.gold);
            } else if (difficulty.contains("실버")) {
                return ContextCompat.getColor(context, R.color.silver);
            } else if (difficulty.contains("브론즈")) {
                return ContextCompat.getColor(context, R.color.bronze);
            } else if (difficulty.contains("플래티넘")) {
                return ContextCompat.getColor(context, R.color.platinum);
            } else {
                return ContextCompat.getColor(context, R.color.default_color);
            }
        } else {
            return ContextCompat.getColor(context, R.color.default_color); // 또는 적절한 기본값으로 처리
        }
    }
}
