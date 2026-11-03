package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import com.example.model.AgriStore
import com.example.model.AppLanguage
import com.example.model.FertilizerType

@Composable
fun StoreMapCanvas(
    stores: List<AgriStore>,
    selectedStore: AgriStore?,
    onStoreSelected: (AgriStore) -> Unit,
    farmerDistrict: String,
    language: AppLanguage,
    categoryFilter: FertilizerType? = null,
    specificFertilizerFilter: String? = null,
    maxProximityKm: Float? = null,
    onProximitySelected: ((Float?) -> Unit)? = null,
    onNavigateDirections: ((AgriStore) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val errorColor = MaterialTheme.colorScheme.error

    var zoomScale by remember { mutableStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    val proximityOptions = listOf(
        null to if (language == AppLanguage.TAMIL) "முழு தமிழ்நாடு" else "All TN",
        15f to if (language == AppLanguage.TAMIL) "< 15 கி.மீ" else "< 15 km",
        30f to if (language == AppLanguage.TAMIL) "< 30 கி.மீ" else "< 30 km",
        50f to if (language == AppLanguage.TAMIL) "< 50 கி.மீ" else "< 50 km",
        100f to if (language == AppLanguage.TAMIL) "< 100 கி.மீ" else "< 100 km"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("store_map_canvas_component"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Map title & Region tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Map Location",
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.TAMIL) "தமிழ்நாடு உரக்கடைகள் & விலை வரைபடம்" else "TN Fertilizer Stores & Price Map",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (language == AppLanguage.TAMIL)
                                "${stores.size} கடைகள் கிடைக்கின்றன (${farmerDistrict} மையம்)"
                            else
                                "${stores.size} stores located (${farmerDistrict} center)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = primaryColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF2E7D32), CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (maxProximityKm == null) "TN State" else "≤ ${maxProximityKm.toInt()} km",
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Proximity Quick Filter Chips
            if (onProximitySelected != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = "Proximity",
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.TAMIL) "தூரம்:" else "Proximity:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.testTag("proximity_filter_row")
                    ) {
                        items(proximityOptions) { (radius, label) ->
                            val isSelected = maxProximityKm == radius
                            FilterChip(
                                selected = isSelected,
                                onClick = { onProximitySelected(radius) },
                                label = { Text(label, fontSize = 11.sp) },
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("proximity_chip_${radius?.toInt() ?: 0}"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Interactive Map Canvas Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(16.dp))
                    .border(1.5.dp, primaryColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .testTag("interactive_canvas_viewport")
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(stores, zoomScale, panOffset) {
                            detectTapGestures { tapOffset ->
                                val width = size.width
                                val height = size.height
                                val originX = width * 0.5f + panOffset.x
                                val originY = height * 0.5f + panOffset.y

                                // Convert tap to canvas unscaled normalized coordinates
                                val unscaledX = (tapOffset.x - originX) / (width * zoomScale) + 0.5f
                                val unscaledY = (tapOffset.y - originY) / (height * zoomScale) + 0.5f

                                // Find closest store within touch radius
                                val tapped = stores.minByOrNull { store ->
                                    val dx = store.mapOffsetX - unscaledX
                                    val dy = store.mapOffsetY - unscaledY
                                    dx * dx + dy * dy
                                }

                                if (tapped != null) {
                                    val dx = (tapped.mapOffsetX - unscaledX) * width * zoomScale
                                    val dy = (tapped.mapOffsetY - unscaledY) * height * zoomScale
                                    val distSq = dx * dx + dy * dy
                                    // Touch target radius approx 44dp
                                    if (distSq < 48.dp.toPx() * 48.dp.toPx()) {
                                        onStoreSelected(tapped)
                                    }
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height

                    val centerX = width * 0.5f + panOffset.x
                    val centerY = height * 0.5f + panOffset.y

                    // Helper function to project normalized map coordinate (0..1) to canvas pixels with zoom & pan
                    fun project(normX: Float, normY: Float): Offset {
                        val px = centerX + (normX - 0.5f) * width * zoomScale
                        val py = centerY + (normY - 0.5f) * height * zoomScale
                        return Offset(px, py)
                    }

                    // 1. Draw stylized Tamil Nadu land boundary
                    val tnShape = Path().apply {
                        val p1 = project(0.40f, 0.10f) // Chennai North / Tiruvallur
                        val p2 = project(0.76f, 0.24f) // Bay of Bengal Coast (Cuddalore)
                        val p3 = project(0.70f, 0.52f) // Delta Coast (Nagapattinam/Tiruvarur)
                        val p4 = project(0.66f, 0.70f) // Ramanathapuram / Palk Strait
                        val p5 = project(0.52f, 0.92f) // Kanyakumari Tip (South)
                        val p6 = project(0.33f, 0.78f) // Tirunelveli / Western Ghats
                        val p7 = project(0.34f, 0.60f) // Dindigul / Theni
                        val p8 = project(0.18f, 0.48f) // Coimbatore / Nilgiris
                        val p9 = project(0.28f, 0.32f) // Salem / Dharmapuri
                        val p10 = project(0.34f, 0.16f) // Vellore / Hosur

                        moveTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        lineTo(p3.x, p3.y)
                        lineTo(p4.x, p4.y)
                        lineTo(p5.x, p5.y)
                        lineTo(p6.x, p6.y)
                        lineTo(p7.x, p7.y)
                        lineTo(p8.x, p8.y)
                        lineTo(p9.x, p9.y)
                        lineTo(p10.x, p10.y)
                        close()
                    }

                    // Land background shading
                    drawPath(
                        path = tnShape,
                        color = Color(0xFFC8E6C9).copy(alpha = 0.65f)
                    )
                    // Border stroke
                    drawPath(
                        path = tnShape,
                        color = Color(0xFF66BB6A),
                        style = Stroke(width = 2.5.dp.toPx() * zoomScale.coerceAtLeast(1f))
                    )

                    // 2. Draw Cauvery River Delta flowing to the Bay of Bengal
                    val riverCauvery = Path().apply {
                        val r1 = project(0.24f, 0.42f) // Erode / Mettur
                        val r2 = project(0.46f, 0.45f) // Karur / Trichy
                        val r3 = project(0.70f, 0.51f) // Thanjavur / Delta mouth
                        moveTo(r1.x, r1.y)
                        quadraticTo(r2.x, r2.y, r3.x, r3.y)
                    }
                    drawPath(
                        path = riverCauvery,
                        color = Color(0xFF42A5F5),
                        style = Stroke(width = 3.dp.toPx() * zoomScale.coerceAtLeast(1f))
                    )

                    // 3. Draw key regional hubs & landmark text
                    val paintLabel = Paint().apply {
                        color = android.graphics.Color.argb(160, 46, 125, 50)
                        textSize = 10.sp.toPx() * zoomScale.coerceIn(0.9f, 1.4f)
                        textAlign = Paint.Align.CENTER
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        isAntiAlias = true
                    }

                    val landmarks = listOf(
                        Triple("Chennai", 0.42f, 0.14f),
                        Triple("Salem", 0.42f, 0.35f),
                        Triple("Coimbatore", 0.20f, 0.52f),
                        Triple("Trichy", 0.48f, 0.48f),
                        Triple("Thanjavur (Delta)", 0.58f, 0.45f),
                        Triple("Madurai", 0.44f, 0.66f),
                        Triple("Tirunelveli", 0.35f, 0.82f)
                    )

                    landmarks.forEach { (name, lx, ly) ->
                        val pos = project(lx, ly)
                        drawContext.canvas.nativeCanvas.drawText(name, pos.x, pos.y, paintLabel)
                    }

                    // 4. Farmer's Location Radar & Proximity Zone Circle
                    val farmerPos = project(0.56f, 0.48f) // Centered near Thanjavur / Farmer district

                    // Proximity radius circle (visualizing radius around farm)
                    val radiusKm = maxProximityKm ?: 60f
                    // Map scale factor: 100 km ≈ 80 dp * zoomScale
                    val radiusPx = (radiusKm * 0.95f).dp.toPx() * zoomScale

                    // Proximity translucent fill
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.10f),
                        radius = radiusPx,
                        center = farmerPos
                    )
                    // Proximity dashed border
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.40f),
                        radius = radiusPx,
                        center = farmerPos,
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Farmer radar pulse circles
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.25f),
                        radius = 18.dp.toPx() * zoomScale.coerceAtLeast(1f),
                        center = farmerPos
                    )
                    drawCircle(
                        color = primaryColor,
                        radius = 6.dp.toPx(),
                        center = farmerPos
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.5.dp.toPx(),
                        center = farmerPos
                    )

                    // Farmer label
                    val farmerPaint = Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 9.sp.toPx()
                        textAlign = Paint.Align.CENTER
                        typeface = Typeface.DEFAULT_BOLD
                        isAntiAlias = true
                    }
                    val badgeWidth = 72.dp.toPx()
                    val badgeHeight = 16.dp.toPx()
                    drawRoundRect(
                        color = primaryColor,
                        topLeft = Offset(farmerPos.x - badgeWidth / 2, farmerPos.y + 10.dp.toPx()),
                        size = Size(badgeWidth, badgeHeight),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        if (language == AppLanguage.TAMIL) "உங்கள் பண்ணை" else "Your Farm",
                        farmerPos.x,
                        farmerPos.y + 22.dp.toPx(),
                        farmerPaint
                    )

                    // 5. Draw Store Markers with Live Price Pills!
                    val priceTextPaint = Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 10.sp.toPx()
                        textAlign = Paint.Align.CENTER
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        isAntiAlias = true
                    }

                    val priceSubTextPaint = Paint().apply {
                        color = android.graphics.Color.argb(220, 255, 255, 255)
                        textSize = 8.sp.toPx()
                        textAlign = Paint.Align.CENTER
                        typeface = Typeface.DEFAULT_BOLD
                        isAntiAlias = true
                    }

                    stores.forEach { store ->
                        val isSelected = store.id == selectedStore?.id
                        val pinPos = project(store.mapOffsetX, store.mapOffsetY)

                        // Store type base color
                        val pinColor = when {
                            store.storeType.contains("PACCS") -> Color(0xFF1B5E20) // Deep Forest Green (Co-op)
                            store.storeType.contains("Organic") || store.storeType.contains("FPO") -> Color(0xFF00796B) // Teal
                            else -> Color(0xFFE65100) // Deep Harvest Orange (Dealer)
                        }

                        // Determine live price to render on map pin
                        val priceData = store.getDisplayPrice(categoryFilter, specificFertilizerFilter)
                        val price = priceData?.first ?: (store.products.firstOrNull()?.subsidizedPrice ?: 266)
                        val inStock = priceData?.second ?: true

                        // If selected: Draw radiant glow animation
                        if (isSelected) {
                            drawCircle(
                                color = pinColor.copy(alpha = 0.28f),
                                radius = 22.dp.toPx(),
                                center = pinPos
                            )
                            drawCircle(
                                color = pinColor.copy(alpha = 0.45f),
                                radius = 15.dp.toPx(),
                                center = pinPos
                            )
                        }

                        // Draw Pin marker circle
                        drawCircle(
                            color = pinColor,
                            radius = if (isSelected) 10.dp.toPx() else 8.dp.toPx(),
                            center = pinPos
                        )
                        drawCircle(
                            color = Color.White,
                            radius = if (isSelected) 4.5.dp.toPx() else 3.5.dp.toPx(),
                            center = pinPos
                        )

                        // Draw Floating Price Tag Pill above the Pin
                        val pricePillBgColor = if (inStock) pinColor else Color(0xFF757575)
                        val pillWidth = (if (price >= 1000) 56.dp else 48.dp).toPx()
                        val pillHeight = 20.dp.toPx()
                        val pillTopLeft = Offset(pinPos.x - pillWidth / 2, pinPos.y - pillHeight - 12.dp.toPx())

                        // Pill background rounded rect
                        drawRoundRect(
                            color = pricePillBgColor,
                            topLeft = pillTopLeft,
                            size = Size(pillWidth, pillHeight),
                            cornerRadius = CornerRadius(6.dp.toPx())
                        )
                        // Pill white border
                        drawRoundRect(
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                            topLeft = pillTopLeft,
                            size = Size(pillWidth, pillHeight),
                            cornerRadius = CornerRadius(6.dp.toPx()),
                            style = Stroke(width = if (isSelected) 2.dp.toPx() else 1.dp.toPx())
                        )

                        // Stock dot inside pill
                        val stockDotColor = if (inStock) Color(0xFF76FF03) else Color(0xFFFF5252)
                        drawCircle(
                            color = stockDotColor,
                            radius = 2.5.dp.toPx(),
                            center = Offset(pillTopLeft.x + 7.dp.toPx(), pillTopLeft.y + pillHeight / 2)
                        )

                        // Draw price text
                        val priceStr = "₹$price"
                        drawContext.canvas.nativeCanvas.drawText(
                            priceStr,
                            pillTopLeft.x + (pillWidth / 2) + 2.dp.toPx(),
                            pillTopLeft.y + 14.dp.toPx(),
                            priceTextPaint
                        )
                    }
                }

                // Map Overlay Controls (Top Right: Zoom In, Zoom Out, Reset Center)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SmallFloatingActionButton(
                        onClick = { zoomScale = (zoomScale + 0.3f).coerceAtMost(2.5f) },
                        containerColor = surfaceColor.copy(alpha = 0.9f),
                        contentColor = primaryColor,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("map_zoom_in_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
                    }

                    SmallFloatingActionButton(
                        onClick = { zoomScale = (zoomScale - 0.3f).coerceAtLeast(0.8f) },
                        containerColor = surfaceColor.copy(alpha = 0.9f),
                        contentColor = primaryColor,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("map_zoom_out_button")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
                    }

                    SmallFloatingActionButton(
                        onClick = {
                            zoomScale = 1.0f
                            panOffset = Offset.Zero
                        },
                        containerColor = surfaceColor.copy(alpha = 0.9f),
                        contentColor = primaryColor,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("map_reset_center_button")
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Center on Farm", modifier = Modifier.size(18.dp))
                    }
                }

                // Map Legend Overlay (Bottom Left)
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = surfaceColor.copy(alpha = 0.92f),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(8.dp).background(Color(0xFF1B5E20), CircleShape))
                            Spacer(Modifier.width(4.dp))
                            Text("PACCS Co-op (Govt)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Box(Modifier.size(8.dp).background(Color(0xFF00796B), CircleShape))
                            Spacer(Modifier.width(4.dp))
                            Text(if (language == AppLanguage.TAMIL) "இயற்கை FPO" else "Organic FPO", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(8.dp).background(Color(0xFFE65100), CircleShape))
                            Spacer(Modifier.width(4.dp))
                            Text(if (language == AppLanguage.TAMIL) "தனியார் அங்காடி" else "Private Dealer", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Box(Modifier.size(6.dp).background(Color(0xFF76FF03), CircleShape))
                            Spacer(Modifier.width(4.dp))
                            Text(if (language == AppLanguage.TAMIL) "இருப்பு உண்டு" else "In Stock", fontSize = 8.sp)
                        }
                    }
                }
            }

            // Selected Store Detail Drawer Card
            AnimatedVisibility(
                visible = selectedStore != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 3 })
            ) {
                selectedStore?.let { store ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("selected_store_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, primaryColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Store Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (language == AppLanguage.TAMIL) store.tamilName else store.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsWalk,
                                            contentDescription = "Distance",
                                            tint = primaryColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${store.distanceKm} km away",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = primaryColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = " • ${store.taluk}, ${store.district}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (store.isGovtAided) primaryColor.copy(alpha = 0.15f) else secondaryColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (store.isGovtAided)
                                            (if (language == AppLanguage.TAMIL) "100% அரசு மானியம்" else "Govt Subsidized")
                                        else
                                            (if (language == AppLanguage.TAMIL) "அங்கீகரிக்கப்பட்டது" else "Authorized Dealer"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (store.isGovtAided) primaryColor else secondaryColor,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Current Fertilizer Pricing & Stock List in this Store
                            Text(
                                text = if (language == AppLanguage.TAMIL) "நேரடி உர விலை பட்டியல்:" else "Current Fertilizer Pricing & Stock:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            val productsToShow = if (categoryFilter == null) store.products else store.products.filter { it.category == categoryFilter }

                            productsToShow.take(3).forEach { product ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (language == AppLanguage.TAMIL) product.tamilName else product.name,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${product.brand} • ${product.bagSize}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            val price = product.subsidizedPrice ?: product.standardPrice
                                            Text(
                                                text = "₹$price",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (product.subsidizedPrice != null) primaryColor else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (product.inStock)
                                                    (if (language == AppLanguage.TAMIL) "இருப்பில் உள்ளது" else "In Stock")
                                                else
                                                    (if (language == AppLanguage.TAMIL) "இருப்பில்லை" else "Out of Stock"),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (product.inStock) Color(0xFF2E7D32) else Color(0xFFC62828),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons: Call Store & Directions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val phoneNumber = store.phone.replace("-", "").replace(" ", "")
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                                        try {
                                            context.startActivity(dialIntent)
                                        } catch (e: Exception) {
                                            // Handle exception gracefully
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("call_store_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (language == AppLanguage.TAMIL) "தொடர்பு கொள்க" else "Call Store",
                                        fontSize = 12.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        val lat = store.latitude ?: 10.7870
                                        val lng = store.longitude ?: 79.1378
                                        val label = if (language == AppLanguage.TAMIL) store.tamilName else store.name
                                        val mapUri = Uri.parse("geo:$lat,$lng?q=${Uri.encode(label)}")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                                        try {
                                            context.startActivity(mapIntent)
                                        } catch (e: Exception) {
                                            onNavigateDirections?.invoke(store)
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("navigate_store_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Navigation, contentDescription = "Directions", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (language == AppLanguage.TAMIL) "வழித்தடம்" else "Directions",
                                        fontSize = 12.sp
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
