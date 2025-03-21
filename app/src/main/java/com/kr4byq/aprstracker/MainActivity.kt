package com.kr4byq.aprstracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MapsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        return // TODO temp fix have to debug android manifest.xml bc mapsactivity isn't getting called at launch :/
        setContentView(R.layout.activity_main)
        Log.d("TAG", "MAINACTIVITY INIT")

        checkAndRequestPermissions()

        val startButton = findViewById<Button>(R.id.startButton)
        val stopButton = findViewById<Button>(R.id.stopButton)
        val startGPSButton = findViewById<Button>(R.id.startGPSButton)
        val stopGPSButton = findViewById<Button>(R.id.stopGPSButton)

        startButton.setOnClickListener {
            Log.d("TAG", "START BUTTON CLICKED")
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val serviceIntent = Intent(this, AprsService::class.java)
                startForegroundService(serviceIntent)
            } else {
                Log.e("TAG", "Permissions not granted!")
                Toast.makeText(this, "Grant location permissions!", Toast.LENGTH_SHORT).show()
            }
        }

        stopButton.setOnClickListener {
            Log.d("TAG", "STOP BUTTON CLICKED")
            stopService(Intent(this, AprsService::class.java))

        }

        startGPSButton.setOnClickListener {
            Log.d("TAG", "START GPS BUTTON CLICKED")
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val serviceIntent = Intent(this, AwsService::class.java)
                startForegroundService(serviceIntent)
            } else {
                Log.e("TAG", "Permissions not granted!")
                Toast.makeText(this, "Grant location permissions!", Toast.LENGTH_SHORT).show()
            }
        }

        stopGPSButton.setOnClickListener {
            Log.d("TAG", "STOP GPS BUTTON CLICKED")
            stopService(Intent(this, AwsService::class.java))
        }

        val mapButton = findViewById<Button>(R.id.mapButton)
        mapButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
        val replayButton = findViewById<Button>(R.id.replayButton)
        replayButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }




    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // >=android 14
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                Toast.makeText(this, "APRS tracking enabled.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions denied.", Toast.LENGTH_LONG).show()
            }
        }
    }
}