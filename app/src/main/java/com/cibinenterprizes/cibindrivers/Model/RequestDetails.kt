package com.cibinenterprizes.cibindrivers.Model

class RequestDetails {
    var Bin1: String?= null
    var Bin2: String?= null
    var Bin3: String?= null
    var Bin4: String?= null
    var Bin5: String?= null
    var Bin6: String?= null
    var Bin7: String?= null
    var Bin8: String?= null
    var Bin9: String?= null
    var Bin10: String?= null
    var Bin11: String?= null
    var Bin12: String?= null
    var DriverId: String?= null
    var DriverMobile: String?= null
    var DriverName: String?= null
    var Verification: String?= null

    constructor(){

    }
    constructor(
        Bin1: String?,
        Bin2: String?,
        Bin3: String?,
        Bin4: String?,
        Bin5: String?,
        Bin6: String?,
        Bin7: String?,
        Bin8: String?,
        Bin9: String?,
        Bin10: String?,
        Bin11: String?,
        Bin12: String?,
        DriverId: String?,
        DriverMobile: String?,
        DriverName: String?,
        Verification: String?
    ) {
        this.Bin1 = Bin1
        this.Bin2 = Bin2
        this.Bin3 = Bin3
        this.Bin4 = Bin4
        this.Bin5 = Bin5
        this.Bin6 = Bin6
        this.Bin7 = Bin7
        this.Bin8 = Bin8
        this.Bin9 = Bin9
        this.Bin10 = Bin10
        this.Bin11 = Bin11
        this.Bin12 = Bin12
        this.DriverId = DriverId
        this.DriverMobile = DriverMobile
        this.DriverName = DriverName
        this.Verification = Verification
    }
}