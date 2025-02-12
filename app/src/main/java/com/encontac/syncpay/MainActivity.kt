package com.encontac.syncpay

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.encontac.syncpay.ui.theme.SyncPayTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.text.clear

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyncPayTheme {
                Scaffold { innerPadding ->
                    LazyRowData(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LazyRowData(modifier: Modifier = Modifier){

    val db = Firebase.firestore
    val myItems = remember { mutableStateListOf<Usuario>() }

    LaunchedEffect(Unit) {
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { result ->
                myItems.clear()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    myItems.add(
                        Usuario(
                            document.id,
                            document.data["imagen"].toString(),
                            document.data["montoTotal"].toString().toInt(),
                            0
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = myItems) { item ->
            MyItem(item)
        }
    }
}

@Composable
fun MyItem(usuario : Usuario ){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // Padding exterior (fuera de la sombra y el borde)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp)) // Sombra
            .background(Color.Transparent) // Fondo transparente para que se vea la sombra
        // Borde
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ) // Color de fondo
                .padding(16.dp), // Padding interior (dentro del borde)
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = usuario.montoTotal.toString(),
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) { // <-- Aquí se abre la lambda del contenido del Box
                Column { // <-- Puedes usar una Column para organizar los textos
                    Text(text = usuario.montoTotal.toString())
                    Text(text = usuario.montoParcial.toString())
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SyncPayTheme {
        LazyRowData()
    }
}