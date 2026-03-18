package com.d_shield_parent.presentation.auth

import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.activity.compose.BackHandler
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.d_shield_parent.Dashboard.ProfileViewModel
import com.d_shield_parent.R
import kotlinx.coroutines.delay

// ══════════════════════════════════════════════════════════════
//  THEME: GOLD × WHITE  —  Clean Light Dashboard
//  Gold  = Authority / Premium / Brand
//  White = Clean / Professional / Open
// ══════════════════════════════════════════════════════════════

private val Gold        = Color(0xFFCFA849)
private val GoldSoft    = Color(0xFFE8C97A)
private val GoldDim     = Color(0xFF8A6E28)
private val GoldLight   = Color(0xFFFDF3DC)   // very light gold tint for card bg

private val BgScreen    = Color(0xFFF8F6F2)   // off-white / warm white
private val BgCard      = Color(0xFFFFFFFF)   // pure white card
private val TextPrimary = Color(0xFF1A1200)   // near-black warm
private val TextSub     = Color(0xFF6B5B2E)   // warm dark-gold for sub text

data class ServiceItem(
    val title: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val route: String
)


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    BackHandler {
        (context as? Activity)?.finish()
    }
    val profileData by viewModel.profileData.collectAsState()
    Log.d("HomeScreen", "walletBalance = ${profileData.walletBalance}")

    val serviceItems = listOf(
        ServiceItem("Add Customer",  Icons.Default.PersonAdd,             Gold,              Color(0xFFFFF8E1), "add_customer_screen"),
      ServiceItem("Customer List", Icons.Default.People,                Color(0xFF5B8DCA), Color(0xFFE8F0FA), "customer_list_screen"),
//        ServiceItem("Profile",       Icons.Default.Person,                Color(0xFF7B6EAA), Color(0xFFF0ECFA), "profile_screen"),
        ServiceItem("Service",       Icons.Default.MiscellaneousServices, Color(0xFF43A891), Color(0xFFE4F6F3), "service_screen"),
        ServiceItem("Help",          Icons.Default.Help,                  Color(0xFFE07B3A), Color(0xFFFEF0E6), "help_screen"),
        ServiceItem("Setup M-Pin",   Icons.Default.Lock,                  Color(0xFFCFA849), Color(0xFFFFF8E1), "setup_mpin_screen"),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgScreen)
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            AnimatedHeader(
                username         = profileData.shop_name,
                profileImageUri  = profileData.profileImageUri,
           onProfileClick   = {  }
            )

            Spacer(modifier = Modifier.height(16.dp))
            BannerSection()
            Spacer(modifier = Modifier.height(20.dp))

            StatsRow(
                activeUsers   = profileData.activeDevices.toString(),
                walletBalance = profileData.walletBalance,
                enrollDevices = profileData.enrolledDevices.toString()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Section Header ─────────────────────────────────────────
            Row(
                modifier          = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(22.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Brush.verticalGradient(listOf(Gold, GoldSoft)))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Services",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = TextPrimary
                )

            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Service Grid ────────────────────────────────────────────
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                serviceItems.forEachIndexed { index, item ->
                    AnimatedServiceCard(
                        item           = item,
                        animationIndex = index,
                        modifier       = Modifier.fillMaxWidth(),  // ← full width
                        onClick        = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// ── Header ────────────────────────────────────────────────────
@Composable
private fun AnimatedHeader(
    username: String,
    profileImageUri: Any?,
    onProfileClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter   = slideInVertically(initialOffsetY = { -it }) + fadeIn(tween(400))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            // Gold accent bottom separator line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,
                                0.3f to GoldSoft,
                                0.7f to Gold,
                                1.0f to Color.Transparent
                            )
                        )
                    )
                    .align(Alignment.BottomCenter)
            )

            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 14.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    // Avatar with Gold ring
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(Brush.linearGradient(listOf(Gold, GoldSoft)), CircleShape)
                            .padding(2.dp)
                            .background(Color.White, CircleShape)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onProfileClick)
                    ) {
                        Image(
                            painter = if (profileImageUri != null)
                                rememberAsyncImagePainter(profileImageUri)
                            else
                                painterResource(id = R.drawable.placeholder),
                            contentDescription = "Profile",
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(6.dp).background(Color(0xFF43A891), CircleShape))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                "SECURED SESSION",
                                fontSize      = 9.sp,
                                color         = Color(0xFF43A891),
                                fontWeight    = FontWeight.ExtraBold,
                                letterSpacing = 1.4.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(username, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        Text("Welcome back 👋", fontSize = 11.sp, color = GoldDim)
                    }
                }

                // Notification Bell
