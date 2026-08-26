package com.marketplace.onehour.common.network

object MockDataProvider {
    val sampleCategories = listOf(
        CategoryDto("cat_1", "Personal Assistance", "Calendar, organization, tasks", "person"),
        CategoryDto("cat_2", "Electrical Specialist", "Wiring, fixtures & appliance repairs", "bolt"),
        CategoryDto("cat_3", "Tutoring", "Math, Science & Languages", "school"),
        CategoryDto("cat_4", "Photography", "Portraits, events & product shots", "camera_alt"),
        CategoryDto("cat_5", "Home Repairs", "Mounting, plumbing & carpentry", "build"),
        CategoryDto("cat_6", "errands & delivery", "Groceries, pickups & document delivery", "local_shipping"),
        CategoryDto("cat_7", "design/creative", "Graphics, UI logos & media editing", "palette"),
        CategoryDto("cat_8", "business/professional", "Tech support, legal & admin help", "business_center")
    )

    val sampleHelpers = listOf(
        HelperDto(
            id = "h1",
            name = "Alex Rivera",
            photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
            mainCategory = "Electrical Specialist",
            rating = 4.9,
            reviewCount = 124,
            hourlyRate = 500.0, // ₹500/hr
            distanceKm = 0.8,
            bio = "Certified electrician with 6+ years of experience in quick home fixes, wiring & diagnostics.",
            skills = listOf("Wiring", "Appliance Fitting", "Circuit Repair", "Lighting Installation"),
            isAvailable = true
        ),
        HelperDto(
            id = "h2",
            name = "Sarah Jenkins",
            photoUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
            mainCategory = "errands & delivery",
            rating = 4.8,
            reviewCount = 89,
            hourlyRate = 350.0, // ₹350/hr
            distanceKm = 1.2,
            bio = "Organized, prompt assistant for grocery runs, document delivery & local errands.",
            skills = listOf("Errands", "Document Delivery", "Grocery Pickup", "Admin Help"),
            isAvailable = true
        ),
        HelperDto(
            id = "h3",
            name = "Marcus Vance",
            photoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            mainCategory = "Photography",
            rating = 4.9,
            reviewCount = 67,
            hourlyRate = 1200.0, // ₹1200/hr
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
            hourlyRate = 600.0, // ₹600/hr
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
            customerId = "u101",
            customerName = "Priya Sharma",
            serviceName = "Electrical Inspection & Socket Repair",
            status = "Completed",
            paymentStatus = "paid",
            scheduledTime = "Today, 02:00 PM",
            totalAmount = 500.0, // ₹500
            orderId = "order_mock_b101",
            paymentId = "pay_mock_b101_succ"
        ),
        BookingDto(
            id = "b102",
            helperId = "h2",
            helperName = "Sarah Jenkins",
            customerId = "u102",
            customerName = "Ananya Verma",
            serviceName = "Grocery & Document Errands",
            status = "Completed",
            paymentStatus = "paid",
            scheduledTime = "Yesterday, 11:00 AM",
            totalAmount = 350.0, // ₹350
            orderId = "order_mock_b102",
            paymentId = "pay_mock_b102_succ"
        ),
        BookingDto(
            id = "b103",
            helperId = "h3",
            helperName = "Marcus Vance",
            customerId = "u101",
            customerName = "Priya Sharma",
            serviceName = "Portrait Headshots Session",
            status = "Requested",
            paymentStatus = "pending",
            scheduledTime = "Today, 05:00 PM",
            totalAmount = 1200.0, // ₹1200
            orderId = "order_mock_b103",
            paymentId = null
        ),
        BookingDto(
            id = "b104",
            helperId = "h1",
            helperName = "Alex Rivera",
            customerId = "u103",
            customerName = "Rahul Mehta",
            serviceName = "Circuit Breaker Diagnostic",
            status = "Cancelled",
            paymentStatus = "failed",
            scheduledTime = "2 days ago",
            totalAmount = 600.0, // ₹600
            orderId = "order_mock_b104",
            paymentId = null
        ),
        BookingDto(
            id = "b105",
            helperId = "h4",
            helperName = "David Chen",
            customerId = "u102",
            customerName = "Ananya Verma",
            serviceName = "Math & Physics Crash Course",
            status = "Completed",
            paymentStatus = "paid",
            scheduledTime = "3 days ago",
            totalAmount = 600.0, // ₹600
            orderId = "order_mock_b105",
            paymentId = "pay_mock_b105_succ"
        )
    )
}
