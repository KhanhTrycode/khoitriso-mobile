package com.example.khoitriso.ui.loginscreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.R
import com.example.khoitriso.utils.NavRoute
import androidx.compose.runtime.getValue
import com.example.khoitriso.utils.UiState

data class IntroSlide(
    val title: Int,
    val description: Int,
    val imageRes: Int,
)

val slides = listOf(
    IntroSlide(R.string.slider_1, R.string.slider_1_desc, R.drawable.ic_launcher_background),
    IntroSlide(R.string.slider_2, R.string.slider_2_desc, R.drawable.ic_launcher_background),
    IntroSlide(R.string.slider_3, R.string.slider_3_desc, R.drawable.ic_launcher_background),
    IntroSlide(R.string.slider_4, R.string.slider_4_desc, R.drawable.ic_launcher_background)
)

@Composable
fun SignInButton(
    modifier: Modifier = Modifier,
    txtButton: String,
    iconVector: Int,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = iconVector),
                contentDescription = "Google Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = txtButton,
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Bỏ FacebookSignInButton nếu không dùng, tôi sẽ giữ lại các composable khác
// để giữ sự nhất quán với mã của bạn

@Composable
fun SliderPage(page: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val slide = slides[page]
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(slide.imageRes),
                contentDescription = stringResource(slide.title),
                modifier = Modifier.size(200.dp)
            )
            Text(
                text = stringResource(slide.title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(slide.description), // Dùng description thay vì title
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun Slider(pagerState: PagerState, modifier: Modifier = Modifier) {

    HorizontalPager(
        state = pagerState,
        pageSpacing = 16.dp,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
    ) { page ->
        SliderPage(page)
    }
}

@Composable
fun PageIndicator(pagerState: PagerState, modifier: Modifier = Modifier) {
    Row(
        modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color =
                if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(16.dp)
            )
        }
    }
}

@Composable
fun LoginArea(
    navController: NavController, viewModel: LoginViewModel,
    modifier: Modifier =
        Modifier,
) {
    val context = LocalContext.current
    Divider(color = Color.LightGray, thickness = 1.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(Modifier.padding(horizontal = 5.dp)) {
            Text("Học viên đăng nhập với", style = MaterialTheme.typography.titleMedium)
        }

        // Button Google Sign-In thực sự
        SignInButton(
            txtButton = "Google",
            iconVector = R.drawable.icon_google,
            onClick = {
                viewModel.startGoogleSignIn(context)
            }
        )
        SignInButton(
            txtButton = " google",
            iconVector = R.drawable.icon_google,
            onClick = {
                navController.navigate(NavRoute.HOME) {
                    // Đảm bảo người dùng không thể quay lại màn hình đăng nhập
                    popUpTo(NavRoute.LOGIN) {
                        inclusive = true
                    }
                }
            }
        )

    }
}

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val tokenState by viewModel.token.collectAsState()
    val pagerState = rememberPagerState(pageCount = { slides.size })
    LaunchedEffect(tokenState) {
        if (tokenState is UiState.Success) {
            navController.navigate(NavRoute.HOME) {
                // Đảm bảo người dùng không thể quay lại màn hình đăng nhập
                popUpTo(NavRoute.LOGIN) {
                    inclusive = true
                }
            }
        }
    }
    val googleLauncher = rememberLauncherForActivityResult(
        contract     = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            viewModel.handleGoogleSignInResult(result.data)
        }
    )
    LaunchedEffect(Unit) {
        viewModel.setGoogleSignInLauncher(googleLauncher)
    }

    Column(modifier = modifier.fillMaxSize()) {

        Slider(pagerState, Modifier.weight(0.6f))
        PageIndicator(pagerState)

        Divider(color = Color.LightGray, thickness = 1.dp)

        LoginArea(navController, viewModel = viewModel, modifier = Modifier.weight(0.3f))
    }
    when (val state = tokenState) {
        UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)), // Lớp phủ mờ
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 200.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            }
        }

        else -> {}
    }
}

