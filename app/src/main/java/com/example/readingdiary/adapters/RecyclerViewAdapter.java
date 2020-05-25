package com.example.readingdiary.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.readingdiary.Classes.Directory;
import com.example.readingdiary.Classes.Note;
import com.example.readingdiary.Classes.RealNote;
import com.example.readingdiary.R;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


// адаптер элементов каталога Note
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private boolean actionMode;
    private List<Note> notes;
    private OnItemClickListener mListener;
    private final int TYPE_ITEM1 = 0;
    private final int TYPE_ITEM2 = 1;
    private Context context;

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onItemLongClick(int position);
        void onCheckClick(int position);
        void onUncheckClick(int position);
        void onPrivacyChanged(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;    }

    public RecyclerViewAdapter(List<Note> notes, Context context) {
        this.notes = notes;
        this.actionMode = false;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == TYPE_ITEM1){
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_catalog_item0, viewGroup, false);
        }
        else{
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_catalog_item1, viewGroup, false);
        }
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);
        if (actionMode == false){
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        else{
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.checkBox.setChecked(false);
        }
        if (type == TYPE_ITEM1){

            RealNote realNote = (RealNote) notes.get(i);
            viewHolder.author.setText(realNote.getAuthor());
            viewHolder.title.setText(realNote.getTitle());
            viewHolder.ratingBar.setRating((float)realNote.getRating());
            if (realNote.getPrivate()){
                viewHolder.privacyButton.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.ic_action_private_dark));
            }
            else{
                viewHolder.privacyButton.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.ic_action_public_dark));
            }
            if (realNote.getCoverUri() !=null){
                Picasso.get().load(realNote.getCoverUri()).into(viewHolder.cover);
            }
        }
        if (type == TYPE_ITEM2){
            Directory directory = (Directory) notes.get(i);
            String[] dir = directory.getDirectory().split("/");
            viewHolder.path2.setText(dir[dir.length-1]);
        }

    }

    @Override
    public int getItemViewType(int position) {
        // определяем какой тип в текущей позиции
        int type = notes.get(position).getItemType();
        if (type == 0) return TYPE_ITEM1;
        else return TYPE_ITEM2;

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    public void clearAdapter() {
        notes.clear();
        notifyDataSetChanged();
    }

    public void setActionMode(boolean mode){
        actionMode = mode;
    }

    public void updateAdapter(ArrayList<Note> list){
        for (Note note : list){
            notes.remove(note);
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
//        private TextView path1;
        private TextView path2;

        private TextView title;
        private TextView author;
        private ImageView cover;
        private CheckBox checkBox;
        private RatingBar ratingBar;
        private MaterialCardView cardView;
        private MaterialCardView cardView2;
        private ImageButton privacyButton;


//        private ImageView icon;

        public ViewHolder(final View itemView) {
            super(itemView);
//            path1 = (TextView) itemView.findViewById(R.id.catalogN);
            title = (TextView) itemView.findViewById(R.id.catalogNoteTitleView);
            author = (TextView) itemView.findViewById(R.id.catalogNoteAuthorView);
            cover = (ImageView) itemView.findViewById(R.id.catalogNoteImageView) ;
            ratingBar = (RatingBar) itemView.findViewById(R.id.catalogNoteRatingView);
            checkBox = (CheckBox) itemView.findViewById(R.id.catalogNoteCheckBox);
//            cardView = (CardView) itemView.findViewById(R.id.catalogNoteCardView);
            path2 = (TextView) itemView.findViewById(R.id.pathViewCatalog1);
            cardView = (MaterialCardView) itemView.findViewById(R.id.catalogNoteCardView);
            cardView2 = (MaterialCardView) itemView.findViewById(R.id.catalogDirectoryCardView);
            privacyButton = (ImageButton) itemView.findViewById(R.id.privacyButton);



            Log.d("toBeOrNotToBe", cardView + "! ");
            if (cardView != null){
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemLongClick(position);
                        }
                        return true;
                    }
                    return false;
                }

            });
            }

            if (checkBox != null){
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked()){
                            mListener.onCheckClick(getAdapterPosition());
                        }
                        else{
                            mListener.onUncheckClick(getAdapterPosition());
                        }
                    }

                });
            }
            if (cardView2 != null){
                cardView2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mListener != null){
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION){
                                mListener.onItemLongClick(position);
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }

            if (privacyButton != null){
                privacyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null){
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION){
                                mListener.onPrivacyChanged(position);
                            }
//                            privacyButton.setImageDrawable();
                        }
                    }
                });
            }

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



//            cardView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    if (mListener != null){
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION){
//                            mListener.onItemLongClick(position);
//                        }
//                        return true;
//                    }
//                    return false;
//                }
//
//            });
        }

    }



}
