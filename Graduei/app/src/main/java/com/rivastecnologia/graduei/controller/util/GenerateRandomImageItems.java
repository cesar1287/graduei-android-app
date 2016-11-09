package com.rivastecnologia.graduei.controller.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerateRandomImageItems {

    private List<ImageItem> randomItems = new ArrayList<>();

    public List<ImageItem> generateRandomItems(int size, List<ImageItem> items) {
        int qtd = (int) Double.parseDouble(String.valueOf(size*0.005));
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 1; i < size; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        for (int i = 0; i < qtd; i++) {
            randomItems.add(items.get(list.get(i)));
        }

        return randomItems;
    }

}