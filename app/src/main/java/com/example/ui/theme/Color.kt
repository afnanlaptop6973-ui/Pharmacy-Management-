package com.example.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BabyChangingStation
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// =========================================================================
// MEDICAL SHOP THEME PALETTE (Vibrant Pharmacy Green, Clinical Cyan & Coral)
// =========================================================================

// Light Theme: Vibrant Medical Emerald & Pure Clinical Cyan
val PharmacyPrimary = Color(0xFF059669) // Iconic Pharmacy Emerald Green
val PharmacyOnPrimary = Color(0xFFFFFFFF)
val PharmacyPrimaryContainer = Color(0xFFD1FAE5) // Fresh Mint Ice Container
val PharmacyOnPrimaryContainer = Color(0xFF064E3B) // Deep Forest Green

val PharmacySecondary = Color(0xFF0284C7) // Clinical Hospital Cyan / Sterile Blue
val PharmacyOnSecondary = Color(0xFFFFFFFF)
val PharmacySecondaryContainer = Color(0xFFE0F2FE) // Pure Cyan Mist Container
val PharmacyOnSecondaryContainer = Color(0xFF0369A1)

val PharmacyTertiary = Color(0xFFE11D48) // Rx Capsule Coral / Crimson Prescription Red
val PharmacyOnTertiary = Color(0xFFFFFFFF)
val PharmacyTertiaryContainer = Color(0xFFFFE4E6) // Soft Rose Pill Container
val PharmacyOnTertiaryContainer = Color(0xFF881337)

val PharmacyBackgroundLight = Color(0xFFF3FAF6) // Crisp, soothing mint-white clinical canvas
val PharmacyOnBackgroundLight = Color(0xFF0F2922)
val PharmacySurfaceLight = Color(0xFFFFFFFF)
val PharmacyOnSurfaceLight = Color(0xFF0F2922)
val PharmacySurfaceVariantLight = Color(0xFFE6F5EF) // Delicate mint-tinted card surface
val PharmacyOnSurfaceVariantLight = Color(0xFF335C51)
val PharmacyOutlineLight = Color(0xFFA7F3D0) // Mint Border

// Dark Theme: Deep Medical Spruce & Luminous Mint Glow
val PharmacyPrimaryDark = Color(0xFF34D399) // Luminous Mint Green
val PharmacyOnPrimaryDark = Color(0xFF064E3B)
val PharmacyPrimaryContainerDark = Color(0xFF065F46)
val PharmacyOnPrimaryContainerDark = Color(0xFFA7F3D0)

val PharmacySecondaryDark = Color(0xFF38BDF8) // Luminous Medical Cyan
val PharmacyOnSecondaryDark = Color(0xFF082F49)
val PharmacySecondaryContainerDark = Color(0xFF075985)
val PharmacyOnSecondaryContainerDark = Color(0xFFBAE6FD)

val PharmacyTertiaryDark = Color(0xFFFB7185) // Luminous Capsule Rose
val PharmacyOnTertiaryDark = Color(0xFF4C0519)
val PharmacyTertiaryContainerDark = Color(0xFF881337)
val PharmacyOnTertiaryContainerDark = Color(0xFFFFE4E6)

val PharmacyBackgroundDark = Color(0xFF0A1C17) // Deep Medical Pine Slate
val PharmacyOnBackgroundDark = Color(0xFFECFDF5)
val PharmacySurfaceDark = Color(0xFF102A23) // Medical Dark Spruce Surface
val PharmacyOnSurfaceDark = Color(0xFFECFDF5)
val PharmacySurfaceVariantDark = Color(0xFF173A31)
val PharmacyOnSurfaceVariantDark = Color(0xFF99F6E4)
val PharmacyOutlineDark = Color(0xFF275549)

// =========================================================================
// MEDICAL SHOP SPECIFIC ACCENTS & STATUS COLORS
// =========================================================================
val MedicalCrossGreen = Color(0xFF059669)
val MedicalCrossGreenBg = Color(0xFFD1FAE5)

