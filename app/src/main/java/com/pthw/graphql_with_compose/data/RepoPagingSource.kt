package com.pthw.graphql_with_compose.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.pthw.graphql_with_compose.SearchRepositoriesQuery
import kotlinx.coroutines.delay

/**
 * Created by P.T.H.W on 30/08/2024.
 */
class RepoPagingSource(
    private val query: String,
    private val apolloClient: ApolloClient
) : PagingSource<SearchRepositoriesQuery.PageInfo, RepoVo>() {

    override fun getRefreshKey(state: PagingState<SearchRepositoriesQuery.PageInfo, RepoVo>): SearchRepositoriesQuery.PageInfo? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPageIndex = state.pages.indexOf(state.closestPageToPosition(anchorPosition))
            state.pages.getOrNull(anchorPageIndex + 1)?.prevKey ?: state.pages.getOrNull(
                anchorPageIndex - 1
            )?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<SearchRepositoriesQuery.PageInfo>): LoadResult<SearchRepositoriesQuery.PageInfo, RepoVo> {
        val raw = apolloClient.query(
            SearchRepositoriesQuery(
                first = Optional.present(20),
                before = Optional.present(params.key?.startCursor),
                after = Optional.present(params.key?.endCursor),
                query = "$query sort:stars"
            )
        ).execute()

        val prevKey =
            if (raw.data?.search?.pageInfo?.hasPreviousPage == true) raw.data?.search?.pageInfo else null
        val nextKey =
            if (raw.data?.search?.pageInfo?.hasNextPage == true) raw.data?.search?.pageInfo else null

        val results = raw.data?.search?.repos?.map {
            RepoVo(
                nameWithOwner = it?.repo?.onRepository?.nameWithOwner.toString(),
                description = it?.repo?.onRepository?.description.toString(),
                url = it?.repo?.onRepository?.url.toString(),
                stargazerCount = it?.repo?.onRepository?.stargazerCount ?: 0,
                language = if (it?.repo?.onRepository?.languages?.nodes.orEmpty().isEmpty()) null
                else
                    RepoVo.Language(
                        name = it?.repo?.onRepository?.languages?.nodes?.first()?.name.orEmpty(),
                        color = it?.repo?.onRepository?.languages?.nodes?.first()?.color.orEmpty()
                    ),
                owner = it?.repo?.onRepository?.owner?.onUser?.let {
                    RepoVo.Owner(
                        name = it.name.toString(),
                        url = it.url.toString(),
                        websiteUrl = it.websiteUrl.toString(),
                        avatarUrl = it.avatarUrl.toString()
                    )
                }
            )
        }.orEmpty()

        delay(1000)

        return LoadResult.Page(
            data = results,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

}