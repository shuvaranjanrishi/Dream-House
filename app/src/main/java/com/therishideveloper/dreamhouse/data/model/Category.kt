//package com.therishideveloper.dreamhouse.data.model
//
//import android.content.Context
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.TrendingUp
//import androidx.compose.material.icons.filled.*
//import androidx.compose.ui.graphics.vector.ImageVector
//import com.therishideveloper.dreamhouse.R

package com.therishideveloper.dreamhouse.data.model

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.therishideveloper.dreamhouse.R

enum class Category(
    val dbKey: String,
    val titleRes: Int,
    val descriptionRes: Int, // নতুন প্রপার্টি
    val icon: ImageVector,
    val section: String
) {
    // --- INCOME ---
    OWN_SAVINGS("OWN_SAVINGS", R.string.cat_savings, R.string.desc_savings, Icons.Default.AccountBalanceWallet, "INCOME"),
    BANK_LOAN("BANK_LOAN", R.string.cat_bank_loan, R.string.desc_bank_loan, Icons.Default.AccountBalance, "INCOME"),
    PARTNER_INVEST("PARTNER_INVEST", R.string.cat_invest, R.string.desc_invest, Icons.Default.Handshake, "INCOME"),
    RELATIVE_LOAN("RELATIVE_LOAN", R.string.cat_relative_loan, R.string.desc_relative_loan, Icons.Default.Groups, "INCOME"),
    OTHER_INCOME("OTHER_INCOME", R.string.cat_others_income, R.string.desc_others_income, Icons.Default.MoreHoriz, "INCOME"),

    // --- STRUCTURAL ---
    ROD("ROD", R.string.cat_rod, R.string.desc_rod, Icons.Default.Reorder, "STRUCTURAL"),
    CEMENT("CEMENT", R.string.cat_cement, R.string.desc_cement, Icons.Default.Inventory, "STRUCTURAL"),
    BRICKS("BRICKS", R.string.cat_bricks, R.string.desc_bricks, Icons.Default.GridView, "STRUCTURAL"),
    SAND("SAND", R.string.cat_sand, R.string.desc_sand, Icons.Default.Grain, "STRUCTURAL"),
    STONE("STONE", R.string.cat_stone, R.string.desc_stone, Icons.Default.Category, "STRUCTURAL"),

    // --- LABOR ---
    MASON_LABOR("MASON_LABOR", R.string.cat_mason, R.string.desc_mason, Icons.Default.Engineering, "LABOR"),
    SHUTTERING("SHUTTERING", R.string.cat_shuttering, R.string.desc_shuttering, Icons.Default.Architecture, "LABOR"),
    EXCAVATION("EXCAVATION", R.string.cat_excavation, R.string.desc_excavation, Icons.Default.Agriculture, "LABOR"),
    ENGINEER_FEE("ENGINEER_FEE", R.string.cat_engineer, R.string.desc_engineer, Icons.Default.AssignmentInd, "LABOR"),

    // --- FINISHING ---
    TILES("TILES", R.string.cat_tiles, R.string.desc_tiles, Icons.Default.Dashboard, "FINISHING"),
    PAINT("PAINT", R.string.cat_paint, R.string.desc_paint, Icons.Default.FormatPaint, "FINISHING"),
    ELECTRICAL("ELECTRICAL", R.string.cat_electric, R.string.desc_electric, Icons.Default.ElectricBolt, "FINISHING"),
    PLUMBING("PLUMBING", R.string.cat_plumbing, R.string.desc_plumbing, Icons.Default.Plumbing, "FINISHING"),
    DOORS_WINDOWS("DOORS_WINDOWS", R.string.cat_doors, R.string.desc_doors, Icons.Default.MeetingRoom, "FINISHING"),

    // --- MISCELLANEOUS ---
    TRANSPORT("TRANSPORT", R.string.cat_transport, R.string.desc_transport, Icons.Default.LocalShipping, "MISCELLANEOUS"),
    UTILITY("UTILITY_BILL", R.string.cat_utility, R.string.desc_utility, Icons.Default.ElectricalServices, "MISCELLANEOUS"),
    LEGAL_FEE("LEGAL_FEE", R.string.cat_legal, R.string.desc_legal, Icons.Default.Gavel, "MISCELLANEOUS"),
    SECURITY("SECURITY", R.string.cat_security, R.string.desc_security, Icons.Default.AdminPanelSettings, "MISCELLANEOUS"),
    OTHERS("OTHERS_EXPENSE", R.string.cat_others_expense, R.string.desc_others_expense, Icons.Default.MoreHoriz, "MISCELLANEOUS");

    companion object {
        fun fromDbKey(key: String?): Category = Category.entries.find { it.dbKey == key } ?: OTHERS
        fun getIncomeCategories() = entries.filter { it.section == "INCOME" }
        fun getExpenseCategories() = entries.filter { it.section != "INCOME" }
        fun getCategoriesBySection(section: String) = entries.filter { it.section == section }
        fun getAllSections(): List<String> = listOf("INCOME", "STRUCTURAL", "LABOR", "FINISHING", "MISCELLANEOUS")
    }
}

