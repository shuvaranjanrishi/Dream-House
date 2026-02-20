package com.therishideveloper.dreamhouse.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.therishideveloper.dreamhouse.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.therishideveloper.dreamhouse.ui.theme.tealColor

@Composable
fun PolicyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = stringResource(R.string.title_policy),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(8.dp))
                // Disclaimer Section
                Surface(
                    color = Color.Red.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.disclaimer_text),
                        color = Color.Red,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.Medium,
                        lineHeight = 14.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Steps from 1 to 5
                PolicyStepItem(stringResource(R.string.policy_step_1))
                PolicyStepItem(stringResource(R.string.policy_step_2))
                PolicyStepItem(stringResource(R.string.policy_step_3))
                PolicyStepItem(stringResource(R.string.policy_step_4))
                PolicyStepItem(stringResource(R.string.policy_step_5))

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)

                // Excluded Section (Finishing Items)
                Text(
                    text = stringResource(R.string.policy_excluded),
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_close), color = tealColor)
            }
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = Color.White
    )
}

@Composable
fun PolicyStepItem(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = Color.Black.copy(alpha = 0.8f),
        lineHeight = 18.sp
    )
}