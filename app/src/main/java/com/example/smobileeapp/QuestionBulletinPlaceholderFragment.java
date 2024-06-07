package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smobileeapp.Question;
import com.example.smobileeapp.QuestionAdapter;
import com.example.smobileeapp.QuestionDetailActivity;
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
    private String userIdToken; // userIdToken 추가
    private FirebaseAuth mAuth;

    public QuestionBulletinPlaceholderFragment() {
        // Required empty public constructor
    }

    public static QuestionBulletinPlaceholderFragment newInstance(int sectionNumber, String userIdToken) { // userIdToken 인자 추가
        QuestionBulletinPlaceholderFragment fragment = new QuestionBulletinPlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("userIdToken", userIdToken); // userIdToken을 Argument에 추가
        fragment.setArguments(args);
        return fragment;
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

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin");

        ValueEventListener questionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Question> questionList = new ArrayList<>();
                for (DataSnapshot problemSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionSnapshot : problemSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null) {
                            questionList.add(question);
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
                                // 먼저 난이도를 비교
                                int difficultyComparison = Integer.compare(getDifficultyOrder(q1.getProblemTier()), getDifficultyOrder(q2.getProblemTier()));
                                // 난이도가 같으면 문제 번호로 비교
                                if (difficultyComparison == 0) {
                                    return Integer.compare(q1.getProblemNum(), q2.getProblemNum());
                                } else {
                                    return difficultyComparison;
                                }
                            }

                            private int getDifficultyOrder(String difficulty) {
                                // 각 난이도에 대한 순서를 정의하여 반환
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
                                        return Integer.MAX_VALUE; // 그 외의 경우는 가장 큰 값으로 처리하여 가장 뒤로 정렬
                                }
                            }
                        });
                        break;
                    default: // 최신순
                        Collections.sort(questionList, (q1, q2) -> Long.compare(q2.getTimePosted(), q1.getTimePosted()));
                }

                QuestionAdapter adapter = new QuestionAdapter(getActivity(), questionList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled
            }
        };

        mDatabase.addValueEventListener(questionListener);

        // ListView item click listener
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            // Handle item click
            Question question = (Question) parent.getItemAtPosition(position);
            if (question != null) {
                String questionId = question.getQuestionId();
                int problemNum = question.getProblemNum(); // 질문 번호 가져오기
                // Open question detail activity
                Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
                intent.putExtra("questionId", questionId);
                intent.putExtra("problemNum", problemNum); // 질문 번호도 전달
                intent.putExtra("userIdToken", userIdToken); // userIdToken도 전달
                startActivity(intent);
            }
        });
    }
}
