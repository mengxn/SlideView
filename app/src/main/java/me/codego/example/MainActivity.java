package me.codego.example;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private List<String> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);

        datas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            datas.add("测试数据--" + i);
        }

        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layout);

        RecyclerView.Adapter<MyViewHolder> way1Adapter = new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                SlideView slideView = (SlideView) getLayoutInflater().inflate(R.layout.item_main_custom, null);
                return new MyViewHolder(slideView);
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                holder.mName.setText(datas.get(position));
            }

            @Override
            public int getItemCount() {
                return datas.size();
            }
        };

        RecyclerView.Adapter<MyViewHolder> way2Adapter = new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.item_main, null);
                SlideView slideView = new SlideView(MainActivity.this);
                slideView.setContentView(view);
                slideView.setParallax(0.3f);
                return new MyViewHolder(slideView);
            }

            @Override
            public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
                holder.mName.setText(datas.get(position));
                SlideView slideView = (SlideView) holder.itemView;
                slideView.clearOptions();
                slideView.addOption(slideView.newOption("删除", Color.RED, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showToast("delete " + position);
                    }
                }));
                if (position == 1) {
                    // you can add option as you can
                    slideView.addOption(slideView.newOption("哈哈哈哈", Color.BLUE, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showToast("hi " + holder.getAdapterPosition());
                        }
                    }));
                }
            }

            @Override
            public int getItemCount() {
                return datas.size();
            }
        };

        // way 1
//        mRecyclerView.setAdapter(way1Adapter);
        // way 2
        mRecyclerView.setAdapter(way2Adapter);
    }

    private void showToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mName;

        public MyViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
        }
    }
}
