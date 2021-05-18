package android.task.network_connection

interface ConnectionCallback {
     fun onStartConnection()

     fun onFailureConnection(errorMessage: Any?)

     fun onSuccessConnection(response: Any?){}
     fun onLoginAgain(errorMessage: Any?)
}