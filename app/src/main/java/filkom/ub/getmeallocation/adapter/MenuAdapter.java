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


import java.io.Serializable;
import java.util.ArrayList;

import filkom.ub.getmeallocation.R;
import filkom.ub.getmeallocation.model.DetailRestoranActivity;
import filkom.ub.getmeallocation.model.RestoranModel;

public class RestoranAdapter extends RecyclerView.Adapter<RestoranAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<RestoranModel> restorans;

    public RestoranAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(ArrayList<RestoranModel> restoranModel) {
        this.restorans = restoranModel;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.row_restoran, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.tvNama.setText(restorans.get(position).getNamaRestoran());
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailRestoranActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("RESTORAN", (Serializable) restorans.get(position));
                intent.putExtra("BUNDLE", args);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restorans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNama;
        private Button btnDetail;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNama = (TextView) itemView.findViewById(R.id.tv_nama);
            btnDetail = (Button) itemView.findViewById(R.id.btn_detail);
        }
    }
}
