package com.example.readingdiary.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.readingdiary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.readingdiary.data.LiteratureContract.NoteTable;
import com.example.readingdiary.data.LiteratureContract.PathTable;

import com.example.readingdiary.data.OpenHelper;

public class AddNoteActivity extends AppCompatActivity {
    SQLiteDatabase sdb;
    OpenHelper dbHelper;
    public static final String DATABASE_NAME = "Literature";
    public static final String DATABASE_TABLE = "Notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_PATH = "path";

    public static final int NUM_COLUMN_ID = 0;
    public static final int NUM_COLUMN_TITLE = 3;
    public static final int NUM_COLUMN_AUTHOR = 2;
    public static final int NUM_COLUMN_PATH = 1;
    public String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        dbHelper = new OpenHelper(this);
        sdb = dbHelper.getWritableDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton cancelAddingNote = (FloatingActionButton)findViewById(R.id.cancelAddingNote);
        FloatingActionButton acceptAddingNote = (FloatingActionButton)findViewById(R.id.acceptAddingNote);
        final EditText pathField = (EditText) findViewById(R.id.pathField);
        final EditText authorField = (EditText) findViewById(R.id.authorField);
        final EditText titleField = (EditText) findViewById(R.id.titleField);


        // отмена создании записи, возвращаемся к активности каталогов
        cancelAddingNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                closeActivity();

            }
        });

        // Подтверждение
        acceptAddingNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path = pathField.getText().toString();
                fixPath(); // приведение пути к единому фромату
                long id = insert(path, authorField.getText().toString(), titleField.getText().toString()); // добавление в бд

                Intent intent = new Intent(AddNoteActivity.this, NoteActivity.class); // вызов активности записи
                intent.putExtra("id", id); // передаем id активности в бд, чтобы понять какую активность надо показывать
                startActivity(intent);
                displayDatabaseInfo(); // фигня не нужная, я использую, чтобы посмотреть, что творится с бд
                Intent returnIntent = new Intent();
                returnIntent.putExtra("path", path); // в CatalogActivity передаем путь для отображения нужной директории
                setResult(RESULT_OK, returnIntent);
                closeActivity();
            }
        });

    }


    private void closeActivity(){
        finish();
    }


    public long insert(String path, String author, String title) {
        // добавляем нужные пути в бд и запись
        sdb = dbHelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        String pathTokens[] = ((String) path).split("/");
        String prev = pathTokens[0] + "/";
        for (int i = 1; i < pathTokens.length; i++){
            if (pathTokens[i].equals("")){
                continue;
            }
            cv.put(PathTable.COLUMN_PARENT, prev);
            prev = prev + pathTokens[i] + "/";
            cv.put(PathTable.COLUMN_CHILD, prev);
            sdb.insert(PathTable.TABLE_NAME, null, cv);
            cv.clear();

        }
        Log.d("PATH", path);
        cv.put(NoteTable.COLUMN_PATH, path);
        cv.put(NoteTable.COLUMN_AUTHOR, author);
        cv.put(NoteTable.COLUMN_TITLE, title);

        return sdb.insert(NoteTable.TABLE_NAME, null, cv);
    }


    public void fixPath(){
        if (path.equals("") || path.equals("/")) path = "./";
        else{
            if (path.charAt(path.length() - 1) != '/'){
                path = path + "/";
            }
            if (path.charAt(0) == '/'){
                path = "." + path;
            }
            if (path.charAt(0) != '.'){
                path = "./" + path;
            }
        }
    }



    private void displayDatabaseInfo() {

        // Создадим и откроем для чтения базу данных

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                NoteTable._ID,
                NoteTable.COLUMN_PATH,
                NoteTable.COLUMN_AUTHOR,
                NoteTable.COLUMN_TITLE
//                NoteTable.COLUMN_DIRECTORY
        };
        // Делаем запрос
        Cursor cursor = db.query(
                NoteTable.TABLE_NAME,   // таблица
                projection,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки


        String[] projection1 = {
                PathTable._ID,
                PathTable.COLUMN_PARENT,
                PathTable.COLUMN_CHILD
        };
        // Делаем запрос
        Cursor cursor1 = db.query(
                PathTable.TABLE_NAME,   // таблица
                projection1,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки



        TextView displayTextView = (TextView) findViewById(R.id.text_view_info);

        try {
            displayTextView.setText("Таблица содержит " + cursor.getCount() + " гостей.\n\n");
            displayTextView.append(
                    NoteTable._ID  + " - " +
                            NoteTable.COLUMN_PATH  + " - " +
                            NoteTable.COLUMN_AUTHOR  + " - " +
                            NoteTable.COLUMN_TITLE  + " - "
//                            + //                            NoteTable.COLUMN_DIRECTORY
                            + "\n");

            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
            int pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
            int authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
            int titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);
//            int directoryColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_DIRECTORY);

            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                String currentPath = cursor.getString(pathColumnIndex);
                String currentAuthor = cursor.getString(authorColumnIndex);
                String currentTitle = cursor.getString(titleColumnIndex);
//                String currentDirectory = cursor.getString(directoryColumnIndex);


                // Выводим значения каждого столбца
                displayTextView.append(("\n" + currentID + " - " +
                        currentPath + " - " +
                        currentAuthor + " - " +
                        currentTitle + " - "
//                        + currentDirectory
                ));
            }

            displayTextView.append(
                    PathTable._ID  + " - " +
                            PathTable.COLUMN_PARENT  + " - " +
                            PathTable.COLUMN_CHILD  + "\n");

            idColumnIndex = cursor1.getColumnIndex(PathTable._ID);
            int parentColumnIndex = cursor1.getColumnIndex(PathTable.COLUMN_PARENT);
            int childColumnIndex = cursor1.getColumnIndex(PathTable.COLUMN_CHILD);

            while (cursor1.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor1.getInt(idColumnIndex);
                String currentParent = cursor1.getString(parentColumnIndex);
                String currentChild = cursor1.getString(childColumnIndex);

                // Выводим значения каждого столбца
                displayTextView.append("\n" + currentID + " - " +
                        currentParent + " - " +
                        currentChild);
            }




        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
            cursor1.close();
        }
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }
}
