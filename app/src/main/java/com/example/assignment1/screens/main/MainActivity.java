package com.example.assignment1.screens.main;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import com.airbnb.lottie.LottieAnimationView;
import com.example.assignment1.R;
import com.example.assignment1.dialogs.ErrorDialogFragment;
import com.example.assignment1.room.User;
import com.example.assignment1.screens.user.UserActivity;
import com.example.assignment1.utils.Constants;
import com.example.assignment1.utils.SharedPrefsUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private ImageView mainImgGrid;
    private FloatingActionButton mainBtnAdd;
    private LottieAnimationView mainBarLoading;
    private RecyclerView mainRecyclerView;
    private UserAdapter userAdapter;
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            User user = new User();

                            user.id = data.getIntExtra(Constants.ID, -1);
                            user.first_name = data.getStringExtra(Constants.FIRST_NAME);
                            user.last_name = data.getStringExtra(Constants.LAST_NAME);
                            user.email = data.getStringExtra(Constants.EMAIL);
                            user.avatar = data.getStringExtra(Constants.AVATAR);

                            int position = data.getIntExtra(Constants.POSITION, -1);
                            if (position != -1)
                                userAdapter.updateUser(position, user);
                            else
                                userAdapter.addUser(user);
                        }
                    } else if (result.getResultCode() == Constants.RESULT_DELETED) {
                        Intent data = result.getData();
                        if (data != null) {
                            int position = data.getIntExtra(Constants.POSITION, -1);
                            if (position != -1)
                                userAdapter.deleteUser(position);
                        }
                    }
                }
            }
    );
    private Switch themeSwitch;
    private final Observer<ArrayList<User>> usersLiveDataObserver = new Observer<ArrayList<User>>() {
        @Override
        public void onChanged(ArrayList<User> users) {
            mainBarLoading.setVisibility(View.GONE);
            userAdapter.setUsers(users);
        }
    };
    private final Observer<String> errorLiveDataObserver = new Observer<String>() {
        @Override
        public void onChanged(String errorMessage) {
            if (errorMessage != null) {
                mainBarLoading.setVisibility(View.GONE);
                openErrorDialog(errorMessage);
                viewModel.clearError(); // Clear the value after handling
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initViews();

        setRecyclerViewAdapter();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setLiveDataObservers();
    }

    /**
     * Setup the initial theme based on the Shared Preferences
     */
    private void setInitialThemeState() {
        boolean isDarkTheme = SharedPrefsUtil.getInstance().getBooleanFromSP(Constants.IS_DARK_THEME, false);
        AppCompatDelegate.setDefaultNightMode(isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    /**
     * Find and assign view references from the layout
     */
    private void findViews() {
        mainBarLoading = findViewById(R.id.main_lottie_animation_view);
        mainRecyclerView = findViewById(R.id.main_recycler_view);
        mainBtnAdd = findViewById(R.id.main_btn_add);
        themeSwitch = findViewById(R.id.themeSwitch);
        mainImgGrid = findViewById(R.id.main_img_grid);
    }

    /**
     * Set up LiveData observers for observing data changes
     */
    private void setLiveDataObservers() {
        viewModel.usersLiveData.observe(this, usersLiveDataObserver);
        viewModel.errorLiveData.observe(this, errorLiveDataObserver);
    }

    /**
     * Set up the recycler view Layout manager and adapter
     */
    private void setRecyclerViewAdapter() {
        boolean isGrid = SharedPrefsUtil.getInstance().getBooleanFromSP(Constants.IS_GRID, false);
        userAdapter = new UserAdapter(this, launcher);
        mainRecyclerView.setLayoutManager(
                isGrid ?
                        new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false) :
                        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mainRecyclerView.setAdapter(userAdapter);
    }

    /**
     * Initialize view interactions such as button clicks and listeners.
     */
    private void initViews() {
        mainBtnAdd.setOnClickListener(view -> onAddClicked());
        mainRecyclerView.addOnScrollListener(createPaginationScrollListener());
        themeSwitch.setOnCheckedChangeListener(onThemeChange());
        themeSwitch.setChecked(SharedPrefsUtil.getInstance().getBooleanFromSP(Constants.IS_DARK_THEME, false));
        mainImgGrid.setAlpha(SharedPrefsUtil.getInstance().getBooleanFromSP(Constants.IS_GRID, false) ? 0.5f : 1f);
        mainImgGrid.setOnClickListener(onGridClicked());
        setInitialThemeState();
    }

    /**
     * Create and return a scroll listener for handling pagination in the RecyclerView.
     * @return A RecyclerView.OnScrollListener that triggers loading more data when reaching the bottom.
     */
    private RecyclerView.OnScrollListener createPaginationScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) { // Check if scrolling down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        // Reached the bottom
                        if (viewModel.getPage() < viewModel.getTotalPages()) {
                            mainBarLoading.setVisibility(View.VISIBLE);
                            viewModel.loadNextPage();
                        }
                    }
                }
            }
        };
    }

    /**
     * Create and return a listener to handle switch view toggle
     * @return A CompoundButton.OnCheckedChangeListener that toggles between light and dark theme
     */
    private CompoundButton.OnCheckedChangeListener onThemeChange() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPrefsUtil.getInstance().putBooleanToSP(Constants.IS_DARK_THEME, isChecked);
                AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            }
        };
    };

    /**
     * Create and return a listener for handling grid view toggle.
     * @return A View.OnClickListener that toggles between grid and list layouts.
     */
    private View.OnClickListener onGridClicked() {
        return view -> {
            boolean isGrid = SharedPrefsUtil.getInstance().getBooleanFromSP(Constants.IS_GRID, false);
            mainImgGrid.setAlpha(!isGrid ? 0.5f : 1f);
            mainRecyclerView.setLayoutManager(
                    !isGrid
                            ? new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
                            : new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            );
            mainRecyclerView.setAdapter(userAdapter);
            SharedPrefsUtil.getInstance().putBooleanToSP(Constants.IS_GRID, !isGrid);
        };
    };

    /**
     * Handle add button clicked to launch the user activity
     */
    private void onAddClicked() {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(Constants.POSITION, -1);
        launcher.launch(intent);
    }

    /**
     * Show an error message dialog containing the message you want
     * @param errorMessage
     */
    private void openErrorDialog(String errorMessage) {
        new ErrorDialogFragment()
                .setErrorMessage(errorMessage)
                .setCallback(new ErrorDialogFragment.ErrorCallback() {
                    @Override
                    public void onConfirm() {
                        // Fetch current page after loading failed and user clicked ok
                        viewModel.loadCurrentPage();
                    }
                })
                .show(getSupportFragmentManager(), ErrorDialogFragment.TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.usersLiveData.removeObserver(usersLiveDataObserver);
        viewModel.errorLiveData.removeObserver(errorLiveDataObserver);
    }
}