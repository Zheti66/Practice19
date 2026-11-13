package com.example.practice19;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AnimImageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anim_image, container, false);
        Init(view);
        return view;
    }

    private void Init(View view) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.logo_rotate_animation);
        view.findViewById(R.id.imageForLoaded).startAnimation(animation);
    }
}