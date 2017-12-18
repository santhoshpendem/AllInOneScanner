package com.application.santhosh.allinonescanner.recycler;

import com.application.santhosh.allinonescanner.presentation.FullscreenActivity;
import com.application.santhosh.allinonescanner.R;
import com.application.santhosh.allinonescanner.model.BillScannerBo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by santhoshkumar on 1/4/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<Holder> {
    Context c;
    String uri;
    List<BillScannerBo> itemList;


    public RecyclerAdapter(Context ctx, List<BillScannerBo> itemList) {
        this.c = ctx;
        this.itemList = itemList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the xml view and Hold in View
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);

        Holder holder = new Holder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        final String img = itemList.get(position).getImgpath().trim();
        final Bitmap preview_bitmap = BitmapFactory.decodeFile(Uri.parse(img).toString());

        holder.ivImg.setImageBitmap(preview_bitmap);
        holder.tvName.setText(itemList.get(position).getName());
        holder.tvDate.setText(itemList.get(position).getDate());

        //Set the Item Click Listener
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent i = new Intent(c, FullscreenActivity.class);
                Uri uri = Uri.parse(img);

                i.setData(uri);
                c.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (itemList == null) {

        } else {
            size = itemList.size();
        }
        return size;
    }
}
