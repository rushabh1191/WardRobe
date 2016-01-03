package com.crowdfire.wardrobe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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
    public void addFav(int shirtId,int pantId){
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
}
