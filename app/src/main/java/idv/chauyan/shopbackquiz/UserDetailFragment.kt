package idv.chauyan.shopbackquiz

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.user_detail.*
import kotlinx.android.synthetic.main.user_detail_more.view.*

class UserDetailFragment : Fragment() {

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
}
