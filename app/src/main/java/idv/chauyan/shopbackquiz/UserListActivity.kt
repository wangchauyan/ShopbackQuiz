package idv.chauyan.shopbackquiz

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import kotlinx.android.synthetic.main.user_list_loading.view.*

/**
 * An activity representing a list of users from github.
 */
class UserListActivity : AppCompatActivity() {

    // check if we need to show two fragments at the same time for tablet devices
    private var twoPane: Boolean = false
    private var loadMore: Boolean = false
    private var since: Int = 0

    private val per_page: Int = 20
    private val comDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        /**
         * setup floating button
         */
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

        /**
         * setup refresh update gesture
         */
        refresh.setOnRefreshListener {
            queryUserList(true)
        }

        /**
         * setup recyclerview scrolling handler
         */
        user_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItems = (recyclerView.layoutManager as LinearLayoutManager).itemCount
                val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()


                if (!loadMore && totalItems <= lastVisibleItem+1) {
                    loadMore = true

                    Handler().post {
                        (user_list.adapter as UserListAdapter).showLoadingItem()
                    }
                    Handler().post {
                        queryUserList(false)
                    }
                }
            }
        })

        user_list.adapter = UserListAdapter(this, twoPane)

        /**
         * check if tablet devices
         */
        if (user_detail_container != null) {
            // tablet devices
            twoPane = true
        }

        /**
         * start getting first page - since = 0
         */
        queryUserList(false)
    }

    override fun onDestroy() {
        comDisposable.clear()
        super.onDestroy()
    }

    private fun queryUserList(bRefresh:Boolean) {

        /**
         * query user list from github
         */
        val respository = NetworkRepository.getInstance()
        comDisposable.add(
                respository
                        .getUsers(since, per_page)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            result ->

                            since += per_page
                            (user_list.adapter as UserListAdapter).setupUsers(result)

                            if (bRefresh)
                                refresh.isRefreshing = false

                            loadMore = false
                        }, {
                            error ->
                            error.printStackTrace()
                            (user_list.adapter as UserListAdapter).dismissLoadingItem()

                            if (bRefresh)
                                refresh.isRefreshing = false
                            loadMore = false
                        })
        )
    }

    /**
     * setup user list adapter for recyclerview
     */
    class UserListAdapter(private val parentActivity: UserListActivity,
                          private val twoPane: Boolean) :
            RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val onClickListener: View.OnClickListener
        private val viewTypeUser = 1
        private val viewTypeLoading = 2
        private var userList: ArrayList<User>? = null

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


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val viewHolder:RecyclerView.ViewHolder = when(viewType) {
                viewTypeUser -> {
                    val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.user_list_content, parent, false)
                    UserView(view)
                }
                else -> {
                    val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.user_list_loading, parent, false)
                    LoadingView(view)
                }
            }

            return viewHolder
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            if (holder is UserView) {
                userList?.let {
                    val item = it[position]
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

            }
            else if (holder is LoadingView){
                holder.loading.isIndeterminate = true
            }
        }

        override fun getItemCount(): Int {
            userList?.let {
                return it.size
            }

            return 0
        }

        override fun getItemViewType(position: Int): Int {

            userList?.let {
                it[position]?.let {
                    if (it.loadingMore)
                        return viewTypeLoading
                    return viewTypeUser
                }
                return viewTypeLoading
            }

            return viewTypeUser
        }

        fun setupUsers(data: List<User>) {
            userList?.let {
                // remove loading item data
                if (it[it.lastIndex].loadingMore)
                    it.removeAt(it.lastIndex)
                it.addAll(data)
            } ?: kotlin.run {
                userList = ArrayList(data)
            }

            notifyDataSetChanged()
        }

        /**
         * show loading item
         */
        fun showLoadingItem() {
            userList?.let {
                val loadingItem = User("", "", false, true)
                it.add(loadingItem)
                notifyItemInserted(it.size-1)
            }
        }

        /**
         * dismiss loading item
         */
        fun dismissLoadingItem() {
            userList?.let {
                it.removeAt(it.size-1)
                notifyItemRemoved(it.size)
            }
        }

        /**
         * viewholder for user item
         */
        inner class UserView(view: View) : RecyclerView.ViewHolder(view) {
            val userAvatar: CircleImageView = view.id_avatar
            val userName: TextView = view.id_name
            val userRole: TextView = view.id_role
        }

        /**
         * viewholder for loading item
         */
        inner class LoadingView(view: View) : RecyclerView.ViewHolder(view) {
            val loading: ProgressBar = view.id_loading
        }
    }
}
