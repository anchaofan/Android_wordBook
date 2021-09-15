package com.example.wordbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //利用NavigationUI进行界面跳转
        navController = Navigation.findNavController((findViewById(R.id.fragment)));
        NavigationUI.setupActionBarWithNavController(this, navController);//返回图标，但不生效
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navController.navigateUp();
    }


    //处理因点击返回自设按键键盘不收回的设置
    @Override
    public boolean onSupportNavigateUp() {
        //返回界面时隐藏键盘的操作
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.fragment).getWindowToken(), 0);//两个参数
        navController.navigateUp();
        return super.onSupportNavigateUp();//是返回按钮生效，不然没有反应
    }


}
