package com.example.semantic

    import android.content.ContentValues
    import android.content.Context
    import android.database.Cursor
    import android.database.sqlite.SQLiteConstraintException
    import android.database.sqlite.SQLiteDatabase
    import android.database.sqlite.SQLiteException
    import android.database.sqlite.SQLiteOpenHelper

//creating the database logic, extending the SQLiteOpenHelper base class
    class DatabaseHandler(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private val DATABASE_VERSION = 8
            private val DATABASE_NAME = "EmployeeDatabase"
            private val TABLE_EMPLOYEE = "EmployeeTable"
            private val TABLE_DEPENDANTS = "EmployeeDependantsTable"
            private val TABLE_LOGIN = "LoginTable"

            //employee details
            private val KEY_EMP_ID = "emp_id"
            private val KEY_EMP_NAME = "name"
            private val KEY_EMP_EMAIL  = "email"
            private val KEY_EMP_MOBILE  = "mobile_number"
            private val KEY_EMP_DOB = "dob"
            private val KEY_EMP_AGE = "age"
            private val KEY_EMP_GENDER = "gender"
            private val KEY_EMP_DESIGNATION = "designation"
            private val KEY_EMP_DEPARTMENT = "department"


            //employee dependant details
            private val KEY_DEPENDANT_ID = "dependant_id"
            private val KEY_DEPENDANT_EMP_MOB_NUMBER = "employee_mobile"
            private val KEY_DEPENDANT_NAME = "dependant_name"
            private val KEY_DEPENDANT_DOB = "dependant_dob"
            private val KEY_DEPENDANT_RELATION = "dependant_relation"

            //login details
            private val KEY_USER_NAME = "username"
            private val KEY_USER_MOBILE = "usermobile"
        }

        override fun onCreate(db: SQLiteDatabase?) {
            //creating table with fields
            val CREATE_TABLE_LOGIN = ("CREATE TABLE " + TABLE_LOGIN + "("+ KEY_USER_NAME+" TEXT," + KEY_USER_MOBILE+ " TEXT"+")" )
            db?.execSQL(CREATE_TABLE_LOGIN)

            val CREATE_EMP_TABLE = ("CREATE TABLE " + TABLE_EMPLOYEE + "("
                    + KEY_EMP_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT," + KEY_EMP_NAME + " TEXT,"+ KEY_EMP_MOBILE + " TEXT UNIQUE,"
                    + KEY_EMP_EMAIL + " TEXT," + KEY_EMP_DOB + " TEXT,"+ KEY_EMP_AGE + " INTEGER,"+ KEY_EMP_GENDER+ " TEXT,"+ KEY_EMP_DESIGNATION + " TEXT," + KEY_EMP_DEPARTMENT + " TEXT"+ ")")
            db?.execSQL(CREATE_EMP_TABLE)

            val CREATE_DEPENDANT_TABLE = ("CREATE TABLE " + TABLE_DEPENDANTS + "("
                    + KEY_DEPENDANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ KEY_DEPENDANT_EMP_MOB_NUMBER + " TEXT," + KEY_DEPENDANT_NAME + " TEXT,"
                    + KEY_DEPENDANT_DOB + " TEXT," + KEY_DEPENDANT_RELATION + " TEXT"+")")
            db?.execSQL(CREATE_DEPENDANT_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_EMPLOYEE")
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_DEPENDANTS")
            onCreate(db)
        }
        /**
         * Function to insert data of Employee
         */
        fun addEmployee(emp: EmpModelClass): Int {
            val db = this.writableDatabase
             var success = 0
            val contentValues = ContentValues()
            contentValues.put(KEY_EMP_NAME, emp.name) // EmpModelClass Name
            contentValues.put(KEY_EMP_MOBILE, emp.mobile) // EmpModelClass Email
            contentValues.put(KEY_EMP_EMAIL, emp.email) // EmpModelClass Email
            contentValues.put(KEY_EMP_DOB, emp.dob) // EmpModelClass dob
            contentValues.put(KEY_EMP_AGE, emp.age) // EmpModelClass age
            contentValues.put(KEY_EMP_GENDER, emp.gender) // EmpModelClass gender
            contentValues.put(KEY_EMP_DESIGNATION, emp.designation) // EmpModelClass designation
            contentValues.put(KEY_EMP_DEPARTMENT, emp.dept) // EmpModelClass dept

            try {
                // Inserting employee details using insert query.
                 success = db.insertOrThrow(TABLE_EMPLOYEE, null, contentValues).toInt()
                //2nd argument is String containing nullColumnHack
            }catch(e : SQLiteException){
                success = -2
            }
            db.close() // Closing database connection
            return success
        }
    /***
     * Method to read the records from database in form of ArrayList
     */
        fun viewEmployee(mobile : String): ArrayList<EmpModelClass> {

            val empList: ArrayList<EmpModelClass> = ArrayList<EmpModelClass>()

            // Query to select all the records from the table.
            val selectQuery = "SELECT  * FROM $TABLE_EMPLOYEE"

            val db = this.readableDatabase
            // Cursor is used to read the record one by one. Add them to data model class.
            var cursor: Cursor? = null

            try {
                cursor = db.rawQuery(selectQuery, null)

            } catch (e: SQLiteException) {
                db.execSQL(selectQuery)
                return ArrayList()
            }

            var id: Int
            var name: String
            var mobile : String
            var email: String
            var dob : String
            var age : Int
            var gender :String
            var designation :String
            var dept :String

            if (cursor.moveToFirst()) {
                do {
                    id = cursor.getInt(cursor.getColumnIndex(KEY_EMP_ID))
                    name = cursor.getString(cursor.getColumnIndex(KEY_EMP_NAME))
                    mobile = cursor.getString(cursor.getColumnIndex(KEY_EMP_MOBILE))
                    email = cursor.getString(cursor.getColumnIndex(KEY_EMP_EMAIL))
                    dob = cursor.getString(cursor.getColumnIndex(KEY_EMP_DOB))
                    age = cursor.getInt(cursor.getColumnIndex(KEY_EMP_AGE))
                    gender = cursor.getString(cursor.getColumnIndex(KEY_EMP_GENDER))
                    designation = cursor.getString(cursor.getColumnIndex(KEY_EMP_DESIGNATION))
                    dept = cursor.getString(cursor.getColumnIndex(KEY_EMP_DEPARTMENT))

                    val emp = EmpModelClass(id = id, name = name,mobile=mobile ,email = email,dob = dob, age = age,gender = gender, designation = designation, dept = dept)
                    empList.add(emp)

                } while (cursor.moveToNext())
            }
            return empList
        }

        /**
         * Function to update record
         */
        fun updateEmployee(emp: EmpModelClass): Int {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(KEY_EMP_NAME, emp.name) // EmpModelClass Name
            contentValues.put(KEY_EMP_EMAIL, emp.email) // EmpModelClass Email

            // Updating Row
            val success = db.update(TABLE_EMPLOYEE, contentValues, KEY_EMP_ID + "=" + emp.id, null)
            //2nd argument is String containing nullColumnHack

            // Closing database connection
            db.close()
            return success
        }
        /**
         * Function to delete record
         */
        fun deleteEmployee(emp: EmpModelClass): Int {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(KEY_EMP_ID, emp.id) // EmpModelClass id
            // Deleting Row
            val success = db.delete(TABLE_EMPLOYEE, KEY_EMP_ID + "=" + emp.id, null)
            //2nd argument is String containing nullColumnHack

            // Closing database connection
            db.close()
            return success
        }
    fun checkUserAlreadyRegistered(mobileNumber : String) : Int{
        val selectQuery = "SELECT  * FROM $TABLE_EMPLOYEE where $KEY_EMP_MOBILE = '" + mobileNumber+"'"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        var flag = 0
        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
        }
        var count = cursor?.count
        if(count!=null && count>=1)
            flag =1
        else flag = 0
        return flag
    }
    /**
     * Function to insert data of Dependanr
     */
    fun addDependant(dependant: DependantModelClass): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_DEPENDANT_NAME, dependant.dependantName) // Dependant's Name
        contentValues.put(KEY_DEPENDANT_EMP_MOB_NUMBER, dependant.dependantEmpMobile) // Dependant's emp mobile
        contentValues.put(KEY_DEPENDANT_DOB, dependant.dependantDOB) // Dependant's DOB
        contentValues.put(KEY_DEPENDANT_RELATION, dependant.dependantRelation) // Dependant's Relation with employee

        // Inserting employee details using insert query.
        val success = db.insert(TABLE_DEPENDANTS, null, contentValues)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return success
    }
    /***
     * Method to read the records from database in form of ArrayList
     */
    fun viewDependant(mobile : String): ArrayList<DependantModelClass> {

        val dependantList: ArrayList<DependantModelClass> = ArrayList<DependantModelClass>()

        // Query to select all the records from the table.
        val selectQuery = "SELECT  * FROM $TABLE_DEPENDANTS where $KEY_DEPENDANT_EMP_MOB_NUMBER = '" + mobile + "'"

        val db = this.readableDatabase
        // Cursor is used to read the record one by one. Add them to data model class.
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var dob : String
        var relation :String
        var depEmpMobile : String


        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_DEPENDANT_ID))
                name = cursor.getString(cursor.getColumnIndex(KEY_DEPENDANT_NAME))
                dob = cursor.getString(cursor.getColumnIndex(KEY_DEPENDANT_DOB))
                relation = cursor.getString(cursor.getColumnIndex(KEY_DEPENDANT_RELATION))
                depEmpMobile = cursor.getString((cursor.getColumnIndex(KEY_DEPENDANT_EMP_MOB_NUMBER)))
                val emp = DependantModelClass(dependantId = id, dependantName = name,dependantEmpMobile = depEmpMobile,dependantDOB = dob,dependantRelation = relation)
                dependantList.add(emp)

            } while (cursor.moveToNext())
        }
        return dependantList
    }
    /**
     * Function to update dependant
     */
    fun updateDependant(dependant: DependantModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_DEPENDANT_NAME, dependant.dependantName) // EmpModelClass Name
        contentValues.put(KEY_DEPENDANT_DOB, dependant.dependantDOB) // EmpModelClass Email
        contentValues.put(KEY_DEPENDANT_RELATION, dependant.dependantRelation) // EmpModelClass Email

        // Updating Row
        val success = db.update(TABLE_DEPENDANTS, contentValues, KEY_DEPENDANT_ID + "=" + dependant.dependantId, null)
        //2nd argument is String containing nullColumnHack

        // Closing database connection
        db.close()
        return success
    }
    /**
     * Function to delete dependant
     */
    fun deleteDependant(dependant: DependantModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_DEPENDANT_ID, dependant.dependantId) // EmpModelClass id
        // Deleting Row
        val success = db.delete(TABLE_DEPENDANTS, KEY_DEPENDANT_ID + "=" + dependant.dependantId, null)
        //2nd argument is String containing nullColumnHack

        // Closing database connection
        db.close()
        return success
    }

    fun getUsername(mobile : String) : String {
        val selectQuery = "SELECT  * FROM $TABLE_EMPLOYEE where $KEY_EMP_MOBILE = '" + mobile + "'"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        lateinit var name : String
        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return name
        }
        if(cursor.count>0){
        if (cursor.moveToFirst()) {
            do { name = cursor.getString(cursor.getColumnIndex(KEY_EMP_NAME))
            } while (cursor.moveToNext())
        }}
        else name = "nullVal"
        return name
    }

    /***
     * login enrty
     */
    fun addLoginEntry(login : LoginModelClass) : Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_USER_NAME, login.userName) // LoginModelClass Name
        contentValues.put(KEY_USER_MOBILE, login.userMobile) // LoginModelClass Name


        // Inserting employee details using insert query.
        val success = db.insert(TABLE_LOGIN, null, contentValues)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return success
    }
    fun checkIfUserAlreadyLoggedIn() :  ArrayList<LoginModelClass>{
        val selectQuery = "SELECT  * FROM $TABLE_LOGIN"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        val loginDetails: ArrayList<LoginModelClass> = ArrayList<LoginModelClass>()

        try {
            cursor = db.rawQuery(selectQuery, null)

            var uname : String
            var mobile :String
            if (cursor.moveToFirst()) {
                do {
                    uname = cursor.getString(cursor.getColumnIndex(KEY_USER_NAME))
                    mobile = cursor.getString(cursor.getColumnIndex(KEY_USER_MOBILE))
                    val logDetails = LoginModelClass(userName = uname,userMobile = mobile)
                    loginDetails.add(logDetails)
                }while (cursor.moveToNext())
            }
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
        }

        return loginDetails
    }
    fun logOut(){
        val db = this.writableDatabase
        var cursor = db.delete(TABLE_LOGIN,null,null)

    }
}
