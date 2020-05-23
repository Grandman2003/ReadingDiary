package com.example.readingdiary.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.readingdiary.Activities.VariousNotebook;
import com.example.readingdiary.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SaveDialogFragment extends DialogFragment {
    SaveDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getResources().getString(R.string.saveDialogTitle);
        String message = getResources().getString(R.string.saveDialogMessage);
        final String saveButtonString = getResources().getString(R.string.saveDialogSaveButton);
        final String notSaveButtonString = getResources().getString(R.string.saveDialogNotSaveButton);
        final String returnButtonString = getResources().getString(R.string.saveDialogReturnButton);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(title);  // заголовок
        builder.setMessage(message); // сообщение

        final  VariousNotebook VariousNotebook = new VariousNotebook();
//        final String aLot= "Текст слишком длинный, он не должен привышать 5000 символов"+"\nУ вас"+String.valueOf(q-5000)+"лишних символов";
//        final String aMin = "Введите что-нибудь";



        builder.setPositiveButton(saveButtonString, new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialog, int id) {

//                if (q==0)
//                {
//                    Toast.makeText(getActivity(), aMin,
//                    Toast.LENGTH_LONG).show();
//                }
//                else if (q>5000)
//                {
//                    Toast.makeText(getActivity(), aLot,
//                    Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    Toast.makeText(getActivity(), saveButtonString,
//                    Toast.LENGTH_LONG).show();
//                    listener.onSaveClicked();
//                }
//                q=0;
                Toast.makeText(getActivity(), saveButtonString,
                        Toast.LENGTH_LONG).show();
                listener.onSaveClicked();
            }
        });
        builder.setNeutralButton(returnButtonString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), returnButtonString, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton(notSaveButtonString, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(), notSaveButtonString, Toast.LENGTH_LONG)
                        .show();
                listener.onNotSaveClicked();
            }
        });


        return builder.create();
    }

    public interface SaveDialogListener {
        void onSaveClicked();
        void onNotSaveClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SaveDialogFragment.SaveDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement ExampleDialogListener");
        }
    }
}
