package io.kirikcoders.bitcse.Tools;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.kirikcoders.bitcse.R;

/**
 * Created by Kartik on 24-Jul-18.
 */
public class ToolsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tools,container,false);
        return rootView;
    }
}