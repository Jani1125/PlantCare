package hu.nje.plantcare.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.List;

import hu.nje.plantcare.R;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<String> menuItems;
    private OnMenuItemClickListener listener;
    private Context context;

    public MenuAdapter(Context context, List<String> menuItems, OnMenuItemClickListener listener) {
        this.context = context;
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        String item = menuItems.get(position);
        holder.menuItemText.setText(item);
        holder.itemView.setOnClickListener(v -> listener.onMenuItemClick(item));
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView menuItemText;

        public MenuViewHolder(View itemView) {
            super(itemView);
            menuItemText = itemView.findViewById(R.id.menuItemText);
        }
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClick(String item);
    }
}
