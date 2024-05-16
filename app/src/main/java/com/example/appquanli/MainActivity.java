package com.example.appquanli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.appquanli.adapter.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    BottomNavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager=findViewById(R.id.viewpager);
        navigationView=findViewById(R.id.nav_view);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),4);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0: navigationView.getMenu().findItem(R.id.nav_donhang).setChecked(true);
                    break;
                    case 1: navigationView.getMenu().findItem(R.id.nav_quanli).setChecked(true);
                        break;
                    case 2: navigationView.getMenu().findItem(R.id.nav_chat).setChecked(true);
                        break;
                    case 3: navigationView.getMenu().findItem(R.id.nav_thongke).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.nav_donhang){
                    viewPager.setCurrentItem(0);
                }
                else if(item.getItemId()==R.id.nav_quanli){
                    viewPager.setCurrentItem(1);
                } else if (item.getItemId()==R.id.nav_chat) {
                    viewPager.setCurrentItem(2);

                }else if (item.getItemId()==R.id.nav_thongke){
                    viewPager.setCurrentItem(3);
                }
                return true;
            }
        });
    }
}