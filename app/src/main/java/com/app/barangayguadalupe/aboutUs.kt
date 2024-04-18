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

class aboutUs : Fragment() {

    private lateinit var userId: String
    private lateinit var missionTextView: TextView
    private lateinit var visionTextView: TextView
    private lateinit var goalTextView: TextView
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about_us, container, false)

        val bak = view.findViewById<Button>(R.id.back)

        missionTextView = view.findViewById(R.id.missionTextView)
        visionTextView = view.findViewById(R.id.visionTextView)
        goalTextView = view.findViewById(R.id.goalTextView)

        arguments?.let { args ->
            userId = args.getString("userId") ?: ""
        } ?: run {
            userId = ""
        }

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("page-info").child("mvg")

        // Retrieve data from Firebase Realtime Database
        retrieveMVGInfo()

        bak.setOnClickListener(){
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            findNavController().navigate(R.id.action_aboutUs_to_home2, bundle)
        }

        return view
    }

    private fun retrieveMVGInfo() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val mission = snapshot.child("mission").getValue(String::class.java)
                    val vision = snapshot.child("vision").getValue(String::class.java)
                    val goal = snapshot.child("goal").getValue(String::class.java)

                    // Update TextViews with retrieved data
                    missionTextView.text = mission
                    visionTextView.text = vision
                    goalTextView.text = goal
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }
}