val MedicalCrossRed = Color(0xFFDC2626)
val MedicalCrossRedBg = Color(0xFFFEE2E2)

val StatusHealthy = Color(0xFF16A34A) // Optimal inventory
val StatusHealthyBg = Color(0xFFDCFCE7)
val StatusHealthyBgDark = Color(0xFF052E16)

val StatusWarning = Color(0xFFD97706) // Low stock / Near expiry
val StatusWarningBg = Color(0xFFFEF3C7)
val StatusWarningBgDark = Color(0xFF78350F)

val StatusCritical = Color(0xFFDC2626) // Expired / Out of stock
val StatusCriticalBg = Color(0xFFFEE2E2)
val StatusCriticalBgDark = Color(0xFF7F1D1D)

val StatusInfo = Color(0xFF0284C7) // In transit / Billing records
val StatusInfoBg = Color(0xFFE0F2FE)
val StatusInfoBgDark = Color(0xFF075985)

val StatusPurple = Color(0xFF7C3AED) // Injections / Vaccines
val StatusPurpleBg = Color(0xFFEDE9FE)

val StatusOrange = Color(0xFFEA580C) // Nutritional & Supplements
val StatusOrangeBg = Color(0xFFFFEDD5)

val StatusPink = Color(0xFFDB2777) // Pediatric / Baby Care
val StatusPinkBg = Color(0xFFFCE7F3)

val StatusTeal = Color(0xFF0D9488) // Antiseptic / Ointments
val StatusTealBg = Color(0xFFCCFBF1)

// =========================================================================
// VIBRANT MEDICAL CARD GRADIENTS
// =========================================================================
val MedicalGradientPharmacyGreen = Brush.horizontalGradient(
    colors = listOf(Color(0xFF059669), Color(0xFF0D9488))
)

val MedicalGradientCyanBlue = Brush.horizontalGradient(
    colors = listOf(Color(0xFF0284C7), Color(0xFF2563EB))
)

val MedicalGradientPurpleViolet = Brush.horizontalGradient(
    colors = listOf(Color(0xFF7C3AED), Color(0xFF9333EA))
)

val MedicalGradientAmberOrange = Brush.horizontalGradient(
    colors = listOf(Color(0xFFD97706), Color(0xFFEA580C))
)

val MedicalGradientRoseCoral = Brush.horizontalGradient(
    colors = listOf(Color(0xFFE11D48), Color(0xFFF43F5E))
)

val MedicalGradientEmeraldMint = Brush.horizontalGradient(
    colors = listOf(Color(0xFF10B981), Color(0xFF059669))
)

// =========================================================================
// MEDICAL CATEGORY VISUAL MAPPING (Color, Container, Icon)
// =========================================================================
data class MedicalCategoryVisual(
    val name: String,
    val textColor: Color,
    val containerColor: Color,
    val iconColor: Color,
    val icon: ImageVector
)

