package idv.chauyan.shopbackquiz.repository

import idv.chauyan.shopbackquiz.apis.GithubApiService
import idv.chauyan.shopbackquiz.model.User
import idv.chauyan.shopbackquiz.model.UserDetail
import io.reactivex.Observable


class RetrofitRepository(val apiService : GithubApiService) {
    /**
     * outside api - get user list
     */
    fun getUsers() : Observable<List<User>> {
        return apiService.users()
    }

    /**
     * outside api - get user detail information
     */
    fun getUserDetail(username: String) : Observable<UserDetail> {
        return apiService.userDetail(username)
    }
}

object NetworkRepository {

    fun getInstance(): RetrofitRepository {
        return RetrofitRepository(GithubApiService.Factory.create())
    }
}