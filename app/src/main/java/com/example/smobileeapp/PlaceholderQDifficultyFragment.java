package com.example.smobileeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class PlaceholderQDifficultyFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SELECTED_DIFFICULTY = "problemDifficulty";
    private static final String TAG = "PlaceholderQDifficulty";

    private FirebaseAuth mAuth;
    private String selectedDifficulty;
    private QuestionAdapter adapter;
    private List<Question> questionList = new LinkedList<>();

    public PlaceholderQDifficultyFragment() {
    }

    public static PlaceholderQDifficultyFragment newInstance(int sectionNumber, String selectedDifficulty) {
        PlaceholderQDifficultyFragment fragment = new PlaceholderQDifficultyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_SELECTED_DIFFICULTY, selectedDifficulty);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            selectedDifficulty = getArguments().getString(ARG_SELECTED_DIFFICULTY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_placeholder_q_difficulty, container, false);
        ListView listView = rootView.findViewById(R.id.question_list_view);

        adapter = new QuestionAdapter(getActivity(), questionList);
        listView.setAdapter(adapter);

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("QuestionBulletin");
            Query query = null;
            switch (sectionNumber) {
                case 0: // 등록 시간 순
                    query = mDatabase.orderByChild("timePosted");
                    break;
                case 1: // 문제 번호 순
                    query = mDatabase.orderByChild("problemNum");
                    break;
            }

            if (query != null) {
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        questionList.clear();

                        for (DataSnapshot problemSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot questionSnapshot : problemSnapshot.getChildren()) {
                                Question question = questionSnapshot.getValue(Question.class);
                                if (question != null && question.getProblemTier().equals(selectedDifficulty)) {
                                    questionList.add(question);
                                }
                            }
                        }

                        switch (sectionNumber) {
                            case 0: // 등록 시간 순
                                Collections.sort(questionList, Comparator.comparingLong(Question::getTimePosted).reversed());
                                break;
                            case 1: // 문제 번호 순
                                Collections.sort(questionList, Comparator.comparingInt(Question::getProblemNum));
                                break;
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }

        return rootView;
    }
}