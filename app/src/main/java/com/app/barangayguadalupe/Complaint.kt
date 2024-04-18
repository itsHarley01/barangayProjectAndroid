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
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class Complaint : Fragment() {

    private lateinit var userId: String
    private lateinit var firstNameEditText: EditText
    private lateinit var middleNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var contactEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var complaintEditText: EditText
    private lateinit var userEmail: String
    private lateinit var userFname: String
    private lateinit var userMname: String
    private lateinit var userLname: String
    private lateinit var contact: String
    private lateinit var age: String
    private lateinit var birthDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_complaint, container, false)
        val submitButton = view.findViewById<Button>(R.id.button_submit)
        val backbtn = view.findViewById<Button>(R.id.back)
        val check = view.findViewById<CheckBox>(R.id.checkbox)

        firstNameEditText = view.findViewById(R.id.edittext_first_name)
        middleNameEditText = view.findViewById(R.id.edittext_middle_name)
        lastNameEditText = view.findViewById(R.id.edittext_last_name)
        contactEditText = view.findViewById(R.id.edittext_contact)
        emailEditText = view.findViewById(R.id.edittext_email)
        complaintEditText = view.findViewById(R.id.edittext_complaint)

        arguments?.let { args ->
            userId = args.getString("userId") ?: ""
            userEmail = args.getString("userEmail") ?: ""
            userFname = args.getString("userF") ?: ""
            userMname = args.getString("userM") ?: ""
            userLname = args.getString("userL") ?: ""
            contact = args.getString("contact") ?: ""
            age = args.getString("age") ?: ""
            birthDate = args.getString("birthDate") ?: ""
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
        }


        firstNameEditText.setText(userFname)
        middleNameEditText.setText(userMname)
        lastNameEditText.setText(userLname)
        emailEditText.setText(userEmail)
        contactEditText.setText(contact)

        backbtn.setOnClickListener(){
            findNavController().navigate(R.id.action_complaint_to_home2, bundle)
        }

        submitButton.setOnClickListener {
            if(!firstNameEditText.text.toString().isEmpty()
                && !middleNameEditText.text.toString().isEmpty()
                && !lastNameEditText.text.toString().isEmpty()
                && !emailEditText.text.toString().isEmpty()
                && !contactEditText.text.toString().isEmpty()
                && !complaintEditText.text.toString().isEmpty()
                && check.isChecked){

                val firstName = firstNameEditText.text.toString().trim()
                val middleName = middleNameEditText.text.toString().trim()
                val lastName = lastNameEditText.text.toString().trim()
                val contact = contactEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val complaint = complaintEditText.text.toString().trim()

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirm Submission")
                builder.setMessage("Are you sure you want to submit the form?")
                builder.setPositiveButton("Submit") { _, _ ->
                    uploadComplaintData(firstName, middleName, lastName, contact, email, complaint)
                    Toast.makeText(requireContext(), "Form Submitted", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_complaint_to_home2, bundle)
                }
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }else{
                Toast.makeText(requireContext(), "Please fill all the fields.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun uploadComplaintData(
        firstName: String,
        middleName: String,
        lastName: String,
        contact: String,
        email: String,
        complaint: String
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val database = FirebaseDatabase.getInstance()

        // Get a reference to the submissions and users nodes
        val submissionsRef = database.getReference("submissions/forms/complaints/")
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
        submissionData["contact"] = contact
        submissionData["email"] = email
        submissionData["complaint"] = complaint
        submissionData["dateSubmitted"] = formattedDate

        // Upload the submission data to the submissions node
        submissionsRef.child(submissionId).setValue(submissionData)
            .addOnSuccessListener {
                Log.d(TAG, "Complaint uploaded successfully")

                // Associate the submission with the user's submissions
                userSubmissionsRef.child(submissionId).setValue(true)
                    .addOnSuccessListener {
                        Log.d(TAG, "Complaint associated with user")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error associating complaint with user: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error uploading complaint: ${e.message}")
            }
    }

    companion object {
        private const val TAG = "Complaint"
    }
}
