package com.java.liyonghui.ui.cluster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.java.liyonghui.R;
import com.java.liyonghui.ui.news.NewsFragment;

public class ClusterFragment extends Fragment {

    private ClusterViewModel clusterViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clusterViewModel =
                ViewModelProviders.of(this).get(ClusterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cluster, container, false);
        final TextView textView = root.findViewById(R.id.text_cluster);
        clusterViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

}