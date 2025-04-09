package hu.nje.plantcare;

import java.util.ArrayList;
import java.util.List;

public class MenuManager {

    public static List<String> getMenuItems() {
        List<String> menuItems = new ArrayList<>();
        menuItems.add("Home");
        menuItems.add("Search");
        menuItems.add("Favourite plants");
        menuItems.add("Own plants");
        menuItems.add("Plant scanner");
        menuItems.add("Settings");
        return menuItems;
    }
}

