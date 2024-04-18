package com.app.barangayguadalupe

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.w3c.dom.Text

class Home : Fragment() {

    private lateinit var userId: String
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sideBar: RelativeLayout
    private lateinit var database: DatabaseReference
    private lateinit var userEmail: String
    private lateinit var userFname: String
    private lateinit var userMname: String
    private lateinit var userLname: String
    private lateinit var contact: String
    private lateinit var age: String
    private lateinit var birthDate: String
    private lateinit var address: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val homeBtn = view.findViewById<RelativeLayout>(R.id.homebtn)
        val formsBtn = view.findViewById<RelativeLayout>(R.id.formbtn)
        val historyBtn = view.findViewById<RelativeLayout>(R.id.historybtn)
        val logout = view.findViewById<Button>(R.id.logout)
        val profileBtnSide = view.findViewById<TextView>(R.id.profileBtnSide)
        val aboutSide = view.findViewById<TextView>(R.id.about)
        val contactUs = view.findViewById<TextView>(R.id.contact)

        drawerLayout = view.findViewById(R.id.drawerLayout)
        sideBar = view.findViewById(R.id.sideBar)

        // Firebase Database initialization
        database = FirebaseDatabase.getInstance().reference

        // Retrieve user ID from arguments bundle if available
        arguments?.let { args ->
            userId = args.getString("userId") ?: ""
            val autoNav = args.getString("from")
            userEmail = args.getString("userEmail") ?: ""
            userFname = args.getString("userF") ?: ""
            userMname = args.getString("userM") ?: ""
            userLname = args.getString("userL") ?: ""
            contact = args.getString("contact") ?: ""
            age = args.getString("age") ?: ""
            birthDate = args.getString("birthDate") ?: ""
            address = args.getString("address") ?: ""

            when (autoNav) {
                "form" -> {
                    val formsComponent = FormsComponent()
                    val bundle = Bundle().apply {
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
                    formsComponent.arguments = bundle
                    replaceFragment(formsComponent)
                }
                else -> {
                    val homeComponent = HomeComponent()
                    val homeBundle = Bundle().apply {
                        putString("userId", userId)
                    }
                    homeComponent.arguments = homeBundle
                    replaceFragment(homeComponent)
                }
            }

            // Retrieve user details from Firebase Realtime Database
            database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        userEmail = snapshot.child("email").getValue(String::class.java) ?: ""
                         userFname = snapshot.child("firstName").getValue(String::class.java) ?: ""
                         userMname = snapshot.child("middleName").getValue(String::class.java) ?: ""
                         userLname = snapshot.child("lastName").getValue(String::class.java) ?: ""
                        contact = snapshot.child("contact").getValue(String::class.java) ?: ""
                        age = snapshot.child("age").getValue(String::class.java) ?: ""
                        birthDate = snapshot.child("birthDate").getValue(String::class.java) ?: ""
                        address = snapshot.child("address").getValue(String::class.java) ?: ""
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeFragment", "Error retrieving user details: ${error.message}")
                }
            })
        }

        profileBtnSide.setOnClickListener(){
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            findNavController().navigate(R.id.action_home2_to_userProfile, bundle)
        }

        aboutSide.setOnClickListener(){
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            findNavController().navigate(R.id.action_home2_to_aboutUs, bundle)
        }

        contactUs.setOnClickListener(){
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            findNavController().navigate(R.id.action_home2_to_contactUs, bundle)
        }

        val sidebarBtn = view.findViewById<ToggleButton>(R.id.sidebarbtn)
        sidebarBtn.setOnClickListener {
            if (drawerLayout.isDrawerOpen(sideBar)) {
                drawerLayout.closeDrawer(sideBar)
            } else {
                drawerLayout.openDrawer(sideBar)
            }
        }

        logout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout Confirmation")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { dialog, which ->
                findNavController().navigate(R.id.action_home2_to_login)
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        homeBtn.setOnClickListener {
            val homeComponent = HomeComponent()
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            homeComponent.arguments = bundle
            replaceFragment(homeComponent)
        }

        formsBtn.setOnClickListener {
            val formsComponent = FormsComponent()
            val bundle = Bundle().apply {
                putString("userId", userId)
                putString("userEmail", userEmail)
                putString("userF", userFname)
                putString("userM", userMname)
                putString("userL", userLname)
                putString("contact", contact)
                putString("age", age)
                putString("birthDate", birthDate)
            }
            formsComponent.arguments = bundle
            replaceFragment(formsComponent)
        }

        historyBtn.setOnClickListener {
            val historyComponent = HistoryComponent()
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            historyComponent.arguments = bundle
            replaceFragment(historyComponent)
        }

        return view
    }

    // Replace fragment function
    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.content, fragment)
            .commit()
    }
}
