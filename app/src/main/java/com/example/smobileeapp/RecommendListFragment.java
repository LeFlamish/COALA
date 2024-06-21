// RecommendListFragment.java
package com.example.smobileeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecommendListFragment extends Fragment {
    private static final String ARG_TYPE = "type";
    private String type;
    private RecommendListAdapter adapter;
    private DatabaseReference mDatabase;
    private List<RProblem> problemList = new ArrayList<>();
    private boolean isVisibleToUser = false;

    public static RecommendListFragment newInstance(String type) {
        RecommendListFragment fragment = new RecommendListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_list, container, false);
        ListView listView = view.findViewById(R.id.recommend_list_view);

        adapter = new RecommendListAdapter(getActivity(), problemList, type);
        listView.setAdapter(adapter);

        setupDatabaseReference();
        loadProblemData();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && isResumed()) {
            adapter.notifyDataSetChanged(); // Adapter 갱신
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            adapter.notifyDataSetChanged(); // Adapter 갱신
        }
    }

    private void loadProblemData() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                problemList.clear();
                for (DataSnapshot problemSnapshot : dataSnapshot.getChildren()) {
                    RProblem problem = problemSnapshot.getValue(RProblem.class);
                    if (problem != null) {
                        problemList.add(problem);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RecommendListFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void setupDatabaseReference() {
        if (type.equals("bronze") || type.equals("silver") || type.equals("gold") || type.equals("platinum")|| type.equals("diamond")) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Rproblem").child("difficulty").child(type);
        } else if (type.equals("samsung") || type.equals("kakao") || type.equals("naver") || type.equals("lg")) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Rproblem").child("company").child(type);
        } else if (type.equals("BFS") || type.equals("DFS") || type.equals("DP") || type.equals("Greedy")|| type.equals("Back")|| type.equals("구현")) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Rproblem").child("Algorithm").child(type);
        } else {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Rproblem").child("Custom").child(type);
        }
    }
}