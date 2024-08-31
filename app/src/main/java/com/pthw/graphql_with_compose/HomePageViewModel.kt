package com.pthw.graphql_with_compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.apollographql.apollo.ApolloClient
import com.pthw.graphql_with_compose.data.RepoPagingSource
import com.pthw.graphql_with_compose.data.RepoVo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by P.T.H.W on 29/08/2024.
 */
@Suppress("OPT_IN_USAGE")
@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val apolloClient: ApolloClient
) : ViewModel() {
    var repos = emptyFlow<PagingData<RepoVo>>()
    private var searchQuery = MutableStateFlow("")

    init {
        getSearchMovies()
    }

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            searchQuery.value = query
        }
    }

    @OptIn(FlowPreview::class)
    private fun getSearchMovies() {
        repos = searchQuery.debounce(300)
            .flatMapLatest {
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        initialLoadSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { RepoPagingSource(it, apolloClient) }
                ).flow
            }.cachedIn(viewModelScope)
    }
}