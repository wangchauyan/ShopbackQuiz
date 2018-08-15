package idv.chauyan.shopbackquiz

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import idv.chauyan.shopbackquiz.model.UserDetail
import idv.chauyan.shopbackquiz.repository.NetworkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.user_detail.*
import kotlinx.android.synthetic.main.user_detail_more.view.*

class UserDetailFragment : Fragment() {

    private var comDisposable: CompositeDisposable = CompositeDisposable()
    private var queryUserName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * check if there's valid selected user name which means
         * the selected user name is not null
         */
        arguments?.let {
            if (it.containsKey(ARG_USERNAME)) {
                queryUserName = it.getString(ARG_USERNAME)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.user_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * initialize view components
         */
        user_profile.image.setImageResource(R.drawable.user_detail_login)
        user_location.image.setImageResource(R.drawable.user_detail_location)
        user_link.image.setImageResource(R.drawable.user_detail_blog)

        close.setOnClickListener { _ ->
            activity?.finish()
        }
    }


    override fun onResume() {
        super.onResume()

        if (queryUserName != null) {
            val progess = ProgressDialog(activity)
            progess.setMessage(getString(R.string.userlist_activity_loading))
            progess.setCancelable(false)
            progess.show()


            /**
             * query user detail information by passing selected user name
             */
            val respository = NetworkRepository.getInstance()
            comDisposable.add(
                    respository
                            .getUserDetail(queryUserName!!)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                result ->
                                setupUserDetail(result)

                                progess.dismiss()
                            }, {
                                error ->
                                error.printStackTrace()

                                progess.dismiss()
                            })
            )
        }
    }


    /**
     * setup user detail information from github
     */
    private fun setupUserDetail(userdetail:UserDetail) {


        user_avatar.setImageURL(userdetail.avatar_url)
        user_name.text = userdetail.login
        bio.text = userdetail.bio

        user_profile.text1.text = userdetail.login
        if (userdetail.site_admin)
            user_profile.text2.visibility = View.VISIBLE
        else
            user_profile.text2.visibility = View.GONE

        user_location.text1.text = userdetail.location
        user_link.text1.text = userdetail.blog
    }


    companion object {
        const val ARG_USERNAME = "user_name"
    }
}
