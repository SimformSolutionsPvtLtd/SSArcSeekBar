package com.ssarcseekbar.presentation

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.activity_main.navigationDrawer

class MainActivity : AppCompatActivity() {
    private lateinit var navigationAdapter: ArrayAdapter<String>
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                invalidateOptionsMenu()
            }
        }

        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        supportFragmentManager.beginTransaction().replace(R.id.content_frame, FragmentUnstyledSeekbar()).commit()

        navigationAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        navigationAdapter.addAll(getString(R.string.unstyled_seekbar), getString(R.string.animated_progress), getString(R.string.custom_thumb), getString(R.string.seekbar_with_section))

        navigationDrawer.apply {
            adapter = navigationAdapter
            onItemClickListener = AdapterView.OnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
                when (position) {
                    0 -> supportFragmentManager.beginTransaction().replace(R.id.content_frame, FragmentUnstyledSeekbar()).commit()
                    1 -> supportFragmentManager.beginTransaction().replace(R.id.content_frame, FragmentAnimatedProgress()).commit()
                    2 -> supportFragmentManager.beginTransaction().replace(R.id.content_frame, FragmentCustomThumb()).commit()
                    3 -> supportFragmentManager.beginTransaction().replace(R.id.content_frame, FragmentSeekbarWithSection()).commit()
                }
                drawerLayout.closeDrawers()
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
