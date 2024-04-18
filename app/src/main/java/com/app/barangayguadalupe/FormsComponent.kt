package com.app.barangayguadalupe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

class FormsComponent : Fragment() {

    private lateinit var userId: String
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
        val view = inflater.inflate(R.layout.fragment_forms_component, container, false)

        arguments?.let { args ->
            userId = args.getString("userId") ?: ""
            userEmail = args.getString("userEmail") ?: ""
            userFname = args.getString("userF") ?: ""
            userMname = args.getString("userM") ?: ""
            userLname = args.getString("userL") ?: ""
            contact = args.getString("contact") ?: ""
            age = args.getString("age") ?: ""
            birthDate = args.getString("birthDate") ?: ""
            address = args.getString("address") ?: ""
        } ?: run {
            userId = ""
            Log.e("HomeFragment", "Arguments bundle is null or userId key is missing")
        }

        val brgyClearanceBtn = view.findViewById<Button>(R.id.brgyclearancebtn)
        val barangayIndigency = view.findViewById<Button>(R.id.barangayIndigency)
        //val seniorBtn = view.findViewById<MaterialButton>(R.id.seniorbtn)
        val complaintBtn = view.findViewById<Button>(R.id.complaintbtn)

        brgyClearanceBtn.setOnClickListener {
            val BarangayClearanceForm = BarangayClearanceForm()
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
            BarangayClearanceForm.arguments = bundle
            findNavController().navigate(R.id.action_home2_to_barangayClearanceForm, bundle)
        }
        barangayIndigency.setOnClickListener {
            val IndigencyForm = IndigencyForm()
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
            IndigencyForm.arguments = bundle
            findNavController().navigate(R.id.action_home2_to_indigencyForm, bundle)
        }

//        pwdBtn.setOnClickListener {
//            val pwdApplicationForm = PwdApplicationForm()
//            val bundle = Bundle().apply {
//                putString("userId", userId)
//                putString("userEmail", userEmail)
//                putString("userF", userFname)
//                putString("userM", userMname)
//                putString("userL", userLname)
//                putString("contact", contact)
//                putString("age", age)
//                putString("birthDate", birthDate)
//            }
//            pwdApplicationForm.arguments = bundle
//            findNavController().navigate(R.id.action_home2_to_pwdApplicationForm, bundle)
//        }
//        seniorBtn.setOnClickListener {
//            val seniorApplicationForm = SeniorApplicationForm()
//            val bundle = Bundle().apply {
//                putString("userId", userId)
//                putString("userEmail", userEmail)
//                putString("userF", userFname)
//                putString("userM", userMname)
//                putString("userL", userLname)
//                putString("contact", contact)
//                putString("age", age)
//                putString("birthDate", birthDate)
//            }
//            seniorApplicationForm.arguments = bundle
//            findNavController().navigate(R.id.action_home2_to_seniorApplicationForm, bundle)
//        }
        complaintBtn.setOnClickListener {
            val complaint = Complaint()
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
            complaint.arguments = bundle
            findNavController().navigate(R.id.action_home2_to_complaint, bundle)
        }
        return view
    }
    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.forms_content, fragment)
            .commit()
    }
}
