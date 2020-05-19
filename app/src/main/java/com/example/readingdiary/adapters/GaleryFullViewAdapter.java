package com.example.readingdiary.adapters;

//package com.example.galeryproject;

//package com.example.readingdiary;

//package com.example.readingdiary;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.readingdiary.R;

import java.util.List;
// Адаптер поолнога показа изображений
public class GaleryFullViewAdapter extends RecyclerView.Adapter<GaleryFullViewAdapter.ViewHolder>{

    private List<Bitmap> buttons;
    private GaleryFullViewAdapter.OnItemClickListener mListener;
    private GaleryFullViewAdapter.OnItemDeleteListener delListener;
    private Context context;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnItemDeleteListener{
        void onItemDelete(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener){delListener = listener;}



    public GaleryFullViewAdapter(List<Bitmap> buttons, Context context) {
        this.buttons = buttons;
        this.context = context;
        Log.d("BUTTONS", "1");

    }

    /**
     * Создание новых View и ViewHolder элемента списка, которые впоследствии могут переиспользоваться.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.galery_full_view_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        Log.d("BUTTONS", "2");
//        v.setOnClickListener(this);
        return vh;
    }

    /**
     * Заполнение виджетов View данными из элемента списка с номером i
     */
//    private void removeAt(int position) {
//        buttons.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, buttons.size());
//    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Log.d("BUTTONS", "3");
        Bitmap source = buttons.get(i);
        Log.d("BUTTONS", ""+buttons.get(i));

        final int el = i;

        viewHolder.imageView.setImageBitmap(source);
//        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                delListener.onItemDelete(el);
//            }
//        });

    }

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
//        private Button deleteButton;

//        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d("BUTTONS", "4");
            imageView = (ImageView) itemView.findViewById(R.id.galery_image_el1);
//            deleteButton = (Button) itemView.findViewById(R.id.delede_image_full);
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



