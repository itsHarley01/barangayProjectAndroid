package com.app.barangayguadalupe

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class SeniorApplicationForm : Fragment() {

    private lateinit var userId: String
    private lateinit var loadingDialog: AlertDialog
    private lateinit var firstNameEditText: EditText
    private lateinit var middleNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var birthdateEditText: EditText
    private lateinit var contactEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var backbtn: Button
    private lateinit var radioGroupMaritalStatus: RadioGroup
    private lateinit var userEmail: String
    private lateinit var userFname: String
    private lateinit var userMname: String
    private lateinit var userLname: String
    private lateinit var contact: String
    private lateinit var age: String
    private lateinit var birthDate: String

    private lateinit var uploadFrontIdButton: Button
    private lateinit var uploadBackIdButton: Button

    private lateinit var frontIdImageView: ImageView
    private lateinit var backIdImageView: ImageView

    private val PICK_FRONT_IMAGE_REQUEST = 1
    private val PICK_BACK_IMAGE_REQUEST = 2

    private lateinit var frontIdUri: Uri
    private lateinit var backIdUri: Uri

    private val bundle: Bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_senior_application_form, container, false)
        val submitButton = view.findViewById<Button>(R.id.button_submit)
        val checkbox = view.findViewById<CheckBox>(R.id.checkbox)

        firstNameEditText = view.findViewById(R.id.edittext_first_name)
        middleNameEditText = view.findViewById(R.id.edittext_middle_name)
        lastNameEditText = view.findViewById(R.id.edittext_last_name)
        ageEditText = view.findViewById(R.id.edittext_last_age)
        birthdateEditText = view.findViewById(R.id.edittext_last_birthdate)
        contactEditText = view.findViewById(R.id.edittext_contact)
        addressEditText = view.findViewById(R.id.edittext_address)
        emailEditText = view.findViewById(R.id.edittext_email)
        radioGroupMaritalStatus = view.findViewById(R.id.radio_group_marital_status)
        backbtn = view.findViewById(R.id.back)

        uploadFrontIdButton = view.findViewById(R.id.button_upload_image)
        uploadBackIdButton = view.findViewById(R.id.button_upload_imageb)

        frontIdImageView = view.findViewById(R.id.image_preview)
        backIdImageView = view.findViewById(R.id.image_previewb)


        arguments?.let { args ->
            userId = args.getString("userId") ?: ""
            userEmail = args.getString("userEmail") ?: ""
            userFname = args.getString("userF") ?: ""
            userMname = args.getString("userM") ?: ""
            userLname = args.getString("userL") ?: ""
            contact = args.getString("contact") ?: ""
            age = args.getString("age") ?: ""
            birthDate = args.getString("birthDate") ?: ""
        } ?: run {
            userId = ""
        }
        bundle.apply {
            putString("from", "form")
            putString("userId", userId)
            putString("userEmail", userEmail)
            putString("userF", userFname)
            putString("userM", userMname)
            putString("userL", userLname)
            putString("contact", contact)
            putString("age", age)
            putString("birthDate", birthDate)
        }

        firstNameEditText.setText(userFname)
        middleNameEditText.setText(userMname)
        lastNameEditText.setText(userLname)
        emailEditText.setText(userEmail)
        contactEditText.setText(contact)
        ageEditText.setText(age)
        birthdateEditText.setText(birthDate)

