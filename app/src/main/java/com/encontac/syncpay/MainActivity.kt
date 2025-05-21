package com.encontac.syncpay

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.encontac.syncpay.ui.theme.SyncPayTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.platform.LocalContext

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

    val db = Firebase.firestore
    val formato = SimpleDateFormat("MMMM", Locale("es", "ES"))
    val fechaServidor = remember { mutableStateOf<String?>(null) }

    // Obtener fecha del servidor
    LaunchedEffect(Unit) {
        val tempDoc = db.collection("serverTime").document("temp")
        tempDoc.set(mapOf("timestamp" to FieldValue.serverTimestamp()))
            .addOnSuccessListener {
                tempDoc.get()
                    .addOnSuccessListener { document ->
                        val timestamp = document.getTimestamp("timestamp")
                        if (timestamp != null) {
                            val serverDate = timestamp.toDate()
                            fechaServidor.value = formato.format(serverDate) // Actualiza la fecha
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al leer la fecha del servidor", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al guardar el timestamp", e)
            }
    }
    // el contexto local
    val context = LocalContext.current

    // Interfaz de usuario
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { val intent = Intent(context, DescriptionActivity::class.java)
                         context.startActivity(intent)
            }
            .padding(8.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(16.dp),
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
                    .weight(0.7f)
                    .padding(start = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = usuario.nombre.replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3D3D3D)
                        )

                    }
                    Text(text = "Acumulado : ${usuario.montoTotal}", fontWeight = FontWeight.Bold , color = Color(0xFF727171))
                    Text(text = "${fechaActual() ?: ""} : ${usuario.montoParcial}", fontWeight = FontWeight.Bold,  color = Color(0xFF727171))
                }
            }
            FloatingActionButton(
                onClick = { /* acción aquí */ },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                modifier = Modifier.size(45.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
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

