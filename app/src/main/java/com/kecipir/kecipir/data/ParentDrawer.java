package com.kecipir.kecipir.data;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by Kecipir-Dev on 05/01/2017.View
 */

public class ParentDrawer extends ExpandableGroup<ChildDrawer> {

    private String title;
    private int badge;
    private int icon;


    public ParentDrawer(String title, List<ChildDrawer> items, int badge, int icon) {
        super(title, items);
        this.title = title;
        this.badge = badge;
        this.icon = icon;
    }


    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
