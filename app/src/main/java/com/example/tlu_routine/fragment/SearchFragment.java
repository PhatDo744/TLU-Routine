package com.example.tlu_routine.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlu_routine.R;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private ImageView icClear;
    private RecyclerView rvSearchResults;
    private TextView tvEmptyState;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.et_search);
        icClear = view.findViewById(R.id.ic_clear);
        rvSearchResults = view.findViewById(R.id.rv_search_results);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);

        setupSearchBar();
    }

    private void setupSearchBar() {
        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Handle when search bar is focused
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                icClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        icClear.setOnClickListener(v -> {
            etSearch.setText("");
            rvSearchResults.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        });
    }
}