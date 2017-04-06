package com.kecipir.kecipir.data;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Albani on 9/4/2016.
 */
public interface ModelClickListener {
    public void itemClicked (List<StoreList> model, View view, int position);
}
