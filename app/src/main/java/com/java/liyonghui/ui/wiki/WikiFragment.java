package com.java.liyonghui.ui.wiki;

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

public class WikiFragment extends Fragment {

    private WikiViewModel wikiViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        wikiViewModel =
                ViewModelProviders.of(this).get(WikiViewModel.class);
        View root = inflater.inflate(R.layout.fragment_wiki, container, false);
        final TextView textView = root.findViewById(R.id.text_wiki);
        wikiViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}