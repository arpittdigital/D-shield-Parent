package com.d_shield_parent.RoomDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface CustomerDao {

    @Insert
    suspend fun insertCustomer(customer: addCustomerList)

    @Update
    suspend fun updateCustomer(customer: addCustomerList)

    @Delete
    suspend fun deleteCustomer(customer: addCustomerList)

    @Query("SELECT * FROM customer_table ORDER BY id DESC")
    suspend fun getAllCustomers(): List<addCustomerList>

    @Query("SELECT * FROM customer_table WHERE id = :id")
    suspend fun getCustomerById(id: Int): addCustomerList?
}