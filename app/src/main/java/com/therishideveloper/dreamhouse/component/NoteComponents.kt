package com.therishideveloper.dreamhouse.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.data.entity.Note
import com.therishideveloper.dreamhouse.data.model.NoteType
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.util.DateUtils
import com.therishideveloper.dreamhouse.util.NumberUtils

@Composable
fun AddNoteForm(
    noteAmount: String,
    onAmountChange: (String) -> Unit,
    noteDesc: String,
    onDescChange: (String) -> Unit,
    noteType: NoteType,
    onTypeChange: (NoteType) -> Unit,
    noteDate: Long,
    onDateClick: () -> Unit,
    onCalculatorClick: () -> Unit,
    onSave: () -> Unit,
    tealColor: Color
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val msgInvalidAmt = stringResource(R.string.msg_invalid_amount)
    val msgEmptyDesc = stringResource(R.string.msg_empty_desc)
    val msgSaveSuccess = stringResource(R.string.msg_note_saved)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp)
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.label_add_new_note),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = tealColor,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NoteTypeButton(
                label = stringResource(NoteType.DEBT.titleRes),
                isSelected = noteType == NoteType.DEBT,
                activeColor = NoteType.DEBT.color,
                modifier = Modifier.weight(1f),
                onClick = { onTypeChange(NoteType.DEBT) }
            )
            NoteTypeButton(
                label = stringResource(NoteType.RECEIVABLE.titleRes),
                isSelected = noteType == NoteType.RECEIVABLE,
                activeColor = NoteType.RECEIVABLE.color,
                modifier = Modifier.weight(1f),
                onClick = { onTypeChange(NoteType.RECEIVABLE) }
            )
        }

        OutlinedTextField(
            value = noteAmount,
            onValueChange = onAmountChange,
            label = { Text("${stringResource(R.string.amount)} (${stringResource(R.string.currency_symbol)})") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                IconButton(onClick = onCalculatorClick) {
                    Icon(
                        Icons.Default.Calculate,
                        contentDescription = "Calculator",
                        tint = tealColor
                    )
                }
            },
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = noteDesc,
            onValueChange = onDescChange,
            label = { Text(stringResource(R.string.label_note_desc_hint)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = DateUtils.formatToDisplay(context, noteDate),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.date)) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tealColor,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            Surface(
                onClick = { onDateClick() },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = tealColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Pick Date",
                        tint = tealColor
                    )
                }
            }
        }

        Button(
            onClick = {
                val amountValue = noteAmount.toDoubleOrNull()
                if (noteAmount.isEmpty() || amountValue == null || amountValue <= 0) {
                    showToast(context, msgInvalidAmt)
                } else if (noteDesc.trim().isEmpty()) {
                    showToast(context, msgEmptyDesc)
                } else {
                    onSave()
                    showToast(context, msgSaveSuccess)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = tealColor)
        ) {
            Text(
                stringResource(R.string.btn_save_note),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NoteTypeButton(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) activeColor else Color.LightGray.copy(alpha = 0.15f),
        contentColor = if (isSelected) Color.White else Color.Gray
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun NoteItem(note: Note, onDelete: () -> Unit) {
    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val type = NoteType.fromDbKey(note.type)

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.delete_title)) },
            text = { Text(stringResource(R.string.msg_delete_note_confirm)) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteConfirm = false }) {
                    Text(stringResource(R.string.btn_delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(type.color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = type.icon,
                    contentDescription = null,
                    tint = type.color,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(note.description, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    DateUtils.formatToDisplay(context, note.date),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = NumberUtils.formatAmountByLocale(context, note.amount.toString()),
                    color = type.color,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
                Text(
                    text = stringResource(type.titleRes).uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = type.color.copy(alpha = 0.6f)
                )
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Default.DeleteOutline, "Delete", tint = Color.Red.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun NoteDisclaimerCard() {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = tealColor.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, tealColor.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, null, tint = tealColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.disclaimer_note),
                fontSize = 11.sp,
                color = Color.DarkGray,
                lineHeight = 16.sp
            )
        }
    }
}