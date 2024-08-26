package com.example.assignment1.screens.user;

import static com.example.assignment1.utils.Constants.RESULT_DELETED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.assignment1.R;
import com.example.assignment1.dialogs.DeleteDialogFragment;
import com.example.assignment1.dialogs.ErrorDialogFragment;
import com.example.assignment1.room.User;
import com.example.assignment1.utils.Constants;
import com.example.assignment1.utils.Validator;

import java.util.Random;

public class UserActivity extends AppCompatActivity {
    private UserViewModel viewModel;
    private final User user = new User();
    private LinearLayout userLayNotEditable, userLayEditable;
    private EditText userEdtFirstName, userEdtLastName, userEdtEmail, userEdtAvatar;
    private ImageView userAvatarImg, userEditOrCheckImg, userDeleteOrCancelImg;
    private TextView userName, userEmail;
    private boolean isEditMode;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getUserFromExtras();
        findView();
        initView();
        if (savedInstanceState != null) {
            isEditMode = savedInstanceState.getBoolean(Constants.IS_EDIT, false);
            updateView();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.IS_EDIT, isEditMode);
    }

    /**
     * Prepare result data if user data is available
     * @return Boolean
     */
    @Override
    public boolean onSupportNavigateUp() {
        if (user.id != -1) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Constants.ID, user.id);
            resultIntent.putExtra(Constants.FIRST_NAME, user.first_name);
            resultIntent.putExtra(Constants.LAST_NAME, user.last_name);
            resultIntent.putExtra(Constants.EMAIL, user.email);
            resultIntent.putExtra(Constants.AVATAR, user.avatar);
            resultIntent.putExtra(Constants.POSITION, position);

            setResult(RESULT_OK, resultIntent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
        return true;
    }

    /**
     * Extract user data from intent extras
     */
    private void getUserFromExtras() {
        Intent intent = getIntent();
        user.id = intent.getIntExtra(Constants.ID, -1);
        user.first_name = intent.getStringExtra(Constants.FIRST_NAME);
        user.last_name = intent.getStringExtra(Constants.LAST_NAME);
        user.email = intent.getStringExtra(Constants.EMAIL);
        user.avatar = intent.getStringExtra(Constants.AVATAR);
        position = intent.getIntExtra(Constants.POSITION, -1);
    }

    /**
     * Hides the keyboard
     */
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    /**
     * Initialize view references
     */
    private void findView() {
        userEdtFirstName = findViewById(R.id.user_edt_first_name);
        userEdtLastName = findViewById(R.id.user_edt_last_name);
        userEdtEmail = findViewById(R.id.user_edt_email);
        userEdtAvatar = findViewById(R.id.user_edt_avatar);
        userLayNotEditable = findViewById(R.id.user_lay_not_edit_mode);
        userLayEditable = findViewById(R.id.user_lay_edit_mode);
        userEditOrCheckImg = findViewById(R.id.edit_or_check_img);
        userDeleteOrCancelImg = findViewById(R.id.delete_or_cancel_img);
        userAvatarImg = findViewById(R.id.user_avatar);
        userName = findViewById(R.id.user_full_name);
        userEmail = findViewById(R.id.user_email);

    }

    /**
     * Initialize fields with user data
     * Set up text watchers to validate user input
     * Set up click listeners for edit and delete actions
     */
    private void initView() {
        userEdtEmail.setText(user.email);
        userEdtFirstName.setText(user.first_name);
        userEdtLastName.setText(user.last_name);
        userEdtAvatar.setText(user.avatar);
        userEdtEmail.addTextChangedListener(emailTextWatcher);
        userEdtFirstName.addTextChangedListener(firstNameTextWatcher);
        userEdtLastName.addTextChangedListener(lastNameTextWatcher);
        userEdtAvatar.addTextChangedListener(avatarTextWatcher);
        userEditOrCheckImg.setOnClickListener(v -> onEditOrCheckClicked());
        userDeleteOrCancelImg.setOnClickListener(v -> onDeleteOrCancelClicked());

        updateUI();

        // Check if came from Add button, if true then turn on edit mode
        if (user.id == -1)
            onEditOrCheckClicked();
    }

    /**
     * Handle delete or cancel actions based on current mode
     */
    private void onDeleteOrCancelClicked() {
        if (!isEditMode) {
            openDeleteDialog();
        } else {
            if (user.id == -1) {
                onSupportNavigateUp();
                return;
            }
            isEditMode = false;
            updateView();
        }
    }

    /**
     * Show confirmation dialog for user deletion using DeleteDialogFragment
     */
    private void openDeleteDialog() {
        new DeleteDialogFragment()
                .setCallback(new DeleteDialogFragment.DeleteCallback() {
                    @Override
                    public void onConfirm() {
                        viewModel.deleteUser(user);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(Constants.POSITION, position);
                        setResult(RESULT_DELETED, resultIntent);
                        finish();
                    }
                }).show(getSupportFragmentManager(), DeleteDialogFragment.TAG);
    }


    /**
     * Handle edit or cancel actions based on current mode
     * if isEditMode is false and user.id == -1 we create a new user as we on create else we update
     * the user we are currently working on
     */
    private void onEditOrCheckClicked() {
        isEditMode = !isEditMode;
        if(!isEditMode) {
            if(validateUserInput()) {
                user.first_name = userEdtFirstName.getText().toString();
                user.last_name = userEdtLastName.getText().toString();
                user.email = userEdtEmail.getText().toString();
                user.avatar = userEdtAvatar.getText().toString();

                if (user.id == -1) {
                    // TODO check id not duplicated
                    // Generate new id
                    Random random = new Random();
                    user.id = random.nextInt(Integer.MAX_VALUE);
                    // Add new user case
                    viewModel.insertUser(user);
                    hideKeyboard();
                } else {
                    // Update existing user case
                    // Update user on database
                    hideKeyboard();
                    viewModel.updateUser(user);
                }
                updateUI();
            } else {
                openErrorDialog("Please correct the errors before proceeding.");
            }

        }
        updateView();
    }

    /**
     * Validate the user input of string
     * setting the correct error for each of the possibles
     * @return Boolean
     */
    private boolean validateUserInput() {
        boolean isValid = true;

        // Validate First Name
        if (userEdtFirstName.getText().toString().trim().isEmpty()) {
            userEdtFirstName.setError("First name is required");
            isValid = false;
        }

        // Validate Last Name
        if (userEdtLastName.getText().toString().trim().isEmpty()) {
            userEdtLastName.setError("Last name is required");
            isValid = false;
        }

        // Validate Email
        String emailInput = userEdtEmail.getText().toString().trim();
        if(!Validator.isValidEmail(emailInput)){
            userEdtEmail.setError("Valid email is required");
            isValid = false;
        }
        return isValid;
    }

    private void updateUI() {
        Glide.with(this)
                .load(user.avatar)
                .placeholder(R.drawable.ic_user)
                .into(userAvatarImg);

        // Check if came from Add button
        if (user.id == -1) return;

        userName.setText(user.first_name + " " + user.last_name);
        userEmail.setText(user.email);
    }

    private void updateView() {
        userLayEditable.setVisibility(isEditMode ? View.VISIBLE: View.GONE);
        userLayNotEditable.setVisibility(isEditMode ? View.GONE: View.VISIBLE);
        userEditOrCheckImg.setImageResource(isEditMode ? R.drawable.ic_baseline_check_24 : R.drawable.ic_baseline_edit_24);
        userDeleteOrCancelImg.setImageResource(isEditMode ? R.drawable.ic_baseline_cancel_24 : R.drawable.ic_baseline_delete_24);
    }

    private void openErrorDialog(String errorMessage) {
        new ErrorDialogFragment()
                .setErrorMessage(errorMessage)
                .show(getSupportFragmentManager(), ErrorDialogFragment.TAG);
    }


    //TODO add validations to text watchers, make sure the edit text closes when navigate back


    private final TextWatcher firstNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            String firstNameInput = editable.toString();
            if (firstNameInput.isEmpty() || !firstNameInput.matches("[a-zA-Z]+")) {
                userEdtFirstName.setError("Enter a valid first name");
                userEdtLastName.requestFocus();
            } else {
                userEdtFirstName.setError(null);
            }
        }
    };

    private final TextWatcher lastNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            String lastNameInput = editable.toString();
            if (lastNameInput.isEmpty() || !lastNameInput.matches("[a-zA-Z]+")) {
                userEdtLastName.setError("Enter a valid last name");
            } else {
                userEdtLastName.setError(null);
            }
        }
    };

    private final TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            String emailInput = editable.toString();
            if (emailInput.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                userEdtEmail.setError("Invalid email address");
            } else {
                userEdtEmail.setError(null); // Clear error
            }
        }
    };

    private final TextWatcher avatarTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}