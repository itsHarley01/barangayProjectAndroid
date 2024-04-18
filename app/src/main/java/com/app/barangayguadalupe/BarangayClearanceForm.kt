package com.app.barangayguadalupe

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class BarangayClearanceForm : Fragment() {

    private lateinit var userId: String
    private lateinit var firstNameEditText: EditText
    private lateinit var middleNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var contactEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var reasonEditText: EditText
    private lateinit var radioGroupSex: RadioGroup
    private lateinit var radioGroupMaritalStatus: RadioGroup
    private lateinit var userEmail: String
    private lateinit var userFname: String
    private lateinit var userMname: String
    private lateinit var userLname: String
    private lateinit var contact: String
    private lateinit var age: String
    private lateinit var birthDate: String
    private lateinit var address: String
    private var selectedSex: String = ""
    private var selectedMaritalStatus: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_barangay_clearance_form, container, false)
        val submitButton = view.findViewById<Button>(R.id.button_submit)
        val backbtn = view.findViewById<Button>(R.id.back)
        val check = view.findViewById<CheckBox>(R.id.checkbox)


        firstNameEditText = view.findViewById(R.id.edittext_first_name)
        middleNameEditText = view.findViewById(R.id.edittext_middle_name)
        lastNameEditText = view.findViewById(R.id.edittext_last_name)
        contactEditText = view.findViewById(R.id.edittext_contact)
        emailEditText = view.findViewById(R.id.edittext_email)
        addressEditText = view.findViewById(R.id.edittext_address)
        reasonEditText = view.findViewById(R.id.edittext_reason)
        radioGroupSex = view.findViewById(R.id.radio_group_sex)
        radioGroupMaritalStatus = view.findViewById(R.id.radio_group_marital_status)

        radioGroupSex.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)
            selectedSex = selectedRadioButton.text.toString()
        }

        radioGroupMaritalStatus.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)
            selectedMaritalStatus = selectedRadioButton.text.toString()
        }






        arguments?.let { args ->
            userId = args.getString("userId") ?: ""
            userEmail = args.getString("userEmail") ?: ""
            userFname = args.getString("userF") ?: ""
            userMname = args.getString("userM") ?: ""
            userLname = args.getString("userL") ?: ""
            contact = args.getString("contact") ?: ""
            contact = args.getString("contact") ?: ""
            age = args.getString("age") ?: ""
            birthDate = args.getString("birthDate") ?: ""
            address = args.getString("address") ?: ""

        } ?: run {
            userId = ""
        }
        val bundle = Bundle().apply {
            putString("from", "form")
            putString("userId", userId)
            putString("userEmail", userEmail)
            putString("userF", userFname)
            putString("userM", userMname)
            putString("userL", userLname)
            putString("contact", contact)
            putString("age", age)
            putString("birthDate", birthDate)
            putString("address", address)
        }


        firstNameEditText.setText(userFname)
        middleNameEditText.setText(userMname)
        lastNameEditText.setText(userLname)
        emailEditText.setText(userEmail)
        contactEditText.setText(contact)
        addressEditText.setText(address)



        backbtn.setOnClickListener(){
            findNavController().navigate(R.id.action_barangayClearanceForm_to_home2, bundle)
        }

        submitButton.setOnClickListener {
            if (!firstNameEditText.text.toString().isEmpty()
                && !middleNameEditText.text.toString().isEmpty()
                && !lastNameEditText.text.toString().isEmpty()
                && !emailEditText.text.toString().isEmpty()
                && !contactEditText.text.toString().isEmpty()
                && !reasonEditText.text.toString().isEmpty()
                && !selectedSex.isEmpty()
                && !selectedMaritalStatus.isEmpty()
                && !addressEditText.text.toString().isEmpty()
                && check.isChecked
            ) {
                val firstName = firstNameEditText.text.toString().trim()
                val middleName = middleNameEditText.text.toString().trim()
                val lastName = lastNameEditText.text.toString().trim()
                val contact = contactEditText.text.toString().trim()
                val address = addressEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val reason = reasonEditText.text.toString().trim()
                val sex = selectedSex
                val maritalStatus = selectedMaritalStatus

                // Build the alert dialog
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirm Submission")
                builder.setMessage("Are you sure you want to submit the form?")
                builder.setPositiveButton("Submit") { _, _ ->
                    uploadBarangayClearanceData(firstName, middleName, lastName, sex, maritalStatus, contact, address,email, reason)
                    Toast.makeText(requireContext(), "Form Submitted", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_barangayClearanceForm_to_home2, bundle)
                }
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            } else {
                Toast.makeText(requireContext(), "Please fill all the fields and agree to the terms.", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }

    // Function to get the selected sex
    fun getSelectedSex(): String {
        return selectedSex
    }

    // Function to get the selected marital status
    fun getSelectedMaritalStatus(): String {
        return selectedMaritalStatus
    }

    private fun uploadBarangayClearanceData(
        firstName: String,
        middleName: String,
        lastName: String,
        sex: String,
        maritalStatus: String,
        contact: String,
        address: String,
        email: String,
        reason: String
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val database = FirebaseDatabase.getInstance()

        // Get a reference to the submissions and users nodes
        val submissionsRef = database.getReference("submissions/forms/barangay-clearance/pending")
        val userSubmissionsRef = database.getReference("users/$userId/submissions")

        // Get current date and format it
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.ENGLISH)
        val formattedDate = currentDate.format(formatter)

        // Generate a unique ID for the submission
        val submissionId = submissionsRef.push().key ?: ""

        // Create a HashMap to store submission data
        val submissionData = HashMap<String, Any>()
        submissionData["userId"] = userId
        submissionData["firstName"] = firstName
        submissionData["middleName"] = middleName
        submissionData["lastName"] = lastName
        submissionData["sex"] = sex
        submissionData["maritalStatus"] = maritalStatus
        submissionData["contact"] = contact
        submissionData["email"] = email
        submissionData["address"] = address
        submissionData["reason"] = reason
        submissionData["dateSubmitted"] = formattedDate

        // Upload the submission data to the submissions node
        submissionsRef.child(submissionId).setValue(submissionData)
            .addOnSuccessListener {
                Log.d(TAG, "Submission uploaded successfully")

                // Associate the submission with the user's submissions
                userSubmissionsRef.child(submissionId).setValue(true)
                    .addOnSuccessListener {
                        Log.d(TAG, "Submission associated with user")

                        // Create a placeholder for the submission ID under user's submissions
                        userSubmissionsRef.child(submissionId).setValue("")
                            .addOnSuccessListener {
                                Log.d(TAG, "Submission ID added to user's submissions")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error adding submission ID to user's submissions: ${e.message}")
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error associating submission with user: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error uploading submission: ${e.message}")
            }
    }


    companion object {
        private const val TAG = "BarangayClearanceForm"
    }
}
