package com.kecipir.kecipir.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kecipir-Dev on 05/01/2017.
 */

public class ChildDrawer implements Parcelable {

    private String title;
    private int badge;
    private int icon;


    protected ChildDrawer(Parcel in){
        title = in.readString();
    }
    public ChildDrawer(String title, int badge, int icon) {
        this.title = title;
        this.badge = badge;
        this.icon  = icon;
    }

    public String getTitle() {
        return title;
    }
    public int getBadge() {
        return badge;
    }


    public static final Creator<ChildDrawer> CREATOR = new Creator<ChildDrawer>() {
        @Override
        public ChildDrawer createFromParcel(Parcel in) {
            return new ChildDrawer(in);
        }

        @Override
        public ChildDrawer[] newArray(int size) {
            return new ChildDrawer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
    }

    public int getIcon() {
        return icon;
    }
}
