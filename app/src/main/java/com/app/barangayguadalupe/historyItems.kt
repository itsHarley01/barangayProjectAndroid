package com.app.barangayguadalupe

data class Submission(
    val contact: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val sex: String = "",
    val middleName: String = "",
    val reason: String = "",
    val complaint: String = "",
    var status: String = "",
    val dateSubmitted: String = "",
    var type: String = "",
    val dateApproved: String = "",
    var age: String = "",
    var birthDate: String = "",
    var maritalStatus: String = "",
    var address: String = "",

) {
    constructor() : this("", "", "", "", "","", "", "","","","","","","",)
}
