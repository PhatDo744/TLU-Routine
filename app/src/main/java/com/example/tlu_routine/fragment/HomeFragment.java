package com.example.tlu_routine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.tlu_routine.R;
import com.google.android.material.appbar.MaterialToolbar;

// Tạm thời import adapter trống để không báo lỗi
// import com.example.tlu_routine.adapter.DateAdapter;
// import com.example.tlu_routine.adapter.TaskAdapter;

public class HomeFragment extends Fragment {

    private RecyclerView dateRecyclerView;
    private RecyclerView tasksRecyclerView;
    private MaterialToolbar toolbar;
    private View emptyStateLayout;

    // Tạm thời comment các adapter
    // private DateAdapter dateAdapter;
    // private TaskAdapter taskAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các view từ layout
        toolbar = view.findViewById(R.id.toolbar);
        dateRecyclerView = view.findViewById(R.id.date_recycler_view);
        tasksRecyclerView = view.findViewById(R.id.tasks_recycler_view);
        emptyStateLayout = view.findViewById(R.id.empty_state_layout);

        // Thiết lập Toolbar
        setupToolbar();

        // Thiết lập RecyclerView cho bộ chọn ngày
        setupDateRecyclerView();

        // Thiết lập RecyclerView cho danh sách công việc (ban đầu ẩn đi)
        setupTasksRecyclerView();

        // Tạm thời, chúng ta sẽ luôn hiển thị trạng thái rỗng
        showEmptyState(true);
    }

    private void setupToolbar() {
        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_search) {
                // Xử lý tìm kiếm
                return true;
            } else if (itemId == R.id.action_filter) {
                // Xử lý lọc
                return true;
            } else if (itemId == R.id.action_settings) {
                // Mở màn hình cài đặt
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.fragment_notification);
                return true;
            }
            return false;
        });
    }

    private void setupDateRecyclerView() {
        // Thiết lập LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(layoutManager);

        // Khởi tạo và thiết lập Adapter (sẽ cần tạo lớp DateAdapter)
        // dateAdapter = new DateAdapter(getSampleDates());
        // dateRecyclerView.setAdapter(dateAdapter);
    }

    private void setupTasksRecyclerView() {
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Khởi tạo và thiết lập Adapter (sẽ cần tạo lớp TaskAdapter)
        // taskAdapter = new TaskAdapter(getSampleTasks());
        // tasksRecyclerView.setAdapter(taskAdapter);
    }

    private void showEmptyState(boolean show) {
        if (show) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            tasksRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            tasksRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
