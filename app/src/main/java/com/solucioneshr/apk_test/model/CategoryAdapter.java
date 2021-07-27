package com.solucioneshr.apk_test.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.solucioneshr.apk_test.MainActivity;
import com.solucioneshr.apk_test.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private List<String> data;

    public CategoryAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View theView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cards_category, parent, false);
        theView.setOnClickListener(MainActivity.Click_Category);
        return new MyViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.MyViewHolder holder, int position) {
        TextView text = holder.textDialog;
        text.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textDialog;

        public MyViewHolder(View itemView) {
            super(itemView);
            textDialog = itemView.findViewById(R.id.textview_cardsDialogCategory);
        }
    }


}
