package com.example.wordbook;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;



public class AddFragment extends Fragment {
    private Button buttonSubmit;
    private EditText editTextEnglish, editTextChinese;
    private WordViewModel wordViewModel;


    public AddFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FragmentActivity activity = requireActivity();
        wordViewModel = ViewModelProviders.of(activity).get(WordViewModel.class);
        buttonSubmit = activity.findViewById(R.id.buttonSubmit);
        editTextEnglish = activity.findViewById(R.id.editTextEnglish);
        editTextChinese = activity.findViewById(R.id.editTextChinese);
        buttonSubmit.setEnabled(false);//有效信息不足时，按键设置为灰色状态，刚开始时，为不启用状态
        //定义了添加单词界面的功能
        //进入界面后，将输入框聚焦在英语输入框上，并弹出键盘
        editTextEnglish.requestFocus();//光标获取焦点
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextEnglish,0);//强制显示键盘，两个参数为（1. 关联的View；2. flag）

        //制作edit监听器
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
           //若输入框两个都不为空，设置添加按钮可用
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String english = editTextEnglish.getText().toString().trim();
                String chinese = editTextChinese.getText().toString().trim();
                buttonSubmit.setEnabled(!english.isEmpty() && !chinese.isEmpty());//只要两者都不是空
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        //添加到edit中
        editTextEnglish.addTextChangedListener(textWatcher);
        editTextChinese.addTextChangedListener(textWatcher);


        //点击添加按钮后，通过WordViewModel将数据写入数据库，并通过导航回到主界面
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String english = editTextEnglish.getText().toString().trim();
                String chinese = editTextChinese.getText().toString().trim();
                Word word = new Word(english,chinese);
                wordViewModel.insertWords(word);//数据添加
                NavController navController = Navigation.findNavController(v);
                //button触发方法中添加键盘回缩方法
                navController.navigateUp();
                //返回界面时隐藏键盘的操作
                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);//两个参数
            }
        });




    }
}
