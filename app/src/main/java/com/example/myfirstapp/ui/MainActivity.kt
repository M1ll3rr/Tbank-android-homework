package com.example.myfirstapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.domain.usecases.LibraryUseCases
import com.example.domain.usecases.PreferencesUseCases
import com.example.myfirstapp.MyApplication
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment).navController
    }
    @Inject
    lateinit var libraryUseCases: LibraryUseCases
    @Inject
    lateinit var preferencesUseCases: PreferencesUseCases


    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).presentationComponent.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

}