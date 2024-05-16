package com.example.appquanli.fragment;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appquanli.R;
import com.example.appquanli.adapter.DonHangAdapter;
import com.example.appquanli.model.DonHang;
import com.example.appquanli.model.EventBus.DonHangEvent;
import com.example.appquanli.model.NotiSendData;
import com.example.appquanli.retrofit.ApiBanHang;
import com.example.appquanli.retrofit.ApiPushNofication;
import com.example.appquanli.retrofit.RetrofitClient;
import com.example.appquanli.retrofit.RetrofitClientNoti;
import com.example.appquanli.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DonhangFragment extends Fragment {
    CompositeDisposable compositeDisposable=new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView redonhang;
    Toolbar toolbar;
    DonHang donHang;
    int tinhtrang;
    AlertDialog dialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donhang,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        getOrder();
    }
    private void getOrder() {
        compositeDisposable.add(apiBanHang.xemDonHang(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            DonHangAdapter adapter=new DonHangAdapter(getContext(),donHangModel.getResult());
                            redonhang.setAdapter(adapter);
                        },
                        throwable -> {
                            Toast.makeText(getContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                ));
    }


    public void init(View view){
        apiBanHang= RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        redonhang=view.findViewById(R.id.recycleview_donhang);
        LinearLayoutManager manager=new LinearLayoutManager(view.getContext());
        redonhang.setLayoutManager(manager);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }


    private void showCustomDialog() {
        LayoutInflater layoutInflater=getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.dialog_donhang,null,false);
        Spinner spinner=view.findViewById(R.id.spinner_dialog);
        AppCompatButton button=view.findViewById(R.id.dongy_dialog);
        List<String> list=new ArrayList<>();
        list.add("Đơn hàng đang được xử lí");
        list.add("Đơn hàng đã chấp nhận");
        list.add("Đơn hàng đã giao cho đơn vị vận chuyển");
        list.add("Đơn hàng đã giao thành công");
        list.add("Đơn hàng đã hủy");
        ArrayAdapter<String> stringArrayAdapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,list);
        spinner.setAdapter(stringArrayAdapter);
        spinner.setSelection(donHang.getTrangthai());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tinhtrang=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capNhatDonHang();
            }
        });
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setView(view);
        dialog=builder.create();
        dialog.show();
    }



    private void capNhatDonHang() {
        compositeDisposable.add(apiBanHang.updateOrder(donHang.getId(),tinhtrang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            getOrder();
                            dialog.dismiss();
                            pushNotiToUser();
                        },throwable -> {
                            Log.d("log",throwable.getMessage());
                        }
                ));
    }
    private void pushNotiToUser() {
        compositeDisposable.add(apiBanHang.getToken(0,donHang.getIduser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()){
                                for (int i=0;i<userModel.getResult().size();i++){
                                    Map<String, String> data=new HashMap<>();
                                    data.put("title","thong bao");
                                    data.put("body",trangThai(tinhtrang));
                                    NotiSendData notiSendData=new NotiSendData(userModel.getResult().get(i).getToken(),data);
                                    ApiPushNofication apiPushNofication= RetrofitClientNoti.getInstance().create(ApiPushNofication.class);
                                    compositeDisposable.add(apiPushNofication.sendNofitication(notiSendData)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    notiResponse -> {

                                                    },
                                                    throwable -> {
                                                        Log.d("log",throwable.getMessage());
                                                    }
                                            ));
                                }
                            }
                        },throwable -> {
                            Log.d("log",throwable.getMessage());
                        }
                ));

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
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void evenDonHang(DonHangEvent donHangEvent){
        if(donHangEvent!=null){
            donHang=donHangEvent.getDonHang();
            showCustomDialog();
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
