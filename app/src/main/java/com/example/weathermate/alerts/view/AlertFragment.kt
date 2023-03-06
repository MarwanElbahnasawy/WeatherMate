package com.example.weathermate.alerts.view


import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermate.MyApp
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentAlertBinding
import com.example.weathermate.model.AlertItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AlertFragment : Fragment() {

    private val TAG = "commonnn"

    lateinit var binding: FragmentAlertBinding

    lateinit var alertsAdapter: AlertsAdapter

    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor: Editor

    lateinit var builder: AlertDialog.Builder
    lateinit var dialogView: View
    lateinit var alertDialog: AlertDialog

    private var start_dateTime = ""
    private var end_dateTime = ""

    private var fromTime: Long = 0L
    private var toTime: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentAlertBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated: of alarms")

        initFrag()

        activateFABAlerts()


        setupAlertsAdapter()

    }


    private fun initFrag() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferencesEditor = sharedPreferences.edit()

        builder = AlertDialog.Builder(activity)
        dialogView = layoutInflater.inflate(R.layout.dialogue_new_alert, null)

    }


    private fun activateFABAlerts() {
        binding.fabAlerts.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                val builder = AlertDialog.Builder(requireContext())

                builder.setMessage("This app needs permission to show notifications. Please enable it in the app's settings.")
                    .setTitle("Permission required")

                builder.setPositiveButton("OK") { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }

                val dialog = builder.create()
                dialog.show()
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
                val date = day.toString() + " " + NativeDate.getMonth(month) + ", " + year
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

    class NativeDate {
        companion object {
            fun getMonth(month: Int): String {
                val months = arrayOf(
                    "January",
                    "February",
                    "March",
                    "April",
                    "May",
                    "June",
                    "July",
                    "August",
                    "September",
                    "October",
                    "November",
                    "December"
                )
                return months[month]
            }
        }
    }

    private fun tvLocationClicked() {
        sharedPreferencesEditor.putBoolean("tvLocationClicked", true)
        sharedPreferencesEditor.apply()

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

            lifecycleScope.launch {
                if (unixTimeDTStart != null && unixTimeDTEnd != null) {

                    var alertType = ""
                    if (dialogView.findViewById<RadioButton>(R.id.rbNotification).isChecked) {
                        alertType = "notification"
                    } else if (dialogView.findViewById<RadioButton>(R.id.rbAlarm).isChecked) {
                        alertType = "alarm"
                    }

                    val alertItem = AlertItem(
                        address = dialogView.findViewById<TextView>(R.id.tv_location).text.toString(),
                        longitudeString = sharedPreferences.getString(
                            "ALERT_LONGITUDE_FROM_MAP",
                            ""
                        )!!,
                        latitudeString = sharedPreferences.getString(
                            "ALERT_LATITUDE_FROM_MAP",
                            ""
                        )!!,
                        startString = textViewStartDate.text.toString(),
                        endString = textViewEndDate.text.toString(),
                        startDT = unixTimeDTStart.toInt(),
                        endDT = unixTimeDTEnd.toInt(),
                        idHashLongFromLonLatStartStringEndStringAlertType = (sharedPreferences.getString(
                            "ALERT_LONGITUDE_FROM_MAP",
                            ""
                        )!! + sharedPreferences.getString(
                            "ALERT_LATITUDE_FROM_MAP",
                            ""
                        )!! + textViewStartDate.text.toString() + textViewEndDate.text.toString() + alertType).hashCode()
                            .toLong(),
                        alertType = alertType
                    )
                    MyApp.getInstanceRepository().insertAlert(alertItem)


                }
            }

        } else {
            Log.i(TAG, "saveClicked: not all fields are clicked")
        }
    }

    private fun setupAlertsAdapter() {
        val mlayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        alertsAdapter = AlertsAdapter(lifecycleScope)

        binding.rvAlerts.apply {
            layoutManager = mlayoutManager
            adapter = alertsAdapter
        }

        MyApp.getInstanceRepository().getAllAlerts().observe(viewLifecycleOwner) {
            alertsAdapter.submitList(it)
        }


    }

    override fun onPause() {

        super.onPause()
        if (sharedPreferences.getBoolean("tvLocationClicked", false)) {

            sharedPreferencesEditor.putBoolean("tvLocationClicked", false)

            sharedPreferencesEditor.putString(
                "start_date",
                dialogView.findViewById<TextView>(R.id.tv_start_date).text.toString()
            )
            sharedPreferencesEditor.putString(
                "end_date",
                dialogView.findViewById<TextView>(R.id.tv_end_date).text.toString()
            )
            sharedPreferencesEditor.putString(
                "ALERT_ADDRESS",
                dialogView.findViewById<TextView>(R.id.tv_location).text.toString()
            )

            var alartTypeSelected = ""

            if (dialogView.findViewById<RadioButton>(R.id.rbNotification).isChecked) {
                alartTypeSelected = "notification"
            } else if (dialogView.findViewById<RadioButton>(R.id.rbAlarm).isChecked) {
                alartTypeSelected = "alarm"
            }

            sharedPreferencesEditor.putString("alarm_type_selected", alartTypeSelected)

            sharedPreferencesEditor.apply()

            alertDialog.dismiss()
        }

    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        Log.i(TAG, "onViewStateRestored: ")

        if (sharedPreferences.getString("start_date", "")!!
                .isNotEmpty() || sharedPreferences.getString("end_date", "")!!
                .isNotEmpty() || sharedPreferences.getString("alarm_type_selected", "")!!
                .isNotEmpty() || sharedPreferences.getString("ALERT_ADDRESS", "")!!.isNotEmpty()
        ) {
            showAddAlertDialog()
            if (sharedPreferences.getString("start_date", "")!!.isNotEmpty()) {
                dialogView.findViewById<TextView>(R.id.tv_start_date).text =
                    sharedPreferences.getString("start_date", "")
            }
            if (sharedPreferences.getString("end_date", "")!!.isNotEmpty()) {
                dialogView.findViewById<TextView>(R.id.tv_end_date).text =
                    sharedPreferences.getString("end_date", "")
            }
            if (sharedPreferences.getString("ALERT_ADDRESS", "")!!.isNotEmpty()) {
                dialogView.findViewById<TextView>(R.id.tv_location).text =
                    sharedPreferences.getString("ALERT_ADDRESS", "")
            }
            if (sharedPreferences.getString("alarm_type_selected", "") == "notification") {
                dialogView.findViewById<RadioButton>(R.id.rbNotification).isChecked = true
            } else if (sharedPreferences.getString("alarm_type_selected", "") == "alarm") {
                dialogView.findViewById<RadioButton>(R.id.rbAlarm).isChecked = true
            }



            sharedPreferencesEditor.putString("start_date", "")
            sharedPreferencesEditor.putString("end_date", "")
            sharedPreferencesEditor.putString("alarm_type_selected", "")
            sharedPreferencesEditor.putString("ALERT_ADDRESS", "")

            sharedPreferencesEditor.apply()


        }


    }


}