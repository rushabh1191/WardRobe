package com.crowdfire.wardrobe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, ImageChooserDialog.ImageURICapturedListener, OnAlertDialogButtonClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        ButterKnife.bind(this);

        databaseManager = DatabaseManager.getDatabaseInstance(this);


        File cacheDir = StorageUtils.getCacheDirectory(this);

        pantSwiper.addOnPageChangeListener(this);
        shirtSwiper.addOnPageChangeListener(this);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).
                memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024).
                diskCache(new UnlimitedDiscCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100).build();

        ImageLoader.getInstance().init(config);


        int currentShirt = 0;
        int currentPant = 0;
        pants = databaseManager.getAllPantIds();
        shirts = databaseManager.getAllShirtIds();
        if (savedInstanceState != null) {

            pants = savedInstanceState.getIntegerArrayList(PANT_LIST);
            shirts = savedInstanceState.getIntegerArrayList(SHIRT_LIST);

            currentShirt = savedInstanceState.getInt(CURRENT_SHIRT);
            currentPant = savedInstanceState.getInt(CURRENT_PANT);

        }

        pantSwiperAdapter = new

                ClothsAdapter(getSupportFragmentManager(), pants

        );
        shirtSwiperAdapter = new

                ClothsAdapter(getSupportFragmentManager(), shirts

        );


        pantSwiper.setAdapter(pantSwiperAdapter);
        shirtSwiper.setAdapter(shirtSwiperAdapter);


        showCloths(currentShirt, currentPant);

        showFav(currentShirt, currentPant);


        if (pants.size() == 0 && shirts.size() == 0) {
            TextView textView = new TextView(this);

            textView.setPadding(20, 20, 20, 20);

            textView.setText("Let's start adding Pants and Shirts");
            PopupWindowAlert popupWindowAlert = new PopupWindowAlert(this,
                    "No Cloths", textView, "Add Pant", "Add Shirt", "", this, 1);
        }


    }

    @OnClick(R.id.iv_shuffle)
    void shuffleChoices() {
        if (pants.size() > 0 & shirts.size() > 0) {
            int randomPant = randInt(0, pants.size() - 1);
            int randomShirt = randInt(0, shirts.size() - 1);
            pantSwiper.setCurrentItem(randomPant);
            shirtSwiper.setCurrentItem(randomShirt);
        } else {
            Toast.makeText(this, "Not enough data to shuffle", Toast.LENGTH_LONG).show();
        }


    }

    @OnClick({R.id.fab_shirt, R.id.fab_pant})
    void fabClicked(View view) {

        int type = view.getId() == R.id.fab_shirt ? ClothInformation.IS_SHIRT : ClothInformation.IS_PANT;
        new ImageChooserDialog(this, this, type);
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(CURRENT_PANT, shirtSwiper.getCurrentItem());
        outState.putInt(CURRENT_SHIRT, pantSwiper.getCurrentItem());
        outState.putIntegerArrayList(SHIRT_LIST, shirts);
        outState.putIntegerArrayList(PANT_LIST, pants);
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
            Toast.makeText(this, "Please add a shirt and a pant to make it favorite", Toast.LENGTH_LONG).show();
        }
    }

    void removeFav(int currentPant, int currentShirt) {
        databaseManager.removeFav(pants.get(currentPant), shirts.get(currentShirt));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseManager.releaseDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    void setFav(int currentShirtPosition, int currentPantPosition) {
        databaseManager.addFav(shirts.get(currentShirtPosition), pants.get(currentPantPosition));
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
    public void onPageScrollStateChanged(int state) {

    }

    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

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

        Cursor cursor = getContentResolver().query(
                uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
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
}
