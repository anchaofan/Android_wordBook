package com.example.wordbook;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class WordsFragment extends Fragment {

    private WordViewModel wordViewModel;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter1, myAdapter2;
    private FloatingActionButton floatingActionButton;//定义变量
    private LiveData<List<Word>> filteredWords;//过滤后的词汇
    private static final String VIEW_TYPE_SHP = "view_type_shp";
    private static final String IS_USING_CARD_VIEW = "is_using_card_view";
    private List<Word> allWords;
    private boolean undoAction;
    private DividerItemDecoration dividerItemDecoration;


    public WordsFragment() {
        setHasOptionsMenu(true);
        // 必需的空公共构造函数，由于Fragement中默认不显示菜单，所以需要在构造方法中设置显示拥有菜单为真
    }


    //制作清空数据功能和切换布局功能
//在WordsFragment中设置了菜单功能，点击清空数据会弹出alertDialog对话框，若选择确认，则调用wordViewModel中的deleteAll（）方法删除所以数据；
// 点击切换视图时，会先在SharePreferences中获得现在时什么视图的值，然后调用recyclerView的方法设置另一个视图。
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //菜单栏的可选项点击事件的实现（本次为清空数据和切换视图）
        //多个菜单分类处理
        switch (item.getItemId()) {
            case R.id.clearData: //选中清空数据时弹出确认对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wordViewModel.deleteAllWords();//确定时才从ViewModel中清空数据
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          //取消时不做处理即可
                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.switchViewType: //选中切换视图时
                //创建用户偏好设置SharedPreferences保存切换的视图（不设置会在切换视图做添加数据操作的时候回归普通视图）
                SharedPreferences shp = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
                boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW, false);
                SharedPreferences.Editor editor = shp.edit();
                if (viewType) {
                    //当前使用的时卡片布局
                    recyclerView.setAdapter(myAdapter1);//改变为普通布局
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    editor.putBoolean(IS_USING_CARD_VIEW, false);//将用户偏好设置改变为false
                } else {
                    recyclerView.setAdapter(myAdapter2);
                    recyclerView.removeItemDecoration(dividerItemDecoration);
                    editor.putBoolean(IS_USING_CARD_VIEW, true);
                }
                editor.apply();//用户偏好设置存储
        }
        return super.onOptionsItemSelected(item);

    }

    //搜索功能


    //制作fragement中的搜索栏的监听器
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_enu, menu);
        //设置搜索栏点击时，系统名不被隐藏的方法：
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(750);//设置搜索栏宽度以防系统名被隐藏
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {//监听里面内容改变的监听器
            @Override
            public boolean onQueryTextSubmit(String query) {//确定提交相关
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {//内容改变
                //制作模糊匹配查询
                String patten = newText.trim();
                //避免两次观察的碰撞，先移除之前的观察
                filteredWords.removeObservers(requireActivity()); //不写这句会报错
                //根据筛选条件获取新的模糊查询添加观察
                filteredWords = wordViewModel.findWordsWithPattern(patten);
                //因LiveData造成的添加数据后的搜索观察（observer）失误
                /*
                 * 对于filteredWords.observe观察
                 * fragement在观察过程中依赖requireActivity
                 * 由于Activity不曾被摧毁，
                 * 所以将Activity作为观察的owner
                 * 会出现界面上的一个重叠
                 * 需要解决将getActivity()修改采用getViewLifecycleOwner()
                 * */
                filteredWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {//onChanged()内部方法
                        int temp = myAdapter1.getItemCount();
                        allWords = words;
                        if (temp != words.size()) {
                            myAdapter1.submitList(words);
                            myAdapter2.submitList(words);
                        }
                    }
                });
                return true; //如果事件处理结束，则返回true
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_words, container, false);
    }


