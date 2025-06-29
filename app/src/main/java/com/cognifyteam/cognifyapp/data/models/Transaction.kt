package com.cognifyteam.cognifyapp.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// =====================================================================
// 1. Model untuk Jaringan (JSON)
// =====================================================================

/**
 * Model JSON untuk respons API paling luar (generik).
 */
@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "status")
    val status: String,
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val data: ApiDataResponse<T>
)

/**
 * Model JSON untuk wrapper data (generik).
 */
@JsonClass(generateAdapter = true)
data class ApiDataResponse<T>(
    @Json(name = "message")
    val message: String,
    @Json(name = "data")
    val data: T
)


/**
 * Model JSON spesifik untuk sebuah transaksi yang diterima dari API.
 */
@JsonClass(generateAdapter = true)
data class TransactionJson(
    @Json(name = "order_id")
    val orderId: String,

    @Json(name = "gross_amount")
    val grossAmount: Int,

    @Json(name = "status")
    val status: String,

    @Json(name = "createdAt")
    val createdAt: String,

    // Data Course yang ter-nest di dalam JSON
    @Json(name = "Course")
    val course: TransactionCourseInfoJson,

    @Json(name = "payment_token")
    val paymentToken: String
)

/**
 * Model JSON untuk info kursus yang ada di dalam TransactionJson.
 */
@JsonClass(generateAdapter = true)
data class TransactionCourseInfoJson(
    @Json(name = "course_name")
    val name: String
)


// =====================================================================
// 2. Model untuk Database Lokal (Entity)
// =====================================================================

/**
 * Model Entity untuk disimpan di database Room.
 * Data di-flatten (diratakan) untuk kemudahan penyimpanan.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "order_id")
    val orderId: String,

    @ColumnInfo(name = "user_id")
    val userId: String, // Untuk memfilter per pengguna

    @ColumnInfo(name = "course_name")
    val courseName: String,

    @ColumnInfo(name = "gross_amount")
    val grossAmount: Int,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "createdAt")
    val createdAt: String,

    @ColumnInfo(name = "payment_token")
    val paymentToken: String
)

// =====================================================================
// 3. Model untuk Domain/UI
// =====================================================================

/**
 * Model Domain yang bersih, digunakan di seluruh aplikasi (UI, ViewModel).
 * Model ini tidak memiliki anotasi Room atau Moshi.
 */
@Parcelize
data class Transaction(
    val orderId: String,
    val courseName: String,
    val grossAmount: Int,
    val status: String,
    val createdAt: String,
    val paymentToken: String
) : Parcelable {

    // Fungsi helper untuk UI, agar tidak ada logika format di Composable
    val formattedAmount: String
        get() {
            val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            return format.format(this.grossAmount)
        }

    val formattedDate: String
        get() {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(this.createdAt)
                val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                outputFormat.format(date!!)
            } catch (e: Exception) {
                this.createdAt
            }
        }

    val displayStatus: Pair<String, String> // Pair(Text, Type)
        get() {
            return when (this.status.lowercase()) {
                "settlement", "capture" -> "Success" to "Success"
                "pending" -> "Pending" to "Pending"
                "expire", "cancel", "deny" -> "Failed" to "Failed"
                else -> this.status.replaceFirstChar { it.uppercase() } to "Other"
            }
        }

    // Companion object berisi fungsi-fungsi mapper
    companion object {
        /**
         * Mengubah dari Model JSON (API) ke Model Domain (UI).
         */
        fun fromJson(json: TransactionJson): Transaction {
            return Transaction(
                orderId = json.orderId,
                courseName = json.course.name,
                grossAmount = json.grossAmount,
                status = json.status,
                createdAt = json.createdAt,
                paymentToken = json.paymentToken
            )
        }

        /**
         * Mengubah dari Model Entity (Database) ke Model Domain (UI).
         */
        fun fromEntity(entity: TransactionEntity): Transaction {
            return Transaction(
                orderId = entity.orderId,
                courseName = entity.courseName,
                grossAmount = entity.grossAmount,
                status = entity.status,
                createdAt = entity.createdAt,
                paymentToken = entity.paymentToken
            )
        }
    }

    /**
     * Mengubah dari Model Domain (UI) ke Model Entity (Database).
     * Membutuhkan userId karena Entity harus tahu milik siapa transaksi ini.
     */
    fun toEntity(userId: String): TransactionEntity {
        return TransactionEntity(
            orderId = this.orderId,
            userId = userId, // Menyimpan userId
            courseName = this.courseName,
            grossAmount = this.grossAmount,
            status = this.status,
            createdAt = this.createdAt,
            paymentToken = this.paymentToken
        )
    }
}