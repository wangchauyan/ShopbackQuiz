package idv.chauyan.shopbackquiz.model

import com.google.gson.annotations.SerializedName

data class UserDetail(
        @SerializedName("avatar_url") val avatar_url: String,
        @SerializedName("name") val name: String,
        @SerializedName("bio") val bio: String,
        @SerializedName("login") val login: String,
        @SerializedName("location") val location: String,
        @SerializedName("blog") val blog: String,
        @SerializedName("site_admin") val site_admin: Boolean
)
