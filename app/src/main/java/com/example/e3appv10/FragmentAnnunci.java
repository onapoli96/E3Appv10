package com.example.e3appv10;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.e3appv10.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.util.zip.Inflater;

public class FragmentAnnunci extends Fragment {

    private View view;
    private PhotoView imageView;
    private PhotoViewAttacher mAttacher;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_annunci_layout, container, false);
        // Per settare dinamicamente un immagine
        imageView =  (PhotoView) view.findViewById(R.id.mappa);
        imageView.setImageResource(R.drawable.ic_pontedicomando);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_pontedicomando);
        imageView.setImageDrawable(drawable);

        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        mAttacher = new PhotoViewAttacher(imageView);

        return view;
    }
}
