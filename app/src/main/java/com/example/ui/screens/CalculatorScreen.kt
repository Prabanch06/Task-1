package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.CalculatorEvent
import com.example.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel) {
    val expression by viewModel.expression.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
    val aiExplanation = viewModel.history.collectAsStateWithLifecycle().value.firstOrNull()?.aiExplanation

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Column {
                    Text("Smart Calc", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                    Text("AI POWERED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = {}) { Icon(Icons.Default.History, contentDescription = "History", tint = MaterialTheme.colorScheme.onBackground) }
                IconButton(onClick = {}) { Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = MaterialTheme.colorScheme.onBackground) }
            }
        }

        // AI Insight Card
        if (aiExplanation != null || isAiThinking) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(28.dp))
                    .border(1.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(28.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Text("AI EXPLANATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Text(
                    text = if (isAiThinking) "AI is thinking..." else aiExplanation ?: "",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    Badge("Step-by-step")
                    Badge("Graph")
                }
            }
        }

        // Calculation Display Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = expression.ifEmpty { "" },
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = result.ifEmpty { "0" },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 56.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 56.sp
            )
        }

        // Smart Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmartAction(Icons.Default.Mic, "VOICE", Modifier.weight(1f)) {}
            SmartAction(Icons.Default.CameraAlt, "OCR", Modifier.weight(1f)) {}
            SmartAction(Icons.Default.Edit, "SKETCH", Modifier.weight(1f)) {}
            SmartAction(Icons.Default.SmartToy, "ASK AI", Modifier.weight(1f), isPrimary = true) {
                viewModel.onEvent(CalculatorEvent.AiSolve)
            }
        }

        // Calculator Keypad
        val buttons = listOf(
            "AC", "( )", "%", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "−",
            "1", "2", "3", "+",
            "0", ".", "="
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            items(
                items = buttons,
                span = { btn ->
                    if (btn == "=") GridItemSpan(2) else GridItemSpan(1)
                }
            ) { btn ->
                val bg = when {
                    btn == "=" -> MaterialTheme.colorScheme.tertiary
                    btn in listOf("÷", "×", "−", "+") -> MaterialTheme.colorScheme.secondary
                    btn in listOf("AC", "( )", "%") -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
                val textColor = when {
                    btn in listOf("AC", "( )", "%", "÷", "×", "−", "+", "=") -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onBackground
                }
                val hasBorder = btn in listOf("7", "8", "9", "4", "5", "6", "1", "2", "3", "0", ".")

                Box(
                    modifier = Modifier
                        .height(64.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(bg)
                        .then(if (hasBorder) Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(32.dp)) else Modifier)
                        .clickable {
                            when (btn) {
                                "AC" -> viewModel.onEvent(CalculatorEvent.Clear)
                                "−" -> viewModel.onEvent(CalculatorEvent.Operation("-"))
                                "×" -> viewModel.onEvent(CalculatorEvent.Operation("*"))
                                "÷" -> viewModel.onEvent(CalculatorEvent.Operation("/"))
                                "=" -> viewModel.onEvent(CalculatorEvent.Calculate)
                                "( )" -> { /* Handle parenthesis logic if needed, ignored for now */ }
                                else -> {
                                    if (btn == "." || btn == "%" || btn == "+") viewModel.onEvent(CalculatorEvent.Operation(btn))
                                    else viewModel.onEvent(CalculatorEvent.Number(btn.toInt()))
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = btn,
                        fontSize = if (btn in listOf("÷", "×", "−", "+", "=")) 28.sp else 22.sp,
                        fontWeight = if (btn == "=") FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun SmartAction(icon: ImageVector, label: String, modifier: Modifier = Modifier, isPrimary: Boolean = false, onClick: () -> Unit) {
    val bg = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
    val borderColor = if (isPrimary) Color.Transparent else MaterialTheme.colorScheme.outline

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = contentColor)
    }
}

@Composable
fun Badge(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}
