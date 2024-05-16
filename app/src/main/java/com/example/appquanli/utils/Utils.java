package com.example.appquanli.utils;


import com.example.appquanli.model.GioHang;
import com.example.appquanli.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String BASE_URL="https://banhangonline112.000webhostapp.com/";
    public static List<GioHang> manggiohang;
    public static List<GioHang> mangmuahang=new ArrayList<>();
    // o truong: 192.168.25.210
    // o nha: 192.168.1.194
    public static User user_current=new User();

    public static String ID_RECEIVED;
    public static final String SENDID="idsend";
    public static final String RECEIVEDID="idreceived";
    public static final String MESS="message";
    public static final String DATETIME="datetime";
    public static final String PATH_CHAT="chat";
}
