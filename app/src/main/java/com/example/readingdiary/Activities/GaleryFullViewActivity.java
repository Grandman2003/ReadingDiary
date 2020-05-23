package com.example.readingdiary.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readingdiary.Fragments.DeleteDialogFragment;
import com.example.readingdiary.Fragments.SetCoverDialogFragment;
import com.example.readingdiary.Fragments.SettingsDialogFragment;
import com.example.readingdiary.R;
import com.example.readingdiary.adapters.GaleryFullViewAdapter;
import com.example.readingdiary.data.LiteratureContract;
import com.example.readingdiary.data.OpenHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GaleryFullViewActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogListener,
        SetCoverDialogFragment.SetCoverDialogListener, SettingsDialogFragment.SettingsDialogListener {
// класс отвечает за активность с каталогами
private String TAG_DARK = "dark_theme";
        SharedPreferences sharedPreferences;
    private RecyclerView galeryFullView;;
    int position;
    private GaleryFullViewAdapter adapter;
    private List<Bitmap> images;
    private List<String> names;
    private boolean changed = false;
    String id;
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
        setContentView(R.layout.activity_galery_full_view);

        toolbar = (MaterialToolbar)findViewById(R.id.base_toolbar);
        setSupportActionBar(toolbar);
        // открываем и сохраняем в список изображения для данной записи
        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        position = Integer.parseInt(args.get("position").toString());
        images = new ArrayList<>();
        names = new ArrayList<>();

        File fileDir1 = getApplicationContext().getDir(getResources().getString(R.string.imagesDir) + File.pathSeparator + id, MODE_PRIVATE);
        File[] files = fileDir1.listFiles();
        if (files != null){
            for (int i = 0; i < files.length; i++){
                images.add(BitmapFactory.decodeFile(files[i].getAbsolutePath()));
                names.add(files[i].getAbsolutePath());
            }
        }


        Button deleteButton = (Button) findViewById(R.id.deleteFullImageButton);
        Button coverButton = (Button) findViewById(R.id.setAsCoverButton);
        galeryFullView = (RecyclerView) findViewById(R.id.galery_full_recycle_view);

        // добавляем адаптер
        adapter = new GaleryFullViewAdapter(images, getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        layoutManager.scrollToPosition(position);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        galeryFullView.setAdapter(adapter);
        galeryFullView.setLayoutManager(layoutManager);

        galeryFullView.setItemAnimator(itemAnimator);
        final LinearLayout buttonsLayout = (LinearLayout) findViewById(R.id.full_view_button_layout);

        final Handler uiHandler = new Handler();

        final Runnable makeLayoutGone = new Runnable(){
            @Override
            public void run(){
                buttonsLayout.setVisibility(View.INVISIBLE);
            }
        };

        // при нажатии на картинку появляется менюшка к ней. Там есть кнопки удаления и установки в качестве обложки
        adapter.setOnItemClickListener(new GaleryFullViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                buttonsLayout.setVisibility(View.VISIBLE);
                position = pos;

                // через 8 секунд меню пропадает
                uiHandler.postDelayed(makeLayoutGone, 8000);
            }
        });


        // кнопка удаления. При нажатии изображение удаляется
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(names.get(position));
                if (file.exists()){
                    dialogDeleteOpen();
                }

            }
        });


        coverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetCoverOpen();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.base_menu, menu);
        return true;
    }

    @Override
    public void onDeleteClicked() {
        File file = new File(names.get(position));
        if (file.exists()){
            file.delete();
            names.remove(position);
            images.remove(position);
            adapter.notifyDataSetChanged();
            // Отмечаем, что список изображений был изменен - нужно для возвращаемого интента
            if (!changed){
                changed=true;
                setResultChanged();
            }
        }
    }

    @Override
    public  void onSetCover() {
        OpenHelper dbHelper = new OpenHelper(getApplicationContext());
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LiteratureContract.NoteTable.COLUMN_COVER_IMAGE, names.get(position));
        Log.d("IMAGE1", "!!! " + names.get(position));
        sdb.update(LiteratureContract.NoteTable.TABLE_NAME, cv, LiteratureContract.NoteTable._ID + " = " + id, null);
        Log.d("IMAGE1", "!!!end " + id);
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
        Intent intent = new Intent(GaleryFullViewActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDelete() {

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

    private void dialogDeleteOpen(){
        DeleteDialogFragment dialog = new DeleteDialogFragment();
        dialog.show(getSupportFragmentManager(), "deleteDialog");
    }

    private void dialogSetCoverOpen(){
        SetCoverDialogFragment dialog = new SetCoverDialogFragment();
        dialog.show(getSupportFragmentManager(), "setCover");
    }

    private void setResultChanged(){
        // создание возвращаемого интента
        Log.d("DELETEIMAGE1", "resultChanged");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("changed", changed);
        setResult(RESULT_OK, returnIntent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
