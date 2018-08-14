package idv.chauyan.shopbackquiz.model

import java.util.*

/**
 * Helper class for providing user profile information from github
 */
object UserProfile {

    /**
     * An array of users
     */
    val ITEMS: MutableList<User> = ArrayList()

    /**
     * A map of users, by ID.
     */
    val ITEM_MAP: MutableMap<String, User> = HashMap()

    init {
        for (i in 1..25) {
            addItem(createDummyItem(i))
        }
    }

    private fun addItem(item: User) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createDummyItem(position: Int): User {
        return User(position.toString(), "User " + position, makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about this user: ").append(position)
        builder.append("\nMore details information here.")
        return builder.toString()
    }

    /**
     * User Item for representing a user
     */
    data class User(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}
