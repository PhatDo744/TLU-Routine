package com.example.tlu_routine.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.tlu_routine.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ View
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        // Lấy NavController từ NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();


        // Thiết lập sự kiện click cho menu của BottomAppBar
        bottomAppBar.setOnMenuItemClickListener(item -> {
            // Kiểm tra xem có phải là trang hiện tại không để tránh load lại
            if (item.getItemId() == navController.getCurrentDestination().getId()) {
                return false;
            }
            return NavigationUI.onNavDestinationSelected(item, navController)
                    || super.onOptionsItemSelected(item);
        });

        // Thiết lập sự kiện click cho FAB
        fabAdd.setOnClickListener(view -> {
            // Điều hướng đến màn hình thêm công việc khi có
            // Ví dụ: navController.navigate(R.id.addEditEventFragment);
            Toast.makeText(this, "Thêm công việc mới", Toast.LENGTH_SHORT).show();
        });

        // Thêm Listener để theo dõi thay đổi màn hình
        // và ẩn/hiện BottomAppBar tương ứng
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if ((destination.getId() == R.id.nav_tags) || (destination.getId() == R.id.addEditTagDialogFragment) || (destination.getId() == R.id.deleteConfirmationDialogFragment)) {
                // Ẩn thanh điều hướng trên màn hình Quản lý thẻ
                bottomAppBar.setVisibility(View.GONE);
                fabAdd.setVisibility(View.GONE);
            } else {
                // Hiện thanh điều hướng trên các màn hình khác (Home, Stats,...)
                bottomAppBar.setVisibility(View.VISIBLE);
                fabAdd.setVisibility(View.VISIBLE);
            }
        });
    }
}
