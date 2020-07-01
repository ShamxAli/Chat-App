package com.startup.chatapp.image_account;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.startup.chatapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomSheetEdit extends BottomSheetDialogFragment {

    Button button;

    public BottomSheetEdit() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_bottom_sheet_edit, container, false);

       button=view.findViewById(R.id.editbtn);
       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dismiss();
           }
       });
       return view;
    }
}
