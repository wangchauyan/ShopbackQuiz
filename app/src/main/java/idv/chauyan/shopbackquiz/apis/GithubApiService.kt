package idv.chauyan.shopbackquiz.apis

import idv.chauyan.shopbackquiz.model.User
import idv.chauyan.shopbackquiz.model.UserDetail
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


interface GithubApiService {

    /**
     * get user list from github
     */
    @GET("users")
    fun users(): Observable<List<User>>

    /**
     * query user detail information from github by using an exact user name
     */
    @GET("users/{username}")
    fun userDetail(@Path("username") usserName:String): Observable<UserDetail>

    companion object Factory {
        fun create(): GithubApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://api.github.com/")
                    .build()

            return retrofit.create(GithubApiService::class.java)
        }
    }
}