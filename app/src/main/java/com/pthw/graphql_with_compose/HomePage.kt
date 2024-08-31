package com.pthw.graphql_with_compose

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.pthw.graphql_with_compose.composable.CoilAsyncImage
import com.pthw.graphql_with_compose.composable.ErrorMessage
import com.pthw.graphql_with_compose.composable.LoadingNextPageItem
import com.pthw.graphql_with_compose.composable.PageEmpty
import com.pthw.graphql_with_compose.composable.PageLoader
import com.pthw.graphql_with_compose.data.RepoVo
import com.pthw.graphql_with_compose.ui.theme.Dimens
import com.pthw.graphql_with_compose.ui.theme.GraphqlwithcomposeTheme
import kotlinx.coroutines.flow.flowOf


/**
 * Created by P.T.H.W on 29/08/2024.
 */
@Composable
fun HomePage(
    viewModel: HomePageViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    PageContent(repos = viewModel.repos.collectAsLazyPagingItems()) {
        when (it) {
            is UiEvent.SearchRepos -> viewModel.updateSearchQuery(it.query)
            is UiEvent.ItemClick -> context.openUrl(it.url)
        }
    }
}

sealed class UiEvent {
    data class SearchRepos(val query: String) : UiEvent()
    data class ItemClick(val url: String) : UiEvent()
}

@Composable
private fun PageContent(
    repos: LazyPagingItems<RepoVo>,
    onAction: (UiEvent) -> Unit = {}
) {

    LaunchedEffect(Unit) {
        onAction(UiEvent.SearchRepos("Android"))
    }

    Scaffold(modifier = Modifier.fillMaxSize()) {
        var pagingLoadState by remember { mutableStateOf<CombinedLoadStates?>(null) }

        Column(
            modifier = Modifier.padding(it)
        ) {
            // search
            HomeSearchBarView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                hint = "Search"
            ) { query ->
                onAction(UiEvent.SearchRepos(query))
            }

            // list
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(repos.itemCount) { index ->
                    repos[index]?.let { repo ->
                        RepoListItem(onAction, repo)
                    }
                }
                repos.apply {
                    pagingLoadState = loadState
                    when {
                        loadState.refresh is LoadState.Error -> {
                            val error = repos.loadState.refresh as LoadState.Error
                            item {
                                ErrorMessage(
                                    modifier = Modifier,
                                    message = error.error.localizedMessage!!,
                                    onClickRetry = { retry() })
                            }
                        }

                        loadState.append is LoadState.Loading -> {
                            item { LoadingNextPageItem(modifier = Modifier) }
                        }

                        loadState.append is LoadState.Error -> {
                            val error = repos.loadState.append as LoadState.Error
                            item {
                                ErrorMessage(
                                    modifier = Modifier,
                                    message = error.error.localizedMessage!!,
                                    onClickRetry = { retry() })
                            }
                        }
                    }
                }

            }

        }

        // loadState handle
        when {
            pagingLoadState?.refresh is LoadState.NotLoading && repos.itemCount == 0 ->
                PageEmpty(Modifier.fillMaxSize())

            pagingLoadState?.refresh is LoadState.Loading && repos.itemCount == 0 ->
                PageLoader(Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun RepoListItem(
    onAction: (UiEvent) -> Unit,
    it: RepoVo
) {
    Column(
        modifier = Modifier.clickable {
            onAction(UiEvent.ItemClick(it.url))
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 16.dp, top = Dimens.MARGIN_MEDIUM)
            ) {
                Text(
                    text = it.nameWithOwner,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier
                        .alpha(0.9f),
                    text = it.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "",
                        tint = Color.Yellow.copy(green = 0.7f),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it.stargazerCount.toString(),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.width(20.dp))
                    it.language?.let {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(10.dp)
                                .background(color = Color(it.color.toColorInt()))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            it.owner?.let {
                CoilAsyncImage(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable {
                            onAction(UiEvent.ItemClick(it.url))
                        },
                    imageUrl = it.avatarUrl
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 10.dp))
    }
}

@Composable
fun HomeSearchBarView(
    modifier: Modifier,
    hint: String,
    onValueChange: (String) -> Unit
) {
    var textSearchBox by rememberSaveable { mutableStateOf("Android") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color.LightGray)
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search_normal),
            contentDescription = ""
        )
        TextField(
            value = textSearchBox,
            onValueChange = {
                textSearchBox = it
                onValueChange(it)
            },
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            ),
            placeholder = {
                Text(
                    text = hint,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    fontSize = 14.sp
                )
            },
            trailingIcon = {
                AnimatedVisibility(textSearchBox.length > 1) {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                textSearchBox = ""
                                onValueChange("")
                            }
                            .padding(8.dp),
                        painter = painterResource(id = R.drawable.ic_delete_search),
                        contentDescription = ""
                    )
                }
            }
        )
    }

}

private fun Context.openUrl(url: String) {
    val i = Intent(Intent.ACTION_VIEW)
    i.setData(Uri.parse(url))
    startActivity(i)
}

@Preview
@Composable
private fun PageContentPreview() {
    GraphqlwithcomposeTheme {
        PageContent(repos = flowOf(PagingData.from(RepoVo.fakeList())).collectAsLazyPagingItems())
    }
}