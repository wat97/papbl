package filkom.ub.getmeallocation.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import filkom.ub.getmeallocation.R;
import filkom.ub.getmeallocation.DetailRestoranActivity;
import filkom.ub.getmeallocation.model.MenuModel;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<MenuModel> menus;
    private ArrayList<String> imageKey;

    public MenuAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(ArrayList<MenuModel> menus) {
        this.menus = menus;
        this.imageKey = imageKey;
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.tvNama.setText(menus.get(position).getNamaMenu());
        holder.tvDate.setText(menus.get(position).getDate());
        holder.tvHarga.setText(menus.get(position).getHarga());
        Picasso.get().load(menus.get(position).getImageUrl()).into(holder.iv_menu);
        //Picasso.get().load(menus.get(position).getImageUrl()).into(holder.iv_menu);
        //holder.iv_menu.setImageURI(Uri.parse(imageKey.get(position)));
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, menus.get(position).getImageUrl(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DetailRestoranActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("MENU", (Serializable) menus.get(position));
                intent.putExtra("BUNDLE", args);
                context.startActivity(intent);
            }
        });

//        File localFile;
//        try {
//            localFile = File.createTempFile("images"+ position,"jpg");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference("menu" + "/" + imageKey.get(position) + ".jpg");
//            storageReference.getFile(localFile)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            String uri =  localFile.toURI().toString();
//                            Toast.makeText(context, uri, Toast.LENGTH_SHORT).show();
//                            holder.iv_menu.setImageURI(null);
//                            holder.iv_menu.setImageURI(Uri.parse(uri));
//                            //imageKey.add(uri);
//                        }
//                    });
//    }
//
//    private void getAllImage() throws IOException {
//        for (int i = 0; i < imageKey.size(); i++) {
//            localFile = File.createTempFile("images"+i, "jpg");
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference("menu" + "/" + imageKey.get(i) + ".jpg");
//            storageReference.getFile(localFile)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            String uri =  localFile.toURI().toString();
//                            Toast.makeText(context, uri, Toast.LENGTH_SHORT).show();
//                            imageKey.add(uri);
//                        }
//                    });
//        }
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
        private ImageView iv_menu;

        public ViewHolder(View itemView) {
            super(itemView);

            btnDetail = (Button) itemView.findViewById(R.id.btn_detail);
            tvNama = (TextView) itemView.findViewById(R.id.tv_nama);
            tvHarga = (TextView) itemView.findViewById(R.id.tv_harga);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            iv_menu = (ImageView) itemView.findViewById(R.id.iv_menu);
        }
    }
}
