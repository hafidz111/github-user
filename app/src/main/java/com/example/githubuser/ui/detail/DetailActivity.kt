package com.example.githubuser.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.githubuser.R
import com.example.githubuser.databinding.ActivityDetailBinding
import com.example.githubuser.adapter.SectionsPagerAdapter
import com.example.githubuser.model.DetailViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel>()

    companion object {
        @StringRes
        private val TAB_TITLE = intArrayOf(R.string.tab_text_1, R.string.tab_text_2)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("USERNAME")
        if (username != null) {
            val sectionPagerAdapter = SectionsPagerAdapter(this, username)
            val viewPager: ViewPager2 = findViewById(R.id.view_pager)
            viewPager.adapter = sectionPagerAdapter
            val tabs: TabLayout = findViewById(R.id.tabs)
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = resources.getString(TAB_TITLE[position])
            }.attach()
            supportActionBar?.elevation = 0f
        }
        if (username != null) {
            detailViewModel.getDetailUser(username)
        }

        detailViewModel.userDetail.observe(this) {
            if (it != null) {
                Glide.with(this@DetailActivity)
                    .load(it.avatarUrl)
                    .centerCrop()
                    .into(binding.civUser)
                binding.tvName.text = it.name
                binding.tvUsername.text = it.login
                binding.tvFollower.text = "${it.followers} Follower"
                binding.tvFollowing.text = "${it.following} Following"
            }
        }

        detailViewModel.loading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}