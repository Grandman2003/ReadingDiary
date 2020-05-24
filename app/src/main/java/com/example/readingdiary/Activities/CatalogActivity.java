package com.example.readingdiary.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readingdiary.Classes.DeleteFilesClass;
import com.example.readingdiary.Classes.Directory;
import com.example.readingdiary.Classes.Note;
import com.example.readingdiary.Classes.RealNote;
import com.example.readingdiary.Fragments.SettingsDialogFragment;
import com.example.readingdiary.Fragments.SortDialogFragment;
import com.example.readingdiary.R;
import com.example.readingdiary.adapters.CatalogButtonAdapter;
import com.example.readingdiary.adapters.CatalogSortsSpinnerAdapter;
import com.example.readingdiary.adapters.RecyclerViewAdapter;
import com.example.readingdiary.data.LiteratureContract;
import com.example.readingdiary.data.LiteratureContract.NoteTable;
import com.example.readingdiary.data.OpenHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.util.ArrayList;


public class CatalogActivity extends AppCompatActivity implements SortDialogFragment.SortDialogListener,
        SettingsDialogFragment.SettingsDialogListener {
    // класс отвечает за активность с каталогами

    OpenHelper dbHelper;
    RecyclerViewAdapter mAdapter;
    SQLiteDatabase sdb;
    String parent = "./";
    ArrayList<Note> notes;
    ArrayList<String> buttons;
    //    ArrayList<String> directories;
    RecyclerView recyclerView;
    RecyclerView buttonView;
    CatalogButtonAdapter buttonAdapter;
    CatalogSortsSpinnerAdapter sortsAdapter;
    Button findButton;
    EditText findText1;
    EditText findText;
    ArrayList<String> sortsList;
    Spinner sortsSpinner;
    MaterialToolbar toolbar;
    TextView counterText;
    String comp="";
    String sortTitles1, sortTitles2, sortAuthors1, sortAuthors2, sortRating1, sortRating2;
    int order;
    int startPos;
    int NOTE_REQUEST_CODE = 12345;
    int CREATE_NOTE_REQUEST_CODE = 12346;
    public boolean action_mode = false;
    int count=0;
    int menuType = 0;
    int ext =0;
    ArrayList<RealNote> selectionRealNotesList = new ArrayList<>();
    ArrayList<Directory> selectionDirectoriesList = new ArrayList<>();
    String[] choices = new String[]{"Сортировка по названиям в лексикографическом порядке",
            "Сортировка по названиям в обратном лексикографическим порядке",
            "Сортировка по автору в лексиграфическом порядке",
            "Сортировка по автору в обратном лексиграфическим порядке",
            "Сортировка по возрастанию рейтинга",
            "Сортировка по убыванию рейтинга"};
    private String TAG_DARK = "dark_theme";
    SharedPreferences sharedPreferences;
    Button online;
    MainActivity mein = new MainActivity();


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
        setContentView(R.layout.activity_catalog);

//        Получение разрешений на чтение и запись
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("FILE3", "Permission is granted");

        } else {
            Log.v("FILE3", "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("FILE3", "Permission is granted");

        } else {

            Log.v("FILE3", "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }

        //Toast.makeText(CatalogActivity.this,mein.frgEm,Toast.LENGTH_LONG).show();



        dbHelper = new OpenHelper(this);

        sdb = dbHelper.getReadableDatabase();
        notes = new ArrayList<Note>(); // список того, что будет отображаться в каталоге.
        buttons = new ArrayList<String>(); // Список пройденный каталогов до текущего
        setSortTitles();
//        initSortsList();
        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        counterText.setText("Каталог");
//        toolbar.inflateMenu(R.menu.menu_catalog);

        buttons.add(parent);
        selectAll(); // чтение данных из бд

        setAdapters();


        online= (Button) findViewById(R.id.bOnline);
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, OnlineActivity.class);
                startActivity(intent);

            }
        });


        // Кнопка добавление новой активности
        FloatingActionButton addNote = (FloatingActionButton) findViewById(R.id.addNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // Возвращается intent, если пользователь действительно добавил активность
                Intent intent = new Intent(CatalogActivity.this, EditNoteActivity.class);

                intent.putExtra("path", parent);
                Log.d("putExtra", parent + " !");
                startActivityForResult(intent, CREATE_NOTE_REQUEST_CODE);


            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        getMenuInflater().inflate(R.menu.base_menu, menu);

        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == NOTE_REQUEST_CODE){
            // если изменился путь до записи, добавилась новая запись, то переходим к этой записи
            if (data.getExtras().get("deleted") != null){
                long id = Long.parseLong(data.getExtras().get("id").toString());
                int index = deleteNote(id);
                if (index != -1){
                    mAdapter.notifyItemRemoved(index);
                }
            }

            if (data.getExtras().get("path") != null){
                parent = data.getExtras().get("path").toString();
                reloadRecyclerView();
                reloadButtonsView();
            }
        }
        if (requestCode==CREATE_NOTE_REQUEST_CODE && resultCode == RESULT_OK){
            if ((data.getExtras().get("deleted") == null && data.getExtras().get("noNote") == null)){
                Intent intent = new Intent(CatalogActivity.this, NoteActivity.class); // вызов активности записи
                intent.putExtra("id", data.getExtras().get("id").toString()); // передаем id активности в бд, чтобы понять какую активность надо показывать
                intent.putExtra("changed", "true");
                startActivityForResult(intent, NOTE_REQUEST_CODE);
            }
            else if (data.getExtras().get("noNote") != null){
                parent = data.getExtras().get("path").toString();
                reloadRecyclerView();
                reloadButtonsView();
            }

        }



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
        ext =1;
        mein.currentUser=null;
        mein.mAuth.signOut();
        Intent intent = new Intent(CatalogActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDelete() {
        mein.mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful())
                 {
                     Toast.makeText(CatalogActivity.this,"Аккаунт удалён",Toast.LENGTH_SHORT).show();
                     Intent intent = new Intent(CatalogActivity.this, MainActivity.class);
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     startActivity(intent);
                 }
                 else
                     {
                         Toast.makeText(CatalogActivity.this, "Ошибка: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                     }
             }
         });

    }

    @Override
    public void onForgot()
    {
        Intent intent = new Intent(CatalogActivity.this, ForgotPswActivity.class);
        startActivity(intent);
//        mein.mAuth.sendPasswordResetEmail(mein.ETemail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//               if (task.isSuccessful())
//               {
//                   Toast.makeText(CatalogActivity.this,"На вашу почто отправлено письмо. \nДля сброса пароля перейдите по ссылке в нём.",Toast.LENGTH_SHORT).show();
//               }
//               else
//                   {
//                       Toast.makeText(CatalogActivity.this, "Ошибка: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                   }
//            }
//        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.item_settings){
            int location[] = new int[2];
            toolbar.getLocationInWindow(location);
            int y = getResources().getDisplayMetrics().heightPixels;
            int x = getResources().getDisplayMetrics().widthPixels;

            SettingsDialogFragment settingsDialogFragment = new SettingsDialogFragment(y, x, sharedPreferences.getBoolean(TAG_DARK, false));
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            settingsDialogFragment.show(transaction, "dialog");
//            settingsDialogFragment.getDialog().getWindow();


//
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setView(this.getLayoutInflater().inflate(R.layout.switch_dialog, null));
//            builder.setCancelable(true);
//            AlertDialog materialDialogs = builder.create();
//            WindowManager.LayoutParams wmlp = materialDialogs.getWindow().getAttributes();
//            wmlp.y=-y / 2 + 260;
//            wmlp.x = x/2;
//            materialDialogs.show();
//            Window w = materialDialogs.getWindow();
//            w.setLayout(400, w.getAttributes().height);
//            w.setGravity(Gravity.NO_GRAVITY);
//            materialDialogs.show();
//             if (findViewById(R.id.textView10)==null){
//                 Log.d("qwerty34", "null");
//             }
//            public interface SettingsDialogListener{
//                void onSettingsClick(int position);
//            };

//            final SwitchMaterial switchMaterial = (SwitchMaterial) materialDialogs.findViewById(R.id.switchTheme);
//            switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked){
////                        boolean b = sharedPreferences.getBoolean(TAG_DARK, false);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putBoolean(TAG_DARK, true);
//                        editor.apply();
//
//                    }
//                    else{
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putBoolean(TAG_DARK, false);
//                        editor.apply();
//                        this.recreate();
//                    }
//                    Toast.makeText(getApplicationContext(), ""+isChecked, Toast.LENGTH_LONG).show();
//                }
//            });




//
////            AlertDialog.Builder builder = new AlertDialog.Builder(this);
////            builder.setView(this.getLayoutInflater().inflate(R.layout.switch_dialog, null));
//////            builder.set
////            builder.setCancelable(true);
////            AlertDialog materialDialogs = builder.create();
////            materialDialogs.show();
////            Window w = materialDialogs.getWindow();
//////            w.setLayout(400, 400);
////            w.getAttributes().width=400;
////            w.getAttributes().y=400;
////
////            materialDialogs.show();
//            SettingsDialogFragment settingsDialogFragment = new SettingsDialogFragment(-y / 2 + 260);
////            alertDialog.getWindow().setLayout(200, 200);
////            alertDialog.show();
////            Toast.makeText(getApplicationContext(), alertDialog.getWindow().get)
////
//            FragmentManager manager = getSupportFragmentManager();
////
////            //myDialogFragment.show(manager, "dialog");
////
//            FragmentTransaction transaction = manager.beginTransaction();
//            settingsDialogFragment.show(transaction, "dialog");
//            settingsDialogFragment.getDialog().getWindow().setLayout(300, 300);


        }
        if (item.getItemId()== R.id.item_delete){

            action_mode=false;
            mAdapter.setActionMode(false);
            deleteSelectedRealNote();
            deleteSelectedDirectories();
            mAdapter.notifyDataSetChanged();
//            RecyclerViewAdapter recyclerViewAdapter = (RecyclerViewAdapter) mAdapter;
//            recyclerViewAdapter.updateAdapter(selectionList);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_catalog);
            toolbar.inflateMenu(R.menu.base_menu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            menuType = 0;
            counterText.setText("Каталог");
            count=0;
//            selectionList.clear();


        }
        if (item.getItemId() == R.id.item_search){
            counterText.setVisibility(View.GONE);
            findText1.setVisibility(View.VISIBLE);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_search);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            menuType = 2;

        }
        if (item.getItemId() == R.id.item_search1){
//            counterText.setVisibility(View.VISIBLE);
//            findText1.setVisibility(View.GONE);
//            toolbar.getMenu().clear();
//            toolbar.inflateMenu(R.menu.menu_catalog);

//                buttons.clear();
            if (!findText1.getText().toString().equals("")){
                notes.clear();
                selectTitle(findText1.getText().toString());
                mAdapter.notifyDataSetChanged();
                findText1.clearComposingText();
            }
            counterText.setVisibility(View.VISIBLE);
            findText1.setVisibility(View.GONE);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_catalog);
            toolbar.inflateMenu(R.menu.base_menu);
            menuType = 0;
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        if (item.getItemId() == R.id.item_sort){
            SortDialogFragment sortDialogFragment = new SortDialogFragment(choices);
//            SaveDialogFragment saveDialogFragment = new SaveDialogFragment();
            FragmentManager manager = getSupportFragmentManager();
            //myDialogFragment.show(manager, "dialog");
            FragmentTransaction transaction = manager.beginTransaction();
            sortDialogFragment.show(transaction, "dialog");
        }

        if (item.getItemId() == android.R.id.home){
            if (menuType==0){
                finish();
            }
            else if (menuType == 1){
                action_mode=false;
                mAdapter.setActionMode(false);
                mAdapter.notifyDataSetChanged();
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_catalog);
                toolbar.inflateMenu(R.menu.base_menu);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                menuType = 0;
                counterText.setText("Каталог");
                count=0;
            }
            else if (menuType==2){
                findText1.clearComposingText();
                counterText.setVisibility(View.VISIBLE);
                findText1.setVisibility(View.GONE);
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_catalog);
                toolbar.inflateMenu(R.menu.base_menu);
                menuType = 0;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteSelectedRealNote(){
        File fileArr[] = new File[selectionRealNotesList.size() * 4];
        for (int i = 0; i < selectionRealNotesList.size() * 4; i+=4){
            String id = selectionRealNotesList.get(i / 4).getID() + "";
            sdb.delete(NoteTable.TABLE_NAME, NoteTable._ID + " = ? ", new String[]{id+""});
            notes.remove(selectionRealNotesList.get(i / 4));
            fileArr[i] = getApplicationContext().getDir(getResources().getString(R.string.imagesDir) + File.pathSeparator + id, MODE_PRIVATE);
            fileArr[i + 1] = getApplicationContext().getDir(getResources().getString(R.string.descriptionDir) + File.pathSeparator + id, MODE_PRIVATE);
            fileArr[i + 2] = getApplicationContext().getDir(getResources().getString(R.string.commentDir) + File.pathSeparator + id, MODE_PRIVATE);
            fileArr[i + 3] = getApplicationContext().getDir(getResources().getString(R.string.quoteDir) + File.pathSeparator + id, MODE_PRIVATE);
        }
        DeleteFilesClass deleteClass = new DeleteFilesClass(fileArr);
        deleteClass.start();

        selectionRealNotesList.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void deleteSelectedDirectories(){
        for (Directory directory : selectionDirectoriesList){
            notes.remove(directory);
            deleteDirectory(directory.getDirectory());
//            deleteFilesInDirectory(directory.getDirectory())
            sdb.delete(LiteratureContract.PathTable.TABLE_NAME, LiteratureContract.PathTable.COLUMN_CHILD + " = ? ",
                    new String[]{directory.getDirectory()});
        }
        selectionDirectoriesList.clear();

    }

    @Override
    public void onSortClick(int position) {
        Log.d("strangeSort", choices[position]);
        startSort(choices[position]);
    }


    public void deleteDirectory(String path){
        String[] projection1 = {
                LiteratureContract.PathTable._ID,
                LiteratureContract.PathTable.COLUMN_PARENT,
                LiteratureContract.PathTable.COLUMN_CHILD

        };
        Cursor mCursor1 = sdb.query(LiteratureContract.PathTable.TABLE_NAME, projection1,
                LiteratureContract.PathTable.COLUMN_PARENT + " = ?", new String[] {path},
                null, null, null);
        int count = 0;
        int idColumnIndex1 = mCursor1.getColumnIndex(LiteratureContract.PathTable._ID);
        int childColumnIndex = mCursor1.getColumnIndex(LiteratureContract.PathTable.COLUMN_CHILD);
        while (mCursor1.moveToNext()){
//            long currentId = mCursor1.getLong(idColumnIndex1);
//            String currentChild = mCursor1.getString(childColumnIndex);
            deleteDirectory(mCursor1.getString(childColumnIndex));
            count++;
//            notes.add(new Directory(currentId, currentChild));
        }
        mCursor1.close();
        sdb.delete(LiteratureContract.PathTable.TABLE_NAME, LiteratureContract.PathTable.COLUMN_PARENT + " = ?",
                new String[]{path});

        String[] projection = {
                NoteTable._ID
        };
        Cursor cursor = sdb.query(
                NoteTable.TABLE_NAME,
                projection,
                LiteratureContract.NoteTable.COLUMN_PATH + " = ?",
                new String[] {path},
                null,
                null,
                null);
        int idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
        File[] arr = new File[cursor.getCount()];
        int k = 0;
        while (cursor.moveToNext()) {
            int currentID = cursor.getInt(idColumnIndex);
            File dir0 = new File(path);
//            arr[k] = getApplicationContext().getDir(path + "/" + currentID, MODE_PRIVATE);
            arr[k] = new File(dir0 + File.pathSeparator + currentID);
            // Тут содержится pathSeparator, на что ide ругается
            k++;
            sdb.delete(NoteTable.TABLE_NAME, NoteTable._ID + "= ?", new String[]{currentID + ""});
        }
        cursor.close();
        DeleteFilesClass deleteFilesClass = new DeleteFilesClass(arr);
        deleteFilesClass.start();



    }

    private void deleteFileDir(String path1, long id){
        File fileDir1 = getApplicationContext().getDir(path1 + File.pathSeparator + id, MODE_PRIVATE);
        if (!fileDir1.exists()) return;

        File files1[] = fileDir1.listFiles();
        if (files1 != null){
            for (File file : files1){
                file.delete();
            }
        }
        fileDir1.delete();
    }

    private int deleteNote(long id){
        int index = -1;
        for (int i = 0; i < notes.size(); i++){
            if (notes.get(i).getID() == id){
                index = i;
                break;
            }
        }
        if (index != -1){
            notes.remove(index);
        }
        DeleteFilesClass deleteClass = new DeleteFilesClass(new File[]
                {
                        getApplicationContext().getDir(getResources().getString(R.string.imagesDir) + File.pathSeparator + id, MODE_PRIVATE),
                        getApplicationContext().getDir(getResources().getString(R.string.quoteDir) + File.pathSeparator + id, MODE_PRIVATE),
                        getApplicationContext().getDir(getResources().getString(R.string.descriptionDir) + File.pathSeparator + id, MODE_PRIVATE),
                        getApplicationContext().getDir(getResources().getString(R.string.commentDir) + File.pathSeparator + id, MODE_PRIVATE)
                });
        return index;
    }

    private void selectAll() {

        sdb = dbHelper.getReadableDatabase();

        // Выбор директорий и добавление, находящихся в текущей директории parent.
        String[] projection1 = {
                LiteratureContract.PathTable._ID,
                LiteratureContract.PathTable.COLUMN_PARENT,
                LiteratureContract.PathTable.COLUMN_CHILD

        };
        Cursor mCursor1 = sdb.query(LiteratureContract.PathTable.TABLE_NAME, projection1,
                LiteratureContract.PathTable.COLUMN_PARENT + " = ?", new String[] {parent},
                null, null, LiteratureContract.PathTable.COLUMN_CHILD);
        int idColumnIndex1 = mCursor1.getColumnIndex(LiteratureContract.PathTable._ID);
        int childColumnIndex = mCursor1.getColumnIndex(LiteratureContract.PathTable.COLUMN_CHILD);
        while (mCursor1.moveToNext()){
            long currentId = mCursor1.getLong(idColumnIndex1);
            String currentChild = mCursor1.getString(childColumnIndex);
            notes.add(new Directory(currentId, currentChild));
        }
        mCursor1.close();
        startPos = notes.size();


        // Выбор и добавление записей, находящихся в текущей дирректории parent
        String[] projection = {
                NoteTable._ID,
                NoteTable.COLUMN_PATH,
                NoteTable.COLUMN_AUTHOR,
                NoteTable.COLUMN_TITLE,
                NoteTable.COLUMN_RATING
        };
        Cursor cursor = sdb.query(
                NoteTable.TABLE_NAME,
                projection,
                LiteratureContract.NoteTable.COLUMN_PATH + " = ?",
                new String[] {parent},
                null,
                null,
                NoteTable.COLUMN_TITLE);
        int idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
        int pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
        int authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
        int titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);
        int ratingColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_RATING);
        while (cursor.moveToNext()) {
            int currentID = cursor.getInt(idColumnIndex);
            String currentPath = cursor.getString(pathColumnIndex);
            String currentAuthor = cursor.getString(authorColumnIndex);
            String currentTitle = cursor.getString(titleColumnIndex);
            double currentRating = Double.valueOf(cursor.getString(ratingColumnIndex));
            notes.add(new RealNote(currentID, currentPath, currentAuthor, currentTitle, currentRating));
        }
        cursor.close();

    }

    private void selectTitle(String title){
        String[] projection = {
                NoteTable._ID,
                NoteTable.COLUMN_PATH,
                NoteTable.COLUMN_AUTHOR,
                NoteTable.COLUMN_TITLE,
                NoteTable.COLUMN_RATING
        };

        Cursor cursor = sdb.query(
                NoteTable.TABLE_NAME,
                projection,
                NoteTable.COLUMN_TITLE + " = ?",
                new String[] {title},
                null,
                null,
                null);
        int idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
        int pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
        int authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
        int titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);
        int ratingColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_RATING);
        while (cursor.moveToNext()) {
            int currentID = cursor.getInt(idColumnIndex);
            String currentPath = cursor.getString(pathColumnIndex);
            String currentAuthor = cursor.getString(authorColumnIndex);
            String currentTitle = cursor.getString(titleColumnIndex);
            double currentRating = Double.valueOf(cursor.getString(ratingColumnIndex));
            notes.add(new RealNote(currentID, currentPath, currentAuthor, currentTitle, currentRating));
        }
        cursor.close();

        String[] titles = new String[title.length() + 1];
        titles[0] = title;
        titles[1] = "_" + title.substring(1);
        String quest = NoteTable.COLUMN_TITLE + "!= ? AND (";
        quest += NoteTable.COLUMN_TITLE + " LIKE ?" + " OR ";
        for (int i = 2; i < titles.length - 1; i++){
            titles[i] = title.substring(0, i - 1) + "%" + title.substring(i, title.length());
            quest += NoteTable.COLUMN_TITLE + " LIKE ?" + " OR ";
        }

        titles[titles.length - 1] = title.substring(0, title.length() - 1) + "%";
        quest += NoteTable.COLUMN_TITLE + " LIKE ?)";


        cursor = sdb.query(
                NoteTable.TABLE_NAME,
                projection,
                quest,
                titles,
                null,
                null,
                null);
        idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
        pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
        authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
        titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);
        ratingColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_RATING);
        while (cursor.moveToNext()) {
            int currentID = cursor.getInt(idColumnIndex);
            String currentPath = cursor.getString(pathColumnIndex);
            String currentAuthor = cursor.getString(authorColumnIndex);
            String currentTitle = cursor.getString(titleColumnIndex);
            double currentRating = Double.valueOf(cursor.getString(ratingColumnIndex));
            notes.add(new RealNote(currentID, currentPath, currentAuthor, currentTitle, currentRating));
        }
        cursor.close();

    }

    private void setSortTitles(){
        sortTitles1 = "Сортировка по названиям в лексикографическом порядке";
        sortTitles2 = "Сортировка по названиям в обратном лексикографическим порядке";
        sortAuthors1 = "Сортировка по автору в лексиграфическом порядке";
        sortAuthors2 = "Сортировка по автору в обратном лексиграфическим порядке";
        sortRating1 = "Сортировка по возрастанию рейтинга";
        sortRating2 = "Сортировка по убыванию рейтинга";
    }

    private void reloadRecyclerView(){
        // перезагрузка recyclerView. Удаляются все элементы notes, выбираются новые из бд
        notes.clear();
        selectAll();
        mAdapter.notifyDataSetChanged();
    }

    private void reloadButtonsView(){
        // перезагрузка buttonView. Удаляются все элементы button, выбираются новые из текущего пути
        buttons.clear();
        String pathTokens[] = (parent).split("/");
        // текущий путь - строка из названий директорий
        String prev = "";
        for (int i = 0; i < pathTokens.length; i++){
            if (pathTokens[i].equals("")){
                continue;
            }
            prev = prev + pathTokens[i] + "/";
            buttons.add(prev);
        }
        buttonAdapter.notifyDataSetChanged();
    }

    private void findViews(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCatalog);  // здесь будут отображаться каталоги и файлы notes
        buttonView = (RecyclerView) findViewById(R.id.buttonViewCatalog);  // здесь будут отображаться пройденные поддиректории buttons
        toolbar = (MaterialToolbar) findViewById(R.id.long_click_toolbar);
        counterText = (TextView) findViewById(R.id.counter_text);
        findText1 = (EditText) findViewById(R.id.editTextFind);
    }





    int rep =0;
    @Override
    public void onBackPressed()
    {

        for (int i=0;i<1;i++)
        {
            if (ext==1)
            {
                MainActivity MainActivity = new MainActivity();
                MainActivity.currentUser=null;
                super.onBackPressed();
                ext=0;
                break;
            }

            if (rep<3)
            {
                rep++;

                if(rep==1)
                {
                    Toast.makeText(CatalogActivity.this, "Для выхода из приложения нажмите ещё раз ", Toast.LENGTH_SHORT).show();
                    CountDownTimer mCount=new CountDownTimer(2000,1000)
                    {
                        @Override
                        public void onTick(long millisUntilFinished)
                        {

                        }

                        @Override
                        public void onFinish()
                        {

                            rep--;

                        }
                    }.start();


                }
                else if (rep==2)
                {
                    ext();
                    rep=0;
                }

            }
        }
    }

    private void ext()
    {
        moveTaskToBack(true);
        super.onDestroy();
        System.exit(0);

    }

    private void setAdapters(){
        mAdapter = new RecyclerViewAdapter(notes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

//
//        Button sigout = (Button) findViewById(R.id.button2);
//        sigout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ext =1;
//                MainActivity MainActivity = new MainActivity();
//                MainActivity.currentUser=null;
//                MainActivity. mAuth.signOut();
//                onBackPressed();
//            }
//        });


        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // В notes хранятся объекты двух классов, имплементирующих Note - RealNote и Directory
                // RealNote - собственно запись пользователя. При клике нужно перейти к записи, т.е к NoteActivity
                // Directory - директория. При клике нужно перейти в эту директорию.
                int type = notes.get(position).getItemType();
                if (type == 0){
                    RealNote realNote = (RealNote) notes.get(position);
                    Intent intent = new Intent(CatalogActivity.this, NoteActivity.class);
                    // чтобы понять какую запись нужно отобразить в NoteActivity, запихиваем в intent id записи из бд
                    intent.putExtra("id", realNote.getID());
                    startActivityForResult(intent, NOTE_REQUEST_CODE); // в NoteActivity пользователь может изменить путь.
                    //Если изменит, то вернется intent, чтобы можно было изменить отображение каталогов
                }
                if (type == 1){
                    Directory directory = (Directory) notes.get(position);
                    parent = directory.getDirectory(); // устанавливаем директорию, на которую нажали в качестве отправной
                    notes.clear();
                    buttons.add(parent);
                    buttonAdapter.notifyDataSetChanged();
                    selectAll(); // выбираем новые данные из бд
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onItemLongClick(int position) {
                mAdapter.setActionMode(true);
                action_mode = true;
                counterText.setText(count + " элементов выбрано");
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_long_click);
                menuType=1;
//                toolbar.setMenu(m);
                mAdapter.notifyDataSetChanged();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            @Override
            public void onCheckClick(int position) {
                count++;
                counterText.setText(count + " элементов выбрано");
                Note note = notes.get(position);
                if (note.getItemType()==1){
                    selectionDirectoriesList.add((Directory) note);
                    Toast.makeText(getApplicationContext(), selectionDirectoriesList.size() + " Directory", Toast.LENGTH_LONG).show();
                }
                else{
                    selectionRealNotesList.add((RealNote) note);
                    Toast.makeText(getApplicationContext(), selectionRealNotesList.size() + " RealNote", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onUncheckClick(int position) {
                count--;
                counterText.setText(count + " элементов выбрано");
                Note note = notes.get(position);
                if (note.getItemType()==1){
                    selectionDirectoriesList.remove((Directory) note);
                    Toast.makeText(getApplicationContext(), selectionDirectoriesList.size() + " Directory", Toast.LENGTH_LONG).show();
                }
                else{
                    selectionRealNotesList.remove((RealNote) note);
                    Toast.makeText(getApplicationContext(), selectionRealNotesList.size() + " RealNote", Toast.LENGTH_LONG).show();
                }

            }
        });



        buttonAdapter = new CatalogButtonAdapter(buttons);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.ItemAnimator itemAnimator1 = new DefaultItemAnimator();
        buttonView.setAdapter(buttonAdapter);
        buttonView.setLayoutManager(layoutManager1);
        buttonView.setItemAnimator(itemAnimator1);
        buttonAdapter.setOnItemClickListener(new CatalogButtonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                parent = buttons.get(position);
                reloadButtonsView();
                reloadRecyclerView();
            }
        });
//
//        sortsAdapter = new CatalogSortsSpinnerAdapter(this, sortsList);
//        sortsSpinner.setAdapter(sortsAdapter);
//        sortsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String clickedItem = (String) parent.getItemAtPosition(position);
//                startSort(clickedItem);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
    }
//

    private void startSort(String sortType) {
        if (sortType.equals(sortTitles1)){
            comp = "title";
            order = 1;
        }
        if (sortType.equals(sortTitles2)){
            comp = "title";
            order = -1;
        }
        if (sortType.equals(sortAuthors1)){
            comp = "author";
            order = 1;
        }
        if (sortType.equals(sortAuthors2)){
            comp = "author";
            order = -1;
        }
        if (sortType.equals(sortRating1)){
            comp = "rating";
            order = 1;
        }
        if (sortType.equals(sortRating2)){
            comp = "rating";
            order = -1;
        }
        quickSort(startPos, notes.size() - 1);
        mAdapter.notifyDataSetChanged();


    }

    private void quickSort(int from, int to) {
        if (from < to) {
            int divideIndex;
            if (comp != "rating"){
                divideIndex = partitionString(from, to);
            }
            else{
                divideIndex = partitionDouble(from, to);
            }

            quickSort(from, divideIndex - 1);
            quickSort(divideIndex, to);
        }
    }
    private int partitionString(int from, int to)
    {
        int rightIndex = to;
        int leftIndex = from;

        String pivot = getComparable((RealNote) notes.get(from + (to - from) / 2));
        while (leftIndex <= rightIndex)
        {

            while (order * (getComparable((RealNote) notes.get(leftIndex)).compareTo(pivot)) < 0)
            {
                leftIndex++;
            }

            while (order * (getComparable((RealNote) notes.get(rightIndex)).compareTo(pivot)) > 0)
            {
                rightIndex--;
            }

            if (leftIndex <= rightIndex)
            {
                swap(rightIndex, leftIndex);
                leftIndex++;
                rightIndex--;
            }
        }
        return leftIndex;
    }

    private int partitionDouble(int from, int to){
        int rightIndex = to;
        int leftIndex = from;

        Double pivot = getComparableDouble((RealNote) notes.get(from + (to - from) / 2));
        while (leftIndex <= rightIndex)
        {

            while (order * (getComparableDouble((RealNote) notes.get(leftIndex)).compareTo(pivot)) < 0)
            {
                leftIndex++;
            }

            while (order * (getComparableDouble((RealNote) notes.get(rightIndex)).compareTo(pivot)) > 0)
            {
                rightIndex--;
            }

            if (leftIndex <= rightIndex)
            {
                swap(rightIndex, leftIndex);
                leftIndex++;
                rightIndex--;
            }
        }
        return leftIndex;
    }


    private String getComparable(RealNote realNote){
        if (comp.equals("title")){
            return realNote.getTitle();
        }
        if (comp.equals("author")){
            return realNote.getAuthor();
        }

        return "";
    }

    private void swap(int index1, int index2)
    {
        Note tmp  = notes.get(index1);
        notes.set(index1, notes.get(index2));
        notes.set(index2, tmp);
    }

    private Double getComparableDouble(RealNote realNote){
        return realNote.getRating();
    }




}
