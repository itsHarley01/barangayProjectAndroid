package com.app.barangayguadalupe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*

class ContactUs : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var addressTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var contactTextView: TextView
    private lateinit var fbTextView: TextView
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_us, container, false)

        val bak = view.findViewById<Button>(R.id.back)

        arguments?.let { args ->
            userId = args.getString("userId") ?: ""
        } ?: run {
            userId = ""
        }

        bak.setOnClickListener(){
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            findNavController().navigate(R.id.action_contactUs_to_home2, bundle)
        }

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("page-info").child("contact")

        // Initialize TextViews
        addressTextView = view.findViewById(R.id.addressTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        contactTextView = view.findViewById(R.id.contactTextView)
        fbTextView = view.findViewById(R.id.fbTextView)

        retrieveContactInfo()

        return view
    }

    private fun retrieveContactInfo() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val address = snapshot.child("address").getValue(String::class.java)
                    val email = snapshot.child("email").getValue(String::class.java)
                    val phone = snapshot.child("phone").getValue(String::class.java)
                    val facebook = snapshot.child("facebook").getValue(String::class.java)

                    addressTextView.text = address
                    emailTextView.text = email
                    contactTextView.text = phone
                    fbTextView.text = facebook
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }
}
