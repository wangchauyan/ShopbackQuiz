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
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = UserDetailFragment()

            supportFragmentManager.beginTransaction()
                    .add(R.id.user_detail_container, fragment)
                    .commit()
        }
    }
}
