package fr.iuthaguenau.tpburkle

import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import fr.iuthaguenau.tpburkle.ui.theme.TPBurkleTheme

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class SharedViewModel : ViewModel() {
    var isInMatch by mutableStateOf(false)

    var matchType by mutableStateOf("Simple")
    var isGrandSlam by mutableStateOf(false)
    var player1 by mutableStateOf("")
    var player2 by mutableStateOf("")
    var firstServerFront by mutableStateOf("1")
    var firstServer by mutableStateOf("1")

    var setNumber by mutableStateOf(0)

    var score1 by mutableStateOf(0)
    var score2 by mutableStateOf(0)

    var score1Table by mutableStateOf(mutableListOf<Int>(0, 0, 0))
    var score2Table by mutableStateOf(mutableListOf<Int>(0, 0, 0))

    var score1TableBig by mutableStateOf(mutableListOf<Int>(0, 0, 0, 0, 0))
    var score2TableBig by mutableStateOf(mutableListOf<Int>(0, 0, 0, 0, 0))

    var scoreOrder by mutableStateOf(mutableListOf<String>("0", "15", "30", "40", "A", "W"))

    private fun switchServer() {
        firstServer = if (firstServer == "1") "2" else "1"
    }

    fun addPointToPlayer(player: Int) {
        if (player == 1) {
            score1++
            checkGameWinner()
        } else {
            score2++
            checkGameWinner()
        }
    }

    private fun checkGameWinner() {
        if (score1 >= 4 && score1 - score2 >= 2) {
            winGame(1)
        } else if (score2 >= 4 && score2 - score1 >= 2) {
            winGame(2)
        }
    }

    private fun winGame(player: Int) {
        if (player == 1) {
            if (isGrandSlam) score1TableBig[setNumber]++ else score1Table[setNumber]++
        } else {
            if (isGrandSlam) score2TableBig[setNumber]++ else score2Table[setNumber]++
        }
        resetGameScores()
        checkSetWinner()
        switchServer()
    }

    private fun resetGameScores() {
        score1 = 0
        score2 = 0
    }

    private fun checkSetWinner() {
        val gamesToWinSet = if (isGrandSlam) 6 else 6

        if ((isGrandSlam && score1TableBig[setNumber] >= gamesToWinSet && score1TableBig[setNumber] - score2TableBig[setNumber] >= 2) ||
            (!isGrandSlam && score1Table[setNumber] >= gamesToWinSet && score1Table[setNumber] - score2Table[setNumber] >= 2)) {
            winSet(1)
        } else if ((isGrandSlam && score2TableBig[setNumber] >= gamesToWinSet && score2TableBig[setNumber] - score1TableBig[setNumber] >= 2) ||
            (!isGrandSlam && score2Table[setNumber] >= gamesToWinSet && score2Table[setNumber] - score1Table[setNumber] >= 2)) {
            winSet(2)
        }
    }

    private fun winSet(player: Int) {
        if (player == 1) {
            setNumber++
        } else {
            setNumber++
        }

        val setsToWinMatch = if (isGrandSlam) 3 else 2
        if (setNumber >= setsToWinMatch) {
            setNumber = 0

            score1 = 0
            score2 = 0

            score1Table = mutableListOf<Int>(0, 0, 0)
            score2Table = mutableListOf<Int>(0, 0, 0)

            score1TableBig = mutableListOf<Int>(0, 0, 0, 0, 0)
            score2TableBig = mutableListOf<Int>(0, 0, 0, 0, 0)

            isInMatch = false
        }
    }
}

class MainActivity : ComponentActivity() {
    private val sharedViewModel by lazy { SharedViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TPBurkleTheme {
                TennisScoreApp(SharedViewModel())
            }
        }
    }
}

