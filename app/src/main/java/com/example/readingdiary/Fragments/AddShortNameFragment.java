package com.example.readingdiary.Fragments;

import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.readingdiary.Activities.MainActivity;
import com.example.readingdiary.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AddShortNameFragment extends AppCompatDialogFragment {
    private AddShortNameDialogListener listener;
    private Context context;
    private String error;
    private Activity activity;

    public AddShortNameFragment(Context context){
        this.context=context;
    }
    public AddShortNameFragment(Context context, String error){
        this.context = context;
        this.error = error;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = "Введите никнейм";
        final String acceptButtonString = "Ввод";
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_short_name, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setView(view);
        builder.setTitle(title);  // заголовок
        final TextView editTextView = view.findViewById(R.id.editText2);
        TextView errorTextView = view.findViewById(R.id.textView16);
        editTextView.setText("");
        if (error != null){
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setError(error);
            if (error.equals("space")) errorTextView.setText("Никнейм не может содержать пробелы");
            else if (error.equals("long")) errorTextView.setText("Длина никнейма не может превышать 20 символов");
            else if (error.equals("null")) errorTextView.setText("Никнейм не должен быть пустым");
            else if (error.equals("number")) errorTextView.setText("Никнейм не должен быть числом");
            else if (error.equals("exists")) errorTextView.setText("Введенный никнейм уже существует");


//            errorTextView.setError(error);
//            errorTextView.setText(error);
        }
        else{
            errorTextView.setVisibility(View.GONE);
        }
        builder.setPositiveButton(acceptButtonString, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (editTextView != null){
                    listener.onEnterClicked(editTextView.getText().toString());
                }

            }
        });
        builder.setCancelable(false);
        return builder.create();



//        builder.setView(getActivity().getLayoutInflater().inflate(R.layout.enterNameDialog, null));
//        final EditText input = new EditText(getActivity());
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        input.setLayoutParams(lp);
//        builder.setView(input);
//
//        if (error != null){
//            final TextView errorView = new TextView(getActivity());
//            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT);
//            errorView.setLayoutParams(lp1);
//            errorView.setText(error);
//            builder.setView(errorView);
//        }



//        return builder.create();
    }

    public interface AddShortNameDialogListener {
        void onEnterClicked(String name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (AddShortNameDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement ExampleDialogListener");
        }
    }
}
