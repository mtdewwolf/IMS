package com.example.inventorymanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorymanager.databinding.ActivityMainBinding
import com.example.inventorymanager.ui.scan.BarcodeScannerActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupListeners()
    }
    
    private fun setupListeners() {
        binding.btnScanImport.setOnClickListener {
            startBarcodeScanner(ScanMode.IMPORT)
        }
        
        binding.btnScanExport.setOnClickListener {
            startBarcodeScanner(ScanMode.EXPORT)
        }
    }
    
    private fun startBarcodeScanner(mode: ScanMode) {
        val intent = Intent(this, BarcodeScannerActivity::class.java).apply {
            putExtra(BarcodeScannerActivity.EXTRA_SCAN_MODE, mode.name)
        }
        startActivity(intent)
    }
    
    enum class ScanMode {
        IMPORT, EXPORT
    }
} 