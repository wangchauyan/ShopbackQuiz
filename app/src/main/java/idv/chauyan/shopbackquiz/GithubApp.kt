package idv.chauyan.shopbackquiz

import android.app.Application
import idv.chauyan.shopbackquiz.apis.GithubApiService

class GithubApp : Application() {

    public lateinit var githubApiService: GithubApiService

    override fun onCreate() {
        super.onCreate()


        githubApiService = GithubApiService.create()
    }
}
