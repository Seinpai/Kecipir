package com.kecipir.kecipir.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kecipir.kecipir.MainActivity;
import com.kecipir.kecipir.R;
import com.kecipir.kecipir.SessionManager;
import com.kecipir.kecipir.adapter.MenuCategoryAdapter;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.MenuCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Albani on 10/21/2015.
 */
public class MenuCategoryFragment extends Fragment implements ClickListener {

    private static final String ARG_POSITION = "position";
    private int position;

    TextView txtUser;
    RecyclerView recyclerView;
    MenuCategoryAdapter menuCategoryAdapter;
    List<MenuCategory> data;

    public MenuCategoryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public List<MenuCategory> getData() {
        data = new ArrayList<>();

//        int[] icons = {R.drawable.ic_btn_semua, R.drawable.ic_btn_paket, R.drawable.ic_btn_sayurdaun, R.drawable.ic_btn_sayurbuah, R.drawable.ic_btn_buah, R.drawable.ic_btn_bumbu, R.drawable.ic_btn_extra, R.drawable.ic_btn_herbal, R.drawable.ic_btn_popular};
        int[] icons = {R.drawable.ic_btn_semua, R.drawable.ic_btn_sayurdaun, R.drawable.ic_btn_sayurbuah, R.drawable.ic_btn_buah, R.drawable.ic_btn_bumbu, R.drawable.ic_btn_extra, R.drawable.ic_btn_herbal, R.drawable.ic_btn_popular};
        String[] titles = {"Semua", "Terlaris", "Promo", "Paket", "Sayur Daun", "Sayur Buah", "Buah", "Bumbu", "Extra", "Herbal", "Sayur Bunga", "Umbi" , "Sayur", "Bumbu dan Herbal"};
//        String[] titles2 = {"Semua", "Terlaris", "Paket",  "Sayur Daun", "Sayur Buah", "Buah", "Bumbu", "Extra", "Herbal"};

        for (int i = 0; i < titles.length; i++) {
            MenuCategory current = new MenuCategory();
            current.icon = icons[i];
            current.title = (titles[i]);
            data.add(current);
        }
        return data;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menucategory, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_menu_category);

        menuCategoryAdapter = new MenuCategoryAdapter(getActivity(), getData());
        menuCategoryAdapter.setOnClickListener(this);
        menuCategoryAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(menuCategoryAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return rootView;
    }

    @Override
    public void itemClicked(View view, int position) {

        FragmentManager fragmentManager = getFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.home_replace, new TabMainFragment().newInstance(position));
        MainActivity activity = (MainActivity)getActivity();
        activity.setMenuCategory(0);
        activity.setTitleCategory(data.get(position).title);
        fragmentTransaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

    }
}
