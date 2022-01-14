package com.cibinenterprizes.cibinenterprises.Model

class WorkTodoBinDetails {
    var Area_Village: String? = null
    var Locality: String? = null
    var district: String? = null
    var LoadType: String? = null
    var CollectionPeriod: String? = null
    var Lantitude: String? = null
    var Longitude: String? = null
    var BinId: Int?= null
    var Verification: String? = null

    constructor(){

    }

    constructor(Area_Village: String?, Locality: String?,district: String?, LoadType: String?, CollectionPeriod: String?, Lantitude: String?, Longitude: String?, BinId: Int?, Verification: String?) {
        this.Area_Village = Area_Village
        this.Locality = Locality
        this.district = district
        this.LoadType = LoadType
        this.CollectionPeriod = CollectionPeriod
        this.Lantitude = Lantitude
        this.Longitude = Longitude
        this.BinId = BinId
        this.Verification = Verification
    }
}