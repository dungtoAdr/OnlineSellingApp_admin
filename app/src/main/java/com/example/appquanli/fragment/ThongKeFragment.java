package com.example.appquanli.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.appquanli.R;
import com.example.appquanli.retrofit.ApiBanHang;
import com.example.appquanli.retrofit.RetrofitClient;
import com.example.appquanli.utils.Utils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThongKeFragment extends Fragment {
    PieChart pieChart;
    CompositeDisposable compositeDisposable=new CompositeDisposable();
    ApiBanHang apiBanHang;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thongke,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiBanHang= RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        initView(view);
        getdataChart();
    }
    private void getdataChart() {
        List<PieEntry> list =new ArrayList<>();
        compositeDisposable.add(apiBanHang.getthongke()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        thongKeModel -> {
                            for (int i=0;i<thongKeModel.getResult().size();i++){
                                String tensp=thongKeModel.getResult().get(i).getTensp();
                                int tong=thongKeModel.getResult().get(i).getTong();
                                list.add(new PieEntry(tong,tensp));
                            }
                            PieDataSet pieDataSet=new PieDataSet(list,"Thống kê");
                            PieData data=new PieData();
                            data.setDataSet(pieDataSet);
                            data.setValueTextSize(12f);
                            data.setValueFormatter(new PercentFormatter(pieChart));
                            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                            pieChart.setData(data);
                            pieChart.animateXY(2000,2000);
                            pieChart.setUsePercentValues(true);
                            pieChart.invalidate();
                        },throwable -> {
                            Log.d("Logg",throwable.getMessage());
                        }
                ));
    }
    private void initView(View view) {
        pieChart=view.findViewById(R.id.piechart);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