//                Box(
//                    modifier = Modifier
//                        .size(44.dp)
//                        .background(GoldLight, RoundedCornerShape(13.dp))
//                        .border(1.dp, Gold.copy(alpha = 0.5f), RoundedCornerShape(13.dp)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(Icons.Default.Notifications, null, tint = Gold, modifier = Modifier.size(22.dp))
//                    Box(
//                        modifier = Modifier
//                            .size(8.dp)
//                            .background(Color(0xFFFF3B3B), CircleShape)
//                            .align(Alignment.TopEnd)
//                            .offset(x = (-4).dp, y = 4.dp)
//                    )
//                }
            }
        }
    }
}

// ── Banner ────────────────────────────────────────────────────
@Composable
private fun BannerSection() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(200); visible = true }

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(600)) + scaleIn(initialScale = 0.95f, animationSpec = tween(600))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .height(170.dp)
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(Gold.copy(alpha = 0.6f), GoldSoft.copy(alpha = 0.3f), Color.Transparent)),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Image(
                painter            = painterResource(id = R.drawable.homebanner),
                contentDescription = null,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
            // Dark overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Black.copy(alpha = 0.05f),
                                0.5f to Color.Transparent,
                                1.0f to Color.Black.copy(alpha = 0.50f)
                            )
                        )
                    )
            )

            // PROTECTED badge
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(Brush.linearGradient(listOf(GoldDim, Color(0xFF5C3D00))), RoundedCornerShape(8.dp))
                    .border(1.dp, Gold.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Shield, null, tint = GoldSoft, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PROTECTED", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = GoldSoft, letterSpacing = 1.2.sp)
            }

            // Bottom label
            Row(
                modifier          = Modifier.align(Alignment.BottomStart).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.width(3.dp).height(18.dp).background(Gold, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Secure & Reliable Platform", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Stats Row ─────────────────────────────────────────────────
@Composable
private fun StatsRow(activeUsers: String, walletBalance: String, enrollDevices: String) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(300); visible = true }

    AnimatedVisibility(
        visible = visible,
        enter   = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(tween(500))
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(Modifier.weight(1f), Icons.Default.Person,               activeUsers,   "Active Users", Color(0xFF5B8DCA), Color(0xFFE8F0FA), Color(0xFF5B8DCA))
            StatCard(Modifier.weight(1f), Icons.Default.AccountBalanceWallet, walletBalance, "Points",       Gold,              GoldLight,          Gold)
            StatCard(Modifier.weight(1f), Icons.Default.PermDeviceInformation,enrollDevices, "Devices",      Color(0xFF43A891), Color(0xFFE4F6F3), Color(0xFF43A891))
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    iconColor: Color,
    iconBg: Color,
    borderColor: Color
) {
    Card(
        modifier  = modifier.height(82.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, borderColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
        ) {
            // Top thin color line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, borderColor.copy(alpha = 0.7f), Color.Transparent)
                        )
                    )
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            Column(
                modifier              = Modifier.fillMaxSize(),
                horizontalAlignment   = Alignment.CenterHorizontally,
                verticalArrangement   = Arrangement.Center
            ) {
                Box(
                    modifier         = Modifier.size(28.dp).background(iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                Text(label, fontSize = 9.sp, color = TextSub, letterSpacing = 0.2.sp)
            }
        }
    }
}

// ── Animated Service Card ─────────────────────────────────────
@Composable
private fun AnimatedServiceCard(
    item: ServiceItem,
    animationIndex: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue   = if (visible) 1f else 0.85f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label         = "scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val iconOffset by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = -4f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconFloat"
    )

    AnimatedVisibility(
        visible  = visible,
        enter    = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
        modifier = modifier
    ) {
        Card(
            modifier  = Modifier
                .scale(scale)
                .fillMaxWidth()   // ← full width, NOT half
                .height(76.dp),   // ← 76 NOT 128
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = BgCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = interactionSource,
                        indication        = ripple(color = Gold.copy(alpha = 0.2f)),
                        onClick           = onClick
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            colorStops = arrayOf(
                                0.0f to Gold.copy(alpha = 0.30f),
                                0.6f to item.iconTint.copy(alpha = 0.15f),
                                1.0f to Color.Transparent
                            )
                        ),
                        RoundedCornerShape(16.dp)
                    )
            ) {

                // ── Top color line ───────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    item.iconTint.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            )
                        )
                        .align(Alignment.TopCenter)
                )

                // ── Decorative blob top-right ────────────────────
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 16.dp, y = (-16).dp)
                        .clip(CircleShape)
                        .background(item.iconBg)
                )

                // ── ROW layout (icon | title | arrow) ───────────
                Row(
                    modifier              = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Animated icon box
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(item.iconBg)
                            .border(1.dp, item.iconTint.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .offset(y = iconOffset.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = item.icon,
                            contentDescription = item.title,
                            tint               = item.iconTint,
                            modifier           = Modifier.size(24.dp)
                        )
                    }

                    // Title — takes all remaining space
                    Text(
                        text       = item.title,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                        modifier   = Modifier.weight(1f)
                    )

                    // Arrow
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Gold.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .border(0.5.dp, Gold.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            tint               = Gold,
                            modifier           = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }
    }
}
//869450045151112