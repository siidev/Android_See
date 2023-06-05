package com.example.mobile_scratch.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mobile_scratch.adapter.ProductDetailAdapter;
import com.example.mobile_scratch.fragments.CartFragment;
import com.example.mobile_scratch.fragments.HomeFragment;
import com.example.mobile_scratch.models.CartItem;
import com.example.mobile_scratch.models.ProductModel;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.example.mobile_scratch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProductDetailActivity extends AppCompatActivity {

    ProductModel product;


    FirebaseFirestore db;

    ViewPager imageViewPager;

    RadioGroup sizeGroup;


    LayoutInflater inflater;

    private int quantity = 1; // Default

    private ImageView[] dots;

    LinearLayout indicators;

    int imgCount;

    String user;

    DocumentReference cartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        cartRef = db.collection("cart").document(user);

        imageViewPager = findViewById(R.id.productImagePager);

        product = getIntent().getParcelableExtra("product");
//        Log.d("product in detail", product.getDesc());
        ProductDetailAdapter productDetailAdapter = new ProductDetailAdapter(ProductDetailActivity.this, product.getImg());

        imageViewPager.setAdapter(productDetailAdapter);
        sizeGroup = findViewById(R.id.sizeGroup);


        inflater = LayoutInflater.from(this);

        product.getSize().forEach(size -> {

            Button radioButton = (Button) inflater.inflate(R.layout.button_size, sizeGroup, false);
            radioButton.setText(Double.toString(size));

            sizeGroup.addView(radioButton);
        });




        ImageButton backButton = findViewById(R.id.leftTopBarBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Finish the current activity and navigate back to the previous activity
            }
        });

        ImageButton rightTopBarBtn = findViewById(R.id.rightTopBarBtn);
        rightTopBarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        TextView quantityTextView = findViewById(R.id.quantityTextView);
        Button decrementButton = findViewById(R.id.decrementButton);
        Button incrementButton = findViewById(R.id.incrementButton);

        // Set the initial
        quantityTextView.setText(String.valueOf(quantity));

        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    quantityTextView.setText(String.valueOf(quantity));
                }
            }
        });

        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                quantityTextView.setText(String.valueOf(quantity));
            }
        });


        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        sizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    RadioButton btn = (RadioButton) radioGroup.getChildAt(i);
                    if (!btn.isChecked()) {
                        btn.setBackground(getDrawable(R.drawable.unchecked));
//                        Log.d("unchecked", Integer.toString(i));
                    } else {
                        btn.setBackground(getDrawable(R.drawable.checked));
//                        Log.d("checked", Integer.toString(i));
                    }
                }
            }
        });
        bindView();
        bindIndicators();

    }


    private  void bindIndicators() {
        indicators = findViewById(R.id.pagerIndicator);
        imgCount = product.getImg().size();
        dots = new ImageView[imgCount];
        dots[0] = new ImageView(this);
        dots[0].setImageDrawable(getDrawable(R.drawable.sh_img_indicator_active_dot));
        indicators.addView(dots[0]);
        for (int i=1; i<imgCount;i++) {
            ImageView dot = new ImageView(this);
           dot.setImageDrawable(getDrawable(R.drawable.sh_img_indicator_inactive_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 0, 10, 0);
            dot.setLayoutParams(params);
           dots[i] = dot;
           indicators.addView(dot);
        }
        imageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dots[position].setImageDrawable(getDrawable(R.drawable.sh_img_indicator_active_dot));
                for(int i=0;i<imgCount;i++) {
                    if (i!=position) {
                        dots[i].setImageDrawable(getDrawable(R.drawable.sh_img_indicator_inactive_dot));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    private void bindView() {
        TextView productNameTextView = findViewById(R.id.productName);
        TextView productPriceTextView = findViewById(R.id.productPrice);
        TextView productDescTextView = findViewById(R.id.productDescription);

        productNameTextView.setText(product.getName());
        productPriceTextView.setText(product.getPrice() + "$");
        productDescTextView.setText(product.getDesc());
    }

    private void addToCart() {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(product.getProductID());
        cartItem.setProductName(product.getName());
        cartItem.setPrice(product.getPrice());
        cartItem.setQuantity(quantity);
        String selectedSize = getSelectedSize();
//        Log.d("slt size", selectedSize);
        if (selectedSize.isEmpty()) {
            // No size selected, display an error message or handle it as needed
            Snackbar.make(getWindow().getDecorView(), "Please select a size", Snackbar.LENGTH_SHORT).show();
            return;
        }
        cartItem.setSize(selectedSize);

        FieldPath cartVariantID = FieldPath.of(String.format("%s%s", cartItem.getProductId(), cartItem.getSize()));
        cartRef.get().addOnSuccessListener(task->{
           if (task.exists()) {
               if(task.get(cartVariantID) != null) {
                   //Log.d("data", task.get(cartVariantID).toString());
                   Map<String, Number> data = (Map<String, Number>) task.get(cartVariantID);
                   //tang so luong
                   int newQty = cartItem.getQuantity() + data.get("quantity").intValue();
                   data.replace("quantity", newQty);
                   cartRef
                           .update(cartVariantID, data)
                           .addOnFailureListener(e->{notifyErrAdd2Cart(e);}); //overwrite
                   Snackbar.make(getWindow().getDecorView(), "Product quantity increased in cart", Snackbar.LENGTH_SHORT).show();
                   return;
               }
           }
           //them bien the moi, san pham moi
            //neu khach chua co gio hang thi auto tao moi
            Map<String, Number> variant = new HashMap<>();
            variant.put("quantity", cartItem.getQuantity());
            variant.put("price", cartItem.getPrice());
            Map<String, Object> data = new HashMap<>();
            String path = String.format("%s%s", cartItem.getProductId(), cartItem.getSize());
            data.put(path, variant);
            cartRef
                    .set(data, SetOptions.merge())
                    .addOnFailureListener(e->{notifyErrAdd2Cart(e);});
            Snackbar.make(getWindow().getDecorView(), "New product added into cart", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void notifyErrAdd2Cart(Exception e) {
        Snackbar.make(
                getWindow().getDecorView(),
                String.format("Failed to add product to cart, cause by %s", e.getMessage()),
                Snackbar.LENGTH_LONG).show();
    }
    private String getSelectedSize() {
        int selectedSizeId = sizeGroup.getCheckedRadioButtonId();
        if (selectedSizeId != -1) {
            RadioButton selectedSizeButton = findViewById(selectedSizeId);
            return selectedSizeButton.getText().toString();
        } else {
            return "";
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        sizeGroup.clearCheck();

    }
}