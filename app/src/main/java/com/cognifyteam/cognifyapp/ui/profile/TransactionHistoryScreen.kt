package com.cognifyteam.cognifyapp.ui.profile

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.models.Transaction
import com.cognifyteam.cognifyapp.ui.FabState
import com.cognifyteam.cognifyapp.ui.TopBarState
import com.cognifyteam.cognifyapp.ui.common.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    navController: NavController,
    appContainer: AppContainer,
    userViewModel: UserViewModel,
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val viewModel: TransactionHistoryViewModel = viewModel(
        factory = TransactionHistoryViewModel.provideFactory(
            repository = appContainer.transactionRepository
        )
    )

    val uiState by viewModel.uiState.collectAsState()
    val currentUser by userViewModel.userState.collectAsState()

    // --- State untuk mengontrol WebView ---
    var showPaymentWebView by remember { mutableStateOf(false) }
    var selectedPaymentToken by remember { mutableStateOf<String?>(null) }

    // LaunchedEffect ini memuat data saat pertama kali masuk ke layar
    LaunchedEffect(currentUser) {
        onFabStateChange(FabState(isVisible = false))
        onTopBarStateChange(
            TopBarState(
                isVisible = true,
                title = "Transaction History",
                navigationIcon = {
                    IconButton(onClick = {
                        // Jika webview terbuka, tombol kembali harus menutup webview
                        if (showPaymentWebView) {
                            showPaymentWebView = false
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        )
        // Muat data hanya jika belum dalam proses memuat
        if (uiState is TransactionUiState.Loading) {
            currentUser?.firebaseId?.let { userId ->
                viewModel.loadUserTransactions(userId)
            }
        }
    }

    // --- STRUKTUR UTAMA DENGAN BOX UNTUK OVERLAY ---
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // UI akan bereaksi terhadap perubahan state dari ViewModel
                when (val state = uiState) {
                    is TransactionUiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is TransactionUiState.Error -> {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                    is TransactionUiState.Success -> {
                        if (state.transactions.isEmpty()) {
                            Text("You have no transaction history yet.")
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.transactions) { transaction ->
                                    // --- LOGIKA KLIK DIHUBUNGKAN DI SINI ---
                                    TransactionItem(
                                        transaction = transaction,
                                        onItemClick = {
                                            // Hanya proses jika statusnya pending dan ada token
                                            if (transaction.displayStatus.second == "Pending" && !transaction.paymentToken.isNullOrBlank()) {
                                                selectedPaymentToken = transaction.paymentToken
                                                showPaymentWebView = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- OVERLAY WEBVIEW ---
        // WebView ini akan muncul di atas segalanya jika showPaymentWebView adalah true
        if (showPaymentWebView && selectedPaymentToken != null) {
            PaymentWebView(snapToken = selectedPaymentToken!!) {
                // Callback ini akan dipanggil saat WebView selesai/ditutup
                showPaymentWebView = false
                selectedPaymentToken = null
                // Muat ulang data untuk mendapatkan status transaksi terbaru
                currentUser?.firebaseId?.let { viewModel.loadUserTransactions(it) }
            }
        }
    }
}

@Composable
fun PaymentWebView(snapToken: String, onFinished: () -> Unit) {
    val url = "https://app.sandbox.midtrans.com/snap/v2/vtweb/$snapToken"
    Log.d("PaymentWebView", "Loading URL: $url")

    val isDarkTheme = isSystemInDarkTheme()

    // Menangani tombol kembali fisik agar menutup WebView, bukan keluar dari aplikasi
    BackHandler {
        onFinished()
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("PaymentWebView", "Page finished loading: $url")
                        if (url != null && (url.contains("transaction_status") || url.contains("finish"))) {
                            onFinished()
                        }
                    }
                }

                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    val forceDarkMode = if (isDarkTheme) WebSettingsCompat.FORCE_DARK_ON else WebSettingsCompat.FORCE_DARK_OFF
                    WebSettingsCompat.setForceDark(settings, forceDarkMode)
                }
                loadUrl(url)
            }
        }
    )
}

@Composable
fun TransactionItem(transaction: Transaction, onItemClick: () -> Unit) {
    // Tentukan apakah item ini bisa diklik berdasarkan statusnya
    val isClickable = transaction.displayStatus.second == "Pending"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)) // Clip shape agar efek klik (ripple) terlihat bagus
            .clickable(enabled = isClickable, onClick = onItemClick), // Jadikan Card bisa di-klik
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.courseName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.formattedDate, // Gunakan helper dari model
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = transaction.formattedAmount, // Gunakan helper dari model
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            val (displayText, statusType) = transaction.displayStatus
            TransactionStatusChip(statusText = displayText, statusType = statusType)
        }
    }
}

@Composable
fun TransactionStatusChip(statusText: String, statusType: String) {
    val (backgroundColor, textColor) = when (statusType) {
        "Success" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "Pending" -> Color(0xFFFFFBEB) to Color(0xFFB45309) // Contoh: kuning
        "Failed" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    Box(
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = statusText,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}