package fr.iuthaguenau.tpburkle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import fr.iuthaguenau.tpburkle.ui.theme.TPBurkleTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TPBurkleTheme {
                GuessTheNumberApp()
            }
        }
    }
}

@Composable
fun GuessTheNumberApp() {
    var userGuess by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userGuess,
            onValueChange = { userGuess = it },
            label = { Text("Enter your guess") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Logic to check the guess will go here
            resultMessage = "You guessed: $userGuess"
        }) {
            Text("Submit")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = resultMessage)
    }
}

@Preview(showBackground = true)
@Composable
fun GuessTheNumberAppPreview() {
    TPBurkleTheme {
        GuessTheNumberApp()
    }
}