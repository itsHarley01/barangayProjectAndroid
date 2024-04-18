package com.app.barangayguadalupe

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ForgotPassVerification : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userEmail: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_pass_verification, container, false)

        val submit = view.findViewById<Button>(R.id.submitBtn)
        val backbtn = view.findViewById<Button>(R.id.back)
        userEmail = view.findViewById(R.id.emailInput)

        firebaseAuth = FirebaseAuth.getInstance()


        submit.setOnClickListener {
            val email = userEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                checkAllEmailsForRegistration(email) { isRegistered ->
                    if (isRegistered) {
                        sendOTPToEmail(email)
                        showAlert("Check your Email","Password reset sent to email: $email")
                    } else {
                        Toast.makeText(context, "Email is not registered", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        backbtn.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPassVerification_to_login)
        }

        return view
    }

    private fun showAlert(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }


    private fun checkAllEmailsForRegistration(email: String, callback: (Boolean) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val userEmail = userSnapshot.child("email").getValue(String::class.java)
                    if (userEmail == email) {
                        // Email is registered
                        Log.d(TAG, "Email is registered: $email")
                        callback(true)
                        return  // Exit the loop if email is found
                    }
                }
                // Email not registered
                Log.d(TAG, "Email not registered: $email")
                callback(false)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error reading emails from database: ${error.message}")
                callback(false)
            }
        })
    }


    private fun sendOTPToEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showAlert("Check your Email", "Password reset sent to email: $email")
                } else {
                    val exception = task.exception
                    if (exception != null) {
                        Log.e(TAG, "Failed to send Email: ${exception.message}")
                        Toast.makeText(context, "Failed to send: ${exception.message}", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "Failed to send: Unknown error")
                        Toast.makeText(context, "Failed to send: Unknown error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }




    companion object {
        private const val TAG = "ForgotPassVerification"
    }
}
