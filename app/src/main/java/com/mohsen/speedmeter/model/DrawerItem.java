package com.mohsen.speedmeter.model;


/**
 * Created by alirezaahmadi on 9/28/16.
 */

public class DrawerItem {
    public static final int TYPE_HOME = 1;
    public static final int TYPE_LIKED = 2;
    public static final int TYPE_AGENCIES = 3;
    public static final int TYPE_SELECTED_NEWS = 4;
    public static final int TYPE_TELEGRAM_CHANNEL = 5;
    public static final int TYPE_HUNDRED_SEC = 6;
    public static final int TYPE_UNSUB = 7;
    public static final int TYPE_SUPPORT = 8;

//    public static final int TYPE_EARTHQUAKE = R.id.earthquake;
//    public static final int TYPE_TESTS = R.id.tests;
//    public static final int TYPE_CONTENT = R.id.contents;
//    public static final int TYPE_HAFEZ = R.id.hafez;
//    public static final int TYPE_SPEED_METER = R.id.meter;


    private int type;
    private String title;
    private boolean isSelected;

    public DrawerItem() {
    }

    public DrawerItem(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
