package com.example.appquanli.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appquanli.R;
import com.example.appquanli.adapter.UserAdapter;
import com.example.appquanli.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        getUserFromFire();

    }
    private void initView(View view) {
        recyclerView=view.findViewById(R.id.recycleview_user);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
    }
    private void getUserFromFire() {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<User> users=new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                User user=new User();
                                user.setId(documentSnapshot.getLong("id").intValue());
                                user.setUsername(documentSnapshot.getString("username"));
                                users.add(user);
                            }
                            if(users.size()>0){
                                userAdapter=new UserAdapter(getContext(),users);
                                recyclerView.setAdapter(userAdapter);
                            }
                        }
                    }
                });
    }
}
