package com.marketplace.onehour.common.network

object MockDataProvider {
    val sampleHelpers = listOf(
        HelperDto(
            id = "h1",
            name = "Alex Rivera",
            photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
            mainCategory = "Electrical",
            rating = 4.9,
            reviewCount = 124,
            hourlyRate = 35.0,
            distanceKm = 0.8,
            bio = "Certified electrician with 6+ years of experience in quick home fixes, wiring & diagnostics.",
            skills = listOf("Wiring", "Appliance Fitting", "Circuit Repair", "Lighting Installation"),
            isAvailable = true
        ),
        HelperDto(
            id = "h2",
            name = "Sarah Jenkins",
            photoUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
            mainCategory = "Personal Assistance",
            rating = 4.8,
            reviewCount = 89,
            hourlyRate = 28.0,
            distanceKm = 1.2,
            bio = "Organized, prompt personal assistant for errands, administrative help, scheduling & organization.",
            skills = listOf("Errands", "Schedule Admin", "Event Prep", "Data Entry"),
            isAvailable = true
        ),
        HelperDto(
            id = "h3",
            name = "Marcus Vance",
            photoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            mainCategory = "Photography",
            rating = 4.9,
            reviewCount = 67,
            hourlyRate = 50.0,
            distanceKm = 2.4,
            bio = "Professional portrait & event photographer for 1-hour fast turn-around headshots & coverage.",
            skills = listOf("Portrait Photography", "Event Coverage", "Product Shots", "Photo Editing"),
            isAvailable = false
        ),
        HelperDto(
            id = "h4",
            name = "David Chen",
            photoUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
            mainCategory = "Tutoring",
            rating = 4.95,
            reviewCount = 142,
            hourlyRate = 40.0,
            distanceKm = 1.5,
            bio = "STEM Tutor specializing in High School & College Mathematics, Physics & Coding crash courses.",
            skills = listOf("Calculus", "Physics", "Python", "Exam Prep"),
            isAvailable = true
        )
    )

    val sampleBookings = listOf(
        BookingDto(
            id = "b101",
            helperId = "h1",
            helperName = "Alex Rivera",
            serviceName = "Electrical Inspection & Socket Repair",
            status = "On the way",
            scheduledTime = "Today, 02:00 PM",
            totalAmount = 35.0
        ),
        BookingDto(
            id = "b102",
            helperId = "h2",
            helperName = "Sarah Jenkins",
            serviceName = "Grocery & Document Errands",
            status = "Completed",
            scheduledTime = "Yesterday, 11:00 AM",
            totalAmount = 28.0
        )
    )
}
