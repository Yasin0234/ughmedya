package com.korkutsoftware.ughmedya.ughhaber.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.korkutsoftware.ughmedya.R;
import com.korkutsoftware.ughmedya.ughhaber.adapters.NewsAdapter;
import com.korkutsoftware.ughmedya.ughhaber.models.NewsItem;
import java.util.ArrayList;
import java.util.List;

import com.korkutsoftware.ughmedya.ughhaber.DashboardHaber;

public class SystemDataFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List<NewsItem> savedNewsList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system_data, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new NewsAdapter(savedNewsList, (newsItem, mediaUrl) -> {
            if (getActivity() instanceof DashboardHaber) {
                ((DashboardHaber) getActivity()).switchToEditFragment(newsItem.getTitle(), newsItem.getContent(), mediaUrl, newsItem.getSource());
            }
        });
        recyclerView.setAdapter(adapter);

        fetchSavedNews();

        return view;
    }

    private void fetchSavedNews() {
        db.collection("haberler")
                .orderBy("updated_at", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Hata: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        savedNewsList.clear();
                        for (NewsItem item : value.toObjects(NewsItem.class)) {
                            savedNewsList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}