//enum class Category(
//    val dbKey: String,
//    val titleRes: Int,
//    val icon: ImageVector,
//    val section: String // এখানে সেকশন অ্যাড করা হয়েছে
//) {
//    // ==========================================
//    // --- ১. আয় (INCOME) ---
//    // ==========================================
//    OWN_SAVINGS("OWN_SAVINGS", R.string.cat_savings, Icons.Default.AccountBalanceWallet, "INCOME"),
//    BANK_LOAN("BANK_LOAN", R.string.cat_bank_loan, Icons.Default.AccountBalance, "INCOME"),
//    PARTNER_INVEST("PARTNER_INVEST", R.string.cat_invest, Icons.Default.Handshake, "INCOME"),
//    RELATIVE_LOAN("RELATIVE_LOAN", R.string.cat_relative_loan, Icons.Default.Groups, "INCOME"),
//    OTHER_INCOME("OTHER_INCOME", R.string.cat_others, Icons.Default.MoreHoriz, "INCOME"),
//
//    // ==========================================
//    // --- ২. ব্যয় (EXPENSE) - ৪টি সেকশন ---
//    // ==========================================
//
//    // --- সেকশন ১: কাঠামো (Structural) ---
//    ROD("ROD", R.string.cat_rod, Icons.Default.Reorder, "STRUCTURAL"),
//    CEMENT("CEMENT", R.string.cat_cement, Icons.Default.Inventory, "STRUCTURAL"),
//    BRICKS("BRICKS", R.string.cat_bricks, Icons.Default.GridView, "STRUCTURAL"),
//    SAND("SAND", R.string.cat_sand, Icons.Default.Grain, "STRUCTURAL"),
//    STONE("STONE", R.string.cat_stone, Icons.Default.Category, "STRUCTURAL"),
//
//    // --- সেকশন ২: শ্রম ও সেবা (Labor & Services) ---
//    MASON_LABOR("MASON_LABOR", R.string.cat_mason, Icons.Default.Engineering, "LABOR"),
//    SHUTTERING("SHUTTERING", R.string.cat_shuttering, Icons.Default.Architecture, "LABOR"),
//    EXCAVATION("EXCAVATION", R.string.cat_excavation, Icons.Default.Agriculture, "LABOR"),
//    ENGINEER_FEE("ENGINEER_FEE", R.string.cat_engineer, Icons.Default.AssignmentInd, "LABOR"),
//
//    // --- সেকশন ৩: ফিনিশিং (Finishing) ---
//    TILES("TILES", R.string.cat_tiles, Icons.Default.Dashboard, "FINISHING"),
//    PAINT("PAINT", R.string.cat_paint, Icons.Default.FormatPaint, "FINISHING"),
//    ELECTRICAL("ELECTRICAL", R.string.cat_electric, Icons.Default.ElectricBolt, "FINISHING"),
//    PLUMBING("PLUMBING", R.string.cat_plumbing, Icons.Default.Plumbing, "FINISHING"),
//    DOORS_WINDOWS("DOORS_WINDOWS", R.string.cat_doors, Icons.Default.MeetingRoom, "FINISHING"),
//
//    // --- সেকশন ৪: বিবিধ (Miscellaneous) ---
//    TRANSPORT("TRANSPORT", R.string.cat_transport, Icons.Default.LocalShipping, "MISCELLANEOUS"),
//    UTILITY("UTILITY_BILL", R.string.cat_utility, Icons.Default.ElectricalServices, "MISCELLANEOUS"),
//    LEGAL_FEE("LEGAL_FEE", R.string.cat_legal, Icons.Default.Gavel, "MISCELLANEOUS"),
//    SECURITY("SECURITY", R.string.cat_security, Icons.Default.AdminPanelSettings, "MISCELLANEOUS"),
//
//    // --- ডিফল্ট ---
//    OTHERS("OTHERS", R.string.cat_others, Icons.Default.MoreHoriz, "MISCELLANEOUS");
//
//    companion object {
//
//        fun fromDbKey(key: String?): Category {
//            return Category.entries.find { it.dbKey == key } ?: OTHERS
//        }
//
//        fun getIncomeCategories() = entries.filter { it.section == "INCOME" }
//
//        fun getExpenseCategories() = entries.filter { it.section != "INCOME" }
//
//        fun getCategoriesBySection(section: String) = entries.filter { it.section == section }
//
//        fun findDbKeyByLocalizedName(query: String, context: Context): String? {
//            return Category.entries.find {
//                context.getString(it.titleRes).contains(query, ignoreCase = true)
//            }?.dbKey
//        }
//
//        fun getAllSections(): List<String> {
//            return entries.map { it.section }.distinct().filter { it != "INCOME" }.sorted() + "INCOME" // INCOME সেকশনটি সবার উপরে দেখানোর জন্য
//        }
//    }
//}
