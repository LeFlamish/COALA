package com.example.smobileeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuestionBulletinPlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "sectionNumber";
    private ListView listView;
    private String userIdToken;
    private FirebaseAuth mAuth;
    private Context mContext; // 추가된 멤버 변수

    public QuestionBulletinPlaceholderFragment() {
        // Required empty public constructor
    }

    public static QuestionBulletinPlaceholderFragment newInstance(int sectionNumber, String userIdToken) {
        QuestionBulletinPlaceholderFragment fragment = new QuestionBulletinPlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("userIdToken", userIdToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context; // Context를 저장
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        userIdToken = mAuth.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_question_bulletin_placeholder, container, false);
        listView = rootView.findViewById(R.id.answersListView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        userIdToken = getArguments().getString("userIdToken");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin");

        ValueEventListener questionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Question> questionList = new ArrayList<>();
                for (DataSnapshot problemSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionSnapshot : problemSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null) {
                            if (!question.isDeleted()) { // Check if the question is not deleted
                                questionList.add(question);
                            } else {
                                // If the question is deleted, remove it completely from the database
                                questionSnapshot.getRef().removeValue();
                            }
                        }
                    }
                }

                switch (sectionNumber) {
                    case 1: // 문제 번호 순
                        Collections.sort(questionList, Comparator.comparingInt(Question::getProblemNum));
                        break;
                    case 2: // 난이도 순
                        Collections.sort(questionList, new Comparator<Question>() {
                            @Override
                            public int compare(Question q1, Question q2) {
                                int difficultyComparison = Integer.compare(getDifficultyOrder(q1.getProblemTier()), getDifficultyOrder(q2.getProblemTier()));
                                if (difficultyComparison == 0) {
                                    return Integer.compare(q1.getProblemNum(), q2.getProblemNum());
                                } else {
                                    return difficultyComparison;
                                }
                            }

                            private int getDifficultyOrder(String difficulty) {
                                switch (difficulty) {
                                    case "플래티넘Ⅰ":
                                        return 1;
                                    case "플래티넘Ⅱ":
                                        return 2;
                                    case "플래티넘Ⅲ":
                                        return 3;
                                    case "플래티넘Ⅳ":
                                        return 4;
                                    case "플래티넘Ⅴ":
                                        return 5;
                                    case "골드Ⅰ":
                                        return 6;
                                    case "골드Ⅱ":
                                        return 7;
                                    case "골드Ⅲ":
                                        return 8;
                                    case "골드Ⅳ":
                                        return 9;
                                    case "골드Ⅴ":
                                        return 10;
                                    case "실버Ⅰ":
                                        return 11;
                                    case "실버Ⅱ":
                                        return 12;
                                    case "실버Ⅲ":
                                        return 13;
                                    case "실버Ⅳ":
                                        return 14;
                                    case "실버Ⅴ":
                                        return 15;
                                    case "브론즈Ⅰ":
                                        return 16;
                                    case "브론즈Ⅱ":
                                        return 17;
                                    case "브론즈Ⅲ":
                                        return 18;
                                    case "브론즈Ⅳ":
                                        return 19;
                                    case "브론즈Ⅴ":
                                        return 20;
                                    default:
                                        return Integer.MAX_VALUE;
                                }
                            }
                        });
                        break;
                    default: // 최신순
                        Collections.sort(questionList, (q1, q2) -> Long.compare(q2.getTimePosted(), q1.getTimePosted()));
                }

                QuestionAdapter adapter = new QuestionAdapter(mContext, questionList); // mContext 사용
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled
            }
        };

        mDatabase.addValueEventListener(questionListener);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Question question = (Question) parent.getItemAtPosition(position);
            if (question != null) {
                String questionId = question.getQuestionId();
                int problemNum = question.getProblemNum();
                Intent intent = new Intent(mContext, QuestionDetailActivity.class);
                intent.putExtra("questionId", questionId);
                intent.putExtra("problemNum", problemNum);
                intent.putExtra("userIdToken", userIdToken);
                startActivity(intent);
            }
        });
    }
}