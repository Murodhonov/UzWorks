package dev.goblingroup.uzworks.networking

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface JobService {

    @POST("api/Job/Create")
    fun create(
        @Body title: String,
        @Body salary: Int,
        @Body gender: String,
        @Body workingTime: String,
        @Body workingSchedule: String,
        @Body deadline: String,
        @Body tgLink: String,
        @Body instagramLink: String,
        @Body tgUserName: String,
        @Body phoneNumber: String,
        @Body benefit: String,
        @Body requirement: String,
        @Body minAge: Int,
        @Body maxAge: Int,
        @Body latitude: Double,
        @Body longitude: Double,
        @Body categoryId: String,
        @Body districtId: String,
    )

    @DELETE("api/Job/Delete/{id}")
    fun delete(
        @Path("id") jobId: String
    )

}