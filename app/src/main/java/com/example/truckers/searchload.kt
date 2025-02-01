package com.example.truckers

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar


class searchload : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var originInput: TextInputEditText
    private lateinit var destinationInput: TextInputEditText
    private lateinit var startDate: TextInputEditText
    private lateinit var endDate: TextInputEditText
    private lateinit var length:TextInputEditText
    private lateinit var weight:TextInputEditText
    private lateinit var eqlimits:TextInputEditText
    private lateinit var trucktype:TextInputEditText
    private val calendar = Calendar.getInstance()
    private lateinit var search:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view= inflater.inflate(R.layout.fragment_searchload, container, false)
        return view
    }
   override fun onViewCreated(view: View,savedInstanceState: Bundle?){
       super.onViewCreated(view, savedInstanceState)

       originInput = view.findViewById(R.id.orign_input)
       destinationInput = view.findViewById(R.id.des_input)
       startDate = view.findViewById(R.id.sdateinput)
       endDate = view.findViewById(R.id.edate_input)
       progressBar = view.findViewById(R.id.progress_bar)
       length=view.findViewById(R.id.length_input)
       weight=view.findViewById(R.id.weight_input)
       eqlimits=view.findViewById(R.id.limits_input)
       trucktype=view.findViewById(R.id.Ttype_input)
       search=view.findViewById(R.id.search_button)
       startDate.setOnClickListener{
           showDatePicker()
       }
      endDate.setOnClickListener{
          showDatePicker2()
      }
      search.setOnClickListener{

      }

   }
    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                startDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
    private fun showDatePicker2() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                endDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

}