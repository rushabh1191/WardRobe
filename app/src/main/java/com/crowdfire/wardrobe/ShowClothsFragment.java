package com.crowdfire.wardrobe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ShowClothsFragment extends Fragment implements OnAlertDialogButtonClickListener
        ,ImageChooserDialog.ImageURICapturedListener,ViewPager.OnPageChangeListener {


    @Bind(R.id.vp_pant_swiper)
    ViewPager pantSwiper;

    @Bind(R.id.vp_shirt_swiper)
    ViewPager shirtSwiper;

    @Bind(R.id.iv_favorite)
    ImageView ivFav;


    @Bind(R.id.fab_shirt)
    FloatingActionButton fabShirt;

    @Bind(R.id.fab_pant)
    FloatingActionButton fabPant;

    @Bind(R.id.iv_shuffle)
    ImageView ivShuffle;

    public static int SHOW_ALL_CLOTH=1;
    public static int SHOW_FAV_CLOTH=2;
    public static int SHOW_HISTORY_CLOTH=3;

    public int type=SHOW_ALL_CLOTH;

    ArrayList<Integer> pants;
    ArrayList<Integer> shirts;
    DatabaseManager databaseManager;

    boolean isCurrentFav;

    String CURRENT_SHIRT = "shirt_info";
    String CURRENT_PANT = "pant_info";
    String SHIRT_LIST = "shirt_list";
    String PANT_LIST = "pant_list";

    ClothsAdapter pantSwiperAdapter;
    ClothsAdapter shirtSwiperAdapter;


    public ShowClothsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @OnClick(R.id.iv_shuffle)
    void shuffleChoices() {
        if (pants.size() > 0 & shirts.size() > 0) {
            int randomPant = MainActivity.randInt(0, pants.size() - 1);
            int randomShirt = MainActivity.randInt(0, shirts.size() - 1);
            pantSwiper.setCurrentItem(randomPant);
            shirtSwiper.setCurrentItem(randomShirt);
        } else {
            Toast.makeText(getActivity(), "Not enough data to shuffle", Toast.LENGTH_LONG).show();
        }


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.content_main, container, false);

        ButterKnife.bind(this,view);

        databaseManager = DatabaseManager.getDatabaseInstance(getActivity());

        int currentShirt = 0;
        int currentPant = 0;

        if(type!= SHOW_ALL_CLOTH){
            fabPant.setVisibility(View.GONE);
            fabShirt.setVisibility(View.GONE);
            ivFav.setVisibility(View.GONE);
            ivShuffle.setVisibility(View.GONE);

        }
        if(type==SHOW_ALL_CLOTH) {
            pants = databaseManager.getAllPantIds();
            shirts = databaseManager.getAllShirtIds();
        }
        else {
            ArrayList<ArrayList<Integer>> arrayLists=databaseManager.getFavCombos();
            pants=arrayLists.get(0);
            shirts=arrayLists.get(1);
        }
        if (savedInstanceState != null) {

            pants = savedInstanceState.getIntegerArrayList(PANT_LIST);
            shirts = savedInstanceState.getIntegerArrayList(SHIRT_LIST);

            currentShirt = savedInstanceState.getInt(CURRENT_SHIRT);
            currentPant = savedInstanceState.getInt(CURRENT_PANT);
        }

        pantSwiperAdapter = new

                ClothsAdapter(getChildFragmentManager(), pants

        );
        shirtSwiperAdapter = new

                ClothsAdapter(getChildFragmentManager(), shirts

        );


        pantSwiper.setAdapter(pantSwiperAdapter);
        shirtSwiper.setAdapter(shirtSwiperAdapter);

        if(type==SHOW_ALL_CLOTH) {
            pantSwiper.addOnPageChangeListener(this);
            shirtSwiper.addOnPageChangeListener(this);
        }
        else {

            addSyncableListeners();

        }

        showCloths(currentShirt, currentPant);

        showFav(currentShirt, currentPant);


        if (pants.size() == 0 && shirts.size() == 0) {
            TextView textView = new TextView(getActivity());

            textView.setPadding(20, 20, 20, 20);

            textView.setText("Let's start adding Pants and Shirts");
            PopupWindowAlert popupWindowAlert = new PopupWindowAlert(getActivity(),
                    "No Cloths", textView, "Add Pant", "Add Shirt", "", this, 1);
        }

        return view;
    }

    void addSyncableListeners(){



        addListner(pantSwiper,shirtSwiper);
        addListner(shirtSwiper,pantSwiper);

    }


    void addListner(final ViewPager viewPagerAttacher,final ViewPager toBeSyncViewPager){

        viewPagerAttacher.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int mScrollState = ViewPager.SCROLL_STATE_IDLE;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                    return;
                }
                toBeSyncViewPager.scrollTo(viewPagerAttacher.getScrollX(), toBeSyncViewPager.getScrollY());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                mScrollState = state;
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    toBeSyncViewPager.setCurrentItem(viewPagerAttacher.getCurrentItem(), false);
                }

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(CURRENT_PANT, shirtSwiper.getCurrentItem());
        outState.putInt(CURRENT_SHIRT, pantSwiper.getCurrentItem());
        outState.putIntegerArrayList(SHIRT_LIST, shirts);
        outState.putIntegerArrayList(PANT_LIST, pants);
    }


    private void showCloths(int currentShirt, int currentPant) {

        if (shirtSwiperAdapter.getCount() > 0) {
            shirtSwiper.setCurrentItem(currentShirt);
        }
        if (pantSwiperAdapter.getCount() > 0) {
            pantSwiper.setCurrentItem(currentPant);
        }
        showFav(currentShirt, currentPant);
    }

    void removeFav(int currentPant, int currentShirt) {
        databaseManager.removeFav(pants.get(currentPant), shirts.get(currentShirt));
    }

    void setFav(int currentShirtPosition, int currentPantPosition) {
        databaseManager.addFav(shirts.get(currentShirtPosition), pants.get(currentPantPosition));
    }
    @OnClick(R.id.iv_favorite)
    void toggleFav() {

        if (pants.size() > 0 & shirts.size() > 0) {
            int currentShirt = shirtSwiper.getCurrentItem();
            int currentPant = pantSwiper.getCurrentItem();
            if (!isCurrentFav) {
                setFav(currentShirt, currentPant);
            } else {
                removeFav(currentPant, currentShirt);
            }
            showFav(currentShirt, currentPant);
        } else {
            Toast.makeText(getActivity(), "Please add a shirt and a pant to make it favorite", Toast.LENGTH_LONG).show();
        }
    }

    void showFav(int currentShirtPostion, int currentPantPosition) {

        if (pantSwiperAdapter.getCount() > 0 & shirtSwiperAdapter.getCount() > 0) {
            isCurrentFav = databaseManager.isFav(pants.get(currentPantPosition), shirts.get(currentShirtPostion));
            if (isCurrentFav) {
                ivFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_filled));
            } else {
                ivFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_outline));
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {

            if (requestCode == ClothInformation.IS_PANT || requestCode % 2 == 1) {
                if (requestCode != ClothInformation.IS_PANT) {

                    Uri uri = data.getData();

                    ClothInformation clothInformation = new ClothInformation();


                    clothInformation.imageUrl = getFilePath(uri);
                    clothInformation.type = ClothInformation.IS_PANT;
                    clothInformation.id = (int) databaseManager.addCloth(clothInformation);
                    pants.add(clothInformation.id);
                    pantSwiperAdapter.notifyDataSetChanged();
                }
                pantSwiper.setCurrentItem(pantSwiperAdapter.getCount() - 1);
            } else {
                if (requestCode % 2 == 0) {
                    Uri uri = data.getData();

                    ClothInformation clothInformation = new ClothInformation();


                    clothInformation.imageUrl = getFilePath(uri);
                    clothInformation.type = ClothInformation.IS_SHIRT;
                    clothInformation.id = (int) databaseManager.addCloth(clothInformation);
                    shirts.add(clothInformation.id);
                    shirtSwiperAdapter.notifyDataSetChanged();
                }
                shirtSwiper.setCurrentItem(shirtSwiperAdapter.getCount() - 1);
            }
        }
    }


    String getFilePath(Uri uri) {

        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().getContentResolver().query(
                uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
        DatabaseManager.releaseDatabase();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @OnClick({R.id.fab_shirt, R.id.fab_pant})
    void fabClicked(View view) {

        int type = view.getId() == R.id.fab_shirt ? ClothInformation.IS_SHIRT : ClothInformation.IS_PANT;
        new ImageChooserDialog(this, this, type);
    }

    @Override
    public void onButtonClick(AlertDialog dialog, View view, int buttonId, int popupId) {
        if (buttonId == DialogInterface.BUTTON_POSITIVE) {
            fabClicked(fabPant);
        }
        else {
            fabClicked(fabShirt);
        }

        dialog.dismiss();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int pantPosition = pantSwiper.getCurrentItem();
        int shirtPosition = shirtSwiper.getCurrentItem();
        showFav(shirtPosition, pantPosition);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void uriCaptured(Uri uri, String file, int type) {


        ClothInformation clothInformation = new ClothInformation();
        clothInformation.imageUrl = file;
        clothInformation.type = type;
        clothInformation.id = (int) databaseManager.addCloth(clothInformation);

        if (type == ClothInformation.IS_PANT) {
            pants.add(clothInformation.id);
            pantSwiperAdapter.notifyDataSetChanged();
//            pantSwiper.setCurrentItem(pants.get(clothInformation.id));
        } else {
            shirts.add(clothInformation.id);
            shirtSwiperAdapter.notifyDataSetChanged();
//            shirtSwiper.setCurrentItem(shirts.get(clothInformation.id));
        }


    }
}
