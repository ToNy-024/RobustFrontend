package com.example.robustfrontend.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.robustfrontend.databinding.ActivityAdminUserBinding
import com.example.robustfrontend.viewmodel.admin.AdminUserViewModel

class AdminUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminUserBinding
    private val viewModel: AdminUserViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchView()
        setupObservers()

        viewModel.fetchUsers()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter { user ->
            val intent = Intent(this, EditUserActivity::class.java).apply {
                putExtra(EditUserActivity.EXTRA_USER_ID, user.idUsu)
            }
            startActivity(intent)
        }

        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(this@AdminUserActivity)
            adapter = userAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchViewUsers.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchUsers(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchUsers(newText)
                return true
            }
        })
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressBarAdmin.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.toastMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })

        viewModel.users.observe(this, Observer { users ->
            userAdapter.submitList(users)
        })
    }
    override fun onResume() {
        super.onResume()
        // Recargar los usuarios cuando la actividad se reanuda
        viewModel.fetchUsers()
    }
}
