package com.johndeweydev.awps.viewmodels.bridgeviewmodel

/**
 * Callbacks when an http response is received, the response can be successful or a failure such
 * as when the response body is null
 *
 * @author John Dewey (johndewey02003@gmail.com)
 * */
interface BridgeViewModelEvent {

  fun onHttpGetResponseSuccess(message: String)

  fun onHttpUploadResponseSuccess(hashData: String)

  fun onHttpResponseUnsuccessful(reason: String)

  fun onHttpFailure(reason: String)
}