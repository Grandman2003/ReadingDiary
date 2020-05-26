package com.example.readingdiary.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.readingdiary.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsDialogFragment extends DialogFragment {
    SettingsDialogListener listener;
    int y;
    int x;
    boolean isChecked;
    AlertDialog materialDialogs;
    int position;
    public SettingsDialogFragment(int y, int x, boolean isChecked) {
        this.y = y;
        this.x = x;
        this.isChecked = isChecked;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(getActivity().getLayoutInflater().inflate(R.layout.switch_dialog, null));
        builder.setCancelable(true);
        materialDialogs = builder.create();
        WindowManager.LayoutParams wmlp = materialDialogs.getWindow().getAttributes();
//        Log.d("qwerty47", toolbarHeight + " " + y + " " + wmlp.height + " " + materialDialogs.findViewById(R.id.switchTheme).getHeight() + " " +  materialDialogs.findViewById(R.id.exitButton).getHeight());
        wmlp.y=-2*y / 7;
        wmlp.x = x/2;
        materialDialogs.show();
        final SwitchMaterial switchMaterial = materialDialogs.findViewById(R.id.switchTheme);

        if (isChecked==true){
            switchMaterial.setChecked(true);
        }
            switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.onChangeThemeClick(switchMaterial.isChecked());
                    Log.d("qwerty33", "qwertyui");
                }
            });
        TextView textView = materialDialogs.findViewById(R.id.exitButton);
        TextView txtDel = materialDialogs.findViewById(R.id.textView13);
        TextView txtForg = materialDialogs.findViewById(R.id.textView);
        final TextView idTextView = materialDialogs.findViewById(R.id.idText);
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("PublicID").document(user).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null)    {
                            idTextView.setText("Ваш ник\n"+documentSnapshot.get("id"));
                        }
                    }
                });


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onExitClick();
            }
        });
        txtDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete();
            }
        });
        txtForg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onForgot();
            }
        });




        Window w = materialDialogs.getWindow();
            w.setLayout(x / 2, w.getAttributes().height);
        wmlp = materialDialogs.getWindow().getAttributes();
        wmlp.x = x/2;
        return materialDialogs;
    }

    @Nullable
    @Override
    public Dialog getDialog() {
        return materialDialogs;
    }

    public interface SettingsDialogListener{
        void onChangeThemeClick(boolean t);
        void onExitClick();
        void onDelete();
        void onForgot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SettingsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement ExampleDialogListener");
        }
    }
}
