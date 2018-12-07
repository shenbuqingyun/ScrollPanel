package com.chinstyle.scrollpanel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者    Chin_style
 * 时间    2018/12/7
 * 文件    ScrollPanel
 * 描述    用于填充ScrollPanel的碎片
 */
public class FrontPanelFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_front_panel, container, false);
        return view;
    }
}
