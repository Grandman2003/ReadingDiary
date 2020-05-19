package com.example.readingdiary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.readingdiary.R;
import com.example.readingdiary.adapters.GaleryFullViewAdapter;
import com.example.readingdiary.adapters.GaleryRecyclerViewAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class GaleryActivity extends AppCompatActivity {
    private ImageView imageView;
    private final int Pick_image = 1;
    private RecyclerView galeryView;;
    private GaleryRecyclerViewAdapter adapter;
    private GaleryFullViewAdapter adapter1;
    private List<Bitmap> images;
    private List<String> names;
    private final int FULL_GALERY_CODE = 8800;

    private int count = 3;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galery_activity);
        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        images = new ArrayList<>(); // список bitmap изображений
        names = new ArrayList<>(); // список путей к изображениями в файловой системе
        File fileDir1 = getApplicationContext().getDir(getResources().getString(R.string.imagesDir) + File.pathSeparator + id, MODE_PRIVATE); // путь к папке с изображениями
        File[] files = fileDir1.listFiles(); // список файлов в папке
        if (files != null){
            for (int i = 0; i < files.length; i++){
                images.add(BitmapFactory.decodeFile(files[i].getAbsolutePath()));
                names.add(files[i].getAbsolutePath());
            }
        }

        galeryView = (RecyclerView) findViewById(R.id.galery_recycle_view);
        adapter = new GaleryRecyclerViewAdapter(images, getApplicationContext());

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3); // отображение изображений в 3 колонки
        final LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        galeryView.setAdapter(adapter);
        galeryView.setLayoutManager(layoutManager);

        galeryView.setItemAnimator(itemAnimator);


        // при нажатии на изображение переходим в активность с полным изображением
        adapter.setOnItemClickListener(new GaleryRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(GaleryActivity.this, GaleryFullViewActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("position", position);
                startActivityForResult(intent, FULL_GALERY_CODE);

            }
        });




        Button pickImage = (Button) findViewById(R.id.button);

        // Выбор изображений из галереи
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Pick_image);
            }
        });
        setResultChanged();
    }



    private void setResultChanged(){
        // создание возвращаемого интента
        Intent returnIntent = new Intent();
//        returnIntent.putExtra("changed", "changed);
        setResult(RESULT_OK, returnIntent);
    }


    // методы проверки размера изображения до открытия. Если размер слишком большой - сжимаем
    public Bitmap decodeSampledBitmapFromResource(Uri imageUri,
                                                  int reqWidth, int reqHeight) throws Exception{

        // Читаем с inJustDecodeBounds=true для определения размеров
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = getContentResolver().openInputStream(imageUri);
        BitmapFactory.decodeStream(imageStream, null, options);

        // Вычисляем inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Читаем с использованием inSampleSize коэффициента
        options.inJustDecodeBounds = false;
        imageStream.close();
        imageStream = getContentResolver().openInputStream(imageUri);
        return BitmapFactory.decodeStream(imageStream, null, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Реальные размеры изображения
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d("SCALE", "OPTIONS " + height + " "  + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Вычисляем наибольший inSampleSize, который будет кратным двум
            // и оставит полученные размеры больше, чем требуемые
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void saveAndOpenImage(Uri imageUri) throws Exception{
        int px = 600;
        Bitmap bitmap = decodeSampledBitmapFromResource(imageUri, px, px); // файл сжимается
        long time = new GregorianCalendar().getTimeInMillis();
        File fileDir1 = getApplicationContext().getDir(getResources().getString(R.string.imagesDir) + File.pathSeparator + id, MODE_PRIVATE);
        File file2 = new File(fileDir1, time + ".png"); // создаем файл в директории изображений записи. Имя выбирается на основе времени.
        file2.createNewFile();
        OutputStream stream = null;
        stream = new FileOutputStream(file2);
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, stream);// сохранение
        stream.close();
        String name = file2.getAbsolutePath();
        int pos = names.size();
        int n = names.size();
        for (int i = 0; i < n; i++){
            if (names.get(i).compareTo(name) > 0){
                pos = i;
                break;
            }
        }
        images.add(pos, bitmap);
        names.add(pos, name);
        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("DELETEIMAGE1", "request0 " + requestCode + " " + (requestCode == FULL_GALERY_CODE) + " " + FULL_GALERY_CODE);
        if (requestCode == Pick_image)
        {
            if (resultCode == RESULT_OK) {
                try {
                    saveAndOpenImage(data.getData());
                    // Обработка выбранного изображения из галереи
                } catch (Exception e) {
                    Log.d("IMAGE1", e.toString());
                }
            }
        }

        else if (requestCode == FULL_GALERY_CODE) {
            Log.d("DELETEIMAGE1", "request");

            // Вернулись из показа полных изображений. Если там удалили изображение, то меняем список имен и изображений
            if (resultCode == RESULT_OK) {
                List<Integer> index = new ArrayList<>();
                for (int i = 0; i < names.size(); i++) {
                    if (!(new File(names.get(i)).exists())) {
                        index.add(i);
                    }
                }

                int minus = 0;
                for (int i : index) {
                    names.remove(i - minus);
                    images.remove(i - minus);
                    adapter.notifyItemRemoved(i - minus);
                    minus++;
                }
//                adapter.notifyDataSetChanged();
            }
        }


    }
}
