package com.app.barangayguadalupe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class userProfile : Fragment() {


    private lateinit var textViewFirstName: TextView
    private lateinit var textViewMiddleName: TextView
    private lateinit var textViewLastName: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewContact: TextView
    private lateinit var textViewAge: TextView
    private lateinit var textViewBirthdate: TextView
    private lateinit var textViewAddress: TextView
    private lateinit var database: DatabaseReference

    private lateinit var userId: String
    private lateinit var userF: String
    private lateinit var userM: String
    private lateinit var userL: String
    private lateinit var contact: String
    private lateinit var email: String
    private lateinit var age: String
    private lateinit var birthDate: String
    private lateinit var useraddress: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        val bak = view.findViewById<Button>(R.id.back)
        val editBtn = view.findViewById<Button>(R.id.editBtn)

        arguments?.let { args ->
            userId = args.getString("userId") ?: ""
        } ?: run {
            userId = ""
        }

        textViewFirstName = view.findViewById(R.id.textViewFirstName)
        textViewMiddleName = view.findViewById(R.id.textViewMiddleName)
        textViewLastName = view.findViewById(R.id.textViewLastName)
        textViewEmail = view.findViewById(R.id.textViewEmail)
        textViewContact = view.findViewById(R.id.textViewContact)
        textViewAge = view.findViewById(R.id.textViewAge)
        textViewBirthdate = view.findViewById(R.id.textViewBirthdate)
        textViewAddress = view.findViewById(R.id.textViewAddress)

        database = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (userId.isNotEmpty()) {
            database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(UserData::class.java)
                        user?.let {
                            userF = it.firstName ?: ""
                            userM = it.middleName ?: ""
                            userL = it.lastName ?: ""
                            contact = it.contact ?: ""
                            email = it.email ?: ""
                            age = it.age ?: ""
                            birthDate = it.birthDate ?: ""
                            useraddress = it.address ?: ""

                            textViewFirstName.text = userF
                            textViewMiddleName.text = userM
                            textViewLastName.text = userL
                            textViewEmail.text = email
                            textViewContact.text = contact
                            textViewAge.text = age
                            textViewBirthdate.text = birthDate
                            textViewAddress.text = useraddress
                        }
                    } else {
                        // Handle case where user data is not found
                        textViewFirstName.text = ""
                        textViewMiddleName.text = ""
                        textViewLastName.text = ""
                        textViewEmail.text = ""
                        textViewContact.text = ""
                        textViewAge.text = ""
                        textViewBirthdate.text = ""
                        textViewAddress.text = ""
                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
        } else {
            // Handle case where userId is empty
        }

        bak.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            findNavController().navigate(R.id.action_userProfile_to_home2, bundle)
        }

        editBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userId", userId)
                putString("contact", contact)
                putString("userF", userF)
                putString("userM", userM)
                putString("userL", userL)
                putString("email", email)
                putString("age", age)
                putString("birthDate", birthDate)
                putString("address", useraddress)
            }
            findNavController().navigate(R.id.action_userProfile_to_editUserProfile, bundle)
        }

        return view
    }
}

data class UserData(
    val email: String? = null,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val contact: String? = null,
    val age: String? = null,
    val birthDate: String? = null,
    val address: String? = null
)
