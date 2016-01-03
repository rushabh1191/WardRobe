package com.crowdfire.wardrobe;

/**
 * Created by rushabh on 29/12/15.
 */
public class ClothInformation {

    public static final int IS_PANT=1;
    public static final int IS_SHIRT=2;

    String imageUrl;
    int type;
    int id;

    boolean isPant(){
        return  type==IS_PANT;
    }

}
