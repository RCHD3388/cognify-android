package com.cognifyteam.cognifyapp.ui.material

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.cognifyteam.cognifyapp.data.models.Material
import com.cognifyteam.cognifyapp.ui.TopBarState
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import android.app.Activity
import android.view.View
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.compose.ui.platform.LocalContext
import android.content.pm.ActivityInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialScreen(
    navController: NavController,
    materialJson: String?,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val jsonAdapter = moshi.adapter(Material::class.java)
    val material = materialJson?.let { jsonAdapter.fromJson(it) }

    LaunchedEffect(key1 = Unit) {
        onTopBarStateChange(
            TopBarState(
                isVisible = true,
                title = material?.title ?: "Material",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        )
    }

    // Define gradient colors based on material type
    val gradientColors = when (material?.material_type) {
        "video" -> listOf(
            Color(0xFF6366F1), // Indigo
            Color(0xFF8B5CF6)  // Purple
        )
        "document" -> listOf(
            Color(0xFF10B981), // Emerald
            Color(0xFF06B6D4)  // Cyan
        )
        else -> listOf(
            Color(0xFF6B7280), // Gray
            Color(0xFF9CA3AF)  // Light Gray
        )
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8FAFC),
                            Color(0xFFF1F5F9)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            if (material == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Failed to load material data.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            } else {
                // Enhanced Material Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Material Type Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        brush = Brush.horizontalGradient(gradientColors),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (material.material_type) {
                                            "video" -> Icons.Default.PlayCircle
                                            "document" -> Icons.Default.Description
                                            else -> Icons.Default.Description
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when (material.material_type) {
                                            "video" -> "Video"
                                            "document" -> "Document"
                                            else -> "Material"
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Content Description

                        if(material.material_type!="other"){
                            Text(
                                text = "Description :",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F2937),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = material.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF6B7280),
                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
                            )
                        }

                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Enhanced Web Content Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Content Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(gradientColors),
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Text(
                                text = when (material.material_type) {
                                    "video" -> "Video Content"
                                    "document" -> "Document Viewer"
                                    else -> "Content"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Web Content Area
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            when (material.material_type) {
                                "video" -> {
                                    // Untuk video, gunakan URL langsung
                                    val videoUrl = "https://bucket-storage-mdp.s3.us-east-1.amazonaws.com/Desktop+2025.06.29+-+21.26.56.01.mp4"
                                    val url = material.url.toString()
                                    WebContent(url = url)
                                }
                                "document" -> {
                                    // Untuk dokumen (PDF), bungkus dengan Google Docs Viewer
                                    val pdfUrl = "https://bucket-storage-mdp.s3.us-east-1.amazonaws.com/%5BBAA%5D-Jadwal-UTS-Semester-Genap-2024-2025.pdf"
                                    val url = material.url
                                    val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$url"
                                    WebContentVideo(url = googleDocsUrl)
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize().padding(16.dp),

                                        ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                "${material.description}",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color(0xFF6B7280),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Composable untuk menampilkan konten web (TIDAK BERUBAH)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebContentVideo(url: String) {
    // Dapatkan activity dari context untuk mengontrol tampilan fullscreen
    val activity = LocalContext.current as Activity

    AndroidView(factory = { context ->
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            webChromeClient = object : WebChromeClient() {
                private var customView: View? = null
                private var customViewCallback: CustomViewCallback? = null
                private val FULL_SCREEN_SETTING = View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    super.onShowCustomView(view, callback)
                    if (customView != null) {
                        onHideCustomView()
                        return
                    }

                    customView = view
                    customViewCallback = callback

                    (activity.window.decorView as FrameLayout).addView(
                        customView,
                        FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    )

                    activity.window.decorView.systemUiVisibility = FULL_SCREEN_SETTING

                    // BARIS BARU: Paksa orientasi layar menjadi Landscape
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }

                override fun onHideCustomView() {
                    super.onHideCustomView()
                    (activity.window.decorView as FrameLayout).removeView(customView)
                    customView = null
                    customViewCallback?.onCustomViewHidden()

                    activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

                    // BARIS BARU: Kembalikan orientasi layar ke normal (sesuai sensor)
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }
            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })
}

// Composable untuk menampilkan konten web (TIDAK BERUBAH)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebContent(url: String) {
    // Dapatkan activity dari context untuk mengontrol tampilan fullscreen
    val activity = LocalContext.current as Activity

    // AndroidView adalah cara untuk menempatkan View Android (seperti WebView) di dalam Composable
    AndroidView(factory = { context ->
        // Blok ini dieksekusi sekali saat View pertama kali dibuat
        WebView(context).apply {
            // Mengatur WebView agar mengisi seluruh ruang yang tersedia
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            // WebViewClient untuk menangani navigasi di dalam WebView itu sendiri
            webViewClient = WebViewClient()

            // --- PENGATURAN WAJIB UNTUK MEDIA INTERAKTIF ---
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true // Diperlukan oleh beberapa pemutar video berbasis web
            settings.mediaPlaybackRequiresUserGesture = false // Kunci agar video bisa autoplay

            // Custom WebChromeClient untuk menangani fitur UI browser seperti event fullscreen
            webChromeClient = object : WebChromeClient() {
                private var customView: View? = null
                private var customViewCallback: CustomViewCallback? = null
                private val FULL_SCREEN_SETTING = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    if (customView != null) {
                        onHideCustomView()
                        return
                    }
                    customView = view
                    customViewCallback = callback
                    (activity.window.decorView as FrameLayout).addView(
                        customView,
                        FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    )
                    activity.window.decorView.systemUiVisibility = FULL_SCREEN_SETTING
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }

                override fun onHideCustomView() {
                    (activity.window.decorView as FrameLayout).removeView(customView)
                    customView = null
                    customViewCallback?.onCustomViewHidden()
                    activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }

            // --- BAGIAN UTAMA SOLUSI ---
            // Membuat template HTML on-the-fly untuk membungkus URL video
            // Ini memaksa WebView untuk menggunakan pemutar video HTML5, bukan men-download file
            val htmlData = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                    <style>
                        html, body { 
                            margin: 0; 
                            padding: 0; 
                            width: 100%; 
                            height: 100%; 
                            overflow: hidden; 
                            background-color: black; 
                        }
                        video { 
                            width: 100%; 
                            height: 100%; 
                            object-fit: contain; 
                        }
                    </style>
                </head>
                <body>
                    <video controls autoplay playsinline>
                        <source src="$url" type="video/mp4">
                        Browser Anda tidak mendukung tag video.
                    </video>
                </body>
                </html>
            """.trimIndent()

            // Memuat string HTML yang kita buat, bukan memuat URL secara langsung
            loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
        }
    },
        // Blok update ini dipanggil jika `url` berubah saat recomposition
        update = { webView ->
            // Logika yang sama diterapkan di sini untuk memperbarui konten jika URL berubah
            val htmlData = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                <style>
                    html, body { margin: 0; padding: 0; width: 100%; height: 100%; overflow: hidden; background-color: black; }
                    video { width: 100%; height: 100%; object-fit: contain; }
                </style>
            </head>
            <body>
                <video controls autoplay playsinline>
                    <source src="$url" type="video/mp4">
                    Browser Anda tidak mendukung tag video.
                </video>
            </body>
            </html>
        """.trimIndent()
            webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
        })
}