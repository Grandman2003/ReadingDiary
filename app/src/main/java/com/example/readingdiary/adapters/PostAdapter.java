package com.example.readingdiary.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readingdiary.Classes.Note;
import com.example.readingdiary.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostPosts>
{
    private List<Note> notes;

    public PostAdapter(List<Note> notes)
    {
        this.notes = notes;
    }

    @Override
    public PostPosts onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_activity, viewGroup, false);
        PostAdapter.PostPosts vh = new PostAdapter.PostPosts(v);
        return vh;
    }



    class PostPosts extends RecyclerView.ViewHolder {
        private TextView tvNamePost;
        private TextView tvTextPost;
        private TextView tvReitPost;

        private ImageView imageView2;

        private RatingBar ratingBar2;




        public PostPosts(final View itemView) {
            super(itemView);
            tvNamePost = (TextView) itemView.findViewById(R.id.tvNamePost);
            tvTextPost = (TextView) itemView.findViewById(R.id.tvTextPost);
            tvReitPost = (TextView) itemView.findViewById(R.id.tvReitPost);
            imageView2 = (ImageView) itemView.findViewById(R.id.imageView2);
            ratingBar2 = (RatingBar) itemView.findViewById(R.id.ratingBar2);
        }

    }




    @Override
    public void onBindViewHolder(@NonNull PostPosts holder, int position) {
        holder.tvNamePost.setText("Asdfghj");
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
