package com.crowdfire.wardrobe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by rushabh on 29/12/15.
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static int instanceCounter;
    private static DatabaseManager databaseInstance;
    private static SQLiteDatabase mDB;

    static final String DATABASE_NAME="com.crowdfire";
    static final int VERSION=1;
    static final String TABLE_CLOTH="table_cloth";
    static final String C_TYPE="cloth_type";
    static final String C_URL="c_url";
    static final String KEY_ID="_id";

    static final String TABLE_FAV_COMBO="table_fav_combo";
    static final String C_SHIRT_ID="shirt_id";
    static final String C_PANT_ID="pant_id";

    static  final String TABLE_HISTORY="table_history";

    static final  String C_DATE="date_saved";


    public DatabaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public synchronized static DatabaseManager getDatabaseInstance(Context context) {


        if (databaseInstance == null) {
            databaseInstance = new DatabaseManager(context);
        }
        ++instanceCounter;

        return databaseInstance;
    }

    public long addCloth(ClothInformation cloth){

        ContentValues contentValues=new ContentValues();
        contentValues.put(C_URL,cloth.imageUrl);
        contentValues.put(C_TYPE, cloth.type);
        return mDB.insert(TABLE_CLOTH,null,contentValues);
    }

    public void addFav(ClothInformation shirtInfo,ClothInformation pantInformation){
        addFav(shirtInfo.id, pantInformation.id);
    }

    public void addHistory(int shirtId,int pantId){
        long millis = System.currentTimeMillis();

        ContentValues contentValues=new ContentValues();
        contentValues.put(C_PANT_ID,pantId);
        contentValues.put(C_SHIRT_ID,shirtId);
        contentValues.put(C_DATE,millis);

        Log.d("beta","asd "+millis);

        mDB.insert(TABLE_HISTORY, null, contentValues);
    }

    public void addFav(int shirtId,int pantId){
        long millis = System.currentTimeMillis();

        ContentValues contentValues=new ContentValues();
        contentValues.put(C_PANT_ID,pantId);
        contentValues.put(C_SHIRT_ID,shirtId);


        mDB.insert(TABLE_FAV_COMBO, null, contentValues);
    }


    public ArrayList<Integer> getAllPantIds(){

        return  getClothIds(ClothInformation.IS_PANT);
    }

    public ArrayList<Integer> getAllShirtIds(){

        return  getClothIds(ClothInformation.IS_SHIRT);
    }


    public ArrayList<ArrayList<Integer>> getCombos(int type){

        ArrayList<Integer> pantInformation=new ArrayList<>();
        ArrayList<Integer> shirtInformation=new ArrayList<>();



        String query=null;
        if(type==ShowClothsFragment.SHOW_FAV_CLOTH){
            query="SELECT * FROM "+TABLE_FAV_COMBO;
        }
        else {
            query=getSavedCloths();
        }



        Cursor cursor=mDB.rawQuery(query,null);
        while (cursor.moveToNext()){
            pantInformation.add(cursor.getInt(1));
            shirtInformation.add(cursor.getInt(0));
        }

        ArrayList<ArrayList<Integer>> favCombo=new ArrayList<>();

        favCombo.add(pantInformation);
        favCombo.add(shirtInformation);

        return  favCombo;
//        return  getClothIds(ClothInformation.IS_SHIRT);
    }



    /*public ArrayList<Integer> getFavCloths(){

    }*/
    public ArrayList<Integer> getClothIds(int type){

        Cursor cursor=mDB.query(TABLE_CLOTH,new String[]{KEY_ID+""},C_TYPE+"=?",new String[]{type+""},null,null,null);
        ArrayList<Integer> clothIds=new ArrayList<>();
        while (cursor.moveToNext())
        {
            clothIds.add(cursor.getInt(0));
        }
        cursor.close();
        return  clothIds;
    }
    public void removeFav(int pantId,int shirtId){

        String query="DELETE FROM " +TABLE_FAV_COMBO+" WHERE "+C_SHIRT_ID+"="+shirtId+
                " AND "+C_PANT_ID+"="+pantId;
        mDB.execSQL(query);
    }

    public String getSavedCloths(){

        String query="SELECT * FROM "+TABLE_HISTORY+" ORDER BY "+C_DATE+" DESC";
        return query;
    }



    public boolean isFav(ClothInformation pantInfo,ClothInformation shirtInfo){
        return isFav(pantInfo.id,shirtInfo.id);
    }

    public boolean isFav(int pantId,int shirtId){

        String query="SELECT * FROM "+TABLE_FAV_COMBO+" WHERE "+C_SHIRT_ID+"="+shirtId+
                " AND "+C_PANT_ID+"="+pantId;
        Cursor cursor=mDB.rawQuery(query,null);
        boolean isFav=cursor.getCount()>0;
        cursor.close();
        return  isFav;

    }
    void createTables(SQLiteDatabase database){
        String createClothTable="CREATE TABLE "+TABLE_CLOTH+"("+KEY_ID+" integer PRIMARY KEY AUTOINCREMENT,"
                +C_URL+" text,"+C_TYPE+" integer)";
        database.execSQL(createClothTable);
        String createFavTable="CREATE TABLE "+TABLE_FAV_COMBO+"("+C_SHIRT_ID+" integer,"+C_PANT_ID
                +" integer)";
        database.execSQL(createFavTable);

        String createHistoryTable="CREATE TABLE "+TABLE_HISTORY+"("+C_SHIRT_ID+" integer,"+C_PANT_ID
                +" integer,"+C_DATE+" text)";
        database.execSQL(createHistoryTable);
    }


    public  long saveClothCombo(int shirtId,int pantId){
        long millis = System.currentTimeMillis();

        ContentValues contentValues=new ContentValues();
        contentValues.put(C_PANT_ID,pantId);
        contentValues.put(C_SHIRT_ID,shirtId);
        contentValues.put(C_DATE,millis);


        return mDB.insert(TABLE_HISTORY, null, contentValues);
    }

    public ClothInformation getCloth(int id){
        Cursor cursor=mDB.query(TABLE_CLOTH, null, KEY_ID + "=?", new String[]{id + ""}, null, null, null);
        ClothInformation clothInformation =null;
        if(cursor.moveToNext()){

            clothInformation =new ClothInformation();

            clothInformation.imageUrl=cursor.getString(cursor.getColumnIndex(C_URL));
            clothInformation.id=id;
            clothInformation.type=cursor.getInt(cursor.getColumnIndex(KEY_ID));

        }
        cursor.close();
        return clothInformation;

    }
    public synchronized static void releaseDatabase() {


        instanceCounter--;
        if (instanceCounter < 0) {
            instanceCounter=0;
            if(mDB!=null) {
                mDB.close();
                databaseInstance.close();
                databaseInstance = null;
                mDB = null;
            }

        }
    }
    private DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        mDB = getWritableDatabase();

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getHistoryClothDate(int currenshirt, int currentPant) {
        String query="SELECT "+C_DATE+" FROM "+TABLE_HISTORY+" WHERE "+C_SHIRT_ID+"="+currenshirt
                +" AND "+C_PANT_ID+"="+currentPant;

        Cursor c=mDB.rawQuery(query,null);
        try {


            while (c.moveToNext()) {
                Long date = Long.parseLong(c.getString(0));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date);

                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH)+1;
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                return mDay + "/" + mMonth + "/" + mYear;
            }
        }
        catch (Exception e){
            e.printStackTrace();

        }

        return "Could not retrieve date";
    }
}
