package com.yang.music.base;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.yang.music.R;
import com.yang.music.combination.TopView;

import de.greenrobot.event.EventBus;




public class BaseActivity extends AppCompatActivity implements BaseInterface{

    protected boolean isEvent = false;
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initConfig();
        initgetIntent();
        initUi();
        initListener();
        initData();
        initTitle();
    }

    @Override
    public void initConfig() {

    }

    @Override
    public void initgetIntent() {

    }

    @Override
    public void initUi() {
        TopView.getTopView(this, R.id.top_view);
        registerEvent();
    }

    @Override
    public void initUi(View view) {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initTitle() {

    }

    private void registerEvent(){
        if(isEvent){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isEvent){
            EventBus.getDefault().unregister(this);
        }
    }

    protected void showMsg(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