//        backbtn.setOnClickListener(){
//            findNavController().navigate(R.id.action_seniorApplicationForm_to_home2, bundle)
//        }

        uploadFrontIdButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_FRONT_IMAGE_REQUEST)
        }

        uploadBackIdButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_BACK_IMAGE_REQUEST)
        }


        submitButton.setOnClickListener {
            if (validateForm() && checkbox.isChecked) {
                val firstName = firstNameEditText.text.toString().trim()
                val middleName = middleNameEditText.text.toString().trim()
                val lastName = lastNameEditText.text.toString().trim()
                val age = ageEditText.text.toString().trim()
                val birthDate = birthdateEditText.text.toString().trim()
                val contact = contactEditText.text.toString().trim()
                val address = addressEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val maritalStatus = getSelectedMaritalStatus()

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirm Submission")
                builder.setMessage("Are you sure you want to submit the form?")
                builder.setPositiveButton("Submit") { _, _ ->
                    showLoadingDialog()
                    uploadFormDataToFirebase(
                        firstName, middleName, lastName, age, birthDate, contact, address, email, maritalStatus
                    )
                }
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            } else {
                Toast.makeText(requireContext(), "Please fill all the fields and agree to terms.", Toast.LENGTH_SHORT).show()
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

    private fun validateForm(): Boolean {
        val firstName = firstNameEditText.text.toString().trim()
        val middleName = middleNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val age = ageEditText.text.toString().trim()
        val birthDate = birthdateEditText.text.toString().trim()
        val contact = contactEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val maritalStatus = getSelectedMaritalStatus()

        val frontImageValid = ::frontIdUri.isInitialized && frontIdUri != Uri.EMPTY
        val backImageValid = ::backIdUri.isInitialized && backIdUri != Uri.EMPTY

        return firstName.isNotEmpty() &&
                middleName.isNotEmpty() &&
                lastName.isNotEmpty() &&
                age.isNotEmpty() &&
                birthDate.isNotEmpty() &&
                contact.isNotEmpty() &&
                address.isNotEmpty() &&
                email.isNotEmpty() &&
                maritalStatus.isNotEmpty() &&
                frontImageValid &&
                backImageValid
    }



    private fun getSelectedMaritalStatus(): String {
        val selectedRadioButtonId = radioGroupMaritalStatus.checkedRadioButtonId
        return when (selectedRadioButtonId) {
            R.id.radio_single -> "Single"
            R.id.radio_married -> "Married"
            R.id.radio_divorced -> "Divorced"
            else -> "Unknown"
        }
    }

    private fun uploadFormDataToFirebase(
        firstName: String,
        middleName: String,
        lastName: String,
        age: String,
        birthDate: String,
        contact: String,
        address: String,
        email: String,
        maritalStatus: String
    ) {
        // Validate form including image uploads
        if (validateForm() && frontIdUri != null && backIdUri != null) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val database = FirebaseDatabase.getInstance()
            val storageRef = FirebaseStorage.getInstance().reference

            val pendingSubmissionsRef = database.getReference("submissions/forms/senior/pending")
            val userSubmissionsRef = database.getReference("users/$userId/submissions/")

            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.ENGLISH)
            val formattedDate = currentDate.format(formatter)

            // Upload front ID image to Firebase Storage
            val frontIdImageRef = storageRef.child("Forms/senior/${userId}_front_id.jpg")
            frontIdImageRef.putFile(frontIdUri!!)
                .addOnSuccessListener { frontIdUploadTask ->
                    frontIdImageRef.downloadUrl.addOnSuccessListener { frontIdUri ->
                        // Upload back ID image to Firebase Storage
                        val backIdImageRef = storageRef.child("Forms/senior/${userId}_back_id.jpg")
                        backIdImageRef.putFile(backIdUri!!)
                            .addOnSuccessListener { backIdUploadTask ->
                                backIdImageRef.downloadUrl.addOnSuccessListener { backIdUri ->
                                    // Image upload successful, proceed with form data upload
                                    val submissionData = HashMap<String, Any>()
                                    submissionData["userId"] = userId
                                    submissionData["firstName"] = firstName
                                    submissionData["middleName"] = middleName
                                    submissionData["lastName"] = lastName
                                    submissionData["age"] = age
                                    submissionData["birthDate"] = birthDate
                                    submissionData["contact"] = contact
                                    submissionData["address"] = address
                                    submissionData["email"] = email
                                    submissionData["maritalStatus"] = maritalStatus
                                    submissionData["dateSubmitted"] = formattedDate
                                    submissionData["idFront"] = frontIdUri.toString()
                                    submissionData["idBack"] = backIdUri.toString()

                                    val pendingSubmissionRef = pendingSubmissionsRef.push()
                                    pendingSubmissionRef.setValue(submissionData)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Senior application uploaded successfully to pending submissions")

                                            userSubmissionsRef.child(pendingSubmissionRef.key ?: "").setValue("")
                                                .addOnSuccessListener {
                                                    Log.d(TAG, "Senior application associated with user's submissions")
                                                    loadingDialog.dismiss()
                                                    Toast.makeText(requireContext(), "Form Submitted", Toast.LENGTH_SHORT).show()
                                                    //findNavController().navigate(R.id.action_seniorApplicationForm_to_home2, bundle)
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.e(TAG, "Error associating senior application with user's submissions: ${e.message}")
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG, "Error uploading senior application to pending submissions: ${e.message}")
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error uploading back ID image: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error uploading front ID image: ${e.message}")
                }
        } else {
            Toast.makeText(requireContext(), "Please fill all the fields and upload both ID images.", Toast.LENGTH_SHORT).show()
        }
    }



    companion object {
        private const val TAG = "SeniorApplicationForm"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FRONT_IMAGE_REQUEST) {
                data?.data?.let { uri ->
                    frontIdUri = uri
                    Log.d(TAG, "Front ID image URI: $frontIdUri")
                    try {
                        val imageStream: InputStream? = requireContext().contentResolver.openInputStream(frontIdUri)
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        frontIdImageView.setImageBitmap(selectedImage)
                    } catch (e: FileNotFoundException) {
                        Log.e(TAG, "Error loading front ID image: ${e.message}")
                    }
                } ?: run {
                    Log.e(TAG, "Front ID image selection returned null data")
                }
            } else if (requestCode == PICK_BACK_IMAGE_REQUEST) {
                data?.data?.let { uri ->
                    backIdUri = uri
                    Log.d(TAG, "Back ID image URI: $backIdUri")
                    try {
                        val imageStream: InputStream? = requireContext().contentResolver.openInputStream(backIdUri)
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        backIdImageView.setImageBitmap(selectedImage)
                    } catch (e: FileNotFoundException) {
                        Log.e(TAG, "Error loading back ID image: ${e.message}")
                    }
                } ?: run {
                    Log.e(TAG, "Back ID image selection returned null data")
                }
            }
        } else {
            Log.e(TAG, "Image selection failed with resultCode: $resultCode")
        }
    }


}
