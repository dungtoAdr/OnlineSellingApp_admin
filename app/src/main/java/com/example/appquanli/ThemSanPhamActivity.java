package com.example.appquanli;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appquanli.databinding.ActivityThemSanPhamBinding;
import com.example.appquanli.model.MessageModel;
import com.example.appquanli.model.SanPhamMoi;
import com.example.appquanli.retrofit.ApiBanHang;
import com.example.appquanli.retrofit.RetrofitClient;
import com.example.appquanli.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemSanPhamActivity extends AppCompatActivity {
    Spinner spinner;
    int loai=0;
    ActivityThemSanPhamBinding binding;
    String mediaPath;
    ApiBanHang apiBanHang;
    SanPhamMoi sanPhamSua;
    boolean flag=false;
    CompositeDisposable compositeDisposable=new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityThemSanPhamBinding.inflate(getLayoutInflater());
        apiBanHang= RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        setContentView(binding.getRoot());
        init();
        initData();
        Intent intent=getIntent();
        sanPhamSua= (SanPhamMoi) intent.getSerializableExtra("sua");
        if (sanPhamSua==null){
            //theem moi
            flag=false;
        }else{
            //sua sp
            flag=true;
            binding.toolbar.setTitle("Sửa sản phẩm");
            binding.btthemsp.setText("Sửa sản phẩm");
            //show data
            binding.mota.setText(sanPhamSua.getMota());
            binding.giasanpham.setText(sanPhamSua.getGiasp());
            binding.tensanpham.setText(sanPhamSua.getTensp());
            binding.hinhanh.setText(sanPhamSua.getHinhanh());
            binding.spinnerLoai.setSelection(sanPhamSua.getLoai());
            binding.slsp.setText(sanPhamSua.getSltonkho()+"");
        }

    }

    private void initData() {
        List<String> stringlist=new ArrayList<>();
        stringlist.add("Vui lòng chọn loại");
        stringlist.add("Loại 1");
        stringlist.add("Loại 2");
        ArrayAdapter<String> stringArrayAdapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,stringlist);
        spinner.setAdapter(stringArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loai=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.btthemsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag==false){
                    themsp();
                }else{
                    suaSanPham();
                }
            }
        });


        binding.imgcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ThemSanPhamActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    private void suaSanPham() {
        String str_ten=binding.tensanpham.getText().toString().trim();
        String str_gia=binding.giasanpham.getText().toString().trim();
        String str_mota=binding.mota.getText().toString().trim();
        String str_hinhanh=binding.hinhanh.getText().toString().trim();
        String str_slsp=binding.slsp.getText().toString();
        if (TextUtils.isEmpty(str_slsp) || TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) || TextUtils.isEmpty(str_mota) || TextUtils.isEmpty(str_hinhanh) || loai==0){
            Toast.makeText(getApplicationContext(),"Nhập đầy đủ thong tin",Toast.LENGTH_SHORT).show();
        }else{
            compositeDisposable.add(apiBanHang.updateSp(str_ten,str_gia,str_hinhanh,str_mota,sanPhamSua.getLoai(),sanPhamSua.getId(),Integer.parseInt(str_slsp))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if(messageModel.isSuccess()){
                                    Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                    ));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            mediaPath=data.getDataString();
            uploadMultipleFiles();
            Log.d("log","onActivityresult"+mediaPath);
        }else{
            Toast.makeText(getApplicationContext(),"Khoong co du lieu",Toast.LENGTH_SHORT).show();
        }

    }

    private void themsp() {
        String str_ten=binding.tensanpham.getText().toString().trim();
        String str_gia=binding.giasanpham.getText().toString().trim();
        String str_mota=binding.mota.getText().toString().trim();
        String str_hinhanh=binding.hinhanh.getText().toString().trim();
        String str_soluong=binding.slsp.getText().toString();
        if (TextUtils.isEmpty(str_soluong) || TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) || TextUtils.isEmpty(str_mota) || TextUtils.isEmpty(str_hinhanh) || loai==0){
            Toast.makeText(getApplicationContext(),"Nhập đầy đủ thong tin",Toast.LENGTH_SHORT).show();
        }else{
            compositeDisposable.add(apiBanHang.insertSp(str_ten,str_gia,str_hinhanh,str_mota,(loai),Integer.parseInt(str_soluong))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if(messageModel.isSuccess()){
                                    Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                    ));
        }
    }

    private String getPath(Uri uri){
        String result;
        Cursor cursor=getContentResolver().query(uri,null,null,null,null);
        if(cursor == null){
            result=uri.getPath();
        }else{
            cursor.moveToFirst();
            int index=cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result=cursor.getString(index);
            cursor.close();
        }

        return result;
    }

    // Uploading Image/Video
    private void uploadMultipleFiles() {
        Uri uri=Uri.parse(mediaPath);
        // Map is used to multipart the file using okhttp3.RequestBody
        File file=new File(getPath(uri));
        // Parsing any Media type file
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file", file.getName(), requestBody1);
        Call<MessageModel> call = apiBanHang.uploadFile(fileToUpload1);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(Call<MessageModel> call, Response< MessageModel > response) {
                MessageModel serverResponse = response.body();
                if (serverResponse!=null){
                    if (serverResponse.isSuccess()){
                        binding.hinhanh.setText(serverResponse.getName());
                    }else {
                        Toast.makeText(getApplicationContext(),serverResponse.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.v("Response",serverResponse.toString());
                }
            }
            @Override
            public void onFailure(Call < MessageModel > call, Throwable t) {
                Log.d("log3",t.getMessage());
            }
        });
    }

    public void init(){
        spinner=findViewById(R.id.spinner_loai);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}