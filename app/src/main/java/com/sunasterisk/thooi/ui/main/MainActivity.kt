package com.sunasterisk.thooi.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.sunasterisk.thooi.NavGraphDirections
import com.sunasterisk.thooi.R
import com.sunasterisk.thooi.base.BaseActivity
import com.sunasterisk.thooi.databinding.ActivityMainBinding
import com.sunasterisk.thooi.di.ViewModelFactory

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val mainVM: MainVM by viewModels { ViewModelFactory(this) }

    private val navController by lazy { findNavController(R.id.navHostFragment) }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean,
    ) = ActivityMainBinding.inflate(inflater)

    override fun setupViews() {
        setupBottomNavView()

    }

    private fun setupBottomNavView() = binding.bottomNavView.let {
        NavigationUI.setupWithNavController(it, navController)
        it.setOnNavigationItemSelectedListener { item ->
            if (navController.currentDestination?.id != item.itemId) {
                NavigationUI.onNavDestinationSelected(item, navController)
            } else {
                true
            }
        }
    }

    override fun onObserveLiveData() {
        mainVM.loginStatus.observe(this) {
            if (it == null) return@observe
            if (it.currentUser == null) navController.navigate(NavGraphDirections.globalSignIn())
        }
        mainVM.toolbarState.observe(this) {
            if (it == null) return@observe
            val motionLayout = binding.root
            motionLayout.transitionToState(
                when (it) {
                    ToolbarState.NORMAL -> R.id.main_toolbar_normal
                    ToolbarState.COLLAPSED -> R.id.main_toolbar_collapsed
                    ToolbarState.HIDDEN -> R.id.main_toolbar_hidden
                }
            )
        }
        binding.floatingActionButton.setOnClickListener {
            navController.navigate(R.id.global_newPost)
        }
        mainVM.isFixer.observe(this) {
            binding.floatingActionButton.isClickable = !it
        }
    }

    override fun initListeners() {
        with(binding.root) {
            val fullScreenDestinations = setOf(R.id.signInFragment, R.id.signupFragment)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id in fullScreenDestinations) {
                    mainVM.hideToolbar()
                } else {
                    mainVM.showToolbar()
                }
            }
        }
    }
}
