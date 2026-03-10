package com.mediscan.app.ui.screens.patient.medicine

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediscan.app.core.theme.HealthGreen
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary

// ── Data models ──────────────────────────────────────────────

private data class Medicine(
    val id: String,
    val name: String,
    val genericName: String,
    val category: String,
    val price: Double,
    val originalPrice: Double? = null,
    val dosage: String,
    val description: String,
    val emoji: String,
    val inStock: Boolean = true,
    val requiresPrescription: Boolean = false,
)

private data class CartItem(
    val medicine: Medicine,
    var quantity: Int = 1,
)

private val categories = listOf("All", "Pain Relief", "Antibiotics", "Vitamins", "Cardiac", "Diabetes", "Allergy", "Digestive")

private val sampleMedicines = listOf(
    Medicine("1", "Paracetamol 500mg", "Acetaminophen", "Pain Relief", 3.99, 5.49, "500mg", "Effective for mild to moderate pain and fever. Take 1-2 tablets every 4-6 hours.", "💊"),
    Medicine("2", "Ibuprofen 400mg", "Ibuprofen", "Pain Relief", 5.49, null, "400mg", "Anti-inflammatory painkiller for headaches, dental pain, and muscle aches.", "💊"),
    Medicine("3", "Amoxicillin 250mg", "Amoxicillin", "Antibiotics", 8.99, 12.99, "250mg", "Broad-spectrum antibiotic for bacterial infections. Complete full course.", "💉", requiresPrescription = true),
    Medicine("4", "Azithromycin 500mg", "Azithromycin", "Antibiotics", 12.49, null, "500mg", "Macrolide antibiotic for respiratory and skin infections.", "💉", requiresPrescription = true),
    Medicine("5", "Vitamin C 1000mg", "Ascorbic Acid", "Vitamins", 7.99, 9.99, "1000mg", "Boosts immunity and supports collagen formation. Take 1 tablet daily.", "🍊"),
    Medicine("6", "Vitamin D3 2000IU", "Cholecalciferol", "Vitamins", 6.49, null, "2000IU", "Essential for bone health and calcium absorption. Take daily with food.", "☀️"),
    Medicine("7", "Multivitamin Complex", "Multiple Vitamins", "Vitamins", 11.99, 14.99, "1 Tablet", "Complete daily multivitamin with minerals for overall health.", "🌟"),
    Medicine("8", "Aspirin 75mg", "Acetylsalicylic Acid", "Cardiac", 4.29, null, "75mg", "Low-dose aspirin for cardiovascular protection. Take daily with food.", "❤️"),
    Medicine("9", "Atorvastatin 10mg", "Atorvastatin", "Cardiac", 9.99, 15.99, "10mg", "Cholesterol-lowering statin. Take once daily at bedtime.", "❤️", requiresPrescription = true),
    Medicine("10", "Metformin 500mg", "Metformin HCl", "Diabetes", 6.99, null, "500mg", "First-line treatment for type 2 diabetes. Take with meals.", "🩸", requiresPrescription = true),
    Medicine("11", "Cetirizine 10mg", "Cetirizine HCl", "Allergy", 4.99, 6.99, "10mg", "Non-drowsy antihistamine for allergies, hay fever, and hives.", "🤧"),
    Medicine("12", "Loratadine 10mg", "Loratadine", "Allergy", 5.49, null, "10mg", "24-hour allergy relief from sneezing, runny nose, and itchy eyes.", "🤧"),
    Medicine("13", "Omeprazole 20mg", "Omeprazole", "Digestive", 7.49, 10.99, "20mg", "Proton pump inhibitor for acid reflux and heartburn relief.", "🫁"),
    Medicine("14", "Loperamide 2mg", "Loperamide HCl", "Digestive", 3.49, null, "2mg", "Anti-diarrheal medication for acute and chronic diarrhea.", "🫁"),
    Medicine("15", "Calcium + D3 600mg", "Calcium Carbonate", "Vitamins", 8.99, 11.99, "600mg", "Calcium supplement with Vitamin D3 for strong bones and teeth.", "🦴"),
    Medicine("16", "Diclofenac Gel 1%", "Diclofenac", "Pain Relief", 9.99, null, "1%", "Topical gel for joint and muscle pain. Apply 3-4 times daily.", "🧴"),
)

