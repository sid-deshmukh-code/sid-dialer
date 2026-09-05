package com.sid.phonedialer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sid.phonedialer.fragments.ContactsFragment
import com.sid.phonedialer.fragments.DialpadFragment
import com.sid.phonedialer.fragments.RecentsFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DialpadFragment()
            1 -> RecentsFragment()
            else -> ContactsFragment()
        }
    }
}
