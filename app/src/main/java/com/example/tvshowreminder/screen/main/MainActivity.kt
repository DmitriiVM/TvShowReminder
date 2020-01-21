package com.example.tvshowreminder.screen.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tvshowreminder.R
import com.example.tvshowreminder.TvShowApplication

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main)


        supportFragmentManager.findFragmentById(R.id.fragment_container) as MainFragment? ?:
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment.newInstance())
            .commit()
    }
}



