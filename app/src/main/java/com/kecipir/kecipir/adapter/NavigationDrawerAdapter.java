package com.kecipir.kecipir.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kecipir.kecipir.R;
import com.kecipir.kecipir.data.ChildDrawer;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.ParentDrawer;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * Created by Kecipir-Dev on 05/01/2017.
 */

public class NavigationDrawerAdapter extends ExpandableRecyclerViewAdapter<NavigationDrawerAdapter.ParentViewHolder, NavigationDrawerAdapter.ChildsViewHolder> {

    Context context;
    public NavigationDrawerAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public ParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_navdrawer, parent, false);


        return new ParentViewHolder(view);
    }

    @Override
    public ChildsViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_navdrawer, parent, false);
        return new ChildsViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ChildsViewHolder holder, int flatPosition,
                                      ExpandableGroup group, int childIndex) {

        final ChildDrawer artist = ((ParentDrawer) group).getItems().get(childIndex);
        holder.setChildName(artist);
    }

    @Override
    public void onBindGroupViewHolder(ParentViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {
        holder.setGenreTitle(group);
    }


    class ParentViewHolder extends GroupViewHolder{

        private TextView textList, badgeList;
        private ImageView imgDrawer;
        private RelativeLayout layoutDrawer;

        public ParentViewHolder(View itemView) {
            super(itemView);
            textList = (TextView) itemView.findViewById(R.id.txt_drawer);
            badgeList = (TextView) itemView.findViewById(R.id.badge_drawer);
            imgDrawer = (ImageView) itemView.findViewById(R.id.image_drawer);
            layoutDrawer = (RelativeLayout) itemView.findViewById(R.id.layout_drawer);

        }

        public void setGenreTitle(final ExpandableGroup genre) {
            textList.setText(genre.getTitle());
            badgeList.setText(((ParentDrawer) genre).getBadge()+"");
            imgDrawer.setImageResource(((ParentDrawer) genre).getIcon());

        }

        @Override
        public void expand() {
            animateExpand();
        }

        @Override
        public void collapse() {
            animateCollapse();
        }

        private void animateExpand() {
            RotateAnimation rotate =
                    new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
        }

        private void animateCollapse() {
            RotateAnimation rotate =
                    new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
        }

    }

    class ChildsViewHolder extends ChildViewHolder {

        private TextView textList, badgeList;
        private ImageView imgDrawer;
        private RelativeLayout layoutDrawer;

        public ChildsViewHolder(View itemView) {
            super(itemView);
            textList = (TextView) itemView.findViewById(R.id.txt_drawer);
            badgeList = (TextView) itemView.findViewById(R.id.badge_drawer);
            imgDrawer = (ImageView) itemView.findViewById(R.id.image_drawer);
            layoutDrawer = (RelativeLayout) itemView.findViewById(R.id.layout_drawer);
        }

        public void setChildName(final ChildDrawer child) {
            textList.setText(child.getTitle());
            badgeList.setText(child.getBadge()+"");
            imgDrawer.setImageResource(child.getIcon());

        }

    }
}

