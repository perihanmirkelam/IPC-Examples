package com.pmirkelam.ipcclient.ui.messenger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pmirkelam.ipcclient.R

class MessengerFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_messenger, container, false)
        val textView: TextView = root.findViewById(R.id.text_messenger)
        return root
    }
}