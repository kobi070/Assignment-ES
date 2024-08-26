package com.example.assignment1.screens.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.assignment1.model.UsersListResponse;
import com.example.assignment1.network.NetworkClient;
import com.example.assignment1.room.AppDatabase;
import com.example.assignment1.room.User;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final NetworkClient networkClient = NetworkClient.getInstance();
    private final AppDatabase appDatabase = AppDatabase.getInstance();
    public final MutableLiveData<ArrayList<User>> usersLiveData = new MutableLiveData<>();
    public final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private int page = 1;
    private int totalPages = 2;

    public MainViewModel() {
        // Get first page
        loadCurrentPage();
    }

    /**
     * Handle the GET request callback
     * if the request is successful set the total pages, save the Users data and update LiveData
     * @param pageNumber
     */
    public void getUsers(String pageNumber) {
        networkClient.getUsers(pageNumber).enqueue(new Callback<UsersListResponse>() {
            @Override
            public void onResponse(@NonNull Call<UsersListResponse> call, @NonNull Response<UsersListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UsersListResponse usersListResponse = response.body();
                    setTotalPages(usersListResponse.getTotalPages());

                    ArrayList<User> newUsers = usersListResponse.getData();
                    saveFetchedUsersToDB(newUsers);

                    // Accumulate the users and update LiveData
                    ArrayList<User> currentUsers = usersLiveData.getValue();
                    if (currentUsers != null) {
                        currentUsers.addAll(newUsers);
                        usersLiveData.postValue(currentUsers);
                    } else {
                        usersLiveData.postValue(newUsers);
                    }
                } else {
                    errorLiveData.postValue("Something went wrong please try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UsersListResponse> call, @NonNull Throwable t) {
                errorLiveData.postValue(t.getLocalizedMessage());
            }
        });
    }

    /**
     * Save each of the users fetched to our Room
     * @param users
     */
    private void saveFetchedUsersToDB(ArrayList<User> users) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (User user : users) {
                    appDatabase.userDAO().insertUser(user);
                }
            }
        });
    }

    public void loadCurrentPage() {
        getUsers(String.valueOf(page));
    }

    public void loadNextPage() {
        page++;
        getUsers(String.valueOf(page));
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void clearError() {
        errorLiveData.setValue(null);
    }
}