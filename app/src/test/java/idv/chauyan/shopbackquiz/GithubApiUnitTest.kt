package idv.chauyan.shopbackquiz

import idv.chauyan.shopbackquiz.repository.NetworkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import kotlin.test.assertNotNull

class GithubApiUnitTest {

    val comDisposable = CompositeDisposable()
    val respository = NetworkRepository.getInstance()

    @Test
    fun getUsers_Success() {
        comDisposable.add(
                respository
                        .getUsers(0, 100)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            result ->
                            assert(result.isNotEmpty())
                        }, {
                            error ->
                            error.printStackTrace()
                        })
        )
    }

    @Test
    fun getUserDetailInfo_Success() {
        val userName = "mojombo"

        val respository = NetworkRepository.getInstance()
        comDisposable.add(
                respository
                        .getUserDetail(userName)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            result ->
                            assertNotNull(result)
                        }, {
                            error ->
                            error.printStackTrace()
                        })
        )
    }
}
