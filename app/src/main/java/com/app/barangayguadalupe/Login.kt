package com.app.barangayguadalupe

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class Login : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var loadingDialog: AlertDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val emailEditText = view.findViewById<EditText>(R.id.editText)
        val passwordEditText = view.findViewById<EditText>(R.id.editText2)
        val toggleButton = view.findViewById<ToggleButton>(R.id.passwordVisibilityToggle)
        val tv1 = view.findViewById<TextView>(R.id.tv1)
        val forgotbtn = view.findViewById<TextView>(R.id.forgot)

        auth = FirebaseAuth.getInstance()

        toggleButton.visibility = View.GONE

        toggleButton.setOnClickListener {
            // Toggle password visibility
            val inputType = if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            passwordEditText.inputType = inputType


            passwordEditText.setSelection(passwordEditText.text.length)
            val iconRes = if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                R.drawable.eye_vector
            } else {
                R.drawable.eye_slash_vector
            }
            toggleButton.setBackgroundResource(iconRes)
        }

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                toggleButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })

        tv1.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        forgotbtn.setOnClickListener(){
            findNavController().navigate(R.id.action_login_to_forgotPassVerification)
        }

        view.findViewById<MaterialButton>(R.id.loginBtn).setOnClickListener {
            if (!isInternetAvailable()) {
                showNoInternetDialog()
                return@setOnClickListener
            }

            showLoadingDialog()

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (!email.isEmpty() && !password.isEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        loadingDialog.dismiss() // Dismiss loading dialog

                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.let {
                                val userId = it.uid
                                val bundle = Bundle().apply {
                                    putString("userId", userId)
                                }
                                findNavController().navigate(R.id.action_login_to_home2, bundle)
                            }
                        } else {
                            Toast.makeText(requireContext(), "Email or Password incorrect.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                loadingDialog.dismiss() // Dismiss loading dialog
                Toast.makeText(requireContext(), "Please fill all the fields.", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun showLoadingDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.loading_dialog)
        builder.setCancelable(false)
        loadingDialog = builder.create()
        loadingDialog.show()
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("No Internet Connection")
        builder.setMessage("Please check your internet connection and try again.")
        builder.setPositiveButton("Retry") { _, _ ->
            if (isInternetAvailable()) {
                showLoadingDialog()
            } else {
                showNoInternetDialog()
            }
        }
        builder.setNegativeButton("Exit") { _, _ ->
            activity?.finish()
        }
        builder.setCancelable(false)
        builder.show()
    }


    private fun isInternetAvailable(): Boolean {
        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}
