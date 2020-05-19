package com.example.readingdiary.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readingdiary.Classes.DeleteFilesClass;
import com.example.readingdiary.Classes.VariousNotes;
import com.example.readingdiary.R;
import com.example.readingdiary.adapters.VariousViewAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class VariousShow extends AppCompatActivity {
    private String id;
    private String type;
    VariousViewAdapter viewAdapter;
    RecyclerView recyclerView;
    ArrayList<VariousNotes> variousNotes;
    private final int ADD_VIEW_RESULT_CODE = 666;
    File fileDir1;
    MaterialToolbar toolbar;
    TextView counterText;
    int count=0;
    boolean action_mode=false;
    ArrayList<VariousNotes> selectedNotes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_various_show);

        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        type = args.get("type").toString();
        variousNotes = new ArrayList<>();
        fileDir1 = getApplicationContext().getDir(type + File.pathSeparator + id, MODE_PRIVATE);
        openNotes();
        findViews();
        toolbar.getMenu().clear();
        toolbar.setTitle("");
        counterText.setText(type);
        setSupportActionBar(toolbar);

//        counterText.setText("Каталог");
        setAdapters();
        setButtons();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.item_delete){
            action_mode=false;
            viewAdapter.setActionMode(false);
            deleteVariousNotes();
            viewAdapter.notifyDataSetChanged();
            toolbar.getMenu().clear();
//            toolbar.inflateMenu(R.menu.menu_catalog);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            counterText.setText(type);
            count=0;
//            selectionList.clear();


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == ADD_VIEW_RESULT_CODE && resultCode == RESULT_OK){
                Bundle args = data.getExtras();
                if (args.get("time") != null){
                    long time = Long.parseLong(args.get("time").toString());
                    File file = new File(fileDir1, time+".txt");
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    StringBuilder str = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null){
                        str.append(line);
                        str.append('\n');
                    }
                    variousNotes.add(new VariousNotes(str.toString(), file.getAbsolutePath(), time, false));
                    viewAdapter.notifyDataSetChanged();
                }
                else if (args.get("updatePath") != null){
                    File file = new File(args.get("updatePath").toString());
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    StringBuilder str = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null){
                        str.append(line);
                        str.append('\n');
                    }
                    int position = Integer.parseInt(args.get("position").toString());
                    variousNotes.get(position).setText(str.toString());
                    viewAdapter.notifyItemChanged(position);
                }

            }
        }
        catch (Exception e){
            Log.e("resultShowException", e.toString());
        }

    }

    private void deleteVariousNotes(){
        File deleteArr[] = new File[selectedNotes.size()];
        for (int i = 0; i < deleteArr.length; i++){
            variousNotes.remove(selectedNotes.get(i));
            deleteArr[i] = new File(selectedNotes.get(i).getPath());
        }
        DeleteFilesClass deleteClass = new DeleteFilesClass(deleteArr);
        deleteClass.start();
        selectedNotes.clear();
    }

    private void findViews(){
        recyclerView = (RecyclerView) findViewById(R.id.various_recycler_view);
        toolbar = (MaterialToolbar) findViewById(R.id.long_click_toolbar);
        counterText = (TextView) findViewById(R.id.counter_text);
    }

    private void setAdapters(){
        viewAdapter = new VariousViewAdapter(variousNotes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(viewAdapter);
        viewAdapter.setOnItemClickListener(new VariousViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(VariousShow.this, VariousNotebook.class);
                intent.putExtra("id", id);
                intent.putExtra("type", type);
                intent.putExtra("path", variousNotes.get(position).getPath());
                intent.putExtra("position", position+"");
                startActivityForResult(intent, ADD_VIEW_RESULT_CODE);
            }

            @Override
            public void onItemLongClick(int position) {
                viewAdapter.setActionMode(true);
                action_mode = true;
                counterText.setText(count + " элементов выбрано");
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_long_click);
//                toolbar.setMenu(m);
                viewAdapter.notifyDataSetChanged();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            @Override
            public void onCheckClick(int position) {
                selectedNotes.add(variousNotes.get(position));
                count++;
                counterText.setText(count + " элементов выбрано");
                Toast.makeText(getApplicationContext(), selectedNotes.size() + " items selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUncheckClick(int position) {
                selectedNotes.remove(variousNotes.get(position));
                count--;
                counterText.setText(count + " элементов выбрано");

                Toast.makeText(getApplicationContext(), selectedNotes.size() + " items selected", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void setButtons(){
        Button addVariousItem = (Button) findViewById(R.id.addVariousItem);
        addVariousItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VariousShow.this, VariousNotebook.class);
                intent.putExtra("id", id);
                intent.putExtra("type", type);
                startActivityForResult(intent, ADD_VIEW_RESULT_CODE);
            }
        });
    }


    private void openNotes(){
        try{


            File[] files = fileDir1.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    BufferedReader br = new BufferedReader(new FileReader(files[i]));
                    StringBuilder str = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        str.append(line);
                        str.append('\n');
                    }
                    String[] pathTokens = files[i].getAbsolutePath().split(File.pathSeparator);


                    variousNotes.add(new VariousNotes(str.toString(), files[i].getAbsolutePath(),
                            Long.parseLong(pathTokens[pathTokens.length - 1].split("\\.")[0].split("/")[1]),
                            false));

                }
            }
        }
        catch (Exception e){
            Log.e("openShowException", e.toString());
        }
    }

    private void saveChanges(){
        try {
            for (VariousNotes note : variousNotes) {
                if (note.isChanged()) {
                    if (!fileDir1.exists()) fileDir1.mkdirs();
                    File file = new File(fileDir1, note.getTime() + ".txt");
                    if (!file.exists()) file.createNewFile();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                    // пишем данные
                    bw.write(note.getText());
                    // закрываем поток
                    bw.close();
                }
            }
        }
        catch (Exception e){
            Log.e("saveShowException", e.toString());
        }
    }

}
