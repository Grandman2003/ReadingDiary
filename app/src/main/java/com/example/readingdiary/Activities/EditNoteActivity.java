package com.example.readingdiary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readingdiary.Fragments.CreateWithoutNoteDialogFragment;
import com.example.readingdiary.Fragments.DeleteDialogFragment;
import com.example.readingdiary.Fragments.DeleteTitleAndAuthorDialogFragment;
import com.example.readingdiary.Fragments.SaveDialogFragment;
import com.example.readingdiary.R;
import com.example.readingdiary.data.LiteratureContract.NoteTable;

import com.example.readingdiary.data.OpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

import javax.xml.transform.Result;
import com.example.readingdiary.data.LiteratureContract.PathTable;

public class EditNoteActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogListener,
        CreateWithoutNoteDialogFragment.CreateWithoutNoteDialogListener,
        SaveDialogFragment.SaveDialogListener {
    EditText pathView;
    EditText titleView;
    EditText authorView;
    RatingBar ratingView;
    EditText genreView;
    EditText timeView;
    EditText placeView;
    EditText shortCommentView;
    ImageView coverView;
    String imagePath;
    SQLiteDatabase sdb;
    OpenHelper dbHelper;
    String id;
    String path;
    boolean change = false;
    private ImageView imageView;
    private final int Pick_image = 1;
    private final int EDIT_REQUEST_CODE = 123;
    private String[] beforeChanging;
    private final int GALERY_REQUEST_CODE = 124;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        dbHelper = new OpenHelper(this);
        sdb = dbHelper.getReadableDatabase();
        findViews();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle args = getIntent().getExtras();

        if (args != null && args.get("id") != null){
            id = args.get("id").toString();
            select(id);
        }
        else if (args != null && args.get("path") != null){
            path = args.get("path").toString();
            beforeChanging = new String[]{path, "", "", "0.0", "", "", "", "", ""};
            setViews(beforeChanging);
        }
        else{
            path = "./";
            beforeChanging = new String[]{"./", "", "", "0.0", "", "", "", "", ""};
            setViews(beforeChanging);
        }
        Log.d("putExtra", "start");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setButtons();
        setFocuses();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
//            findText.clearFocus();
            setCursorsVisible(false);
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onDeleteClicked() {
        deleteNote();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("deleted", "true");
        returnIntent.putExtra("id", id);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onCreateWithoutNoteClicked() {
        String path1 = pathView.getText().toString();
        path1 = fixPath(path1);
        if (!beforeChanging[0].equals(path1)){
            beforeChanging[0] = path1;
            savePaths();
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("noNote", "true");
        returnIntent.putExtra("path", path);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onSaveClicked() {
        if (saveChanges()){
            finish();
        }
    }

    @Override
    public void onNotSaveClicked() {
        finish();
    }

    private void setFocuses(){
        setFocuse(pathView);
        setFocuse(titleView);
        setFocuse(authorView);
        setFocuse(timeView);
        setFocuse(placeView);
        setFocuse(shortCommentView);

    }

    private void setFocuse(final EditText editText){
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    editText.setCursorVisible(true);
                }
                return false;
            }
        });
    }

    private void setCursorsVisible(boolean arg){
        pathView.setCursorVisible(arg);
        titleView.setCursorVisible(arg);
        authorView.setCursorVisible(arg);
        timeView.setCursorVisible(arg);
        placeView.setCursorVisible(arg);
        shortCommentView.setCursorVisible(arg);


    }

    public void findViews(){
        pathView = (EditText) findViewById(R.id.editPath);
        titleView = (EditText) findViewById(R.id.editTitleNoteActivity);
        authorView = (EditText) findViewById(R.id.editAuthorNoteActivity);
        ratingView = (RatingBar) findViewById(R.id.editRatingBar);
        genreView = (EditText) findViewById(R.id.editGenre);
        timeView = (EditText) findViewById(R.id.editTime);
        placeView = (EditText) findViewById(R.id.editPlace);
        shortCommentView = (EditText) findViewById(R.id.editShortComment);
        coverView = (ImageView) findViewById(R.id.editCoverImage);
//        changeViews = {pathView, authorView, titleView, ratingView, genreView, timeView, placeView, shortCommentView, imageView};
    }

    public void setViews(String[] strings){

        // String path, String author, String title, String rating, String genre,
        //                         String time, String place, String shortComment, String imagePath
        this.pathView.setText(strings[0]);
//        if (path == null) this.path="./";
//        else this.path = path;
//        beforeChanging = {this.pathView,  author, title, rating, genre, time, place, shortComment, imagePath};

        this.authorView.setText(strings[1]);
        this.titleView.setText(strings[2]);
        if (!strings[3].equals("")){
            this.ratingView.setRating(Float.parseFloat(strings[3]));
        }
        this.genreView.setText(strings[4]);
        this.timeView.setText(strings[5]);
        this.placeView.setText(strings[6]);
        this.shortCommentView.setText(strings[7]);
////        File file = new File(imagePath);
//        if (!imagePath.equals("")){
//            this.coverView.setImageBitmap(BitmapFactory.decodeFile(strings[8]));
//            this.imagePath = imagePath;
//        }
        if (!strings[8].equals("")){
            this.coverView.setImageBitmap(BitmapFactory.decodeFile(strings[8]));
            this.imagePath = imagePath;
        }
    }

    private void setButtons(){



        FloatingActionButton accept =  (FloatingActionButton) findViewById(R.id.acceptAddingNote2);
        FloatingActionButton cancel =  (FloatingActionButton) findViewById(R.id.cancelAddingNote2);
        Button deleteButton = (Button) findViewById(R.id.deleteNoteButton);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveChanges()){
                    finish();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button bAddObl = (Button) findViewById(R.id.bAddObl);
        bAddObl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (id!=null) {
                    Intent intent = new Intent(EditNoteActivity.this, GaleryActivity.class);
                    intent.putExtra("id", id);
                    startActivityForResult(intent, GALERY_REQUEST_CODE);
                }
                else
                    {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, Pick_image);

                        GaleryActivity GaleryActivity = new GaleryActivity();
//                        GaleryActivity.onActivityResult(1, 1,null);
                        GaleryActivity.setResultChanged();

                    }
            }
        });



        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeletDialog();
