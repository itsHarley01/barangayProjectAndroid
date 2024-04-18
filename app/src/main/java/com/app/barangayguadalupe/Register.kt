package com.app.barangayguadalupe

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class Register : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var noInternetDialog: AlertDialog
    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        loadingIndicator = view.findViewById(R.id.loadingIndicator)

        val tv1 = view.findViewById<TextView>(R.id.tv1)
        val firstname = view.findViewById<EditText>(R.id.firstname)
        val middlename = view.findViewById<EditText>(R.id.middlename)
        val lastname = view.findViewById<EditText>(R.id.lastname)
        val email = view.findViewById<EditText>(R.id.editText)
        val password = view.findViewById<EditText>(R.id.editText2)
        val conPass = view.findViewById<EditText>(R.id.editText3)

        noInternetDialog = createNoInternetDialog()

        tv1.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        view.findViewById<MaterialButton>(R.id.regBtn).setOnClickListener {
            if (!isInternetAvailable()) {
                showNoInternetDialog()
                return@setOnClickListener
            }

            val firstNameText = firstname.text.toString().trim()
            val middleNameText = middlename.text.toString().trim()
            val lastNameText = lastname.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val confirmPasswordText = conPass.text.toString().trim()

            if (firstNameText.isEmpty() || middleNameText.isEmpty() || lastNameText.isEmpty() ||
                emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()
            ) {
                Toast.makeText(requireContext(), "Please fill up all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordText != confirmPasswordText) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoadingDialog()

            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(requireActivity()) { task ->

                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val userRef = database.getReference("users").child(userId ?: "")
                        val userData = User(firstNameText, middleNameText, lastNameText, emailText)
                        userRef.setValue(userData)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    loadingDialog.dismiss()
                                    Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT).show()
                                    clearEditTextFields(firstname, middlename, lastname, email, password, conPass)
                                    showRegistrationConfirmationDialog()
                                } else {
                                    Toast.makeText(requireContext(), "Failed to store user data", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(requireContext(), "Email is already in use", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        return view
    }

    private fun createNoInternetDialog(): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("No Internet Connection")
        builder.setMessage("Please check your internet connection and try again.")
        builder.setPositiveButton("Retry") { _, _ ->
            if (isInternetAvailable()) {
                // Add your retry logic here
            } else {
                showNoInternetDialog()
            }
        }
        builder.setNegativeButton("Exit") { _, _ ->
            activity?.finish()
        }
        builder.setCancelable(false)
        return builder.create()
    }

    private fun showNoInternetDialog() {
        noInternetDialog.show()
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showLoadingDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.loading_dialog)
        builder.setCancelable(false)
        loadingDialog = builder.create()
        loadingDialog.show()
    }

    private fun clearEditTextFields(vararg editTexts: EditText) {
        for (editText in editTexts) {
            editText.text.clear()
        }
    }

    private fun showRegistrationConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Registration Successful")
        builder.setMessage("Registration was successful. Go to login Page?")
        builder.setPositiveButton("Okay") { _, _ ->
            findNavController().navigate(R.id.action_register_to_login)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }
}

data class User(
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val email: String = ""
)

