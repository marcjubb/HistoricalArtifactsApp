package com.example.historicalartifactsapp

import java.sql.Timestamp


class Notification(
    var artifactId: String?, var userId: String, var time: com.google.firebase.Timestamp?) {

    constructor(): this("","",null)
}


