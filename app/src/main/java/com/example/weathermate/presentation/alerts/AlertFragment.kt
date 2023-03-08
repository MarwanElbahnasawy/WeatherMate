package com.example.weathermate.presentation.alerts


import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermate.MyApp
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentAlertBinding
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.util.MyConverters
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AlertFragment : Fragment() , InterfaceAlerts {

    private val REQUEST_CODE_NOTIFICATION_PERMISSION = 444

    private val TAG = "commonnn"

    lateinit var binding: FragmentAlertBinding

    lateinit var alertsAdapter: AlertsAdapter

    lateinit var builder: AlertDialog.Builder
    lateinit var dialogView: View
    lateinit var alertDialog: AlertDialog

    lateinit var alertsVewModel: AlertsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentAlertBinding.inflate(inflater, container, false)

        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated: of alarms")

        initFrag()

        activateFABAlerts()

        setupAlertsAdapter()

    }


    private fun initFrag() {

        val alertsViewModelFactory = AlertsViewModelFactory(MyApp.getInstanceRepository())
        alertsVewModel = ViewModelProvider(this, alertsViewModelFactory)[AlertsViewModel::class.java]

        builder = AlertDialog.Builder(activity)
        dialogView = layoutInflater.inflate(R.layout.dialogue_new_alert, null)

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun activateFABAlerts() {
        binding.fabAlerts.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATION_PERMISSION
                )
            }
            if (!Settings.canDrawOverlays(requireContext())) {

                val builder = AlertDialog.Builder(requireContext())

                builder.setMessage("This app needs permission to display an alarm over other apps. Please enable it in the app's settings.")
                    .setTitle("Permission required")

                builder.setPositiveButton("OK") { _, _ ->
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.data = Uri.parse("package:" + requireActivity().packageName)
                    startActivityForResult(intent, 0)
                }

                val dialog = builder.create()
                dialog.show()
            }
            if ((ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                        ) && (Settings.canDrawOverlays(requireContext()))
            ) {
                showAddAlertDialog();
            }


        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            Toast.makeText(requireContext(),"return", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddAlertDialog() {

        (dialogView.parent as ViewGroup?)?.removeView(dialogView)

        builder.setView(dialogView)
        val textViewStartDate = dialogView.findViewById<TextView>(R.id.tv_start_date)
        val textViewEndDate = dialogView.findViewById<TextView>(R.id.tv_end_date)
        val tvLocation = dialogView.findViewById<TextView>(R.id.tv_location)
        textViewStartDate.setOnClickListener { showDatePicker(textViewStartDate) }
        textViewEndDate.setOnClickListener { showDatePicker(textViewEndDate) }
        tvLocation.setOnClickListener { tvLocationClicked() }
        builder.setPositiveButton(
            "Save"
        ) { _, i ->
            saveClicked(textViewStartDate, textViewEndDate)
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialogInterface, i -> dialogInterface.dismiss() }

        alertDialog = builder.create()
        alertDialog.show()
    }


    private fun showDatePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            { datePicker, year, month, day ->
                val date = day.toString() + " " + MyConverters.getMonth(month) + ", " + year
                showTimePicker(textView, date)
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(textView: TextView, date: String) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            activity,
            { timePicker, hourOfDay, minute ->
                val time = "$hourOfDay:$minute"
                val dateTime = "$date $time"
                textView.text = dateTime
            }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], false
        )
        timePickerDialog.show()
    }

    private fun tvLocationClicked() {
        alertsVewModel.putBooleanInSharedPreferences("tvLocationClicked", true)

        findNavController().navigate(
            AlertFragmentDirections.actionNavigationAlertsToMapFragment(
                isFromAlerts = true
            )
        )
    }

    private fun saveClicked(textViewStartDate: TextView, textViewEndDate: TextView) {
        if (dialogView.findViewById<TextView>(R.id.tv_location).text.isNotEmpty() && textViewStartDate.text.isNotEmpty() && textViewEndDate.text.isNotEmpty() && (dialogView.findViewById<RadioButton>(
                R.id.rbNotification
            ).isChecked || dialogView.findViewById<RadioButton>(R.id.rbAlarm).isChecked)
        ) {

            val formatter = SimpleDateFormat("d MMMM, yyyy HH:mm")
            formatter.timeZone = TimeZone.getTimeZone("GMT+2")
            val dateStart = formatter.parse(textViewStartDate.text.toString())
            val dateEnd = formatter.parse(textViewStartDate.text.toString())
            val unixTimeDTStart = dateStart?.time?.div(1000)
            val unixTimeDTEnd = dateEnd?.time?.div(1000)

            if (unixTimeDTStart != null && unixTimeDTEnd != null) {

                var alertType = ""
                if (dialogView.findViewById<RadioButton>(R.id.rbNotification).isChecked) {
                    alertType = "notification"
                } else if (dialogView.findViewById<RadioButton>(R.id.rbAlarm).isChecked) {
                    alertType = "alarm"
                }

                val alertItem = AlertItem(
                    address = dialogView.findViewById<TextView>(R.id.tv_location).text.toString(),
                    longitudeString = alertsVewModel.getStringFromSharedPreferences(
                        "ALERT_LONGITUDE_FROM_MAP",
                        ""
                    ),
                    latitudeString = alertsVewModel.getStringFromSharedPreferences(
                        "ALERT_LATITUDE_FROM_MAP",
                        ""
                    ),
                    startString = textViewStartDate.text.toString(),
                    endString = textViewEndDate.text.toString(),
                    startDT = unixTimeDTStart.toInt(),
                    endDT = unixTimeDTEnd.toInt(),
                    idHashLongFromLonLatStartStringEndStringAlertType = (alertsVewModel.getStringFromSharedPreferences(
                        "ALERT_LONGITUDE_FROM_MAP",
                        ""
                    ) + alertsVewModel.getStringFromSharedPreferences(
                        "ALERT_LATITUDE_FROM_MAP",
                        ""
                    ) + textViewStartDate.text.toString() + textViewEndDate.text.toString() + alertType).hashCode()
                        .toLong(),
                    alertType = alertType
                )

                alertsVewModel.insertAlert(alertItem)

            }

        } else {
            Log.i(TAG, "saveClicked: not all fields are clicked")
        }
    }

    private fun setupAlertsAdapter() {
        val mlayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        alertsAdapter = AlertsAdapter(lifecycleScope , this)

        binding.rvAlerts.apply {
            layoutManager = mlayoutManager
            adapter = alertsAdapter
        }

        alertsVewModel.getAllAlerts().observe(viewLifecycleOwner) {
            alertsAdapter.submitList(it)
        }


    }

    override fun onPause() {

        super.onPause()
        if (alertsVewModel.getBooleanFromSharedPreferences("tvLocationClicked", false)) {

            alertsVewModel.putBooleanInSharedPreferences("tvLocationClicked", false)

            alertsVewModel.putStringInSharedPreferences(
                "start_date",
                dialogView.findViewById<TextView>(R.id.tv_start_date).text.toString()
            )
            alertsVewModel.putStringInSharedPreferences(
                "end_date",
                dialogView.findViewById<TextView>(R.id.tv_end_date).text.toString()
            )
            alertsVewModel.putStringInSharedPreferences(
                "ALERT_ADDRESS",
                dialogView.findViewById<TextView>(R.id.tv_location).text.toString()
            )

            var alartTypeSelected = ""

            if (dialogView.findViewById<RadioButton>(R.id.rbNotification).isChecked) {
                alartTypeSelected = "notification"
            } else if (dialogView.findViewById<RadioButton>(R.id.rbAlarm).isChecked) {
                alartTypeSelected = "alarm"
            }

            alertsVewModel.putStringInSharedPreferences("alarm_type_selected", alartTypeSelected)


            alertDialog.dismiss()
        }

    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        Log.i(TAG, "onViewStateRestored: ")

        if (alertsVewModel.getStringFromSharedPreferences("start_date", "")!!
                .isNotEmpty() || alertsVewModel.getStringFromSharedPreferences("end_date", "")!!
                .isNotEmpty() || alertsVewModel.getStringFromSharedPreferences("alarm_type_selected", "")!!
                .isNotEmpty() || alertsVewModel.getStringFromSharedPreferences("ALERT_ADDRESS", "")!!.isNotEmpty()
        ) {
            showAddAlertDialog()
            if (alertsVewModel.getStringFromSharedPreferences("start_date", "")!!.isNotEmpty()) {
                dialogView.findViewById<TextView>(R.id.tv_start_date).text =
                    alertsVewModel.getStringFromSharedPreferences("start_date", "")
            }
            if (alertsVewModel.getStringFromSharedPreferences("end_date", "")!!.isNotEmpty()) {
                dialogView.findViewById<TextView>(R.id.tv_end_date).text =
                    alertsVewModel.getStringFromSharedPreferences("end_date", "")
            }
            if (alertsVewModel.getStringFromSharedPreferences("ALERT_ADDRESS", "")!!.isNotEmpty()) {
                dialogView.findViewById<TextView>(R.id.tv_location).text =
                    alertsVewModel.getStringFromSharedPreferences("ALERT_ADDRESS", "")
            }
            if (alertsVewModel.getStringFromSharedPreferences("alarm_type_selected", "") == "notification") {
                dialogView.findViewById<RadioButton>(R.id.rbNotification).isChecked = true
            } else if (alertsVewModel.getStringFromSharedPreferences("alarm_type_selected", "") == "alarm") {
                dialogView.findViewById<RadioButton>(R.id.rbAlarm).isChecked = true
            }



            alertsVewModel.putStringInSharedPreferences("start_date", "")
            alertsVewModel.putStringInSharedPreferences("end_date", "")
            alertsVewModel.putStringInSharedPreferences("alarm_type_selected", "")
            alertsVewModel.putStringInSharedPreferences("ALERT_ADDRESS", "")

        }


    }

    override fun onItemClickAlerts(alert: AlertItem) {
        lifecycleScope.launch {
            alertsVewModel.deleteAlert(alert)
        }
    }


}