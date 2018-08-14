package idv.chauyan.shopbackquiz

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
import idv.chauyan.shopbackquiz.model.UserProfile
import kotlinx.android.synthetic.main.activity_user_list.*
import kotlinx.android.synthetic.main.user_list.*
import kotlinx.android.synthetic.main.user_list_content.view.*

/**
 * An activity representing a list of users from github.
 */
class UserListActivity : AppCompatActivity() {

    // check if we need to show two fragments at the same time for tablet devices
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Email me if you have any questions", Snackbar.LENGTH_LONG)
                    .setAction("Email", { view ->
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

        setupRecyclerView(user_list)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, UserProfile.ITEMS, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: UserListActivity,
                                        private val values: List<UserProfile.User>,
                                        private val twoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as UserProfile.User
                if (twoPane) {
                    val fragment = UserDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(UserDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.user_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, UserDetailActivity::class.java).apply {
                        putExtra(UserDetailFragment.ARG_ITEM_ID, item.id)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.id
            holder.contentView.text = item.content

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
            val contentView: TextView = view.content
        }
    }
}
