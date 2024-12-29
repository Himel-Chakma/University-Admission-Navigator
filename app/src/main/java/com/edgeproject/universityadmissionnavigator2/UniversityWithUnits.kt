package com.edgeproject.universityadmissionnavigator2

data class UnitWithUniversity(
    val universityName: String,
    val applicationLink: String,
    val unitName: String,
    val unitTitle: String,
    val sscgpa: Double,
    val hscgpa: Double,
    val totalgpa: Double
)