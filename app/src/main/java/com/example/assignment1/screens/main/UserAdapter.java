package com.example.assignment1.screens.main;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.assignment1.R;
import com.example.assignment1.room.User;
import com.example.assignment1.screens.user.UserActivity;
import com.example.assignment1.utils.Constants;
import com.example.assignment1.utils.SharedPrefsUtil;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<User> users;
    private final ActivityResultLauncher<Intent> launcher;

    public UserAdapter(Context context, ActivityResultLauncher<Intent> launcher) {
        this.context = context;
        this.launcher = launcher;
        this.users = new ArrayList<>();
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public void updateUser(int position, User newUser) {
        users.set(position, newUser);
        notifyItemChanged(position);
    }

    public void addUser(User newUser) {
        users.add(newUser);
        notifyItemChanged(users.size() - 1);
    }

    public void deleteUser(int position) {
        users.remove(position);
        notifyItemRemoved(position);
    }

    private void applyGridLayout(MyViewHolder holder) {
        // Remove the image and add it back to the parent layout at the top
        ((ViewGroup) holder.imageAvatar.getParent()).removeView(holder.imageAvatar);
        ((ViewGroup) holder.textFullName.getParent()).addView(holder.imageAvatar, 0);
        // Adjust layout parameters to center the image, texts
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                300
        );
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        holder.imageAvatar.setLayoutParams(imageLayoutParams);

        // Adjust the layout parameters for text views to also wrap content
        holder.textFullName.setLayoutParams(textLayoutParams);
        holder.textEmail.setLayoutParams(textLayoutParams);

        // Adjust the size of the image and text
        holder.textFullName.setTextSize(16);
        holder.textEmail.setTextSize(10);
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_list_item,parent,false);
        return new MyViewHolder(view);
    }

    /**
     * Binds the data to the ViewHolder for the specified position.
     * @param holder The ViewHolder which holds the views to be updated.
     * @param position The position of the items within the adapter dataset
     */
    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {
        User user = users.get(position);
        holder.textFullName.setText(user.first_name + " " + user.last_name);
        holder.textEmail.setText(user.email);
        // Use Glide to load the image from URL into the ImageView
        Glide.with(context)
                .load(user.avatar)
                .placeholder((R.drawable.ic_user))
                .error(R.drawable.ic_user)
                .into(holder.imageAvatar);
        // Apply dynamic styling based on layout mode
        boolean isGrid = SharedPrefsUtil.getInstance().getBooleanFromSP(Constants.IS_GRID, false);
        if (isGrid) {
            applyGridLayout(holder);
        }

        holder.userListItemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra(Constants.ID, user.id);
                intent.putExtra(Constants.FIRST_NAME, user.first_name);
                intent.putExtra(Constants.LAST_NAME, user.last_name);
                intent.putExtra(Constants.EMAIL, user.email);
                intent.putExtra(Constants.AVATAR, user.avatar);
                intent.putExtra(Constants.POSITION, holder.getAdapterPosition());
                launcher.launch(intent);
            }
        });
    }

    /**
     *
     * @return A int representing the number of items inside the UserAdapter
     */
    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView userListItemCard;
        private TextView textFullName, textEmail;
        private ImageView imageAvatar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            findViews();
            userListItemCard.setAnimation(AnimationUtils.loadAnimation(context, R.anim.translate_anim));
        }

        /**
         * Find and assign view references from the layout
         */
        private void findViews() {
            userListItemCard = itemView.findViewById(R.id.user_list_item);
            textFullName = itemView.findViewById(R.id.text_full_name);
            textEmail = itemView.findViewById(R.id.text_email);
            imageAvatar = itemView.findViewById(R.id.image_avatar);
        }


    }
}
