package com.jcbsh.mygreyscalecamera;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by JCBSH on 9/03/2016.
 */
public class ListImageFragment extends Fragment{

    private static final String TAG = ListImageFragment.class.getSimpleName();
    private ListView listView;

    public static Fragment getInstance() {
        Fragment fragment = new ListImageFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_video, container, false);
        listView = (ListView) v.findViewById(R.id.listView);

        File[] videoFiles = findVideos();
        listView.setAdapter(new VideoFilesAdapter(getActivity(), videoFiles));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = (File) listView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                intent.putExtra(ImageActivity.EXTRA_VIDEO_FILE_PATH, file.getPath());
                startActivity(intent);
            }
        });
        return v;
    }

    private File[] findVideos() {
        File[] files = getActivity().getExternalFilesDir(null).listFiles();
        for (File video: files) {
            Log.d(TAG, "files: " +  video.getName());
        }
        return files;
    }

    private class VideoFilesAdapter extends ArrayAdapter<File> {
        public VideoFilesAdapter(Context context, File[] videos) {
            super(context, android.R.layout.simple_expandable_list_item_1, videos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView textView = (TextView) v.findViewById(android.R.id.text1);
            textView.setText(getItem(position).getName());
            return v;
        }
    }
}
