package com.example.wordbook;
//处理后台提交的列表数据,适配器
//制作关于RecyclerView的适配器Adapter
//创建一个MyAdapter类继承RecyclerView.Adapter

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


//继承ListAdapter并改写构造方法
public class MyAdapter extends ListAdapter<Word,MyAdapter.MyViewHolder> { //<>中表示使用的ViewHolder为自己写的静态内部类MyViewHolder

    private boolean useCardView;//是否使用卡片布局视图
    private WordViewModel wordViewModel;


    public MyAdapter(boolean useCardView, WordViewModel wordViewModel) {
        super(new DiffUtil.ItemCallback<Word>() {//列表数据的差异化处理，是后台异步进行的

            @Override
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) { //比较列表元素是否相同
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {//比较内容是否相同
                return (oldItem.getWord().equals(newItem.getWord())
                && oldItem.getChineseMeaning().equals(newItem.getChineseMeaning())
                && oldItem.isChineseInvisible() == newItem.isChineseInvisible());
            }
        });
        this.useCardView = useCardView;
        this.wordViewModel = wordViewModel;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
         * 当创建ViewHolder时
         * 从Layout文件中
         * 加载View
         * */
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if(useCardView){//实现加载不同视图
            itemView = layoutInflater.inflate(R.layout.cell_card_2,parent,false);//卡片模式界面
        }else {
            itemView = layoutInflater.inflate(R.layout.cell_normal_2,parent,false);//普通模式界面
        }
        final MyViewHolder holder = new MyViewHolder(itemView);

        //通过点击单词条，用intent并传入一个网址可以跳到有道词典搜索
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https:///m.youdao.com/dict?le=eng&q=" + holder.textViewEnglish.getText());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);
            }
        });


        //监听开关，若发生改变显示/隐藏中文释义，同时修改数据库中ChineseVisible字段的值。
        holder.aSwitchChineseInvisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Word word = (Word)holder.itemView.getTag(R.id.word_for_view_holder);//广义object要强制转换，转换成word类型
                if(isChecked){
                    holder.textViewChinese.setVisibility(View.GONE);
                    word.setChineseInvisible(true);
                    wordViewModel.updateWords(word);

                }else {
                    holder.textViewChinese.setVisibility(View.VISIBLE);
                    word.setChineseInvisible(false);
                    wordViewModel.updateWords(word);
                }
            }
        });
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        //绑定逻辑上的关联
        final Word word = getItem(position);
        //itemView的setTag方法可以获取对象并在其他地方通过getTag获取对应对象
        holder.itemView.setTag(R.id.word_for_view_holder,word);//用资源封装保证不冲突
        holder.textViewNumber.setText(String.valueOf(position + 1));
        holder.textViewEnglish.setText(word.getWord());
        holder.textViewChinese.setText(word.getChineseMeaning());
        //通过查询数据库ChineseInvisible字段，初始化设置是否显示中文释义
        if(word.isChineseInvisible()){
            holder.textViewChinese.setVisibility(View.GONE);//中文不可视
            holder.aSwitchChineseInvisible.setChecked(true);//设置开关为开
        }else {
            holder.textViewChinese.setVisibility(View.VISIBLE);
            holder.aSwitchChineseInvisible.setChecked(false);
        }

    }


    //防止因数据过多数据条目的项目不稳定
    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.textViewNumber.setText(String.valueOf(holder.getAdapterPosition() + 1));
    }



    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textViewNumber, textViewEnglish, textViewChinese;
        Switch aSwitchChineseInvisible;
        public MyViewHolder(@NonNull View itemView) {//自带View类型的itemView
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);
            textViewChinese = itemView.findViewById(R.id.textViewChinese);
            aSwitchChineseInvisible = itemView.findViewById(R.id.switchChineseInvisible);
        }
    }

}
