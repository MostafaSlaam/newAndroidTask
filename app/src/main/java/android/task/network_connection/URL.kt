package android.task.network_connection

object URL {

    fun getCheckPhoneUrl(): String {
        var url = "check_phone"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getLoginUrl(): String {
        var url = "login"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getSignUpUrl(): String {
        var url = "register"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getGovernorateUrl(): String {
        var url = "governorate"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }


    fun getCountriesUrl(): String {
        var url = "country"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getUserUrl(): String {
        var url = "user"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getHomeUrl(): String {
        var url = "home"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getDepartmentDetailsUrl(): String {
        var url = "sub-details"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getDepartmentServicesUrl(): String {
        var url = "services"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getDepartmentsUrl(): String {
        var url = "departments"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getSubCategoriesUrl(): String {
        var url = "departments-sub"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getAllArticlesUrl(): String {
        var url = "articles"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getDepartmentVideosUrl(): String {
        var url = "videos"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getToggleFavUrl(): String {
        var url = "toggle-favourite"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getFavData(): String {
        var url = "get-favourite"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getServiceDetailsUrl(): String {

        var url = "show-service"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getAddRateUrl(): String {

        var url = "add-rate"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getUpdateAccountUrl(): String {
        var url = "update-profile"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getForgetPasswordUrl(): String {
        var url = "forget-password"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }


}