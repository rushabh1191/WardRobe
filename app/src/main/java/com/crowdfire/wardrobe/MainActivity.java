package com.crowdfire.wardrobe;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

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


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @Bind(R.id.vp_pant_swiper)
    ViewPager pantSwiper;

    @Bind(R.id.vp_shirt_swiper)
    ViewPager shirtSwiper;

    @Bind(R.id.iv_favorite)
    ImageView ivFav;


    ArrayList<Integer> pants;
    ArrayList<Integer> shirts;
    DatabaseManager databaseManager;

    boolean isCurrentFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        ButterKnife.bind(this);

        ClothInformation shirtInfo=new ClothInformation();
        shirtInfo.imageUrl="http://8020.photos.jpgmag.com/3799736_236369_4042d98607_m.jpg";

        shirtInfo.type=ClothInformation.IS_SHIRT;
        ClothInformation pantInfo=new ClothInformation();
        pantInfo.imageUrl="http://8020.photos.jpgmag.com/3799725_322556_e00db2183b_m.jpg";
        pantInfo.type=ClothInformation.IS_PANT;

        databaseManager=DatabaseManager.getDatabaseInstance(this);
        databaseManager.addCloth(shirtInfo);
        databaseManager.addCloth(pantInfo);
         pants=databaseManager.getAllPantIds();
        shirts=databaseManager.getAllShirtIds();


        File cacheDir = StorageUtils.getCacheDirectory(this);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).
                memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024).
                diskCache(new UnlimitedDiscCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100).build();

        ImageLoader.getInstance().init(config);

        ClothsAdapter pantsSwiperAdapter=new ClothsAdapter(getSupportFragmentManager(),pants);
        ClothsAdapter shirtSwiperAdapter=new ClothsAdapter(getSupportFragmentManager(),shirts);

        pantSwiper.setAdapter(pantsSwiperAdapter);
        shirtSwiper.setAdapter(shirtSwiperAdapter);


        pantSwiper.addOnPageChangeListener(this);
        shirtSwiper.addOnPageChangeListener(this);

        showFav(0,0);


    }

    @OnClick(R.id.iv_favorite)
    void toggleFav(){
        int currentShirt=shirtSwiper.getCurrentItem();
        int currentPant=pantSwiper.getCurrentItem();
        if(!isCurrentFav) {
            setFav(currentShirt,currentPant);
        }
        else {
            removeFav(currentPant, currentShirt);
        }
        showFav(currentShirt,currentPant);
    }

    void removeFav(int currentPant,int currentShirt){
        databaseManager.removeFav(pants.get(currentPant),shirts.get(currentShirt));
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

        int pantPosition=pantSwiper.getCurrentItem();
        int shirtPosition=shirtSwiper.getCurrentItem();
        showFav(shirtPosition,pantPosition);

    }
    void setFav(int currentShirtPosition,int currentPantPosition){
        databaseManager.addFav(shirts.get(currentShirtPosition),pants.get(currentPantPosition));
    }

    void showFav(int currentShirtPostion,int currentPantPosition){

        isCurrentFav=databaseManager.isFav(pants.get(currentPantPosition),shirts.get(currentShirtPostion));
        if(isCurrentFav){
            ivFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_filled));
        }
        else {
            ivFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_outline));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
