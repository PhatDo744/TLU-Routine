package com.example.tlu_routine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    private List<Tag> tagList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        setupFragmentResultListeners();
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar_tag_manager);
        recyclerView = view.findViewById(R.id.rv_tags);
        createTagButton = view.findViewById(R.id.btn_create_tag);
    }

    private void setupToolbar() {
        final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());
    }

    private void setupRecyclerView() {
        tagList = getSampleTags();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TagManagerAdapter(tagList, position -> {
            // Hiển thị dialog xác nhận khi nhấn xóa
            Tag tagToDelete = tagList.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("tag_name", tagToDelete.getName());
            bundle.putInt("tag_position", position);
            Navigation.findNavController(requireView()).navigate(R.id.action_tagManagerFragment_to_deleteConfirmationDialogFragment, bundle);
        });

        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        createTagButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_tagManagerFragment_to_addEditTagDialogFragment);
        });
    }

    private void setupFragmentResultListeners() {
        // Lắng nghe kết quả từ dialog Thêm/Sửa
        getParentFragmentManager().setFragmentResultListener("tag_request", this, (requestKey, bundle) -> {
            Tag resultTag = bundle.getParcelable("tag_result");
            int position = bundle.getInt("tag_position", -1);

            if (resultTag != null) {
                if (position == -1) {
                    tagList.add(resultTag);
                    adapter.notifyItemInserted(tagList.size() - 1);
                } else {
                    tagList.set(position, resultTag);
                    adapter.notifyItemChanged(position);
                }
            }
        });

        // Lắng nghe kết quả từ dialog Xóa
        getParentFragmentManager().setFragmentResultListener("delete_request", this, (requestKey, bundle) -> {
            int positionToDelete = bundle.getInt("position_to_delete", -1);
            if (positionToDelete != -1) {
                tagList.remove(positionToDelete);
                adapter.notifyItemRemoved(positionToDelete);
                adapter.notifyItemRangeChanged(positionToDelete, tagList.size());
                Toast.makeText(getContext(), "Đã xóa thẻ", Toast.LENGTH_SHORT).show();
            }
        });
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
