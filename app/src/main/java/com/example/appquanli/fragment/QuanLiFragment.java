package com.example.appquanli.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appquanli.R;
import com.example.appquanli.ThemSanPhamActivity;
import com.example.appquanli.adapter.SanPhamMoiAdapter;
import com.example.appquanli.model.EventBus.SuaXoaEvent;
import com.example.appquanli.model.SanPhamMoi;
import com.example.appquanli.retrofit.ApiBanHang;
import com.example.appquanli.retrofit.RetrofitClient;
import com.example.appquanli.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import soup.neumorphism.NeumorphCardView;

public class QuanLiFragment extends Fragment {
    NeumorphCardView themsp;
    CompositeDisposable compositeDisposable=new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpmoi;
    SanPhamMoiAdapter sanPhamMoiAdapter;
    SanPhamMoi sanPhamSuaXoa;
    RecyclerView recycleviewQl;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quanli,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiBanHang= RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        initView(view);
        initControl();
        getSpMoi();
    }

    private void initView(View view) {
        themsp=view.findViewById(R.id.neu_themsanpham);
        recycleviewQl=view.findViewById(R.id.recycleview_ql);
        RecyclerView.LayoutManager manager=new GridLayoutManager(getContext(),2);
        recycleviewQl.setLayoutManager(manager);
        recycleviewQl.setHasFixedSize(true);
    }
    private void initControl() {
        themsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ThemSanPhamActivity.class);
                startActivity(intent);
            }
        });
    }
    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getspmoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if(sanPhamMoiModel.isSuccess()){
                                mangSpmoi=sanPhamMoiModel.getResult();
                                sanPhamMoiAdapter=new SanPhamMoiAdapter(getContext(),mangSpmoi);
                                recycleviewQl.setAdapter(sanPhamMoiAdapter);
                            }
                        }
                ));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Sửa")){
            suaSanPham();
        }else if(item.getTitle().equals("Xóa")){
            xoaSanPham();
        }
        return super.onContextItemSelected(item);
    }
    private void xoaSanPham() {
        compositeDisposable.add(apiBanHang.xoaSanPham(sanPhamSuaXoa.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()){
                                Toast.makeText(getContext(),messageModel.getMessage(),Toast.LENGTH_SHORT).show();
                                getSpMoi();
                            }else{
                                Toast.makeText(getContext(),messageModel.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void suaSanPham() {
        Intent intent=new Intent(getContext(),ThemSanPhamActivity.class);
        intent.putExtra("sua",sanPhamSuaXoa);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void evenSuaXoa(SuaXoaEvent suaXoaEvent){
        if (suaXoaEvent!=null){
            sanPhamSuaXoa=suaXoaEvent.getSanPhamMoi();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
