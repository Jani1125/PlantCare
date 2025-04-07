package hu.nje.plantcare.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.nje.plantcare.R;
import hu.nje.plantcare.recyclerview.model.RecyclerModel;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {


    Context context;
    List<RecyclerModel> recyclerModels;

    public RecyclerAdapter( Context context,List<RecyclerModel> recyclerModels){
        this.context = context;
        this.recyclerModels = recyclerModels;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtTitle.setText(recyclerModels.get(position).getText());
        RecyclerModel model = recyclerModels.get(position);


    }

    @Override
    public int getItemCount() {
        return recyclerModels.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView txtTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle= itemView.findViewById(R.id.txt_title);
        }
    }
}


