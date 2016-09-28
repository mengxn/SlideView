package me.codego.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SlideView.OnSlideListener {

    private RecyclerView mRecyclerView;
    private SlideView mLastSlideViewWithStatusOn;

    private List<String> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        datas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            datas.add("测试数据--" + i);
        }

        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                SlideView slideView = (SlideView) getLayoutInflater().inflate(R.layout.item_main, null);
                /*SlideView slideView = new SlideView(MainActivity.this);
                slideView.setContentView(view);*/
                slideView.setOnSlideListener(MainActivity.this);
                return new MyViewHolder(slideView);
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                holder.mName.setText(datas.get(position));
                holder.mDelete.setOnClickListener(MainActivity.this);
            }

            @Override
            public int getItemCount() {
                return datas.size();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete:
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mDelete = (TextView) itemView.findViewById(R.id.delete);
        }
    }
}
