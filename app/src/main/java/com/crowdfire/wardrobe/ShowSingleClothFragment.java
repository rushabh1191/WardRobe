package com.crowdfire.wardrobe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.logging.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowSingleClothFragment extends Fragment {


    @Bind(R.id.iv_cloth_image)
    ImageView ivImage;

    ClothInformation clothInformation;

    int clothId;

    String CLOTH_ID="cloth_id";
    public ShowSingleClothFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_cloth, container, false);

        ButterKnife.bind(this, view);

        DatabaseManager databaseManager=DatabaseManager.getDatabaseInstance(getActivity());


        if(savedInstanceState!=null){
            clothId=savedInstanceState.getInt(CLOTH_ID);
        }

        Log.d("beta","Cloth "+clothId);
        clothInformation=databaseManager.getCloth(clothId);
        DatabaseManager.releaseDatabase();

        ImageLoader imageLoader=ImageLoader.getInstance();
        imageLoader.displayImage("file:/"+clothInformation.imageUrl,ivImage);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CLOTH_ID,clothId);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
