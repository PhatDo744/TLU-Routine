package com.example.tlu_routine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlu_routine.R;
import com.example.tlu_routine.adapter.TagManagerAdapter;
import com.example.tlu_routine.model.Tag;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class TagManagerFragment extends Fragment {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private TagManagerAdapter adapter;
    private MaterialButton createTagButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View
        toolbar = view.findViewById(R.id.toolbar_tag_manager);
        recyclerView = view.findViewById(R.id.rv_tags);
        createTagButton = view.findViewById(R.id.btn_create_tag);

        // Thiết lập Toolbar
        setupToolbar();

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Thiết lập sự kiện cho nút tạo thẻ
        createTagButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_tagManagerFragment_to_addEditTagDialogFragment);
        });
    }

    private void setupToolbar() {
        // Lấy NavController và thiết lập sự kiện quay lại
        final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Khởi tạo và thiết lập adapter với dữ liệu mẫu
        adapter = new TagManagerAdapter(getContext(), getSampleTags());
        recyclerView.setAdapter(adapter);
    }

    private List<Tag> getSampleTags() {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("Học tập", "#4CAF50", R.drawable.ic_book));
        tags.add(new Tag("Công việc gấp", "#F44336", R.drawable.ic_briefcase));
        tags.add(new Tag("Cá nhân", "#9C27B0", R.drawable.ic_person));
        tags.add(new Tag("Thi cuối kỳ HK2 Năm 3", "#FFC107", R.drawable.ic_trophy));
        return tags;
    }
}
