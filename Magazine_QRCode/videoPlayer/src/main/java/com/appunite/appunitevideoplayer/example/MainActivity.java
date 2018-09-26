package com.appunite.appunitevideoplayer.example;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appunite.appunitevideoplayer.PlayerActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    interface OnVideoClickListener {
        void onVideoClick(@NonNull String item);
    }


    static class VideoViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final TextView textView;
        @NonNull
        private final OnVideoClickListener listener;
        @NonNull
        private String item;

        public VideoViewHolder(@NonNull View itemView, @NonNull final OnVideoClickListener listener) {
            super(itemView);
            this.listener = listener;
            textView = ButterKnife.findById(itemView, R.id.main_adapter_item_text);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onVideoClick(item);
                }
            });
        }

        public void bind(@NonNull String item) {
            this.item = item;
            textView.setText(item);
        }
    }

    static class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {

        @NonNull
        private final OnVideoClickListener listener;
        private List<String> items = Collections.emptyList();

        public VideoAdapter(@NonNull OnVideoClickListener listener) {
            this.listener = listener;
        }

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_adapter_item, parent, false);
            return new VideoViewHolder(view, listener);
        }

        @Override
        public void onBindViewHolder(VideoViewHolder holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void swapData(@NonNull List<String> items) {
            this.items = items;
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final RecyclerView recyclerView = ButterKnife.findById(this, R.id.main_activity_recycler_view);
        ButterKnife.inject(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        final VideoAdapter adapter = new VideoAdapter(new OnVideoClickListener() {
            @Override
            public void onVideoClick(@NonNull String item) {
                startActivity(PlayerActivity.getVideoPlayerIntent(MainActivity.this, item, item, 0));
            }
        });
        recyclerView.setAdapter(adapter);


        final ArrayList<String> items = new ArrayList<>();
        items.add("https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
        items.add("http://distribution.bbb3d.renderfarming.net/video/mp4/bbb_sunflower_1080p_30fps_normal.mp4");
        items.add("http://distribution.bbb3d.renderfarming.net/video/mp4/bbb_sunflower_1080p_60fps_normal.mp4");
        items.add("http://mirror.bigbuckbunny.de/peach/bigbuckbunny_movies/big_buck_bunny_1080p_surround.avi");
        items.add("https://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_1080p_surround.avi");
        items.add("https://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_1080p_h264.mov");
        items.add("https://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_1080p_stereo.ogg");
        items.add("https://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_1080p_stereo.avi");
        items.add("https://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_720p_surround.avi");
        items.add("https://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_720p_h264.mov");
        items.add("https://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_720p_stereo.ogg");
        items.add("https://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_720p_stereo.avi");
        items.add("https://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_480p_h264.mov");
        items.add("https://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_480p_stereo.ogg");
        items.add("https://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_480p_stereo.avi");
        adapter.swapData(items);
    }

}
