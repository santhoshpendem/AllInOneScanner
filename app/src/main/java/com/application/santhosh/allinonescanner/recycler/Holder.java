package com.application.santhosh.allinonescanner.recycler;

import com.application.santhosh.allinonescanner.R;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by santhoshkumar on 1/4/2017.
 */

public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView ivImg;
    TextView tvName,tvDate;
    ItemClickListener itemClickListener;

    public Holder(View itemView) {
        super(itemView);
        ivImg = (ImageView)itemView.findViewById(R.id.imgicon);
        tvName = (TextView)itemView.findViewById(R.id.tx_imgname);
        tvDate = (TextView) itemView.findViewById(R.id.tx_date);

        itemView.setOnClickListener(this);

    }

    //When Clicked
    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());

    }

    //shall be called outside
    public void setItemClickListener(ItemClickListener ic){
         this.itemClickListener = ic;
    }
}
