package com.example.readingdiary.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.readingdiary.Fragments.SaveDialogFragment;
import com.example.readingdiary.Fragments.SettingsDialogFragment;
import com.example.readingdiary.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.GregorianCalendar;


public class VariousNotebook extends AppCompatActivity implements SaveDialogFragment.SaveDialogListener, SettingsDialogFragment.SettingsDialogListener {
    // класс отвечает за активность с каталогами
    private String TAG_DARK = "dark_theme";
    SharedPreferences sharedPreferences;
    private boolean shouldSave = true;
    private String id;
    private String type;
    public TextInputEditText text;
    private String path;
    private String position;
    MaterialToolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = this.getSharedPreferences(TAG_DARK, Context.MODE_PRIVATE);
        boolean dark = sharedPreferences.getBoolean(TAG_DARK, false);
        if (dark){
            setTheme(R.style.DarkTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coments);
        toolbar = findViewById(R.id.base_toolbar);
        setSupportActionBar(toolbar);
        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        type = args.get("type").toString();
        if (type.equals("description")){
            TextView textView12 = (TextView) findViewById(R.id.textView12);
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


    @Override
    public void onChangeThemeClick(boolean isChecked) {
        if (isChecked){
//                        boolean b = sharedPreferences.getBoolean(TAG_DARK, false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TAG_DARK, true);
            editor.apply();

        }
        else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TAG_DARK, false);
            editor.apply();
            this.recreate();
        }
        this.recreate();
    }

    @Override
    public void onExitClick() {
//        ext =1;
        MainActivity MainActivity = new MainActivity();
        MainActivity.currentUser=null;
        MainActivity.mAuth.signOut();
        Intent intent = new Intent(VariousNotebook.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDelete() {

    }

    @Override
    public void onForgot() {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_settings) {
            int location[] = new int[2];
            toolbar.getLocationInWindow(location);
            int y = getResources().getDisplayMetrics().heightPixels;
            int x = getResources().getDisplayMetrics().widthPixels;

            SettingsDialogFragment settingsDialogFragment = new SettingsDialogFragment(y, x, sharedPreferences.getBoolean(TAG_DARK, false));
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            settingsDialogFragment.show(transaction, "dialog");
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.base_menu, menu);
        return true;
    }
    private void openText() throws Exception{
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
        SaveDialogFragment dialog = new SaveDialogFragment(getApplicationContext());
        dialog.show(getSupportFragmentManager(), "saveNoteDialog");
    }

    @Override
    public void onNotSaveClicked() {
        finish();
    }
}
