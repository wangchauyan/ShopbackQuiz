package idv.chauyan.shopbackquiz

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import idv.chauyan.shopbackquiz.model.User
import idv.chauyan.shopbackquiz.repository.NetworkRepository
import idv.chauyan.shopbackquiz.views.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_list.*
import kotlinx.android.synthetic.main.user_list.*
import kotlinx.android.synthetic.main.user_list_content.view.*

/**
 * An activity representing a list of users from github.
 */
class UserListActivity : AppCompatActivity() {

    // check if we need to show two fragments at the same time for tablet devices
    private var twoPane: Boolean = false
    private var comDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Email me if you have any questions", Snackbar.LENGTH_LONG)
                    .setAction("Email", { _ ->
                        val emailIntent = Intent(Intent.ACTION_SENDTO)
                        emailIntent.data = Uri.parse("mailto:") // only email apps should handle this
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Questions about this app...")
                        if (emailIntent.resolveActivity(packageManager) != null) {
                            startActivity(emailIntent)
                        }
                    }).show()
        }

        if (user_detail_container != null) {
            // tablet devices
            twoPane = true
        }

        val progess = ProgressDialog(this)
        progess.setMessage(getString(R.string.userlist_activity_loading))
        progess.setCancelable(false)
        progess.show()


        /**
         * query user list from github
         */
        val respository = NetworkRepository.getInstance()
        comDisposable.add(
            respository
                    .getUsers()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        result ->
                        setupRecyclerView(user_list, result)

                        progess.dismiss()
                    }, {
                        error ->
                        error.printStackTrace()

                        progess.dismiss()
                    })
        )
    }

    override fun onDestroy() {
        comDisposable.clear()
        super.onDestroy()
    }

    /**
     * setup user list
     */
    private fun setupRecyclerView(recyclerView: RecyclerView, data: List<User>) {
        recyclerView.adapter = UserListAdapter(this, data, twoPane)
    }

    /**
     * setup user list adapter for recyclerview
     */
    class UserListAdapter(private val parentActivity: UserListActivity,
                          private val values: List<User>,
                          private val twoPane: Boolean) :
            RecyclerView.Adapter<UserListAdapter.UserView>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                if (twoPane) {
                    /**
                     * pass selected user name to user detail fragment
                     */
                    val fragment = UserDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(UserDetailFragment.ARG_USERNAME, (v.tag as User).login)
                        }
                    }
                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.user_detail_container, fragment)
                            .commit()
                } else {
                    /**
                     * pass selected user name to user detail activity
                     */
                    val intent = Intent(v.context, UserDetailActivity::class.java).apply {
                        putExtra(UserDetailFragment.ARG_USERNAME, (v.tag as User).login)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserView {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_list_content, parent, false)
            return UserView(view)
        }

        override fun onBindViewHolder(holder: UserView, position: Int) {
            val item = values[position]
            holder.userName.text = item.login
            holder.userAvatar.setImageURL(item.avatar_url)

            if (!item.site_admin)
                holder.userRole.visibility = View.GONE
            else
                holder.userRole.visibility = View.VISIBLE

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class UserView(view: View) : RecyclerView.ViewHolder(view) {
            val userAvatar: CircleImageView = view.id_avatar
            val userName: TextView = view.id_name
            val userRole: TextView = view.id_role
        }
    }
}
