package com.example.khoitriso.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khoitriso.R
import com.example.khoitriso.utils.toVND

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailHeader(
    onBack: () -> Unit,
    title: String = "",
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            if (title.isNotEmpty())
                Text(
                    title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier
    )
}

@Composable
fun ActionButtons(
    price: Double, 
    onCart: () -> Unit,
    isInWishlist: Boolean = false,
    onWishlistClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Wishlist button
            IconButton(
                onClick = onWishlistClick,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (isInWishlist) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isInWishlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Wishlist",
                    modifier = Modifier.size(24.dp),
                    tint = if (isInWishlist) Color(0xFFFF6B6B) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Add to cart button
            Button(
                onClick = onCart,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    Icons.Outlined.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.add_to_cart),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                VerticalDivider(
                    modifier = Modifier.height(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = price.toVND(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CreateBy(fullName: String, avatar: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        SafeImage(
            url = avatar,
            contentDescription = fullName,
            error = R.drawable.avatar_default,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column {
            Text(
                text = stringResource(R.string.created_by),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

