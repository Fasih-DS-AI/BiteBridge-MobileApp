package com.malak.bitebridge.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.malak.bitebridge.R;
import com.malak.bitebridge.database.MenuRepository;
import com.malak.bitebridge.models.MenuItem;

import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private MenuRepository menuRepository;
    private RecyclerView recyclerView;
    private List<MenuItem> menuItems;
    private AdminMenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        androidx.coordinatorlayout.widget.CoordinatorLayout layout =
                new androidx.coordinatorlayout.widget.CoordinatorLayout(this);
        layout.setLayoutParams(new androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(
                new androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        layout.addView(recyclerView);

        FloatingActionButton fab = new FloatingActionButton(this);
        androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams fabParams =
                new androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        fabParams.gravity = Gravity.BOTTOM | Gravity.END;
        fabParams.setMargins(0, 0, 32, 32);
        fab.setLayoutParams(fabParams);
        fab.setImageResource(android.R.drawable.ic_input_add);
        layout.addView(fab);

        setContentView(layout);
        setTitle("Admin — Menu Items");

        menuRepository = new MenuRepository(this);
        menuItems = menuRepository.getAllItems();

        adapter = new AdminMenuAdapter(this, menuItems,
                new AdminMenuAdapter.OnAdminActionListener() {
                    @Override
                    public void onDelete(MenuItem item) {
                        new AlertDialog.Builder(AdminActivity.this)
                                .setTitle("Delete Item")
                                .setMessage("Delete " + item.getName() + "?")
                                .setPositiveButton("Delete", (d, w) -> {
                                    menuRepository.deleteItem(item.getItemId());
                                    menuItems.remove(item);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(AdminActivity.this,
                                            "Deleted!",
                                            Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }

                    @Override
                    public void onEdit(MenuItem item) {
                        showEditDialog(item);
                    }
                });

        recyclerView.setAdapter(adapter);
        fab.setOnClickListener(v -> showAddDialog());
    }

    // Reusable method to build input form
    private LinearLayout buildForm(EditText etName, EditText etCategory,
                                   EditText etPrice, EditText etDescription,
                                   EditText etImageUrl) {
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(48, 24, 48, 24);

        etName.setHint("Item Name *");
        etCategory.setHint("Category (e.g. Pizza) *");
        etPrice.setHint("Price *");
        etPrice.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etDescription.setHint("Description");
        etImageUrl.setHint("Image URL (paste from web)");
        etImageUrl.setInputType(InputType.TYPE_TEXT_VARIATION_URI |
                InputType.TYPE_CLASS_TEXT);

        int margin = 12;
        for (EditText et : new EditText[]{etName, etCategory,
                etPrice, etDescription, etImageUrl}) {
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, margin, 0, margin);
            et.setLayoutParams(params);
            ll.addView(et);
        }

        // Helper text for image URL
        TextView tvHint = new TextView(this);
        tvHint.setText("💡 Tip: Find any food image on Google, " +
                "long-press → Copy image address, paste above.");
        tvHint.setTextSize(11);
        tvHint.setTextColor(0xFF757575);
        tvHint.setPadding(0, 4, 0, 0);
        ll.addView(tvHint);

        return ll;
    }

    private void showAddDialog() {
        EditText etName = new EditText(this);
        EditText etCategory = new EditText(this);
        EditText etPrice = new EditText(this);
        EditText etDescription = new EditText(this);
        EditText etImageUrl = new EditText(this);

        LinearLayout form = buildForm(etName, etCategory,
                etPrice, etDescription, etImageUrl);

        new AlertDialog.Builder(this)
                .setTitle("➕ Add Menu Item")
                .setView(form)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String category = etCategory.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String desc = etDescription.getText().toString().trim();
                    String imageUrl = etImageUrl.getText().toString().trim();

                    if (name.isEmpty() || category.isEmpty()
                            || priceStr.isEmpty()) {
                        Toast.makeText(this,
                                "Name, category and price are required",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price = Double.parseDouble(priceStr);
                    MenuItem newItem = new MenuItem(0, name, category,
                            price, desc, imageUrl, true);
                    menuRepository.insertItem(newItem);

                    menuItems.clear();
                    menuItems.addAll(menuRepository.getAllItems());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this,
                            name + " added!",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(MenuItem item) {
        EditText etName = new EditText(this);
        EditText etCategory = new EditText(this);
        EditText etPrice = new EditText(this);
        EditText etDescription = new EditText(this);
        EditText etImageUrl = new EditText(this);

        // Pre-fill existing values
        etName.setText(item.getName());
        etCategory.setText(item.getCategory());
        etPrice.setText(String.valueOf(item.getPrice()));
        etDescription.setText(item.getDescription());
        etImageUrl.setText(item.getImageUrl());

        LinearLayout form = buildForm(etName, etCategory,
                etPrice, etDescription, etImageUrl);

        new AlertDialog.Builder(this)
                .setTitle("✏️ Edit " + item.getName())
                .setView(form)
                .setPositiveButton("Save", (dialog, which) -> {
                    String priceStr = etPrice.getText().toString().trim();
                    if (priceStr.isEmpty()) {
                        Toast.makeText(this,
                                "Price cannot be empty",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    item.setName(etName.getText().toString().trim());
                    item.setCategory(etCategory.getText().toString().trim());
                    item.setPrice(Double.parseDouble(priceStr));
                    item.setDescription(
                            etDescription.getText().toString().trim());
                    item.setImageUrl(
                            etImageUrl.getText().toString().trim());
                    menuRepository.updateItem(item);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this,
                            "Updated!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Inner Adapter ──────────────────────────────────────────
    static class AdminMenuAdapter extends
            RecyclerView.Adapter<AdminMenuAdapter.ViewHolder> {

        interface OnAdminActionListener {
            void onDelete(MenuItem item);
            void onEdit(MenuItem item);
        }

        private List<MenuItem> items;
        private android.content.Context context;
        private OnAdminActionListener listener;

        AdminMenuAdapter(android.content.Context ctx,
                         List<MenuItem> items,
                         OnAdminActionListener listener) {
            this.context = ctx;
            this.items = items;
            this.listener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setPadding(24, 16, 24, 16);
            ll.setBackgroundColor(0xFFFFFFFF);
            ll.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(ll);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            MenuItem item = items.get(position);

            holder.tvName.setText(item.getName());
            holder.tvCategory.setText(
                    item.getCategory() + " — $" + item.getPrice());

            // Show image thumbnail using Glide
            if (item.getImageUrl() != null &&
                    !item.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(item.getImageUrl())
                        .centerCrop()
                        .override(80, 80)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.ivThumb);
            } else {
                holder.ivThumb.setImageResource(
                        android.R.drawable.ic_menu_gallery);
            }

            holder.btnEdit.setOnClickListener(
                    v -> listener.onEdit(item));
            holder.btnDelete.setOnClickListener(
                    v -> listener.onDelete(item));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            android.widget.ImageView ivThumb;
            TextView tvName, tvCategory;
            Button btnEdit, btnDelete;

            ViewHolder(LinearLayout ll) {
                super(ll);

                // Thumbnail
                ivThumb = new android.widget.ImageView(ll.getContext());
                LinearLayout.LayoutParams imgParams =
                        new LinearLayout.LayoutParams(80, 80);
                imgParams.setMargins(0, 0, 16, 0);
                ivThumb.setLayoutParams(imgParams);
                ivThumb.setScaleType(
                        android.widget.ImageView.ScaleType.CENTER_CROP);
                ll.addView(ivThumb);

                // Text block
                LinearLayout textLayout = new LinearLayout(ll.getContext());
                textLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams textParams =
                        new LinearLayout.LayoutParams(
                                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                textLayout.setLayoutParams(textParams);

                tvName = new TextView(ll.getContext());
                tvName.setTextSize(14);
                tvName.setTextColor(0xFF1A1A1A);
                tvName.setTypeface(null, android.graphics.Typeface.BOLD);
                textLayout.addView(tvName);

                tvCategory = new TextView(ll.getContext());
                tvCategory.setTextSize(12);
                tvCategory.setTextColor(0xFF757575);
                textLayout.addView(tvCategory);

                ll.addView(textLayout);

                // Buttons
                btnEdit = new Button(ll.getContext());
                btnEdit.setText("Edit");
                btnEdit.setTextSize(11);
                LinearLayout.LayoutParams btnParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                btnParams.setMargins(8, 0, 0, 0);
                btnEdit.setLayoutParams(btnParams);
                ll.addView(btnEdit);

                btnDelete = new Button(ll.getContext());
                btnDelete.setText("Del");
                btnDelete.setTextSize(11);
                btnDelete.setLayoutParams(btnParams);
                ll.addView(btnDelete);
            }
        }
    }
}