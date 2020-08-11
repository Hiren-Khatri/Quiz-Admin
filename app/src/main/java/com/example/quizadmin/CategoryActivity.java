package com.example.quizadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView rv;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firebaseDatabase.getReference();
    public static List<CategoryModel> list;
    private Dialog loadingDialog, addCategoryDialog;

    private EditText categoryName;
    private Button addCategoryBtn;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        setAddCategoryDialog();

        rv = findViewById(R.id.rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rv.setLayoutManager(linearLayoutManager);

        list = new ArrayList<>();

        adapter = new CategoryAdapter(list, new CategoryAdapter.DeleteListener() {
            @Override
            public void onDelete(final String key, final int position) {

                new AlertDialog.Builder(CategoryActivity.this,R.style.Theme_AppCompat_Light_Dialog)
                        .setTitle("Delete Category")
                        .setMessage("Confirm delete")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();
                                myRef.child("Categories").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            for (String setIds : list.get(position).getSets()){
                                                myRef.child("SETS").child(setIds).removeValue();
                                            }

                                            list.remove(position);
                                            adapter.notifyDataSetChanged();
                                           loadingDialog.dismiss();
                                        } else {
                                            Toast.makeText(CategoryActivity.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        rv.setAdapter(adapter);

        loadingDialog.show();
        myRef.child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    List<String> sets = new ArrayList<>();
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("sets").getChildren()     ){
                        sets.add(dataSnapshot2.getKey());
                    }
                    list.add(new CategoryModel(dataSnapshot1.child("name").getValue().toString(),
                            sets, dataSnapshot1.getKey()));
                }
                adapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CategoryActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            //dialog show
            addCategoryDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAddCategoryDialog() {
        addCategoryDialog = new Dialog(this);
        addCategoryDialog.setContentView(R.layout.add_category_dialog);
        addCategoryDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
        addCategoryDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        addCategoryDialog.setCancelable(true);

        categoryName = addCategoryDialog.findViewById(R.id.categoryname);
        addCategoryBtn = addCategoryDialog.findViewById(R.id.addCategory);

        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryName.getText().toString().isEmpty()) {
                    categoryName.setError("required");
                    return;
                }
                for (CategoryModel model: list){
                    if(categoryName.getText().toString().equals(model.getName())){
                        categoryName.setError("Category already exist!");
                        return;
                    }
                }
                uploadCategoryName();
                addCategoryDialog.dismiss();
            }
        });
    }

    private void uploadCategoryName() {
        loadingDialog.show();
        Map<String, Object> map = new HashMap<>();
        map.put("name", categoryName.getText().toString());
        map.put("sets", 0);

        final String id = UUID.randomUUID().toString();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Categories").child(id).setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            list.add(new CategoryModel(categoryName.getText().toString(), new ArrayList<String>(), id));
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CategoryActivity.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        loadingDialog.dismiss();
    }
}