@Composable
fun TennisScoreApp(sharedViewModel: SharedViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainScreen(navController, sharedViewModel) }
        composable("new_match") { NewMatchScreen(navController, sharedViewModel) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tennis Score App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFFF).copy(alpha = 0.5f),
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("new_match") },
                containerColor = Color(0xFFfad9a4)) {
                Icon(Icons.Default.Add, contentDescription = "Add Match")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (sharedViewModel.isInMatch){
                Text(
                    text = "Match en cours",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                var titre = (sharedViewModel.player1 + " VS " + sharedViewModel.player2)
                Text(
                    text = titre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(bottom = 12.dp)

                )
                Column(
                    modifier = Modifier
                        .background(Color(0xfffad9a4))
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFFFFFFF))
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if ( sharedViewModel.firstServer == "1"){
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(Color(0xFFf5e83f), shape = CircleShape)
                                )
                            }
                            Text(text = sharedViewModel.player1)
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (sharedViewModel.isGrandSlam){
                                for (element in sharedViewModel.score1TableBig) {
                                    var text = element.toString()
                                    Text(text = text)
                                }
                            } else {
                                for (element in sharedViewModel.score1Table) {
                                    var text = element.toString()
                                    Text(text = text)
                                }
                            }
                            Text(text = sharedViewModel.scoreOrder[sharedViewModel.score1],
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFFFFFFF))
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if ( sharedViewModel.firstServer == "2"){
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(Color(0xFFf5e83f), shape = CircleShape)
                                )
                            }
                            Text(text = sharedViewModel.player2)
                        }
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ){
                            if (sharedViewModel.isGrandSlam){
                                for (element in sharedViewModel.score2TableBig) {
                                    var text = element.toString()
                                    Text(text = text)
                                }
                            } else {
                                for (element in sharedViewModel.score2Table) {
                                    var text = element.toString()
                                    Text(text = text)
                                }
                            }
                            Text(text = sharedViewModel.scoreOrder[sharedViewModel.score2],
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                        ) {
                        Button(onClick = { sharedViewModel.addPointToPlayer(1) }) {
                            Text(text = sharedViewModel.player1)
                        }
                        Button(onClick = { sharedViewModel.addPointToPlayer(2)}
                        ) {
                            Text(text = sharedViewModel.player2)
                        }
                    }
                }

            } else {
                Text(text = "Pour commencer, configurez votre match !")
                Button(onClick = { navController.navigate("new_match") }) {
                    Text(text = "Commencer")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMatchScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tennis Score App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFFF).copy(alpha = 0.5f),
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("main") },
                containerColor = Color(0xFFfad9a4)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Configurer un nouveau match",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(bottom = 12.dp)

                )
            Text(text = "Type de match :")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = sharedViewModel.matchType == "Simple", onClick = { sharedViewModel.matchType = "Simple" })
                Text(text = "Simple")
                RadioButton(selected = sharedViewModel.matchType == "Double", onClick = { sharedViewModel.matchType = "Double" })
                Text(text = "Double")
            }
            Text(text = "Est-ce un match du grand chelem ?")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = sharedViewModel.isGrandSlam, onClick = { sharedViewModel.isGrandSlam = true })
                Text(text = "Oui")
                RadioButton(selected = !sharedViewModel.isGrandSlam, onClick = { sharedViewModel.isGrandSlam = false })
                Text(text = "Non")
            }
            Text(text = "Configurez les joueurs :")
            if (sharedViewModel.matchType == "Simple") {
                OutlinedTextField(
                    value = sharedViewModel.player1,
                    onValueChange = { sharedViewModel.player1 = it },
                    label = { Text("Joueur 1") }
                )
                OutlinedTextField(
                    value = sharedViewModel.player2,
                    onValueChange = { sharedViewModel.player2 = it },
                    label = { Text("Joueur 2") }
                )
            } else {
                OutlinedTextField(
                    value = sharedViewModel.player1,
                    onValueChange = { sharedViewModel.player1 = it },
                    label = { Text("Equipe 1") }
                )
                OutlinedTextField(
                    value = sharedViewModel.player2,
                    onValueChange = { sharedViewModel.player2 = it },
                    label = { Text("Equipe 2") }
                )
            }
            Text(text = "Qui servira en premier ?")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = sharedViewModel.firstServerFront == "1" , onClick = { sharedViewModel.firstServerFront = "1"
                sharedViewModel.firstServer = "1"
                })
                Text(text = "Joueur 1")
                RadioButton(selected = sharedViewModel.firstServerFront == "2", onClick = { sharedViewModel.firstServerFront = "2"
                sharedViewModel.firstServer = "2"})
                Text(text = "Joueur 2")
                RadioButton(selected = sharedViewModel.firstServerFront == "0", onClick = { sharedViewModel.firstServer = "Aléatoire"
                    val randomValue = if (Random.nextInt(1, 3) == 1) "1" else "2"
                    sharedViewModel.firstServer = randomValue
                    sharedViewModel.firstServerFront = "0"
                })
                Text(text = "Aléatoire")
            }
            Button(onClick = {
                sharedViewModel.isInMatch = true
                navController.navigate("main")
                if (sharedViewModel.matchType == "Simple") {
                    if (sharedViewModel.player1 == "") {
                        sharedViewModel.player1 = "Joueur 1"
                    }
                    if (sharedViewModel.player2 == "") {
                        sharedViewModel.player2 = "Joueur 2"
                    }
                } else {
                    if (sharedViewModel.player1 == "") {
                        sharedViewModel.player1 = "Equipe 1"
                    }
                    if (sharedViewModel.player2 == "") {
                        sharedViewModel.player2 = "Equipe 2"
                    }
                }

                sharedViewModel.score1 = 0
                sharedViewModel.score2 = 0

                sharedViewModel.score1Table = mutableListOf<Int>(0, 0, 0)
                sharedViewModel.score2Table = mutableListOf<Int>(0, 0, 0)

                sharedViewModel.score1TableBig = mutableListOf<Int>(0, 0, 0, 0, 0)
                sharedViewModel.score2TableBig = mutableListOf<Int>(0, 0, 0, 0, 0)

                sharedViewModel.setNumber = 0

            }) {
                Text(text = "Commencer le match")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TPBurkleTheme {
        MainScreen(rememberNavController(), SharedViewModel())
    }
}

@Preview(showBackground = true)
@Composable
fun NewMatchScreenPreview() {
    TPBurkleTheme {
        NewMatchScreen(rememberNavController(), SharedViewModel())
    }
}