package com.example.microstrujillo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microstrujillo.R
import com.example.microstrujillo.ui.theme.Azul

@Composable
fun PantallaLogin(
    onLoginSuccess: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var recordar by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Logo
        Box(
            modifier = Modifier
                .size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.logo_micro_real),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Título de la app
        Text(
            text = "MicrosTrujillo",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Azul
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Campo de texto de Usuario
        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto de Contraseña
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "Ocultar" else "Ver", color = Azul, fontWeight = FontWeight.Bold)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Casilla Recordar y Botón de Olvidaste contraseña
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = recordar,
                    onCheckedChange = { recordar = it }
                )
                Text(text = "Recordar", fontSize = 14.sp)
            }

            TextButton(onClick = { /* Acción para recuperar contraseña */ }) {
                Text(text = "¿Olvidaste tu contraseña?", fontSize = 14.sp, color = Azul)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón principal de Iniciar Sesión
        Button(
            onClick = onLoginSuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Azul)
        ) {
            Text(text = "Iniciar sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Crear Cuenta
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "¿No tienes cuenta?", fontSize = 15.sp, color = Color.Gray)
            TextButton(onClick = { /* Acción para crear cuenta */ }) {
                Text(text = "Crea cuenta", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Azul)
            }
        }
    }
}
