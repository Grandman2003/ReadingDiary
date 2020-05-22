package com.example.readingdiary.adapters;

//package com.example.galeryproject;

//package com.example.readingdiary;

//package com.example.readingdiary;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.readingdiary.R;

import java.util.List;

// Адаптер для GaleryActivity.
public class GaleryRecyclerViewAdapter extends RecyclerView.Adapter<GaleryRecyclerViewAdapter.ViewHolder>{

    private List<Bitmap> buttons;
    private GaleryRecyclerViewAdapter.OnItemClickListener mListener;
    private Context context;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public GaleryRecyclerViewAdapter(List<Bitmap> buttons, Context context) {
        this.buttons = buttons;
        this.context = context;
    }

    /**
     * Создание новых View и ViewHolder элемента списка, которые впоследствии могут переиспользоваться.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.galery_view_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
//        v.setOnClickListener(this);
        return vh;
    }

    /**
     * Заполнение виджетов View данными из элемента списка с номером i
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
//        String path = buttons.get(i);
//        FileInputStream fileInputStream = openFileInput(path);
//        Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
        Bitmap source = buttons.get(i);
//        Display display = context.getResources().getDisplayMetrics()
        DisplayMetrics metricsB = context.getResources().getDisplayMetrics();
//        display.getMetrics(metricsB);
        float size = metricsB.widthPixels / 3;
        float size1 = Math.min(source.getWidth(), source.getHeight());
        float k = size / size1;
        Log.d("SCALE", k+" " + size + " " + size1);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, (int)(source.getWidth() * k), (int)(source.getHeight() * k), false);
        int x = (int)((resizedBitmap.getWidth() - size) / 2);
        int y = (int)((resizedBitmap.getHeight() - size) / 2);
        Bitmap result = Bitmap.createBitmap(resizedBitmap, x, y, (int)size, (int)size);

        viewHolder.imageView.setImageBitmap(result);
//        viewHolder.imageView.setImageBitmap(buttons.get(i);


//        viewHolder.imageview.setText(tokens[tokens.length - 1] + " > ");
//        int type = getItemViewType(i);
//        if (type == TYPE_ITEM1){
//            RealNote realNote = (RealNote) notes.get(i);
//            viewHolder.path1.setText(realNote.getPath());
//            viewHolder.author.setText(realNote.getAuthor());
//            viewHolder.title.setText(realNote.getTitle());
//        }
//        if (type == TYPE_ITEM2){
//            Directory directory = (Directory) notes.get(i);
//            viewHolder.path2.setText(directory.getDirectory());
//        }

//
//
//        Note note = notes.get(i);
//        viewHolder.path.setText(note.getPath());
//        viewHolder.title.setText(note.getTitle());
//        viewHolder.author.setText(note.getAuthor());

    }
//
//    @Override
//    public int getItemViewType(int position) {
//        // определяем какой тип в текущей позиции
//        int type = notes.get(position).getItemType();
//        if (type == 0) return TYPE_ITEM1;
//        else return TYPE_ITEM2;
//
//    }

    @Override
    public int getItemCount() {
        return buttons.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void clearAdapter() {
        buttons.clear();
        notifyDataSetChanged();
    }

    /**
     * Реализация класса ViewHolder, хранящего ссылки на виджеты.
     */

    class ViewHolder extends RecyclerView.ViewHolder {
        //        private TextView path1;
//        private TextView path2;
//
//        private TextView title;
//        private TextView author;
        private ImageView imageView;

//        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.galery_image_el0);
//            title = (TextView) itemView.findViewById(R.id.titleViewCatalog);
//            author = (TextView) itemView.findViewById(R.id.authorViewCatalog);
//            path2 = (TextView) itemView.findViewById(R.id.pathViewCatalog1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }


}



