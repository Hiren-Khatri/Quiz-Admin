package com.example.quizadmin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryModel> categoryModelList;
    private DeleteListener deleteListener;

    public CategoryAdapter(List<CategoryModel> categoryModelList,DeleteListener deleteListener) {
        this.categoryModelList = categoryModelList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
        return new CategoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(categoryModelList.get(position).getName(),categoryModelList.get(position).getKey(),position);
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private  ImageButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            delete = itemView.findViewById(R.id.delete);
        }
        private void setData(final String title,final String key,final int position){
            this.title.setText(title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setIntent = new Intent(itemView.getContext(), SetsActivity.class);
                    setIntent.putExtra("title",title);
                    setIntent.putExtra("position",position);
                    setIntent.putExtra("key",key);
                    itemView.getContext().startActivity(setIntent);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListener.onDelete(key,position);
                }
            });
        }
    }

    public interface DeleteListener{
        public void onDelete(String key,int position);
    }
}
