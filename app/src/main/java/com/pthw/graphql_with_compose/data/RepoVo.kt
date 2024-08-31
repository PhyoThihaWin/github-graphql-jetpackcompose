package com.pthw.graphql_with_compose.data


data class RepoVo(
    val nameWithOwner: String,
    val url: String,
    val description: String,
    val stargazerCount: Int,
    val language: Language?,
    val owner: Owner?
) {

    data class Language(
        val name: String,
        val color: String
    )

    data class Owner(
        val name: String,
        val url: String,
        val websiteUrl: String,
        val avatarUrl: String
    )

    companion object {
        fun fakeList(): List<RepoVo> {
            return listOf(
                RepoVo(
                    nameWithOwner = "octocat/Hello-World",
                    url = "https://github.com/octocat/Hello-World",
                    description = "This is your first repository.",
                    stargazerCount = 1534,
                    language = RepoVo.Language(
                        name = "Kotlin",
                        color = "#A97BFF"
                    ),
                    owner = RepoVo.Owner(
                        name = "octocat",
                        url = "https://github.com/octocat",
                        websiteUrl = "https://github.com",
                        avatarUrl = "https://github.com/images/error/octocat_happy.gif"
                    )
                ),
                RepoVo(
                    nameWithOwner = "google/iosched",
                    url = "https://github.com/google/iosched",
                    description = "The Google I/O 2023 app.",
                    stargazerCount = 9983,
                    language = RepoVo.Language(
                        name = "Java",
                        color = "#B07219"
                    ),
                    owner = RepoVo.Owner(
                        name = "google",
                        url = "https://github.com/google",
                        websiteUrl = "https://google.com",
                        avatarUrl = "https://avatars.githubusercontent.com/u/1342004?v=4"
                    )
                ),
                RepoVo(
                    nameWithOwner = "facebook/react",
                    url = "https://github.com/facebook/react",
                    description = "A declarative, efficient, and flexible JavaScript library for building user interfaces.",
                    stargazerCount = 200112,
                    language = RepoVo.Language(
                        name = "JavaScript",
                        color = "#F1E05A"
                    ),
                    owner = RepoVo.Owner(
                        name = "facebook",
                        url = "https://github.com/facebook",
                        websiteUrl = "https://reactjs.org",
                        avatarUrl = "https://avatars.githubusercontent.com/u/69631?v=4"
                    )
                )
            )
        }

    }
}