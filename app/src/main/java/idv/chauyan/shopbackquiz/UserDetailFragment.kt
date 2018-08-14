package idv.chauyan.shopbackquiz

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import idv.chauyan.shopbackquiz.repository.NetworkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.user_detail.*
import kotlinx.android.synthetic.main.user_detail_more.view.*

class UserDetailFragment : Fragment() {

    private var comDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.user_detail, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        user_location.text2.visibility = View.GONE
        user_link.text2.visibility = View.GONE

        close.setOnClickListener { _ ->
            activity?.finish()
        }
    }


    override fun onResume() {
        super.onResume()

        val progess = ProgressDialog(activity)
        progess.setMessage(getString(R.string.userlist_activity_loading))
        progess.setCancelable(false)
        progess.show()


        val respository = NetworkRepository.getInstance()
        comDisposable.add(
                respository
                        .getUserDetail("defunkt")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            result ->
                            println(result.blog)

                            progess.dismiss()
                        }, {
                            error ->
                            error.printStackTrace()

                            progess.dismiss()
                        })
        )
    }
}
