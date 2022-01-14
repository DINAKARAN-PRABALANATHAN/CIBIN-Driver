package com.cibinenterprizes.cibindrivers.Model

class WorkReport {
    var BinId: String? = null
    var CompletionStatus: String? = null
    var Date: String? = null
    var Time: String? = null

    constructor(){

    }

    constructor(BinId: String?, CompletionStatus: String?, Date: String?, Time: String?) {
        this.BinId = BinId
        this.CompletionStatus = CompletionStatus
        this.Date = Date
        this.Time = Time
    }

}