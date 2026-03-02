package com.example.carcleaningapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.UUID
import java.util.Locale

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // Database Info
        private const val DATABASE_NAME = "ServiceAppDatabase.db"
        private const val DATABASE_VERSION = 3 // Current version to force database recreation/seeding

        // User Table
        private const val TABLE_USER = "User"
        private const val COL_USER_ID = "userid"
        private const val COL_USER_USERNAME = "username"
        private const val COL_USER_PASSWORD = "password"
        private const val COL_USER_EMAIL = "email"
        private const val COL_USER_PHONE = "phoneNumber"
        private const val COL_USER_ROLE = "role"
        private const val COL_USER_DATE_REGISTERED = "dateRegistered"

        // Vehicle Table
        private const val TABLE_VEHICLE = "Vehicle"
        private const val COL_VEHICLE_ID = "vehicleid"
        private const val COL_VEHICLE_USER_ID = "userid"
        private const val COL_VEHICLE_MAKE = "make"
        private const val COL_VEHICLE_MODEL = "model"
        private const val COL_VEHICLE_YEAR = "year"

        // Vendor Table
        private const val TABLE_VENDOR = "Vendor"
        private const val COL_VENDOR_ID = "vendorid"
        private const val COL_VENDOR_NAME = "name"
        private const val COL_VENDOR_EMAIL = "email"
        private const val COL_VENDOR_RATING = "rating"
        private const val COL_VENDOR_AVAILABILITY = "availability"

        // Service Table
        private const val TABLE_SERVICE = "Service"
        private const val COL_SERVICE_ID = "serviceid"
        private const val COL_SERVICE_VENDOR_ID = "vendorid"
        private const val COL_SERVICE_PACKAGE_TYPE = "packageType"
        private const val COL_SERVICE_FARE = "fare"
        private const val COL_SERVICE_AVAILABLE_DAYS = "availableDays"

        // Appointment Table
        private const val TABLE_APPOINTMENT = "Appointment"
        private const val COL_APPOINTMENT_ID = "appointmentid"
        private const val COL_APPOINTMENT_SERVICE_ID = "serviceid"
        private const val COL_APPOINTMENT_USER_ID = "userid"
        private const val COL_APPOINTMENT_VEHICLE_ID = "vehicleid"
        private const val COL_APPOINTMENT_TIME = "appointmentTime"
        private const val COL_APPOINTMENT_TOTAL_PRICE = "totalPrice"
        private const val COL_APPOINTMENT_STATUS = "Status"
    }

    // DATABASE CREATION

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("PRAGMA foreign_keys=ON;")

        // CREATE USER TABLE
        val createUserTable = """
            CREATE TABLE $TABLE_USER (
                $COL_USER_ID TEXT PRIMARY KEY,
                $COL_USER_USERNAME TEXT NOT NULL UNIQUE,
                $COL_USER_PASSWORD TEXT NOT NULL,
                $COL_USER_EMAIL TEXT UNIQUE,
                $COL_USER_PHONE TEXT,
                $COL_USER_ROLE TEXT,
                $COL_USER_DATE_REGISTERED LONG
            )
        """.trimIndent()
        db?.execSQL(createUserTable)

        // CREATE VENDOR TABLE
        val createVendorTable = """
            CREATE TABLE $TABLE_VENDOR (
                $COL_VENDOR_ID TEXT PRIMARY KEY,
                $COL_VENDOR_NAME TEXT NOT NULL,
                $COL_VENDOR_EMAIL TEXT,
                $COL_VENDOR_RATING REAL,
                $COL_VENDOR_AVAILABILITY INTEGER
            )
        """.trimIndent()
        db?.execSQL(createVendorTable)

        // CREATE VEHICLE TABLE
        val createVehicleTable = """
            CREATE TABLE $TABLE_VEHICLE (
                $COL_VEHICLE_ID TEXT PRIMARY KEY,
                $COL_VEHICLE_USER_ID TEXT NOT NULL,
                $COL_VEHICLE_MAKE TEXT,
                $COL_VEHICLE_MODEL TEXT,
                $COL_VEHICLE_YEAR TEXT,
                FOREIGN KEY($COL_VEHICLE_USER_ID) REFERENCES $TABLE_USER($COL_USER_ID)
                    ON DELETE CASCADE
            )
        """.trimIndent()
        db?.execSQL(createVehicleTable)

        // CREATE SERVICE TABLE
        val createServiceTable = """
            CREATE TABLE $TABLE_SERVICE (
                $COL_SERVICE_ID TEXT PRIMARY KEY,
                $COL_SERVICE_VENDOR_ID TEXT NOT NULL,
                $COL_SERVICE_PACKAGE_TYPE TEXT,
                $COL_SERVICE_FARE REAL,
                $COL_SERVICE_AVAILABLE_DAYS TEXT,
                FOREIGN KEY($COL_SERVICE_VENDOR_ID) REFERENCES $TABLE_VENDOR($COL_VENDOR_ID)
                    ON DELETE CASCADE
            )
        """.trimIndent()
        db?.execSQL(createServiceTable)

        // CREATE APPOINTMENT TABLE
        val createAppointmentTable = """
            CREATE TABLE $TABLE_APPOINTMENT (
                $COL_APPOINTMENT_ID TEXT PRIMARY KEY,
                $COL_APPOINTMENT_SERVICE_ID TEXT NOT NULL,
                $COL_APPOINTMENT_USER_ID TEXT NOT NULL,
                $COL_APPOINTMENT_VEHICLE_ID TEXT NOT NULL,
                $COL_APPOINTMENT_TIME LONG,
                $COL_APPOINTMENT_TOTAL_PRICE REAL,
                $COL_APPOINTMENT_STATUS TEXT,
                FOREIGN KEY($COL_APPOINTMENT_SERVICE_ID) REFERENCES $TABLE_SERVICE($COL_SERVICE_ID)
                    ON DELETE RESTRICT,
                FOREIGN KEY($COL_APPOINTMENT_USER_ID) REFERENCES $TABLE_USER($COL_USER_ID)
                    ON DELETE RESTRICT,
                FOREIGN KEY($COL_APPOINTMENT_VEHICLE_ID) REFERENCES $TABLE_VEHICLE($COL_VEHICLE_ID)
                    ON DELETE RESTRICT
            )
        """.trimIndent()
        db?.execSQL(createAppointmentTable)

        if (db != null) {
            seedStaticData(db)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_APPOINTMENT")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SERVICE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_VEHICLE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_VENDOR")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    private fun seedStaticData(db: SQLiteDatabase) {
        // DEFAULT ADMIN
        val adminId = UUID.randomUUID().toString()
        val adminValues = ContentValues().apply {
            put(COL_USER_ID, adminId)
            put(COL_USER_USERNAME, "System Admin")
            put(COL_USER_PASSWORD, "admin1")
            put(COL_USER_EMAIL, "admin1@example.com")
            put(COL_USER_PHONE, "0123456789")
            put(COL_USER_ROLE, "admin")
            put(COL_USER_DATE_REGISTERED, System.currentTimeMillis())
        }
        db.insert(TABLE_USER, null, adminValues)

        // DEFAULT CUSTOMER
        val customerId = UUID.randomUUID().toString()
        val customerValues = ContentValues().apply {
            put(COL_USER_ID, customerId)
            put(COL_USER_USERNAME, "Johnny Doey") // Name
            put(COL_USER_PASSWORD, "testing1") // Password
            put(COL_USER_EMAIL, "testing1@example.com") // Email
            put(COL_USER_PHONE, "0198765432")
            put(COL_USER_ROLE, "customer") // Crucial role identifier
            put(COL_USER_DATE_REGISTERED, System.currentTimeMillis())
        }
        db.insert(TABLE_USER, null, customerValues)

        val vendorValues = ContentValues()
        val serviceValues = ContentValues()

        // DEFAULT VENDOR VALUES
        val vendorSeeds = listOf(
            Triple("SparkleJet Auto Spa", 4.8, "sparkle@jet.com"),
            Triple("UrbanShine Mobile", 4.5, "urban@shine.com"),
            Triple("CrystalClean Detailing", 4.9, "crystal@clean.com"),
            Triple("AquaWash Pro", 4.2, "aqua@wash.com")
        )
        val vendorIds = mutableListOf<String>()

        // DEFAULT SERVICES
        val serviceSets = listOf(
            listOf(
                Triple("Standard Exterior Wash", 40.00, "Everyday"),
                Triple("Interior Vacuum Only", 30.00, "Everyday"),
                Triple("Wash & Wax Package", 85.00, "Weekdays"),
                Triple("Engine Bay Degreasing", 55.00, "Weekdays"),
                Triple("Tire & Wheel Treatment", 20.00, "Everyday")
            ),
            listOf(
                Triple("Mobile Exterior Eco-Wash", 65.00, "Weekdays"),
                Triple("Mobile Interior Light Clean", 50.00, "Everyday"),
                Triple("Full Mobile Package", 120.00, "Weekends"),
                Triple("Waterless Exterior Clean", 70.00, "Weekdays"),
                Triple("Headlight Restoration", 90.00, "Weekdays")
            ),
            listOf(
                Triple("Premium Full Detail", 250.00, "Weekends"),
                Triple("Leather Seat Conditioning", 120.00, "Everyday"),
                Triple("Paint Correction Stage 1", 450.00, "Weekdays"),
                Triple("Odor Removal Treatment", 100.00, "Everyday"),
                Triple("Glass Water Spot Removal", 40.00, "Weekdays")
            ),
            listOf(
                Triple("Basic Drive-Through Wash", 25.00, "Everyday"),
                Triple("Ultimate Wash (with Under-carriage)", 60.00, "Everyday"),
                Triple("Truck/SUV Exterior Wash", 70.00, "Weekends"),
                Triple("Interior Air Vent Steam", 35.00, "Weekdays"),
                Triple("Seasonal Wax Application", 95.00, "Weekdays")
            )
        )

        vendorSeeds.forEachIndexed { index, vendorSeed ->
            val vendorId = UUID.randomUUID().toString()
            vendorIds.add(vendorId)

            vendorValues.apply {
                clear()
                put(COL_VENDOR_ID, vendorId)
                put(COL_VENDOR_NAME, vendorSeed.first)
                put(COL_VENDOR_RATING, vendorSeed.second)
                put(COL_VENDOR_EMAIL, vendorSeed.third)
                put(COL_VENDOR_AVAILABILITY, 1) // Default available = 1 (Int for true)
            }
            db.insert(TABLE_VENDOR, null, vendorValues)

            val servicesForVendor = serviceSets[index]
            servicesForVendor.forEach { service ->
                serviceValues.apply {
                    clear()
                    put(COL_SERVICE_ID, UUID.randomUUID().toString())
                    put(COL_SERVICE_VENDOR_ID, vendorId)
                    put(COL_SERVICE_PACKAGE_TYPE, service.first)
                    put(COL_SERVICE_FARE, service.second)
                    put(COL_SERVICE_AVAILABLE_DAYS, service.third)
                }
                db.insert(TABLE_SERVICE, null, serviceValues)
            }
        }
    }

    // CRUD METHODS: USER TABLE ---

    fun insertUser(user: UserDataClass): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_ID, user.userId)
            put(COL_USER_USERNAME, user.username)
            put(COL_USER_PASSWORD, user.password)
            put(COL_USER_EMAIL, user.email)
            put(COL_USER_PHONE, user.phoneNumber)
            put(COL_USER_ROLE, user.role)
            put(COL_USER_DATE_REGISTERED, user.dateRegistered)
        }
        val result = db.insert(TABLE_USER, null, values)
        db.close()
        return result != -1L
    }

    fun authenticateUser(email: String, password: String): String? {
        val db = readableDatabase
        val selection = "$COL_USER_EMAIL = ? AND $COL_USER_PASSWORD = ?"
        val selectionArgs = arrayOf(email, password)
        val columns = arrayOf(COL_USER_ID)

        val cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null)
        var userId: String? = null

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(COL_USER_ID)
            userId = cursor.getString(idIndex)
        }

        cursor.close()
        db.close()
        return userId
    }

    fun readUser(userId: String): UserDataClass? {
        val db = readableDatabase
        val selection = "$COL_USER_ID = ?"
        val selectionArgs = arrayOf(userId)

        val cursor = db.query(TABLE_USER, null, selection, selectionArgs, null, null, null)
        var user: UserDataClass? = null

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(COL_USER_ID)
            val usernameIndex = cursor.getColumnIndexOrThrow(COL_USER_USERNAME)
            val passwordIndex = cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)
            val emailIndex = cursor.getColumnIndexOrThrow(COL_USER_EMAIL)
            val phoneIndex = cursor.getColumnIndexOrThrow(COL_USER_PHONE)
            val roleIndex = cursor.getColumnIndexOrThrow(COL_USER_ROLE)
            val dateIndex = cursor.getColumnIndexOrThrow(COL_USER_DATE_REGISTERED)

            user = UserDataClass(
                cursor.getString(idIndex),
                cursor.getString(usernameIndex),
                cursor.getString(passwordIndex),
                cursor.getString(emailIndex),
                cursor.getString(phoneIndex),
                cursor.getString(roleIndex),
                cursor.getLong(dateIndex)
            )
        }

        cursor.close()
        db.close()
        return user
    }

    fun getUserRole(userId: String): String? {
        val db = readableDatabase
        val selection = "$COL_USER_ID = ?"
        val selectionArgs = arrayOf(userId)
        val columns = arrayOf(COL_USER_ROLE)

        val cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null)
        var role: String? = null

        if (cursor.moveToFirst()) {
            val roleIndex = cursor.getColumnIndexOrThrow(COL_USER_ROLE)
            role = cursor.getString(roleIndex)
        }

        cursor.close()
        db.close()
        return role?.toLowerCase(Locale.ROOT)
    }

    fun readAllUsers(): List<UserDataClass> {
        val userList = mutableListOf<UserDataClass>()
        val db = readableDatabase

        val cursor = db.query(TABLE_USER, null, null, null, null, null, "$COL_USER_DATE_REGISTERED ASC")

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndexOrThrow(COL_USER_ID)
                val usernameIndex = cursor.getColumnIndexOrThrow(COL_USER_USERNAME)
                val passwordIndex = cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)
                val emailIndex = cursor.getColumnIndexOrThrow(COL_USER_EMAIL)
                val phoneIndex = cursor.getColumnIndexOrThrow(COL_USER_PHONE)
                val roleIndex = cursor.getColumnIndexOrThrow(COL_USER_ROLE)
                val dateIndex = cursor.getColumnIndexOrThrow(COL_USER_DATE_REGISTERED)

                val user = UserDataClass(
                    cursor.getString(idIndex),
                    cursor.getString(usernameIndex),
                    cursor.getString(passwordIndex),
                    cursor.getString(emailIndex),
                    cursor.getString(phoneIndex),
                    cursor.getString(roleIndex),
                    cursor.getLong(dateIndex)
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return userList
    }

    fun updateUser(
        userId: String,
        username: String? = null,
        email: String? = null,
        phoneNumber: String? = null
    ): Int {
        val db = writableDatabase
        val values = ContentValues()

        username?.let { values.put(COL_USER_USERNAME, it) }
        email?.let { values.put(COL_USER_EMAIL, it) }
        phoneNumber?.let { values.put(COL_USER_PHONE, it) }

        val rowsAffected = if (values.size() > 0) {
            val whereClause = "$COL_USER_ID = ?"
            val whereArgs = arrayOf(userId)
            db.update(TABLE_USER, values, whereClause, whereArgs)
        } else {
            0
        }
        db.close()
        return rowsAffected
    }

    fun deleteUser(userId: String): Int {
        val db = writableDatabase
        val whereClause = "$COL_USER_ID = ?"
        val whereArgs = arrayOf(userId)

        val rowsAffected = db.delete(TABLE_USER, whereClause, whereArgs)
        db.close()
        return rowsAffected
    }


    // CRUD METHODS: VEHICLE TABLE ---

    fun insertVehicle(vehicle: VehicleDataClass): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_VEHICLE_ID, vehicle.vehicleId)
            put(COL_VEHICLE_USER_ID, vehicle.userId)
            put(COL_VEHICLE_MAKE, vehicle.make)
            put(COL_VEHICLE_MODEL, vehicle.model)
            put(COL_VEHICLE_YEAR, vehicle.year)
        }
        val result = db.insert(TABLE_VEHICLE, null, values)
        db.close()
        return result != -1L
    }

    fun readVehiclesByUserId(userId: String): List<VehicleDataClass> {
        val vehicleList = mutableListOf<VehicleDataClass>()
        val db = readableDatabase
        val selection = "$COL_VEHICLE_USER_ID = ?"
        val selectionArgs = arrayOf(userId)

        val cursor = db.query(
            TABLE_VEHICLE,
            null,
            selection,
            selectionArgs,
            null, null, COL_VEHICLE_ID
        )

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndexOrThrow(COL_VEHICLE_ID)
                val userIdIndex = cursor.getColumnIndexOrThrow(COL_VEHICLE_USER_ID)
                val makeIndex = cursor.getColumnIndexOrThrow(COL_VEHICLE_MAKE)
                val modelIndex = cursor.getColumnIndexOrThrow(COL_VEHICLE_MODEL)
                val yearIndex = cursor.getColumnIndexOrThrow(COL_VEHICLE_YEAR)

                val vehicle = VehicleDataClass(
                    vehicleId = cursor.getString(idIndex),
                    userId = cursor.getString(userIdIndex),
                    make = cursor.getString(makeIndex),
                    model = cursor.getString(modelIndex),
                    year = cursor.getString(yearIndex)
                )
                vehicleList.add(vehicle)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return vehicleList
    }

    fun deleteVehicle(vehicleId: String): Int {
        val db = writableDatabase
        val whereClause = "$COL_VEHICLE_ID = ?"
        val whereArgs = arrayOf(vehicleId)

        val rowsAffected = db.delete(TABLE_VEHICLE, whereClause, whereArgs)
        db.close()
        return rowsAffected
    }

    // CRUD METHODS: VENDOR AND SERVICE

    fun readAllVendors(): List<VendorDataClass> {
        val vendorList = mutableListOf<VendorDataClass>()
        val db = readableDatabase
        val cursor = db.query(TABLE_VENDOR, null, null, null, null, null, "$COL_VENDOR_RATING DESC")

        if (cursor.moveToFirst()) {
            do {
                val vendorId = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENDOR_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENDOR_NAME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENDOR_EMAIL))
                val rating = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_VENDOR_RATING))
                val availabilityInt = cursor.getInt(cursor.getColumnIndexOrThrow(COL_VENDOR_AVAILABILITY))

                // FIX: Convert database INT (1 or 0) to Kotlin BOOLEAN
                val availabilityBoolean = (availabilityInt == 1)

                vendorList.add(VendorDataClass(vendorId, name, email, rating, availabilityBoolean))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return vendorList
    }

    fun insertVendor(name: String, email: String?, rating: Double, availability: Boolean): String? {
        val db = writableDatabase
        val vendorId = UUID.randomUUID().toString()

        // FIX: Convert Kotlin BOOLEAN to database INT (1 or 0)
        val availabilityInt = if (availability) 1 else 0

        val values = ContentValues().apply {
            put(COL_VENDOR_ID, vendorId)
            put(COL_VENDOR_NAME, name)
            put(COL_VENDOR_EMAIL, email)
            put(COL_VENDOR_RATING, rating)
            put(COL_VENDOR_AVAILABILITY, availabilityInt) // Use the Int value
        }
        val result = db.insert(TABLE_VENDOR, null, values)
        db.close()
        return if (result != -1L) vendorId else null
    }

    fun updateVendor(vendor: VendorDataClass): Int {
        val db = writableDatabase

        // FIX: Convert Kotlin BOOLEAN to database INT (1 or 0)
        val availabilityInt = if (vendor.availability) 1 else 0

        val values = ContentValues().apply {
            put(COL_VENDOR_NAME, vendor.name)
            put(COL_VENDOR_EMAIL, vendor.email)
            put(COL_VENDOR_RATING, vendor.rating)
            put(COL_VENDOR_AVAILABILITY, availabilityInt) // Use the Int value
        }
        val whereClause = "$COL_VENDOR_ID = ?"
        val whereArgs = arrayOf(vendor.vendorId)
        val rowsAffected = db.update(TABLE_VENDOR, values, whereClause, whereArgs)
        db.close()
        return rowsAffected
    }

    fun deleteVendor(vendorId: String): Int {
        val db = writableDatabase
        // NOTE: This will fail if the vendor has associated SERVICES due to the RESTRICT constraint.
        val rowsAffected = db.delete(TABLE_VENDOR, "$COL_VENDOR_ID = ?", arrayOf(vendorId))
        db.close()
        return rowsAffected
    }

    fun readAllServices(): List<ServiceDataClass> {
        val serviceList = mutableListOf<ServiceDataClass>()
        val db = readableDatabase
        val joinQuery = """
            SELECT
                S.${COL_SERVICE_ID}, S.${COL_SERVICE_VENDOR_ID}, S.${COL_SERVICE_PACKAGE_TYPE},
                S.${COL_SERVICE_FARE}, S.${COL_SERVICE_AVAILABLE_DAYS},
                V.${COL_VENDOR_NAME}, V.${COL_VENDOR_RATING}
            FROM ${TABLE_SERVICE} AS S
            INNER JOIN ${TABLE_VENDOR} AS V
            ON S.${COL_SERVICE_VENDOR_ID} = V.${COL_VENDOR_ID}
            ORDER BY V.${COL_VENDOR_RATING} DESC
        """.trimIndent()

        val cursor = db.rawQuery(joinQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val serviceId = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_ID))
                val vendorId = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_VENDOR_ID))
                val vendorName = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENDOR_NAME))
                val packageType = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_PACKAGE_TYPE))
                val fare = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_SERVICE_FARE))
                val availableDays = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_AVAILABLE_DAYS))
                val vendorRating = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_VENDOR_RATING))

                val service = ServiceDataClass(
                    serviceId = serviceId,
                    vendorId = vendorId,
                    vendorName = vendorName,
                    packageType = packageType,
                    fare = fare,
                    availableDays = availableDays,
                    vendorRating = vendorRating
                )
                serviceList.add(service)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return serviceList
    }

    // CRUD METHODS: APPOINTMENT TABLE

    fun insertAppointment(
        userId: String,
        serviceId: String,
        vehicleId: String,
        appointmentTime: Long,
        totalPrice: Double,
        status: String = "CONFIRMED"
    ): Long {
        val db = this.writableDatabase
        val appointmentId = UUID.randomUUID().toString()

        val contentValues = ContentValues().apply {
            put(COL_APPOINTMENT_ID, appointmentId)
            put(COL_APPOINTMENT_USER_ID, userId)
            put(COL_APPOINTMENT_SERVICE_ID, serviceId)
            put(COL_APPOINTMENT_VEHICLE_ID, vehicleId)
            put(COL_APPOINTMENT_TIME, appointmentTime)
            put(COL_APPOINTMENT_TOTAL_PRICE, totalPrice)
            put(COL_APPOINTMENT_STATUS, status.toUpperCase(Locale.ROOT))
        }

        val result = db.insert(TABLE_APPOINTMENT, null, contentValues)
        db.close()
        return if (result != -1L) 1L else -1L
    }

    fun readAppointmentsByUserId(userId: String): List<AppointmentDataClass> {
        val appointmentList = mutableListOf<AppointmentDataClass>()
        val db = readableDatabase
        val joinQuery = """
            SELECT
                A.${COL_APPOINTMENT_ID}, A.${COL_APPOINTMENT_TIME}, A.${COL_APPOINTMENT_TOTAL_PRICE}, A.${COL_APPOINTMENT_STATUS},
                A.${COL_APPOINTMENT_USER_ID}, A.${COL_APPOINTMENT_SERVICE_ID}, A.${COL_APPOINTMENT_VEHICLE_ID},
                S.${COL_SERVICE_PACKAGE_TYPE}, V.${COL_VENDOR_NAME}, 
                L.${COL_VEHICLE_MAKE}, L.${COL_VEHICLE_MODEL}
            FROM ${TABLE_APPOINTMENT} AS A
            INNER JOIN ${TABLE_SERVICE} AS S ON A.${COL_APPOINTMENT_SERVICE_ID} = S.${COL_SERVICE_ID}
            INNER JOIN ${TABLE_VENDOR} AS V ON S.${COL_SERVICE_VENDOR_ID} = V.${COL_VENDOR_ID}
            INNER JOIN ${TABLE_VEHICLE} AS L ON A.${COL_APPOINTMENT_VEHICLE_ID} = L.${COL_VEHICLE_ID}
            WHERE A.${COL_APPOINTMENT_USER_ID} = ?
            ORDER BY A.${COL_APPOINTMENT_TIME} DESC
        """.trimIndent()

        val selectionArgs = arrayOf(userId)
        val cursor = db.rawQuery(joinQuery, selectionArgs)

        if (cursor.moveToFirst()) {
            do {
                val appointmentId = cursor.getString(cursor.getColumnIndexOrThrow(COL_APPOINTMENT_ID))
                val appointmentTime = cursor.getLong(cursor.getColumnIndexOrThrow(COL_APPOINTMENT_TIME))
                val totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_APPOINTMENT_TOTAL_PRICE))
                val status = cursor.getString(cursor.getColumnIndexOrThrow(COL_APPOINTMENT_STATUS))
                val serviceId = cursor.getString(cursor.getColumnIndexOrThrow(COL_APPOINTMENT_SERVICE_ID))
                val vehicleId = cursor.getString(cursor.getColumnIndexOrThrow(COL_APPOINTMENT_VEHICLE_ID))

                val packageType = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_PACKAGE_TYPE))
                val vendorName = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENDOR_NAME))

                val vehicleMake = cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICLE_MAKE))
                val vehicleModel = cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICLE_MODEL))

                val appointment = AppointmentDataClass(
                    appointmentId = appointmentId,
                    userId = userId, // Passed in function call
                    serviceId = serviceId,
                    vehicleId = vehicleId,
                    appointmentTime = appointmentTime,
                    totalPrice = totalPrice,
                    status = status,
                    vendorName = vendorName,
                    packageType = packageType,
                    vehicleMakeModel = "$vehicleMake $vehicleModel"
                )
                appointmentList.add(appointment)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return appointmentList
    }

    fun deleteAppointment(appointmentId: String): Int {
        val db = this.writableDatabase

        val whereClause = "$COL_APPOINTMENT_ID = ?"
        val whereArgs = arrayOf(appointmentId)

        val rowsAffected = db.delete(TABLE_APPOINTMENT, whereClause, whereArgs)
        db.close()
        return rowsAffected
    }
    fun insertService(
        vendorId: String,
        packageType: String,
        fare: Double,
        availableDays: String
    ): String? {
        val db = writableDatabase
        val serviceId = UUID.randomUUID().toString()

        val values = ContentValues().apply {
            put(COL_SERVICE_ID, serviceId)
            put(COL_SERVICE_VENDOR_ID, vendorId)
            put(COL_SERVICE_PACKAGE_TYPE, packageType)
            put(COL_SERVICE_FARE, fare)
            put(COL_SERVICE_AVAILABLE_DAYS, availableDays)
        }
        val result = db.insert(TABLE_SERVICE, null, values)
        db.close()
        return if (result != -1L) serviceId else null
    }
    fun updateService(service: ServiceDataClass): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_SERVICE_VENDOR_ID, service.vendorId)
            put(COL_SERVICE_PACKAGE_TYPE, service.packageType)
            put(COL_SERVICE_FARE, service.fare)
            put(COL_SERVICE_AVAILABLE_DAYS, service.availableDays)
        }
        val whereClause = "$COL_SERVICE_ID = ?"
        val whereArgs = arrayOf(service.serviceId)
        val rowsAffected = db.update(TABLE_SERVICE, values, whereClause, whereArgs)
        db.close()
        return rowsAffected
    }
    fun deleteService(serviceId: String): Int {
        val db = writableDatabase
        val rowsAffected = db.delete(TABLE_SERVICE, "$COL_SERVICE_ID = ?", arrayOf(serviceId))
        db.close()
        return rowsAffected
    }
}