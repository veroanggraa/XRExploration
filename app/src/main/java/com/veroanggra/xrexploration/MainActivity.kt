package com.veroanggra.xrexploration

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.xr.compose.platform.LocalHasXrSpatialFeature
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.spatial.EdgeOffset
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.OrbiterEdge
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.layout.SpatialRoundedCornerShape
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.movable
import androidx.xr.compose.subspace.layout.offset
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.width
import com.veroanggra.xrexploration.data.Articles
import com.veroanggra.xrexploration.data.getDummyNewsArticles
import com.veroanggra.xrexploration.ui.theme.XRExplorationTheme
import com.veroanggra.xrexploration.view.ArticleDetailScreen
import com.veroanggra.xrexploration.view.ArticleListScreen

class MainActivity : ComponentActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            XRExplorationTheme {
                val session = LocalSession.current
                if (LocalSpatialCapabilities.current.isSpatialUiEnabled) {
                    Subspace {
                        MySpatialContent(onRequestHomeSpaceMode = { session?.requestHomeSpaceMode() })
                    }
                } else {
                    My2DContent(onRequestFullSpaceMode = { session?.requestFullSpaceMode() })
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun MySpatialContent(onRequestHomeSpaceMode: () -> Unit) {
    var selectedArticle by remember { mutableStateOf<Articles?>(null) }
    val articles = remember { getDummyNewsArticles() }

    SpatialPanel(
        SubspaceModifier
            .width(if (selectedArticle == null) 1280.dp else 640.dp) // Dynamic width
            .height(800.dp)
            .resizable()
            .movable()
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
            ArticleListScreen(
                articles = articles,
                onArticleClick = { article -> selectedArticle = article },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        Orbiter(
            position = OrbiterEdge.Top,
            offset = EdgeOffset.inner(offset = 20.dp),
            alignment = Alignment.End,
            shape = SpatialRoundedCornerShape(CornerSize(28.dp))
        ) {
            HomeSpaceModeIconButton(
                onClick = onRequestHomeSpaceMode, modifier = Modifier.size(56.dp)
            )
        }
    }

    selectedArticle?.let { currentArticle ->
        SpatialPanel(
            SubspaceModifier
                .width(640.dp)
                .height(800.dp)
                .resizable()
                .movable()
                .offset(x = 650.dp)
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                ArticleDetailScreen(
                    articles = currentArticle,
                    onBack = { selectedArticle = null },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(48.dp)
                )
            }
            Orbiter(
                position = OrbiterEdge.Top,
                offset = EdgeOffset.inner(offset = 20.dp),
                alignment = Alignment.Start,
                shape = SpatialRoundedCornerShape(CornerSize(28.dp))
            ) {
                FilledTonalIconButton(
                    onClick = { selectedArticle = null },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back to list"
                    )
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun My2DContent(onRequestFullSpaceMode: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) { // Or your app's background color
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MainContent(modifier = Modifier.weight(1f).padding(start = 48.dp, top = 48.dp, bottom = 48.dp)) // Allow MainContent to take available space
            if (LocalHasXrSpatialFeature.current) {
                FullSpaceModeIconButton(
                    onClick = onRequestFullSpaceMode,
                    modifier = Modifier.padding(top = 32.dp, end = 32.dp) // Adjusted padding
                )
            }
        }
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    var selectedArticle by remember { mutableStateOf<Articles?>(null) }
    val articles = remember { getDummyNewsArticles() }

    if (selectedArticle == null) {
        ArticleListScreen(
            articles = articles,
            onArticleClick = { article -> selectedArticle = article },
            modifier = modifier
        )
    } else {
        ArticleDetailScreen(
            articles = selectedArticle!!,
            onBack = { selectedArticle = null },
            modifier = modifier
        )
    }
}

@Composable
fun FullSpaceModeIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_full_space_mode_switch),
            contentDescription = stringResource(R.string.switch_to_full_space_mode)
        )
    }
}

@Composable
fun HomeSpaceModeIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalIconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_home_space_mode_switch),
            contentDescription = stringResource(R.string.switch_to_home_space_mode)
        )
    }
}