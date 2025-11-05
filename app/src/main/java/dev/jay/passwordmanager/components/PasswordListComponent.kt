package dev.jay.passwordmanager.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jay.passwordmanager.ui.theme.TextInputBG

@Composable
fun PasswordListComponentItem(
    modifier: Modifier = Modifier,
    title: String,
    onArrowClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(52.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = TextInputBG,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 25.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "*******",
            color = Color.Gray.copy(alpha = 0.6f),
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            contentDescription = "Details",
            tint = Color.Black,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(30.dp)
                .clickable { onArrowClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordListItemPrev() {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PasswordListComponentItem(
            title = "Google",
            onArrowClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
