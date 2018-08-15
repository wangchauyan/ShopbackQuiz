package idv.chauyan.shopbackquiz

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * An activity representing a single User detail screen.
 */
class UserDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        if (savedInstanceState == null) {
            /**
             * pass selected user name from user list page to user detail fragment
             */
            val fragment = UserDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(UserDetailFragment.ARG_USERNAME,
                            intent.getStringExtra(UserDetailFragment.ARG_USERNAME))
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.user_detail_container, fragment)
                    .commit()
        }
    }
}
