package com.crowdfire.wardrobe;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by rushabh on 29/12/15.
 */
public class ClothsAdapter extends FragmentStatePagerAdapter {

    ArrayList<Integer> clotheList;


    public ClothsAdapter(FragmentManager fm,ArrayList<Integer> list)  {
        super(fm);
        this.clotheList=list;

    }

   /* @Override
    public int getItemPosition(Object object) {

        ShowSingleClothFragment fragment = (ShowSingleClothFragment)object;

        int position = clotheList.indexOf(fragment.clothId);
        if (position>0){
            return position;
        }
        else {
            return  POSITION_NONE;
        }
    }*/

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
