package com.example.seeStore;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.seeStore.activity.ProductListActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class SearchActivity extends AppCompatActivity {
    // Show search history? https://stackoverflow.com/questions/21585326/implementing-searchview-in-action-bar
    // It can be done later... :D
    private LinearLayout searchWrapperLayout;
    private ImageButton backBtn;
    private SearchView inputField;
    private ImageButton cameraBtn;
    private MaterialButton submitBtn;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViews();

        setEvents();
    }

    private void initViews() {
        searchWrapperLayout = findViewById(R.id.searchWrapperLayout);
        backBtn = findViewById(R.id.searchBackBtn);
        inputField = findViewById(R.id.searchInputField);
        cameraBtn = findViewById(R.id.searchCameraBtn);
        submitBtn = findViewById(R.id.searchSubmitBtn);
    }

    private void setEvents() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        inputField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    cameraBtn.setVisibility(View.VISIBLE);
                } else {
                    cameraBtn.setVisibility(View.GONE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                submitTextQuery(query);
                return true;
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTextQuery(inputField.getQuery().toString());
            }
        });

        setupCamera();
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runImagePicker();
            }
        });
    }

    private void setupCamera() {
        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Bitmap bitmap = ImagePicker.getImageFromResult(SearchActivity.this, data);
                            if (bitmap != null) {
                                submitImageQuery(bitmap);
                            } else {
                                Snackbar.make(searchWrapperLayout, "Đã có lỗi xảy ra. Vui lòng thử lại sau", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    private void submitTextQuery(String query) {
        if (query.length() == 0) {
            Snackbar.make(searchWrapperLayout, "Vui lòng nhập nội dung tìm kiếm", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(SearchActivity.this, ProductListActivity.class);
        intent.putExtra("text", query);
        startActivity(intent);
    }

    private void submitImageQuery(Bitmap bitmap) {
        // Bitmap to Base64 string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Intent intent = new Intent(SearchActivity.this, ProductListActivity.class);
        intent.putExtra("image", encoded);
        startActivity(intent);
    }

    private void runImagePicker() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        cameraActivityResultLauncher.launch(chooseImageIntent);
    }
}