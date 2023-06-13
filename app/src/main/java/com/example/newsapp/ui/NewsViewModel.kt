package com.example.newsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Query

class NewsViewModel(
    val newsRepository: NewsRepository
) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1


    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1


    init {
        getBreakingNews("us")
    }

    fun getBreakingNews (countryCode: String) =
        viewModelScope.launch {
            breakingNews.postValue(Resource.Loading())
            val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
            breakingNews.postValue(handleBreakingNewsResponse(response))
        }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchResponse(response))
    }

    private fun handleBreakingNewsResponse (response: Response<NewsResponse>)
    : Resource<NewsResponse> {
     if (response.isSuccessful) {
         response.body()?.let { resultResponse ->
             return Resource.Success(resultResponse)
         }
     }
        return Resource.Error(response.message())
    }


    private fun handleSearchResponse (response: Response<NewsResponse>)
            : Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle (article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews () = newsRepository.getSavedNews()

    fun deleteArticle (article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

}