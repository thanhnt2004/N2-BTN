package com.example.btln2.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btln2.R;
import com.example.btln2.data.local.entities.Category;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvCategoryName.setText(category.categoryName);
        
        if (selectedPosition == position) {
            holder.cardCategory.setStrokeColor(Color.parseColor("#2196F3"));
            holder.cardCategory.setCardBackgroundColor(Color.parseColor("#E3F2FD"));
            holder.tvCategoryName.setTextColor(Color.parseColor("#2196F3"));
        } else {
            holder.cardCategory.setStrokeColor(Color.parseColor("#DDDDDD"));
            holder.cardCategory.setCardBackgroundColor(Color.WHITE);
            holder.tvCategoryName.setTextColor(Color.BLACK);
        }
        
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            listener.onCategoryClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        MaterialCardView cardCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            cardCategory = itemView.findViewById(R.id.cardCategory);
        }
    }
}