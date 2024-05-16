package com.example.appquanli.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.appquanli.Interface.ItemClickListener;
import com.example.appquanli.R;
import com.example.appquanli.model.DonHang;
import com.example.appquanli.model.EventBus.DonHangEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    private RecyclerView.RecycledViewPool recycledViewPool=new RecyclerView.RecycledViewPool();
    Context context;
    List<DonHang> list;


    public DonHangAdapter(Context context, List<DonHang> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang=list.get(position);
        holder.txtdonhang.setText("Đơn hàng: "+donHang.getId());
        holder.trangthai.setText(trangThai(donHang.getTrangthai()));
        LinearLayoutManager manager=new LinearLayoutManager(
          holder.reChitiet.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        manager.setInitialPrefetchItemCount(donHang.getItem().size());
        // adapter chi tiet
        ChiTietAdapter adapter=new ChiTietAdapter(context,donHang.getItem());
        holder.reChitiet.setAdapter(adapter);
        holder.reChitiet.setLayoutManager(manager);
        holder.reChitiet.setRecycledViewPool(recycledViewPool);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick){
                    EventBus.getDefault().postSticky(new DonHangEvent(donHang));
                }
            }
        });
    }
    private String trangThai(int status){
        String result="";
        switch (status){
            case 0:
                result ="Đơn hàng đang được xử lí";
                break;
            case 1:
                result="Đơn hàng đã chấp nhận";
                break;
            case 2:
                result="Đơn hàng đã giao cho đơn vị vận chuyển";
                break;
            case 3:
                result="Đơn hàng đã giao thành công";
                break;
            case 4:
                result="Đơn hàng đã hủy";
                break;
        }
        return result;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView txtdonhang,trangthai;
        RecyclerView reChitiet;
        ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtdonhang=itemView.findViewById(R.id.iddonhang);
            reChitiet=itemView.findViewById(R.id.recycleview_chitiet);
            trangthai=itemView.findViewById(R.id.tinhtrang);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),true);
            return false;
        }
    }
}
