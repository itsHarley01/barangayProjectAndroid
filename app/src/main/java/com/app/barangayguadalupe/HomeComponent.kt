package com.app.barangayguadalupe

import android.app.AlertDialog
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

interface SidebarToggleListener {
    fun onSidebarToggle(isOpen: Boolean)
}

class HomeComponent : Fragment() {
    private lateinit var userId: String
    private lateinit var fullNameTextView: TextView
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var loadingDialog: AlertDialog
    private lateinit var database: FirebaseDatabase
    private lateinit var newsRef: DatabaseReference
    private lateinit var usersRef: DatabaseReference
    private var newsList: MutableList<NewsItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homehome, container, false)

        val tp = view.findViewById<LinearLayout>(R.id.profileBB)
        val ap = view.findViewById<LinearLayout>(R.id.about)
        val con = view.findViewById<LinearLayout>(R.id.contact)

        fullNameTextView = view.findViewById<TextView>(R.id.namefield)

        newsRecyclerView = view.findViewById(R.id.newsRecyclerView)
        val imageLoader = InputStreamImageLoader()
        newsAdapter = NewsAdapter(newsList, imageLoader)
        newsRecyclerView.adapter = newsAdapter
        newsRecyclerView.layoutManager = LinearLayoutManager(activity)

        userId = arguments?.getString("userId") ?: ""

        if (userId.isNotEmpty()) {
            showLoadingDialog()
            fetchData()
        } else {
            Log.e("HomeComponent", "User ID is empty")
        }

        tp.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            findNavController().navigate(R.id.action_home2_to_userProfile, bundle)
        }
        ap.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            findNavController().navigate(R.id.action_home2_to_aboutUs, bundle)
        }
        con.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            findNavController().navigate(R.id.action_home2_to_contactUs, bundle)
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

    private fun fetchData() {
        val connectivityManager = requireActivity().getSystemService(ConnectivityManager::class.java)
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            database = FirebaseDatabase.getInstance()
            newsRef = database.getReference("page-info/news")
            usersRef = database.getReference("users")

            newsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(newsSnapshot: DataSnapshot) {
                    newsList.clear()
                    for (newsData in newsSnapshot.children) {
                        val imageUrl = newsData.child("image").getValue(String::class.java) ?: ""
                        val title = newsData.child("title").getValue(String::class.java) ?: ""
                        val description = newsData.child("description").getValue(String::class.java) ?: ""
                        val datePublished = newsData.child("datePublished").getValue(String::class.java) ?: ""

                        val newsItem = NewsItem(imageUrl, title, description, datePublished)
                        newsList.add(newsItem)
                    }
                    newsAdapter.notifyDataSetChanged()
                    loadingDialog.dismiss()
                }

                override fun onCancelled(newsError: DatabaseError) {
                    loadingDialog.dismiss()
                    Log.e("HomeComponent", "News database error: ${newsError.message}")
                }
            })

            usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(userSnapshot: DataSnapshot) {
                    if (userSnapshot.exists()) {
                        val firstName = userSnapshot.child("firstName").getValue(String::class.java)
                        val lastName = userSnapshot.child("lastName").getValue(String::class.java)
                        if (firstName != null && lastName != null) {
                            val fullName = "$firstName $lastName"
                            fullNameTextView.text = fullName
                            Log.d("HomeComponent", "Full Name: $fullName")
                        } else {
                            fullNameTextView.text = "Name not available"
                            Log.e("HomeComponent", "First name or last name is null")
                        }
                    } else {
                        fullNameTextView.text = "User not found"
                        Log.d("HomeComponent", "User not found in database")
                    }
                }

                override fun onCancelled(userError: DatabaseError) {
                    fullNameTextView.text = "Database error"
                    Log.e("HomeComponent", "User database error: ${userError.message}")
                }
            })
        } else {
            loadingDialog.dismiss()
            showNoInternetDialog()
        }
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("No Internet Connection")
        builder.setMessage("Please check your internet connection and try again.")
        builder.setPositiveButton("Retry") { _, _ ->
            showLoadingDialog()
            fetchData()
        }
        builder.setNegativeButton("Exit") { _, _ ->
            activity?.finish()
        }
        builder.setCancelable(false)
        builder.show()
    }
}