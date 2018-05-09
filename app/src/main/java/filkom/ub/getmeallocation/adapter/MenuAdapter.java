package filkom.ub.getmeallocation.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import filkom.ub.getmeallocation.R;
import filkom.ub.getmeallocation.DetailRestoranActivity;
import filkom.ub.getmeallocation.model.MenuModel;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<MenuModel> menus;

    public MenuAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(ArrayList<MenuModel> menus) {
        this.menus = menus;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.row_menu, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.tvNama.setText(menus.get(position).getNamaMenu());
        holder.tvDate.setText(menus.get(position).getDate());
        holder.tvHarga.setText(menus.get(position).getHarga());
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "menu clicked", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(context, DetailRestoranActivity.class);
                //Bundle args = new Bundle();
                //args.putSerializable("MENUS", (Serializable) menus.get(position));
                //intent.putExtra("BUNDLE", args);
                //context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNama;
        private TextView tvHarga;
        private TextView tvDate;
        private Button btnDetail;

        public ViewHolder(View itemView) {
            super(itemView);

            btnDetail = (Button) itemView.findViewById(R.id.btn_detail);
            tvNama = (TextView) itemView.findViewById(R.id.tv_nama);
            tvHarga = (TextView) itemView.findViewById(R.id.tv_harga);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}
