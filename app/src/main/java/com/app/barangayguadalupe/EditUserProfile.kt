package com.app.barangayguadalupe

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditUserProfile : Fragment() {

    private lateinit var userId: String
    private lateinit var userF: String
    private lateinit var userM: String
    private lateinit var userL: String
    private lateinit var contact: String
    private lateinit var email: String
    private lateinit var age: String
    private lateinit var birthDate: String
    private lateinit var address: String

    private lateinit var textViewFirstName: EditText
    private lateinit var textViewMiddleName: EditText
    private lateinit var textViewLastName: EditText
    private lateinit var textViewEmail: EditText
    private lateinit var textViewContact: EditText
    private lateinit var textViewAge: EditText
    private lateinit var textViewBirthdate: EditText
    private lateinit var textViewAddress: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_user_profile, container, false)

        val bak = view.findViewById<Button>(R.id.back)
        val update = view.findViewById<Button>(R.id.updateBtn)

        textViewFirstName = view.findViewById(R.id.textViewFirstName)
        textViewMiddleName = view.findViewById(R.id.textViewMiddleName)
        textViewLastName = view.findViewById(R.id.textViewLastName)
        textViewEmail = view.findViewById(R.id.textViewEmail)
        textViewContact = view.findViewById(R.id.textViewContact)
        textViewAge = view.findViewById(R.id.textViewAge)
        textViewBirthdate = view.findViewById(R.id.textViewBirthdate)
        textViewAddress = view.findViewById(R.id.textViewAddress)

        bak.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            findNavController().navigate(R.id.action_editUserProfile_to_userProfile, bundle)
        }

        arguments?.let { args ->
            userId = args.getString("userId") ?: ""
            userF = args.getString("userF") ?: ""
            userM = args.getString("userM") ?: ""
            userL = args.getString("userL") ?: ""
            email = args.getString("email") ?: ""
            contact = args.getString("contact") ?: ""
            age = args.getString("age") ?: ""
            birthDate = args.getString("birthDate") ?: ""
            address = args.getString("address") ?: ""
        } ?: run {
            userId = ""
        }

        textViewFirstName.setText(userF)
        textViewMiddleName.setText(userM)
        textViewLastName.setText(userL)
        textViewEmail.setText(email)
        textViewContact.setText(contact)
        textViewAge.setText(age)
        textViewBirthdate.setText(birthDate)
        textViewAddress.setText(address)

        update.setOnClickListener {
            updateUserData()
        }

        return view
    }

    private fun updateUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser?.uid ?: "")
        val updatedUserData = hashMapOf<String, Any>(
            "firstName" to textViewFirstName.text.toString(),
            "middleName" to textViewMiddleName.text.toString(),
            "lastName" to textViewLastName.text.toString(),
            "email" to textViewEmail.text.toString(),
            "contact" to textViewContact.text.toString(),
            "age" to textViewAge.text.toString(),
            "birthDate" to textViewBirthdate.text.toString(),
            "address" to textViewAddress.text.toString(),

        )

        databaseReference.updateChildren(updatedUserData)
            .addOnSuccessListener {
                showSuccessAlert()
            }
            .addOnFailureListener {
                // Handle error
            }
    }


    private fun showSuccessAlert() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Success")
            .setMessage("Updated Successfully")
            .setPositiveButton("Go Back") { _, _ ->
                val bundle = Bundle().apply {
                    putString("userId", userId)
                }
                findNavController().navigate(R.id.action_editUserProfile_to_userProfile, bundle)
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }
}
