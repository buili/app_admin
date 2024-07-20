package com.example.bt.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.bt.Interface.ItemClickListener;
import com.example.bt.R;
import com.example.bt.model.DonHang;
import com.example.bt.model.EventBus.DonHangEvent;
import com.example.bt.model.Item;
import com.example.bt.ultil.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    Context context;
    List<DonHang> donHangList;


    RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public DonHangAdapter(Context context, List<DonHang> donHangList) {
        this.context = context;
//        this.donHangList = donHangList;
        this.donHangList = donHangList != null ? donHangList : new ArrayList<>();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder  implements  View.OnLongClickListener{
        TextView txtdonhang, txttrangthai, diachi;
        RecyclerView recyclerView;
        ItemClickListener listener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtdonhang = itemView.findViewById(R.id.iddonhang);
            txttrangthai = itemView.findViewById(R.id.trangthaidon);
            recyclerView = itemView.findViewById(R.id.recyclerview_chitiet);
            diachi = itemView.findViewById(R.id.diachi_donhang);
            itemView.setOnLongClickListener(this);
        }

        public void setListener(ItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onClick(v, getAdapterPosition(), true);
            return false;
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang, parent, false);
        return  new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang = donHangList.get(position);
        holder.txtdonhang.setText("Đơn Hàng: " + donHang.getId());
        holder.txttrangthai.setText(Utils.statusOrder(donHang.getTrangthai()));



        List<Item> itemDonHangList = donHang.getItem();
        if (itemDonHangList == null) {
            itemDonHangList = new ArrayList<>();
        }
        Log.d("DonHangAdapter", "ItemDonHangList size: " + itemDonHangList.size());
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.recyclerView.getContext(), LinearLayoutManager.VERTICAL, false
        );
//        layoutManager.setInitialPrefetchItemCount(donHang.getItemDonHangList().size());

        layoutManager.setInitialPrefetchItemCount(itemDonHangList.size());
        ChiTietDonHangAdapter chiTietDonHangAdapter = new ChiTietDonHangAdapter(context, donHang.getItem());
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setAdapter(chiTietDonHangAdapter);
        holder.recyclerView.setRecycledViewPool(viewPool);

        holder.setListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(isLongClick){
                    EventBus.getDefault().postSticky(new DonHangEvent(donHang));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return donHangList.size();
    }


}