// ══════════════════════════════════════════════════════════════
// Main Screen
// ══════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyMedicineScreen(
    onNavigateBack: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val cart = remember { mutableStateListOf<CartItem>() }
    var showCart by remember { mutableStateOf(false) }
    var showPurchaseSuccess by remember { mutableStateOf(false) }

    val filteredMedicines = sampleMedicines.filter { med ->
        val matchesCategory = selectedCategory == "All" || med.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                med.name.contains(searchQuery, ignoreCase = true) ||
                med.genericName.contains(searchQuery, ignoreCase = true) ||
                med.category.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val totalItems = cart.sumOf { it.quantity }
    val totalPrice = cart.sumOf { it.medicine.price * it.quantity }

    // Cart bottom sheet
    if (showCart) {
        ModalBottomSheet(
            onDismissRequest = { showCart = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CartSheet(
                cart = cart,
                totalPrice = totalPrice,
                onUpdateQuantity = { itemId, delta ->
                    val index = cart.indexOfFirst { it.medicine.id == itemId }
                    if (index >= 0) {
                        val newQty = cart[index].quantity + delta
                        if (newQty <= 0) cart.removeAt(index)
                        else cart[index] = cart[index].copy(quantity = newQty)
                    }
                },
                onRemoveItem = { itemId ->
                    cart.removeAll { it.medicine.id == itemId }
                },
                onCheckout = {
                    showCart = false
                    showPurchaseSuccess = true
                    cart.clear()
                },
            )
        }
    }

    // Purchase success dialog
    if (showPurchaseSuccess) {
        AlertDialog(
            onDismissRequest = { showPurchaseSuccess = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = HealthGreen,
                    modifier = Modifier.size(56.dp)
                )
            },
            title = {
                Text(
                    "Order Placed! 🎉",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    "Your order has been placed successfully! You'll receive a confirmation shortly. Estimated delivery: 30-45 minutes.",
                    textAlign = TextAlign.Center,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPurchaseSuccess = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                ) {
                    Text("Continue Shopping", fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = cart.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = Color(0xFFF44336),
                            contentColor = Color.White,
                        ) {
                            Text("$totalItems", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                ) {
                    FloatingActionButton(
                        onClick = { showCart = true },
                        containerColor = Color(0xFF1A237E),
                        contentColor = Color.White,
                        shape = CircleShape,
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            }
        },
        containerColor = Color(0xFFF4F6FB),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Gradient Header ──
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF1A237E),
                                    Color(0xFF3F51B5),
                                    Color(0xFF5C6BC0)
                                )
                            )
                        )
                        .padding(top = 16.dp, bottom = 24.dp)
                ) {
                    Column {
                        // Top bar with back + title + cart
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Buy Medicines",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 22.sp
                                )
                                Text(
                                    text = "Quality medicines at your doorstep",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            // Cart icon in header
                            BadgedBox(
                                badge = {
                                    if (totalItems > 0) {
                                        Badge(
                                            containerColor = Color(0xFFF44336),
                                            contentColor = Color.White,
                                        ) {
                                            Text("$totalItems", fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                IconButton(onClick = { if (cart.isNotEmpty()) showCart = true }) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = "Cart",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search medicines...", color = Color.White.copy(alpha = 0.6f)) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
                            },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White.copy(alpha = 0.7f))
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White.copy(alpha = 0.5f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                            ),
                            singleLine = true,
                        )
                    }
                }
            }

            // ── Category filter chips ──
            item {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category,
                                    fontWeight = if (selectedCategory == category) FontWeight.SemiBold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF1A237E),
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = TextSecondary,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = Color(0xFFE0E0E0),
                                selectedBorderColor = Color(0xFF1A237E),
                                enabled = true,
                                selected = selectedCategory == category,
                            ),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ── Results count ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${filteredMedicines.size} medicines found",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                    if (cart.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = HealthGreen.copy(alpha = 0.12f),
                        ) {
                            Text(
                                text = "Cart: $${"%.2f".format(totalPrice)}",
                                color = HealthGreen,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // ── Medicine list ──
            items(filteredMedicines, key = { it.id }) { medicine ->
                val cartItem = cart.find { it.medicine.id == medicine.id }
                MedicineCard(
                    medicine = medicine,
                    quantityInCart = cartItem?.quantity ?: 0,
                    onAddToCart = {
                        val existing = cart.indexOfFirst { it.medicine.id == medicine.id }
                        if (existing >= 0) {
                            cart[existing] = cart[existing].copy(quantity = cart[existing].quantity + 1)
                        } else {
                            cart.add(CartItem(medicine))
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // ── Empty state ──
            if (filteredMedicines.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("🔍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No medicines found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A2E)
                        )
                        Text(
                            "Try adjusting your search or category",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ══════════════════════════════════════════════════════════════
// Medicine Card
// ══════════════════════════════════════════════════════════════

@Composable
private fun MedicineCard(
    medicine: Medicine,
    quantityInCart: Int,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categoryColor = when (medicine.category) {
        "Pain Relief" -> Color(0xFFE65100)
        "Antibiotics" -> Color(0xFFC62828)
        "Vitamins" -> Color(0xFF2E7D32)
        "Cardiac" -> Color(0xFFAD1457)
        "Diabetes" -> Color(0xFF6A1B9A)
        "Allergy" -> Color(0xFF00838F)
        "Digestive" -> Color(0xFF4E342E)
        else -> MediBlue
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(categoryColor)
            )

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    // Emoji + info
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(categoryColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(medicine.emoji, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = medicine.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A237E),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = medicine.genericName,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = categoryColor.copy(alpha = 0.1f),
                            ) {
                                Text(
                                    text = medicine.category,
                                    color = categoryColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text("•", color = TextSecondary, fontSize = 10.sp)
                            Text(
                                text = medicine.dosage,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                            if (medicine.requiresPrescription) {
                                Text("•", color = TextSecondary, fontSize = 10.sp)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFFF3E0),
                                ) {
                                    Text(
                                        text = "Rx",
                                        color = Color(0xFFE65100),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = medicine.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Price + Add to cart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$${"%.2f".format(medicine.price)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A237E),
                            fontSize = 18.sp
                        )
                        if (medicine.originalPrice != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$${"%.2f".format(medicine.originalPrice)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textDecoration = TextDecoration.LineThrough,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val discount = ((1 - medicine.price / medicine.originalPrice) * 100).toInt()
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = HealthGreen.copy(alpha = 0.12f),
                            ) {
                                Text(
                                    text = "-$discount%",
                                    color = HealthGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    if (quantityInCart > 0) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = HealthGreen.copy(alpha = 0.12f),
                        ) {
                            Text(
                                text = "✓ In Cart ($quantityInCart)",
                                color = HealthGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A237E),
                            contentColor = Color.White,
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════
// Cart Bottom Sheet
// ══════════════════════════════════════════════════════════════

@Composable
private fun CartSheet(
    cart: List<CartItem>,
    totalPrice: Double,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onCheckout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
    ) {
        // Handle bar
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFE0E0E0))
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Your Cart 🛒",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )
            Text(
                text = "${cart.sumOf { it.quantity }} items",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (cart.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("🛒", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Your cart is empty", color = TextSecondary)
            }
        } else {
            // Cart items
            cart.forEach { item ->
                CartItemRow(
                    item = item,
                    onIncrement = { onUpdateQuantity(item.medicine.id, 1) },
                    onDecrement = { onUpdateQuantity(item.medicine.id, -1) },
                    onRemove = { onRemoveItem(item.medicine.id) },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtotal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Subtotal", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                Text("$${"%.2f".format(totalPrice)}", fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Delivery Fee", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = if (totalPrice >= 25.0) "FREE" else "$2.99",
                    fontWeight = FontWeight.SemiBold,
                    color = if (totalPrice >= 25.0) HealthGreen else Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            val deliveryFee = if (totalPrice >= 25.0) 0.0 else 2.99
            val grandTotal = totalPrice + deliveryFee

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
                Text(
                    "$${"%.2f".format(grandTotal)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }

            if (totalPrice < 25.0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add $${"%.2f".format(25.0 - totalPrice)} more for free delivery!",
                    style = MaterialTheme.typography.bodySmall,
                    color = HealthGreen,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Checkout button
            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A237E),
                    contentColor = Color.White
                ),
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Place Order — $${"%.2f".format(grandTotal)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// ── Cart Item Row ────────────────────────────────────────────

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1A237E).copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.medicine.emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.medicine.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$${"%.2f".format(item.medicine.price)} each",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            // Quantity controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onDecrement,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF1A237E)
                    )
                }
                Text(
                    text = "${item.quantity}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.width(24.dp),
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF1A237E)
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Item total
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${"%.2f".format(item.medicine.price * item.quantity)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E),
                    fontSize = 14.sp
                )
                Text(
                    text = "Remove",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFF44336),
                    fontSize = 10.sp,
                    modifier = Modifier.clickable { onRemove() }
                )
            }
        }
    }
}
