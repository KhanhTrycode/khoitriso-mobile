package com.example.khoitriso.ui.cart

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.outlined.ShoppingCart
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.khoitriso.R
import com.example.khoitriso.domain.models.CartItem
import com.example.khoitriso.domain.models.Carts
import com.example.khoitriso.ui.common.SafeImage
import com.example.khoitriso.ui.common.ErrorCard
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.toVND
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CartScreen(
    navController: NavHostController,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val cartState by viewModel.carts.collectAsState()
    var showClearCartDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.loadCart()
    }
    if (showClearCartDialog) {
        AlertDialog(
            onDismissRequest = { showClearCartDialog = false },
            title = {
                Text(text = "Xóa giỏ hàng?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Bạn có chắc chắn muốn xóa tất cả sản phẩm trong giỏ hàng không?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearCart() // Cần thêm hàm này trong ViewModel
                        showClearCartDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa tất cả")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCartDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.cart_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Chỉ hiện nút xóa nếu load thành công và giỏ hàng không rỗng
                    if (cartState is UiState.Success && (cartState as UiState.Success).data.cartItems.isNotEmpty()) {
                        IconButton(onClick = { showClearCartDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep, // Icon quét sạch
                                contentDescription = "Clear Cart",
                                tint = MaterialTheme.colorScheme.error // Màu đỏ cảnh báo
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        bottomBar = {
            when (val state = cartState) {
                is UiState.Success -> {
                    if (state.data.cartItems.isNotEmpty()) {
                        CartBottomBar(
                            totalAmount = state.data.totalPrice,
                            itemCount = state.data.totalItems,
                            onCheckout = { navController.navigate(NavRoute.CHECKOUT) }
                        )
                    }
                }
                else -> {}
            }
        }
    ) { paddingValues ->
        when (val state = cartState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                }
            }

            is UiState.Error -> {
                ErrorCard(
                    message = state.message,
                    onRetry = { viewModel.loadCart() },
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is UiState.Success -> {
                val cart = state.data
                if (cart.cartItems.isEmpty()) {
                    EmptyCartScreen(
                        onContinueShopping = {
                            navController.navigate(NavRoute.HOME) {
                                popUpTo(NavRoute.HOME) { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp), // Padding 2 bên
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        // Header nhỏ hiển thị số lượng
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCartCheckout,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Bạn có ${cart.totalItems} sản phẩm trong giỏ",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        items(
                            items = cart.cartItems,
                            key = { it.id }
                        ) { item ->
                            // Modifier.animateItemPlacement() giúp item trượt lên mượt mà khi xóa item phía trên
                            Box() {
                                CartItemCard(
                                    item = item,
                                    onRemove = { viewModel.removeItem(item.id) },
                                    isRemoving = false,
                                    onItemClick = {
                                        when (item.itemType) {
                                            0 -> navController.navigate(NavRoute.NavBookDetail(item.itemId))
                                            1 -> navController.navigate(NavRoute.NavCourseDetail(item.itemId))
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
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onRemove: () -> Unit,
    isRemoving: Boolean,
    onItemClick: () -> Unit,
) {
    // Sử dụng ElevatedCard để nổi bật trên nền
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(IntrinsicSize.Min) // Để chiều cao Row phụ thuộc vào content cao nhất
        ) {
            // 1. Hình ảnh
            SafeImage(
                url = item.coverImage,
                contentDescription = item.title,
                modifier = Modifier
                    .width(90.dp)
                    .aspectRatio(0.75f) // Tỉ lệ 3:4 phù hợp cho sách/khóa học
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant), // Placeholder background
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Nội dung text
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Tag loại sản phẩm (Book/Course)
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = when (item.itemType) {
                                    0 -> stringResource(R.string.book)
                                    1 -> stringResource(R.string.course)
                                    else -> "Item"
                                },
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        border = null,
                        modifier = Modifier.height(24.dp) // Chip nhỏ gọn
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = MaterialTheme.typography.titleMedium.lineHeight * 1.1
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Giá và Nút xóa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = item.price.toVND(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    IconButton(
                        onClick = onRemove,
                        enabled = !isRemoving,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    ) {
                        if (isRemoving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = stringResource(R.string.remove_item),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartBottomBar(
    totalAmount: Double,
    itemCount: Int,
    onCheckout: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 16.dp, // Đổ bóng mạnh tạo cảm giác nổi
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Row tổng tiền
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.total),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "($itemCount items)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Text(
                    text = totalAmount.toVND(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp), // Nút cao to dễ bấm
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.checkout).uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    letterSpacing = MaterialTheme.typography.labelLarge.letterSpacing
                )
            }
        }
    }
}

@Composable
private fun EmptyCartScreen(
    onContinueShopping: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Vòng tròn nền cho icon
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.cart_empty),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.cart_empty_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        FilledTonalButton(
            onClick = onContinueShopping,
            modifier = Modifier.height(48.dp)
        ) {
            Text(stringResource(R.string.continue_shopping))
        }
    }
}

