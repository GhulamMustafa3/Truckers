package com.example.truckers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TruckersDB"
        private const val DATABASE_VERSION = 1

        // CNIC Table
        const val TABLE_CNIC = "CNIC"
        const val COLUMN_ID = "id"
        const val COLUMN_CNIC_NUMBER = "cnic_number"
        const val CNIC_FRONT_IMAGE_PATH = "front_image_path"
        const val CNIC_BACK_IMAGE_PATH = "back_image_path"

        // Vehicle Photo Table
        const val TABLE_VEHICLE_PHOTO = "VehiclePhoto"
        const val COLUMN_VEHICLE_ID = "vehicle_id"
        const val COLUMN_PHOTO_PATH = "photo_path"

        // Driver License Table
        const val TABLE_DRIVER_LICENSE = "DriverLicense"
        const val COLUMN_DRIVER_ID = "driver_id"
        const val COLUMN_LICENSE_IMAGE_PATH = "license_image_path"

        // Vehicle Certificate Table
        const val TABLE_VEHICLE_CERTIFICATE = "VehicleCertificate"
        const val COLUMN_CERTIFICATE_ID = "certificate_id"
        const val VREGISTER_FRONT_IMAGE_PATH = "front_image_path"
        const val VREGISTER_BACK_IMAGE_PATH = "back_image_path"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create CNIC Table
        val createCnicTable = """
            CREATE TABLE $TABLE_CNIC (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CNIC_NUMBER TEXT NOT NULL,
                $CNIC_FRONT_IMAGE_PATH TEXT NOT NULL,
                $CNIC_BACK_IMAGE_PATH TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createCnicTable)

        // Create Vehicle Photo Table
        val createVehiclePhotoTable = """
            CREATE TABLE $TABLE_VEHICLE_PHOTO (
                $COLUMN_VEHICLE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PHOTO_PATH TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createVehiclePhotoTable)

        // Create Driver License Table
        val createDriverLicenseTable = """
            CREATE TABLE $TABLE_DRIVER_LICENSE (
                $COLUMN_DRIVER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LICENSE_IMAGE_PATH TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createDriverLicenseTable)

        // Create Vehicle Certificate Table
        val createVehicleCertificateTable = """
            CREATE TABLE $TABLE_VEHICLE_CERTIFICATE (
                $COLUMN_CERTIFICATE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $VREGISTER_FRONT_IMAGE_PATH TEXT NOT NULL,
                $VREGISTER_BACK_IMAGE_PATH TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createVehicleCertificateTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CNIC")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VEHICLE_PHOTO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DRIVER_LICENSE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VEHICLE_CERTIFICATE")
        onCreate(db)
    }

    // Insert method for Driver License
    fun insertDriverLicenseImage(licenseImagePath: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LICENSE_IMAGE_PATH, licenseImagePath)
        }
        val result = db.insert(TABLE_DRIVER_LICENSE, null, values)
        db.close()
        return result
    }


    // Insert method for Vehicle Certificate
    fun insertVehicleCertificate(frontImagePath: String, backImagePath: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(VREGISTER_FRONT_IMAGE_PATH, frontImagePath)
            put(VREGISTER_BACK_IMAGE_PATH, backImagePath)
        }
        val result = db.insert(TABLE_VEHICLE_CERTIFICATE, null, values)
        db.close()
        return result
    }

    // Update method for Vehicle Certificate
    fun updateVehicleCertificate(certificateId: Int, frontImagePath: String, backImagePath: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(VREGISTER_FRONT_IMAGE_PATH, frontImagePath)
            put(VREGISTER_BACK_IMAGE_PATH, backImagePath)
        }
        val result = db.update(TABLE_VEHICLE_CERTIFICATE, values, "$COLUMN_CERTIFICATE_ID = ?", arrayOf(certificateId.toString()))
        db.close()
        return result > 0
    }

    // Delete method for Vehicle Certificate
    fun deleteVehicleCertificate(certificateId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_VEHICLE_CERTIFICATE, "$COLUMN_CERTIFICATE_ID = ?", arrayOf(certificateId.toString()))
        db.close()
        return result > 0
    }
}
