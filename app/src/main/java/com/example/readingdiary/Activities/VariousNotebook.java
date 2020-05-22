package com.example.readingdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.readingdiary.Fragments.SaveDialogFragment;
import com.example.readingdiary.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.GregorianCalendar;

//import android.os.FileUtils;

public class VariousNotebook extends AppCompatActivity implements SaveDialogFragment.SaveDialogListener {
    private boolean shouldSave = true;
    private String id;
    private String type;
    private TextInputEditText text;
    private String path;
    private String position;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coments);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        type = args.get("type").toString();

        if (type.equals("description")){
            TextView textView12 = (TextView) findViewById(R.id.textView12);
            //textView12.setText("Описание");
        }
        text = (TextInputEditText) findViewById(R.id.editTextComments);
        if (args.get("path") != null){
            path = args.get("path").toString();
            try{
                openText();
                position= args.get("position").toString();
            }
            catch (Exception e){
                Log.e("openTextException", e.toString());
            }
        }
    }

    @Override
    public void onBackPressed() {
        dialogSaveOpen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveClicked() {
        returnResult(saveText());
        super.onBackPressed();
    }

    private void openText() throws Exception{
//        File fileDir1 = getApplicationContext().getDir(type, MODE_PRIVATE);
//        if (!fileDir1.exists()) fileDir1.mkdirs();
//        File file = new File(fileDir1, id+".txt");
        File file = new File(path);
        if (!file.exists()) file.createNewFile();
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder str = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null){
            str.append(line);
            str.append('\n');
        }
        text.setText(str.toString());
        br.close();
    }

    private long saveText(){

        try{
            File file;
            if (path==null){
                File fileDir1 = getApplicationContext().getDir(type + File.pathSeparator + id, MODE_PRIVATE);
                if (!fileDir1.exists()) fileDir1.mkdirs();
                long time = new GregorianCalendar().getTimeInMillis();
                file = new File(fileDir1, time+".txt");
                if (!file.exists()) file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                bw.write(text.getText().toString());
                bw.close();
                return time;
            }
            else{
                file = new File(path);
                if (!file.exists()) file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                bw.write(text.getText().toString());
                bw.close();
                return -2;
            }
//            if (!file.exists()) file.createNewFile();
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
//            bw.write(text.getText().toString());
//            bw.close();
//            return time;


        }
        catch (Exception e){
            Log.e("openException", e.toString());
        }
        return -1;
    }

    private void returnResult(long time){
        if (time == -1) return;
        Intent resultIntent = new Intent();
        if (time == -2) {
            resultIntent.putExtra("updatePath", path);
            resultIntent.putExtra("position", position);
        }
        else{
            resultIntent.putExtra("time", time+"");
        }
        setResult(RESULT_OK, resultIntent);

    }

    private void dialogSaveOpen(){
        SaveDialogFragment dialog = new SaveDialogFragment();
        dialog.show(getSupportFragmentManager(), "saveNoteDialog");
    }

    @Override
    public void onNotSaveClicked() {
        finish();
    }
}