fun getMedicineCategoryVisual(category: String): MedicalCategoryVisual {
    val norm = category.trim().lowercase()
    return when {
        norm.contains("antibiotic") || norm.contains("tablet") || norm.contains("capsule") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFF0369A1),
                containerColor = Color(0xFFE0F2FE),
                iconColor = Color(0xFF0284C7),
                icon = Icons.Default.Medication
            )
        }
        norm.contains("syrup") || norm.contains("liquid") || norm.contains("suspension") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFFB45309),
                containerColor = Color(0xFFFEF3C7),
                iconColor = Color(0xFFD97706),
                icon = Icons.Default.Science
            )
        }
        norm.contains("inject") || norm.contains("vaccine") || norm.contains("vial") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFF6D28D9),
                containerColor = Color(0xFFEDE9FE),
                iconColor = Color(0xFF7C3AED),
                icon = Icons.Default.Vaccines
            )
        }
        norm.contains("ointment") || norm.contains("cream") || norm.contains("gel") || norm.contains("topical") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFF0F766E),
                containerColor = Color(0xFFCCFBF1),
                iconColor = Color(0xFF0D9488),
                icon = Icons.Default.Healing
            )
        }
        norm.contains("drop") || norm.contains("eye") || norm.contains("ear") || norm.contains("nasal") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFF0E7490),
                containerColor = Color(0xFFCFFAFE),
                iconColor = Color(0xFF0891B2),
                icon = Icons.Default.Visibility
            )
        }
        norm.contains("ayurved") || norm.contains("herbal") || norm.contains("homeo") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFF15803D),
                containerColor = Color(0xFFDCFCE7),
                iconColor = Color(0xFF16A34A),
                icon = Icons.Default.Spa
            )
        }
        norm.contains("pain") || norm.contains("analgesic") || norm.contains("anti-inflam") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFFBE123C),
                containerColor = Color(0xFFFFE4E6),
                iconColor = Color(0xFFE11D48),
                icon = Icons.Default.LocalHospital
            )
        }
        norm.contains("cardio") || norm.contains("heart") || norm.contains("bp") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFFB91C1C),
                containerColor = Color(0xFFFEE2E2),
                iconColor = Color(0xFFDC2626),
                icon = Icons.Default.Favorite
            )
        }
        norm.contains("diabet") || norm.contains("sugar") || norm.contains("insulin") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFF1D4ED8),
                containerColor = Color(0xFFDBEAFE),
                iconColor = Color(0xFF2563EB),
                icon = Icons.Default.Biotech
            )
        }
        norm.contains("vitamin") || norm.contains("supplement") || norm.contains("nutrition") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFFC2410C),
                containerColor = Color(0xFFFFEDD5),
                iconColor = Color(0xFFEA580C),
                icon = Icons.Default.FitnessCenter
            )
        }
        else -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFF065F46),
                containerColor = Color(0xFFD1FAE5),
                iconColor = Color(0xFF059669),
                icon = Icons.Default.MedicalServices
            )
        }
    }
}

fun getGeneralItemCategoryVisual(category: String): MedicalCategoryVisual {
    val norm = category.trim().lowercase()
    return when {
        norm.contains("baby") || norm.contains("infant") || norm.contains("child") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFFBE185D),
                containerColor = Color(0xFFFCE7F3),
                iconColor = Color(0xFFDB2777),
                icon = Icons.Default.BabyChangingStation
            )
        }
        norm.contains("cosmetic") || norm.contains("beauty") || norm.contains("skin") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFF9D174D),
                containerColor = Color(0xFFFCE7F3),
                iconColor = Color(0xFFE11D48),
                icon = Icons.Default.Face
            )
        }
        norm.contains("equipment") || norm.contains("device") || norm.contains("thermometer") || norm.contains("monitor") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFF1D4ED8),
                containerColor = Color(0xFFDBEAFE),
                iconColor = Color(0xFF2563EB),
                icon = Icons.Default.Biotech
            )
        }
        norm.contains("hygiene") || norm.contains("sanitizer") || norm.contains("soap") || norm.contains("wash") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFF0F766E),
                containerColor = Color(0xFFCCFBF1),
                iconColor = Color(0xFF0D9488),
                icon = Icons.Default.CleanHands
            )
        }
        norm.contains("first aid") || norm.contains("bandage") || norm.contains("cotton") || norm.contains("plaster") -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFFB91C1C),
                containerColor = Color(0xFFFEE2E2),
                iconColor = Color(0xFFDC2626),
                icon = Icons.Default.Healing
            )
        }
        else -> {
            MedicalCategoryVisual(
                name = category,
                textColor = Color(0xFF6D28D9),
                containerColor = Color(0xFFEDE9FE),
                iconColor = Color(0xFF7C3AED),
                icon = Icons.Default.Category
            )
        }
    }
}
