package com.example.administrator.pagerviewtest.fragments;

import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.pagerviewtest.R;
import com.example.administrator.pagerviewtest.RecordingAdapter;

import static com.example.administrator.pagerviewtest.MainActivity.FOLDER_PATH;

public class FileViewerFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private RecordingAdapter mRecordingAdapter;

    private int position;

    public static FileViewerFragment newInstance(int position) {
        FileViewerFragment fragment = new FileViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    FileObserver observer = new FileObserver(FOLDER_PATH) {
        @Override
        public void onEvent(int event, String path) {
            if (event == FileObserver.DELETE) {

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);
        observer.startWatching();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_viewer, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // RecyclerView性能优化，避免重复调用requestLayout();
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());   // 添加默认动画效果

        mRecordingAdapter = new RecordingAdapter(getActivity(), mRecyclerView);
        mRecyclerView.setAdapter(mRecordingAdapter);

        return view;
    }
}
