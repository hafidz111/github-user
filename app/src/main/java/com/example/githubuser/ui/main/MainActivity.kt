package com.example.githubuser.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.data.response.GithubResponse
import com.example.githubuser.data.response.ItemsItem
import com.example.githubuser.data.retrofit.ApiConfig
import com.example.githubuser.databinding.ActivityMainBinding
import com.example.githubuser.adapter.UserListAdapter
import com.example.githubuser.model.UserViewModel
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val userViewModel by viewModels<UserViewModel>()

    companion object {
        private const val TAG = "MainActivity"
        private const val USER_NAME = "Hafidz"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    searchBar.setText(searchView.text)
                    searchView.hide()
                    userViewModel.setReviewData(searchBar.text.toString())
                    userViewModel.listUser.observe(this@MainActivity) {
                        if (it.isNullOrEmpty()) {
                            showNotFound(true)
                        } else {
                            showNotFound(false)
                        }
                    }
                    false
                }
        }

        val userViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[UserViewModel::class.java]
        userViewModel.listUser.observe(this) {
            setReviewData(it)
        }

        userViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvUsers.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUsers.addItemDecoration(itemDecoration)
        findUser()
    }

    private fun findUser() {
        showLoading(true)
        val client = ApiConfig.getApiService().getUser(USER_NAME)
        client.enqueue(object : retrofit2.Callback<GithubResponse> {
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>,
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setReviewData(responseBody.items)

                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setReviewData(item: List<ItemsItem>) {
        val adapter = UserListAdapter()
        adapter.submitList(item)
        binding.rvUsers.adapter = adapter
    }

    private fun showNotFound(isDataNotFound: Boolean) {
        binding.apply {
            if (isDataNotFound) {
                rvUsers.visibility = View.GONE
                errorMessage.visibility = View.VISIBLE
            } else {
                rvUsers.visibility = View.VISIBLE
                errorMessage.visibility = View.GONE
            }
        }
    }
}