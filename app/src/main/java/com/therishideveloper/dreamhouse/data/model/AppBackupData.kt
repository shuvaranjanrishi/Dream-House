package com.therishideveloper.dreamhouse.data.model

import com.therishideveloper.dreamhouse.data.entity.Note
import com.therishideveloper.dreamhouse.data.entity.Transaction

data class AppBackupData(
    val transactions: List<Transaction>,
    val notes: List<Note>
)