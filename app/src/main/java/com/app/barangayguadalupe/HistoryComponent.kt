package com.app.barangayguadalupe

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class HistoryComponent : Fragment(), SubmissionAdapter.OnViewButtonClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SubmissionAdapter
    private lateinit var userId: String
    private val submissionList = mutableListOf<Submission>()
    private lateinit var totalHistory: TextView
    private lateinit var pendingNum: TextView
    private lateinit var approvedNum: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_component, container, false)
        totalHistory = view.findViewById(R.id.totalSubNum)
        pendingNum = view.findViewById(R.id.pendingNum)
        approvedNum = view.findViewById(R.id.approvedNum)

        arguments?.let { args ->
            userId = args.getString("userId") ?: ""
        } ?: run {
            userId = ""
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = SubmissionAdapter(submissionList)
        adapter.setOnViewButtonClickListener(this) // Set the click listener to this fragment
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        fetchSubmissionHistory()

        return view
    }


    override fun onViewButtonClick(submission: Submission) {
        // Handle the view button click here
        // Show the popup with the full data of the submission
        showFullDataPopup(submission)
    }

    private fun showFullDataPopup(submission: Submission) {
        val inflater = requireActivity().layoutInflater
        val popupView = inflater.inflate(R.layout.popup_full_data, null)

        // Initialize views in the popup
        val typeTextView = popupView.findViewById<TextView>(R.id.typeTextView)
        val statusTextView = popupView.findViewById<TextView>(R.id.statusTextView)
        val firstNameTextView = popupView.findViewById<TextView>(R.id.firstNameTextView)
        val middleNameTextView = popupView.findViewById<TextView>(R.id.middleNameTextView)
        val lastNameTextView = popupView.findViewById<TextView>(R.id.lastNameTextView)
        val contactTextView = popupView.findViewById<TextView>(R.id.contactTextView)
        val emailTextView = popupView.findViewById<TextView>(R.id.emailTextView)
        val reasonTextView = popupView.findViewById<TextView>(R.id.reasonTextView)
        val complaintTextView = popupView.findViewById<TextView>(R.id.complaintTextView)
        val ageTextView = popupView.findViewById<TextView>(R.id.ageTextView)
        val birthDateTextView = popupView.findViewById<TextView>(R.id.birthDateTextView)
        val maritalStatusTextView = popupView.findViewById<TextView>(R.id.maritalStatusTextView)
        val sexTextView = popupView.findViewById<TextView>(R.id.sexTextView)
        val addresTextView = popupView.findViewById<TextView>(R.id.addressTextView)
        val dateSubmittedTextView = popupView.findViewById<TextView>(R.id.dateSubmittedTextView)

        // Populate data based on the submission type
        when (submission.type) {
            "Barangay Clearance", "Barangay Indigency" -> {
                typeTextView.text = "${submission.type}"
                statusTextView.text = Html.fromHtml("<b>Status:</b> ${submission.status} ${submission.dateApproved}")
                firstNameTextView.text = Html.fromHtml("<b>First Name:</b> ${submission.firstName}")
                middleNameTextView.text = Html.fromHtml("<b>Middle Name:</b> ${submission.middleName}")
                lastNameTextView.text = Html.fromHtml("<b>Last Name:</b> ${submission.lastName}")
                sexTextView.text = Html.fromHtml("<b>Sex:</b> ${submission.sex}")
                maritalStatusTextView.text = Html.fromHtml("<b>Marital Status:</b> ${submission.maritalStatus}")
                contactTextView.text = Html.fromHtml("<b>Contact:</b> ${submission.contact}")
                emailTextView.text = Html.fromHtml("<b>Email:</b> ${submission.email}")
                addresTextView.text = Html.fromHtml("<b>Address:</b> ${submission.address}")
                reasonTextView.text = Html.fromHtml("<b>Reason:</b> ${submission.reason}")
                dateSubmittedTextView.text = "Date Submitted: ${submission.dateSubmitted}"

                // Hide unnecessary TextViews
                //maritalStatusTextView.visibility = View.GONE
                complaintTextView.visibility = View.GONE
                ageTextView.visibility = View.GONE
                birthDateTextView.visibility = View.GONE
            }
            "Complaint" -> {
                typeTextView.text = "${submission.type}"
                firstNameTextView.text = Html.fromHtml("<b>First Name:</b> ${submission.firstName}")
                middleNameTextView.text = Html.fromHtml("<b>Middle Name:</b> ${submission.middleName}")
                lastNameTextView.text = Html.fromHtml("<b>Last Name:</b> ${submission.lastName}")
                contactTextView.text = Html.fromHtml("<b>Contact:</b> ${submission.contact}")
                emailTextView.text = Html.fromHtml("<b>Email:</b> ${submission.email}")
                complaintTextView.text = Html.fromHtml("<b>Complaint:</b> ${submission.complaint}")
                dateSubmittedTextView.text = "Date Submitted: ${submission.dateSubmitted}"

                // Hide unnecessary TextViews
                reasonTextView.visibility = View.GONE
                statusTextView.visibility = View.GONE
                ageTextView.visibility = View.GONE
                birthDateTextView.visibility = View.GONE
                maritalStatusTextView.visibility = View.GONE
            }
//            "PWD Application", "Senior Citizen Application" -> {
//                typeTextView.text = "${submission.type}"
//                statusTextView.text = Html.fromHtml("<b>Status:</b> ${submission.status} ${submission.dateApproved}")
//                firstNameTextView.text = Html.fromHtml("<b>First Name:</b> ${submission.firstName}")
//                middleNameTextView.text = Html.fromHtml("<b>Middle Name:</b> ${submission.middleName}")
//                lastNameTextView.text = Html.fromHtml("<b>Last Name:</b> ${submission.lastName}")
//                ageTextView.text = Html.fromHtml("<b>Age:</b> ${submission.age}")
//                birthDateTextView.text = Html.fromHtml("<b>Birth Date:</b> ${submission.birthDate}")
//                maritalStatusTextView.text = Html.fromHtml("<b>Marital Status:</b> ${submission.maritalStatus}")
//                contactTextView.text = Html.fromHtml("<b>Contact:</b> ${submission.contact}")
//                emailTextView.text = Html.fromHtml("<b>Email:</b> ${submission.email}")
//                addresTextView.text = Html.fromHtml("<b>Address:</b> ${submission.address}")
//
//                loadAndDisplayImage(submission.idFront, frontIdImageView)
//                loadAndDisplayImage(submission.idBack, backIdImageView)
//
//                dateSubmittedTextView.text = "Date Submitted: ${submission.dateSubmitted}"
//
//                // Hide unnecessary TextViews
//                reasonTextView.visibility = View.GONE
//                complaintTextView.visibility = View.GONE
//            }
//
            else -> {
               // Handle other submission types
            }
        }

        // Create the popup dialog
        val popupDialog = AlertDialog.Builder(requireContext())
            .setView(popupView)
            .create()

        // Handle close button click
        val btnClose = popupView.findViewById<Button>(R.id.btnClose)
        btnClose.setOnClickListener {
            popupDialog.dismiss() // Dismiss the popup dialog
        }

        // Show the popup dialog
        popupDialog.show()
    }


    private fun loadAndDisplayImage(imageUrl: String?, imageView: ImageView) {
        imageUrl?.let {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val url = URL(imageUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()

                    val input: InputStream = connection.inputStream
                    val bitmap = BitmapFactory.decodeStream(input)

                    withContext(Dispatchers.Main) {
                        imageView.setImageBitmap(bitmap)
                        imageView.visibility = View.VISIBLE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }





    private fun fetchSubmissionHistory() {
        val userSubmissionsRef = FirebaseDatabase.getInstance().getReference("users/$userId/submissions")
        val submissionRef = FirebaseDatabase.getInstance().getReference("submissions")

        userSubmissionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val totalSubmissions = snapshot.childrenCount.toInt()
                var pendingSubmissions = 0
                var approvedSubmissions = 0

                submissionList.clear()

                snapshot.children.forEach { submissionSnapshot ->
                    val submissionId = submissionSnapshot.key ?: return@forEach

                    // Check if the submission ID exists under different form types
                    val forms = arrayOf("barangay-clearance", "barangay-indigency")
                    for (form in forms) {
                        val formRef = submissionRef.child("forms/$form")

                        formRef.child("pending").child(submissionId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(pendingSnapshot: DataSnapshot) {
                                if (pendingSnapshot.exists()) {
                                    pendingSubmissions++ // Increment pending submissions
                                    val submission = pendingSnapshot.getValue(Submission::class.java)
                                    submission?.let {
                                        it.status = "Pending"
                                        it.type = when (form) {
                                            "barangay-clearance" -> "Barangay Clearance"
                                            "barangay-indigency" -> "Barangay Indigency"
//                                            "pwd" -> "PWD Application"
//                                            "senior" -> "Senior Citizen Application"
                                            else -> "Unknown Type"
                                        }
                                        submissionList.add(it)
                                        adapter.notifyDataSetChanged()
                                    }
                                    updateSubmissionCounts(totalSubmissions, pendingSubmissions, approvedSubmissions)
                                } else {
                                    formRef.child("approved").child(submissionId).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(approvedSnapshot: DataSnapshot) {
                                            if (approvedSnapshot.exists()) {
                                                approvedSubmissions++ // Increment approved submissions
                                                val submission = approvedSnapshot.getValue(Submission::class.java)
                                                submission?.let {
                                                    it.status = "Approved"
                                                    it.type = when (form) {
                                                        "barangay-clearance" -> "Barangay Clearance"
                                                        "barangay-indigency" -> "Barangay Indigency"
//                                                        "pwd" -> "PWD Application"
//                                                        "senior" -> "Senior Citizen Application"
                                                        else -> "Unknown Type"
                                                    }
                                                    submissionList.add(it)
                                                    adapter.notifyDataSetChanged()
                                                }
                                                updateSubmissionCounts(totalSubmissions, pendingSubmissions, approvedSubmissions)
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // Handle onCancelled
                                        }
                                    })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle onCancelled
                            }
                        })
                    }

                    // Separate loop for complaints form
                    val complaintsRef = submissionRef.child("forms/complaints")
                    complaintsRef.child(submissionId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(complaintsSnapshot: DataSnapshot) {
                            val submission = complaintsSnapshot.getValue(Submission::class.java)
                            submission?.let {
                                it.type = "Complaint"
                                submissionList.add(it)
                                adapter.notifyDataSetChanged()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle onCancelled
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })
    }

    private fun updateSubmissionCounts(total: Int, pending: Int, approved: Int) {
        Log.d(TAG, "Total Submissions: $total")
        Log.d(TAG, "Pending Submissions: $pending")
        Log.d(TAG, "Approved Submissions: $approved")

        totalHistory.text = total.toString()
        pendingNum.text = pending.toString()
        approvedNum.text = approved.toString()
    }







}
