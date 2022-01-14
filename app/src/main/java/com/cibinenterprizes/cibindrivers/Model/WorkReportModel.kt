package com.cibinenterprizes.cibinadmin.Model

class WorkReportModel {
    var binId : String? = null
    var completionStatus : String? = null
    var date : String? = null
    var time : String? = null

    constructor(){

    }

    constructor(binId: String?, completionStatus: String?, date: String?, time: String?) {
        this.binId = binId
        this.completionStatus = completionStatus
        this.date = date
        this.time = time
    }

}