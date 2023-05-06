package com.example.seeStore.provider;

import com.example.seeStore.R;
import com.example.seeStore.model.Category;

import java.util.ArrayList;
import java.util.HashMap;

public class Provider {
    private static Provider instance = null;

    public static Provider getInstance() {
        // ref: https://www.digitalocean.com/community/tutorials/java-singleton-design-pattern-best-practices-examples#:~:text=safe%20singleton%20class.-,4.%20Thread%20Safe%20Singleton,-A%20simple%20way
        if (instance == null) {
            synchronized (Provider.class) {
                if (instance == null) {
                    instance = new Provider();
                }
            }
        }
        return instance;
    }

    private HashMap<String, ArrayList<Category>> categoryListMapping;

    private Provider() {
        if (categoryListMapping == null)
            categoryListMapping = new HashMap<>();

        initData();
    }

    private void initData() {
        initCategories();
    }

    private void initCategories() {
        String[] maleCategories = new String[]{"Áo khoác nam", "Áo thun nam", "Áo polo nam", "Áo sơ mi nam", "Quần jean nam", "Quần tây nam", "Quần kaki nam", "Quần short nam"};
        int[] maleImageIds = new int[]{R.drawable.ao_khoac_nam, R.drawable.ao_thun_nam, R.drawable.ao_polo_nam, R.drawable.ao_so_mi_nam, R.drawable.quan_jean_nam, R.drawable.quan_tay_nam, R.drawable.quan_tay_nam, R.drawable.quan_short_nam};
        String[] maleRaws = new String[]{"ao-khoac-nam", "ao-thun-ao-phong-nam", "ao-polo-nam", "ao-so-mi-nam", "quan-jean-nam", "quan-tay-quan-au-nam", "quan-kaki", "quan-short-nam"};
        String[] femaleCategories = new String[]{"Áo khoác nữ", "Áo thun nữ", "Áo sơ mi nữ", "Quần jean nữ", "Quần tây nữ", "Quần short nữ", "Chân váy", "Váy đầm"};
        int[] femaleImageIds = new int[]{R.drawable.ao_khoac_nu, R.drawable.ao_thun_nu, R.drawable.ao_so_mi_nu, R.drawable.quan_jean_nu, R.drawable.quan_tay_nu, R.drawable.quan_short_nu, R.drawable.chan_vay, R.drawable.vay_dam};
        String[] femaleRaws = new String[] {"ao-khoac-nu", "ao-thun-ao-phong-nu", "ao-so-mi-nu", "quan-jean-nu", "quan-au-nu", "quan-short-nu", "chan-vay", "vay-dam"};

        categoryListMapping.put("nam", new ArrayList<>());
        for (int i = 0; i < maleCategories.length; i++) {
            categoryListMapping.get("nam").add(new Category(maleCategories[i], maleImageIds[i], maleRaws[i]));
        }

        categoryListMapping.put("nu", new ArrayList<>());
        for (int i = 0; i < femaleCategories.length; i++) {
            categoryListMapping.get("nu").add(new Category(femaleCategories[i], femaleImageIds[i], femaleRaws[i]));
        }

    }

    public ArrayList<Category> getCategoryList(String gender) {
        return categoryListMapping.get(gender);
    }
}