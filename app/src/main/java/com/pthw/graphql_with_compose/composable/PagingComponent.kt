package com.pthw.graphql_with_compose.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pthw.graphql_with_compose.ui.theme.GraphqlwithcomposeTheme

/**
 * Created by P.T.H.W on 06/04/2024.
 */

@Composable
fun PageLoader(modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Loading from server..",
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        CircularProgressIndicator(Modifier.padding(top = 10.dp))
    }
}

@Composable
fun PageEmpty(modifier: Modifier) {
    Box(modifier = modifier) {
        TitleTextView(
            modifier = Modifier.align(Alignment.Center),
            text = "No Data",
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
        )
    }
}

@Composable
fun LoadingNextPageItem(modifier: Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier,
    onClickRetry: () -> Unit
) {
    Row(
        modifier = modifier.padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f),
            maxLines = 2
        )
        OutlinedButton(onClick = onClickRetry) {
            Text(text = "Try again!")
        }
    }
}


@Preview
@Composable
private fun LoadingNextPageItemPreview() {
    GraphqlwithcomposeTheme {
        Surface {
            LoadingNextPageItem(modifier = Modifier)
        }
    }
}

@Preview
@Composable
private fun PageLoaderPreview() {
    GraphqlwithcomposeTheme {
        Surface {
            PageLoader(modifier = Modifier)
        }
    }
}

@Preview
@Composable
private fun ErrorMessagePreview() {
    GraphqlwithcomposeTheme {
        Surface {
            ErrorMessage(message = "Something went wrong!", onClickRetry = {})
        }
    }
}

@Preview
@Composable
private fun PageEmptyPreview() {
    GraphqlwithcomposeTheme {
        Surface {
            PageEmpty(Modifier)
        }
    }
}