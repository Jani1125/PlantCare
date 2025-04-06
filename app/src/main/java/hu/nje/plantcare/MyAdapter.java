package hu.nje.plantcare;// MyAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<String> dataList;

    // Konstruktor, hogy átadjuk az adatokat
    public MyAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    // ViewHolder osztály a listaelemek kezelésére
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(android.R.id.text1);  // A TextView referencia
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false); // Egyszerű listaelem
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(dataList.get(position));  // Adat hozzárendelése a TextView-hoz
    }

    @Override
    public int getItemCount() {
        return dataList.size();  // Az elemek száma
    }
}