//在填充数据后通过用户偏好设置sharedPreferences保持原本界面布局（包括填充数据提交和重新进入界面）


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wordViewModel = new ViewModelProvider(this, new SavedStateViewModelFactory(requireActivity().getApplication(),this)).get(WordViewModel.class);
        recyclerView = requireActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        myAdapter1 = new MyAdapter(false, wordViewModel);//不使用卡片
        myAdapter2 = new MyAdapter(true, wordViewModel);//使用卡片

        //修正视图序号上的改变（变更为数据层面的序号改变）
        //设置recyclerView的回调
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            //复写回调中的函数onAnimationFinished
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //判空
                if (linearLayoutManager != null) {
                    int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                    //做序号的循环
                    for (int i = firstPosition; i <= lastPosition; i++) {
                        MyAdapter.MyViewHolder holder = (MyAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        //判空
                        if (holder != null) {
                            holder.textViewNumber.setText(String.valueOf(i + 1));
                        }
                    }
                }

            }
        });//当添加数据动画结束，设置回调


        //读取用户偏好设置
        SharedPreferences shp = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
        boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW, false);
        dividerItemDecoration = new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL);
        if (viewType) {//用户偏好卡片则设为卡片布局
            recyclerView.setAdapter(myAdapter2);
        } else {
            recyclerView.setAdapter(myAdapter1);
            recyclerView.addItemDecoration(dividerItemDecoration);//增加下划线
        }
        filteredWords = wordViewModel.getAllWordsLive();//一开始不过滤，显示所有内容


        //制作添加数据时的数据信息界面反馈（添加数据自动跳转到新添加的条目位置）
        filteredWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int temp = myAdapter1.getItemCount();
                allWords = words;
                if (temp != words.size()) {
                    if (temp < words.size() && !undoAction) {
                        recyclerView.smoothScrollBy(0, -200);////设置滑动界面下移200个像素点
                    }
                    undoAction = false;
                    myAdapter1.submitList(words);
                    myAdapter2.submitList(words);
                }
            }
        });


        //移动单词和滑动删除单词功能
        //处理长按拖动事件，发生拖动时，替换两个Word的id，livedate检测到数据发生改变，就重新绘画界面
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END) {//滑动的两个参数分别为拖动方向和滑动方向，可以选中跳到定义查看
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                //先获取位置对象的两个参数ViewHolder，target
                Word wordFrom = allWords.get(viewHolder.getAdapterPosition());
                Word wordTo = allWords.get(target.getAdapterPosition());
                /*
                 * ID交换三部曲
                 * */
                int idTemp = wordFrom.getId();
                wordFrom.setId(wordTo.getId());
                wordFrom.setId(idTemp);
                wordViewModel.updateWords(wordFrom,wordTo);
                myAdapter1.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                myAdapter2.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                return false;
            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final Word wordToDelete = allWords.get(viewHolder.getAdapterPosition());//用allWords代替LiveData避免警告，获取删除节点
                wordViewModel.deleteWords(wordToDelete); //完成删除操作
                //滑动删除后会弹出一个snackbar，点击即可撤销删除
                Snackbar.make(requireActivity().findViewById(R.id.wordFragementView), "删除了一个词汇", Snackbar.LENGTH_SHORT)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                undoAction = true;
                                wordViewModel.insertWords(wordToDelete);//将上面的数据重新插入回数据库中
                            }
                        }).show();
            }
            //制作滑动下的删除图标
            Drawable icon = ContextCompat.getDrawable(requireActivity(),R.drawable.ic_delete_forever_black_24dp);
            Drawable background = new ColorDrawable(Color.LTGRAY);



            //用onChildDraw画滑动后面的灰色阴影和垃圾桶图标
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int iconMargin = ((itemView.getHeight()) - icon.getIntrinsicHeight())/2;
                int iconLeft, iconRight, iconTop, iconBottom;
                int backTop, backBottom, backLeft, backRight;
                backTop = itemView.getTop();
                backBottom = itemView.getBottom();
                iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) /2;
                iconBottom = iconTop + icon.getIntrinsicHeight();
                if(dX > 0){
                    backLeft = itemView.getLeft();
                    backRight = itemView.getLeft() + (int)dX;
                    background.setBounds(backLeft,backTop,backRight,backBottom);
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = iconLeft + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft,iconTop,iconRight,iconBottom);
                }else if (dX < 0){
                    backRight = itemView.getRight();
                    backLeft = itemView.getRight() + (int)dX;
                    background.setBounds(backLeft,backTop,backRight,backBottom);
                    iconRight = itemView.getRight() + iconMargin;
                    iconLeft = iconRight + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft,iconTop,iconRight,iconBottom);
                }else {
                    background.setBounds(0,0,0,0);
                    icon.setBounds(0,0,0,0);
                }
                background.draw(c);
                icon.draw(c);
                //滑动删除完成
            }
        }).attachToRecyclerView(recyclerView);//附加recyclerView生效



        //点击悬浮按钮跳到添加单词界面
        floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_wordsFragment_to_addFragment);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}
