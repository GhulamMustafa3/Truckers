package com.example.truckers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import com.google.android.material.textfield.TextInputEditText


class searchtruckfilters : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var originInput: TextInputEditText
    private lateinit var destinationInput: TextInputEditText
    private lateinit var trucktype:TextInputEditText
    private lateinit var length: TextInputEditText
    private lateinit var weight: TextInputEditText
    private lateinit var eqlimits: TextInputEditText
    private lateinit var search:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_searchtruckfilters, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        originInput = view.findViewById(R.id.orign_input)
        destinationInput = view.findViewById(R.id.des_input)
        length = view.findViewById(R.id.length_input)
        weight = view.findViewById(R.id.weight_input)
        eqlimits = view.findViewById(R.id.limits_input)
        trucktype = view.findViewById(R.id.Ttype_input)
        progressBar = view.findViewById(R.id.progress_bar)
        search = view.findViewById(R.id.search_button)

        search.setOnClickListener {
            progressBar.visibility = View.VISIBLE // Show ProgressBar when the search is clicked

            val bundle = Bundle().apply {
                putString("origin", originInput.text.toString())
                putString("destination", destinationInput.text.toString())
                putString("length", length.text.toString())
                putString("weight", weight.text.toString())
                putString("limits", eqlimits.text.toString())
                putString("truckType", trucktype.text.toString())
            }

            val truckFragment = searchtruck().apply {
                arguments = bundle
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, truckFragment)
                .addToBackStack(null)
                .commit()

            progressBar.visibility = View.GONE // Hide ProgressBar after the fragment transaction
        }
    }


}


