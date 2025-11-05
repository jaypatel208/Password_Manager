package dev.jay.passwordmanager.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import dev.jay.passwordmanager.ui.theme.BlackButton
import dev.jay.passwordmanager.ui.theme.RedButton

@Composable
fun ButtonCommon(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun EditButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    ButtonCommon(
        modifier = modifier,
        text = text,
        backgroundColor = BlackButton,
        textColor = Color.White,
        onClick = onClick
    )
}

@Composable
fun DeleteButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    ButtonCommon(
        modifier = modifier,
        text = "Delete",
        backgroundColor = RedButton,
        textColor = Color.White,
        onClick = onClick
    )
}

@Preview(showBackground = true)
@Composable
private fun AccountButtonCommonPreview() {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EditButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), text = "Edit", onClick = {})
        DeleteButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), onClick = {})
    }
}