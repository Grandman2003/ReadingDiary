//package com.example.readingdiary.Fragments;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatDialogFragment;
//
//import com.example.readingdiary.R;
//import com.google.android.material.datepicker.MaterialDatePicker;
//import com.google.android.material.dialog.MaterialAlertDialogBuilder;
//
//public class ChooseDataDialogFragment extends AppCompatDialogFragment {
////    private ChooseDataDialogListener listener;
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        String title = getResources().getString(R.string.createWithoutNoteDialogTitle);
//        String message = getResources().getString(R.string.createWithoutNoteDialogMessage);
//        final String acceptButtonString = getResources().getString(R.string.createWithoutNoteDialogAcceptButton);
//        final String cancelButtonString = getResources().getString(R.string.createWithoutNoteDialogCancelButton);
//        MaterialDatePicker.Builder builder = new MaterialDatePicker.Builder();
////        builder.setTitle(title);  // заголовок
////        builder.setMessage(message); // сообщение
////
////        builder.setPositiveButton(acceptButtonString, new DialogInterface.OnClickListener() {
////            public void onClick(DialogInterface dialog, int id) {
////                Toast.makeText(getActivity(), acceptButtonString,
////                        Toast.LENGTH_LONG).show();
////                listener.onCreateWithoutNoteClicked();
////
////
////
////
////            }
////        });
////
////        builder.setNegativeButton(cancelButtonString, new DialogInterface.OnClickListener() {
////            public void onClick(DialogInterface dialog, int id) {
////                Toast.makeText(getActivity(), cancelButtonString, Toast.LENGTH_LONG)
////                        .show();
//////                ((EditNoteActivity)getActivity()).finish();
////            }
////        });
//////        builder.setCancelable(true);
//
//        return builder.build();
//    }
//
////    public interface ChooseDataDialogListener {
////        void onCreateWithoutNoteClicked();
////    }
////
////    @Override
////    public void onAttach(Context context) {
////        super.onAttach(context);
////
////        try {
////            listener = (ChooseDataDialogListener) context;
////        } catch (ClassCastException e) {
////            throw new ClassCastException(context.toString()
////                    + "must implement ExampleDialogListener");
////        }
////    }
//}
