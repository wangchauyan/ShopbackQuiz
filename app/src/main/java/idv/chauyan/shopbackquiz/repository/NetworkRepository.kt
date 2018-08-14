package idv.chauyan.shopbackquiz.repository

import idv.chauyan.shopbackquiz.apis.GithubApiService
import idv.chauyan.shopbackquiz.model.User
import io.reactivex.Observable


class RetrofitRepository(val apiService : GithubApiService) {
    fun getUsers() : Observable<List<User>> {
        return apiService.users()
    }
}

object NetworkRepository {

    fun getInstance(): RetrofitRepository {
        return RetrofitRepository(GithubApiService.Factory.create())
    }
}