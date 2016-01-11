package com.crowdfire.wardrobe;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by rushabh on 29/12/15.
 */
public class ClothsAdapter extends FragmentPagerAdapter {

    ArrayList<Integer> clotheList;


    public ClothsAdapter(FragmentManager fm,ArrayList<Integer> list)  {
        super(fm);
        this.clotheList=list;

    }

    @Override
    public Fragment getItem(int position) {
        ShowSingleClothFragment fragment=new ShowSingleClothFragment();
        fragment.clothId=clotheList.get(position);

        return fragment;
    }


    @Override
    public int getCount() {
        return clotheList.size();
    }
}
