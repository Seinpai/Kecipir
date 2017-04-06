package com.kecipir.kecipir.data;

import com.kecipir.kecipir.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Kecipir-Dev on 11/01/2017.
 */

public class NavigationDrawerGenerator {
    public static List<ParentDrawer> makeParentMember() {
        return Arrays.asList(new ParentDrawer("Pembayaran", null, 0, R.drawable.notif_icon),
                new ParentDrawer("Logout", null, 0, R.drawable.notif_icon),
                new ParentDrawer("Info", makeRockArtists(), 0, R.drawable.notif_icon),
                new ParentDrawer("Greencash", null, 0, R.drawable.notif_icon),
                new ParentDrawer("Rock", null, 0, R.drawable.notif_icon));
    }

    public static List<ChildDrawer> makeRockArtists() {
        ChildDrawer queen = new ChildDrawer("Queen", 0, R.drawable.notif_icon);
        ChildDrawer styx = new ChildDrawer("Styx", 0, R.drawable.notif_icon);
        ChildDrawer reoSpeedwagon = new ChildDrawer("REO Speedwagon", 0, R.drawable.notif_icon);
        ChildDrawer boston = new ChildDrawer("Boston", 0, R.drawable.notif_icon);

        return Arrays.asList(queen, styx, reoSpeedwagon, boston);
    }
}
