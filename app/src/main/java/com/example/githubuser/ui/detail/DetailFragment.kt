package com.example.githubuser.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.databinding.FragmentDetailBinding
import com.example.githubuser.adapter.DetailUserAdapter
import com.example.githubuser.model.DetailViewModel

class DetailFragment : Fragment() {
    private var position = 0
    private var username: String = ""

    private lateinit var binding: FragmentDetailBinding
    private val detailViewModel: DetailViewModel by activityViewModels()

    companion object {
        const val ARG_USERNAME = "Hafidz"
        const val ARG_POSITION = "0"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME) ?: "test"
        }

        detailViewModel.getUserFollower(username)
        detailViewModel.getUserFollowing(username)
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFragment.layoutManager = layoutManager

        detailViewModel.loading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        if (position == 1) {
            detailViewModel.userFollower.observe(viewLifecycleOwner) {
                val adapter = DetailUserAdapter()
                adapter.submitList(it)
                binding.rvFragment.adapter = adapter
            }
        } else {
            detailViewModel.userFollowing.observe(viewLifecycleOwner) {
                val adapter = DetailUserAdapter()
                adapter.submitList(it)
                binding.rvFragment.adapter = adapter
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}