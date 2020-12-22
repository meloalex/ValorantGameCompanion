package com.alexespada.valorantgamecompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.FrameLayout
import com.alexespada.valorantgamecompanion.fragment.ChatFragment
import com.alexespada.valorantgamecompanion.fragment.NewsFragment
import com.alexespada.valorantgamecompanion.fragment.ProfileFragment
import com.alexespada.valorantgamecompanion.fragment.StreamsFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.chatTab -> {
                    // Add Chat fragment
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, ChatFragment())
                    transaction.addToBackStack("Chat")
                    transaction.commit()
                }

                R.id.newsTab -> {
                    // Add News fragment
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, NewsFragment())
                    transaction.addToBackStack("News")
                    transaction.commit()
                }

                R.id.profileTab -> {
                    // Add Profile fragment
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, ProfileFragment())
                    transaction.addToBackStack("Profile")
                    transaction.commit()
                }

                R.id.streamsTab -> {
                    // Add Streams fragment
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, StreamsFragment())
                    transaction.addToBackStack("Streams")
                    transaction.commit()
                }
            }
            true
        }

        bottomNavigationView.selectedItemId = R.id.newsTab

        // Init admob
        MobileAds.initialize(this)

        // Load Ad
        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }
}