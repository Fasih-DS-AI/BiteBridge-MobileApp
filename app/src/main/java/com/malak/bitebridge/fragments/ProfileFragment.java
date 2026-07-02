package com.malak.bitebridge.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.malak.bitebridge.R;
import com.malak.bitebridge.activities.AdminActivity;
import com.malak.bitebridge.activities.LoginActivity;
import com.malak.bitebridge.utils.SessionManager;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_profile, container, false);

        TextView tvUsername = view.findViewById(R.id.tv_username);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        SessionManager session = SessionManager.getInstance(
                requireContext());
        tvUsername.setText("Hi, " + session.getUserName() + "!");

        // Show admin button if user is admin
        if (session.isAdmin()) {
            Button btnAdmin = new Button(requireContext());
            btnAdmin.setText("Admin Panel");
            btnAdmin.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            getResources().getColor(R.color.primary)));
            btnAdmin.setTextColor(getResources().getColor(R.color.white));
            android.widget.LinearLayout.LayoutParams params =
                    new android.widget.LinearLayout.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 16);
            btnAdmin.setLayoutParams(params);
            btnAdmin.setOnClickListener(v ->
                    startActivity(new Intent(getActivity(),
                            AdminActivity.class)));

            ((android.widget.LinearLayout) btnLogout.getParent())
                    .addView(btnAdmin,
                            ((android.widget.LinearLayout) btnLogout
                                    .getParent()).indexOfChild(btnLogout));
        }

        btnLogout.setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }
}