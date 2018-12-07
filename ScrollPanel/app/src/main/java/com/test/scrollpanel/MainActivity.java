package com.test.scrollpanel;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.chinstyle.scrollpanel.FrontPanelFragment;

public class MainActivity extends AppCompatActivity {

    private FrontPanelFragment mFrontPanelFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ScrollPanel继承自FrameLayout 使用时可以动态加载进一个碎片
        mFrontPanelFragment = new FrontPanelFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.scroll_panel_layout, mFrontPanelFragment)
                .commit();
    }

    @Override
    protected void onStart() {
        hideBottomUIMenu();
        super.onStart();
    }

    /**
     * 隐藏状态栏和导航栏，并且全屏；手指上滑时出现导航栏和状态栏
     */
    protected void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            //for low api versions.
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
