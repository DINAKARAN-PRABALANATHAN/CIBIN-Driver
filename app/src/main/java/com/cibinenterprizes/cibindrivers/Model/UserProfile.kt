package com.cibinenterprizes.cibinenterprises.Model

class UserProfile {
    var Username: String? = null
    var EmailId: String? = null
    var Mobile: String? = null
    var ProfilePhoto: String? = null
    var CDid: String? = null
    var District: String? = null

    constructor(){

    }

    constructor(Username: String?, EmailId: String?, Mobile: String?, ProfilePhoto: String?, CDid: String?, District: String) {
        this.Username = Username
        this.EmailId = EmailId
        this.Mobile = Mobile
        this.ProfilePhoto = ProfilePhoto
        this.CDid = CDid
        this.District = District
    }

}