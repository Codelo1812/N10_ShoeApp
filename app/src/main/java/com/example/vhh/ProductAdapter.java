package com.example.vhh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public ProductAdapter(Context context, List<Product> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView and ImageView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = mData.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("$%.2f", product.getPrice()));
        holder.productImage.setImageResource(product.getImageResId());
        holder.heartIcon.setImageResource(product.isFavorite() ? R.drawable.ic_heart_filled : R.drawable.ic_heart); // Cập nhật icon trái tim
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        ImageView heartIcon; // Thêm biến cho icon trái tim

        ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            heartIcon = itemView.findViewById(R.id.heart_icon); // Khởi tạo icon trái tim
            itemView.setOnClickListener(this);
            heartIcon.setOnClickListener(this::onHeartIconClick); // Đăng ký sự kiện click cho icon trái tim
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        private void onHeartIconClick(View view) {
            int position = getAdapterPosition();
            Product product = mData.get(position);
            product.setFavorite(!product.isFavorite()); // Đảo ngược trạng thái yêu thích
            notifyItemChanged(position);
        }
    }

    // convenience method for getting data at click position
    public Product getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // thêm phương thức cập nhật dữ liệu
    public void updateData(List<Product> newData) {
        this.mData = newData;
        notifyDataSetChanged();
    }
}