//                deleteNote();
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("deleted", "true");
//                returnIntent.putExtra("id", id);
//                setResult(RESULT_OK, returnIntent);
//                finish();
            }
        });
    }


    private void openDeletDialog(){
        DeleteDialogFragment dialog = new DeleteDialogFragment();
        dialog.show(getSupportFragmentManager(), "deleteDialog");
    }

    public void select(String id){
        // Выбор полей из бд
        // Сейчас тут выбор не всех полей
        String[] projection = {
                NoteTable._ID,
                NoteTable.COLUMN_PATH,
                NoteTable.COLUMN_AUTHOR,
                NoteTable.COLUMN_TITLE,
                NoteTable.COLUMN_COVER_IMAGE,
                NoteTable.COLUMN_RATING,
                NoteTable.COLUMN_GENRE,
                NoteTable.COLUMN_TIME,
                NoteTable.COLUMN_PLACE,
                NoteTable.COLUMN_SHORT_COMMENT

        };
        Cursor cursor = sdb.query(
                NoteTable.TABLE_NAME,   // таблица
                projection,            // столбцы
                NoteTable._ID + " = ?",                  // столбцы для условия WHERE
                new String[] {id},                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);
        try{
            int idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
            int pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
            int authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
            int titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);
            int coverColumnIndex =  cursor.getColumnIndex(NoteTable.COLUMN_COVER_IMAGE);
            int ratingColumnIndex =  cursor.getColumnIndex(NoteTable.COLUMN_RATING);
            int genreColumnIndex =  cursor.getColumnIndex(NoteTable.COLUMN_GENRE);
            int timeColumnIndex =  cursor.getColumnIndex(NoteTable.COLUMN_TIME);
            int placeColumnIndex =  cursor.getColumnIndex(NoteTable.COLUMN_PLACE);
            int shortCommentIndex =  cursor.getColumnIndex(NoteTable.COLUMN_SHORT_COMMENT);

            while (cursor.moveToNext()) {
                beforeChanging = new String[] {cursor.getString(pathColumnIndex), cursor.getString(authorColumnIndex),
                        cursor.getString(titleColumnIndex), cursor.getString(ratingColumnIndex),
                        cursor.getString(genreColumnIndex), cursor.getString(timeColumnIndex),
                        cursor.getString(placeColumnIndex), cursor.getString(shortCommentIndex),
                        cursor.getString(coverColumnIndex)};
                if (beforeChanging[0] == null) beforeChanging[0] = "./";
//                setViews(new String[] {cursor.getString(pathColumnIndex), cursor.getString(authorColumnIndex),
//                        cursor.getString(titleColumnIndex), cursor.getString(ratingColumnIndex),
//                        cursor.getString(genreColumnIndex), cursor.getString(timeColumnIndex),
//                        cursor.getString(placeColumnIndex), cursor.getString(shortCommentIndex),
//                        cursor.getString(shortCommentIndex)});
                setViews(beforeChanging);
            }
        }
        finally{
            if (path==null){
                path="./";
            }
            cursor.close();
        }
    }

    public boolean saveChanges(){

        if (authorView.getText().toString().equals("") && titleView.getText().toString().equals(""))
        {
            showNoTitleAndAuthorDialog();
            return false;
        }
        ContentValues cv = new ContentValues();
//        cv.put(NoteTable.COLUMN_PATH, );
        String path1 = pathView.getText().toString();
        path1 = fixPath(path1);

        cv.put(NoteTable.COLUMN_PATH, path1);
        if (authorView.length()<20){cv.put(NoteTable.COLUMN_AUTHOR, authorView.getText().toString());}
        else {Toast.makeText(EditNoteActivity.this,"Введено слишком большое имя автора ",Toast.LENGTH_SHORT).show(); return false;}

        if (authorView.length()<20){        cv.put(NoteTable.COLUMN_TITLE, titleView.getText().toString());}
        else {Toast.makeText(EditNoteActivity.this,"Введено слишком большое название книги ",Toast.LENGTH_SHORT).show();return false;}

        if (authorView.length()<20){ cv.put(NoteTable.COLUMN_GENRE,genreView.getText().toString()); }
        else {Toast.makeText(EditNoteActivity.this,"Введено слишком большое название жанра ",Toast.LENGTH_SHORT).show();return false;}

        if (authorView.length()<20){ cv.put(NoteTable.COLUMN_TIME, timeView.getText().toString()); }
        else {Toast.makeText(EditNoteActivity.this,"Введенн слишком большой текст для времени прочтения",Toast.LENGTH_SHORT).show();return false;}

        if (authorView.length()<20){ cv.put(NoteTable.COLUMN_PLACE, placeView.getText().toString()); }
        else {Toast.makeText(EditNoteActivity.this,"Введено слишком большое название места прочтения",Toast.LENGTH_SHORT).show();return false;}

        if (authorView.length()<20){ cv.put(NoteTable.COLUMN_SHORT_COMMENT, shortCommentView.getText().toString()); }
        else {Toast.makeText(EditNoteActivity.this,"Введено слишком большой короткий комментарий",Toast.LENGTH_SHORT).show();return false;}
        cv.put(NoteTable.COLUMN_COVER_IMAGE, imagePath);
        cv.put(NoteTable.COLUMN_RATING, String.valueOf(ratingView.getRating()));
        cv.put(NoteTable.COLUMN_COVER_IMAGE, "");

        if (!beforeChanging[0].equals(path1))
        {
            beforeChanging[0] = path1;
            savePaths();
        }

        if (id != null)
        {
            sdb.update(NoteTable.TABLE_NAME, cv, "_id=" + id, null);
            changedIntent();
        }
        else
        {

            id = sdb.insert(NoteTable.TABLE_NAME, null, cv) + "";
            insertIntent();
        }
        return true;
    }

    private void showNoTitleAndAuthorDialog(){
        if (id==null){
            CreateWithoutNoteDialogFragment createDialog = new CreateWithoutNoteDialogFragment();
            createDialog.show(getSupportFragmentManager(), "createWithoutNoteDialog");
        }
        else{
            DeleteTitleAndAuthorDialogFragment dialog = new DeleteTitleAndAuthorDialogFragment();
            dialog.show(getSupportFragmentManager(), "deleteTitleAndAuthorDialog");
        }
    }

    public boolean checkChanges(){
//        beforeChanging = {cursor.getString(pathColumnIndex), cursor.getString(authorColumnIndex),
//                cursor.getString(titleColumnIndex), cursor.getString(ratingColumnIndex),
//                cursor.getString(genreColumnIndex), cursor.getString(timeColumnIndex),
//                cursor.getString(placeColumnIndex), cursor.getString(shortCommentIndex),
//                cursor.getString(shortCommentIndex)};
        Log.d("putExtra", ratingView.getRating() +"");
        if (beforeChanging[0].equals(fixPath(pathView.getText().toString())) &&
                beforeChanging[1].equals(authorView.getText().toString()) &&
                beforeChanging[2].equals(titleView.getText().toString()) &&
                beforeChanging[3].equals(ratingView.getRating()+"")  &&
                beforeChanging[4].equals(genreView.getText().toString()) &&
                beforeChanging[5].equals(timeView.getText().toString()) &&
                beforeChanging[6].equals(placeView.getText().toString()) &&
                beforeChanging[7].equals(shortCommentView.getText().toString()))
        {
            return false;
        }
        return true;
    }

    public void savePaths(){
        ContentValues cv = new ContentValues();
        String pathTokens[] = ((String) beforeChanging[0]).split("/");
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
    }

    public String fixPath(String path){
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
        return path;
    }

    private void deleteNote(){
        sdb.delete(NoteTable.TABLE_NAME, NoteTable._ID + " = ?", new String[]{id});
    }

    public void changedIntent(){
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
    }

    public void insertIntent()
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("id", id);
        returnIntent.putExtra("path", path);
        setResult(RESULT_OK, returnIntent);
        Log.d("DeleteNote", "insertIntent");
    }

    private void saveDialog(){
        SaveDialogFragment saveDialogFragment = new SaveDialogFragment();
//        MyDialogFragment myDialogFragment = new MyDialogFragment();
        FragmentManager manager = getSupportFragmentManager();
        //myDialogFragment.show(manager, "dialog");

        FragmentTransaction transaction = manager.beginTransaction();
        saveDialogFragment.show(transaction, "dialog");
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (checkChanges()){
            saveDialog();
        }
        else{
            finish();
        }
//        saveChanges();
//        super.onBackPressed();
//        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

