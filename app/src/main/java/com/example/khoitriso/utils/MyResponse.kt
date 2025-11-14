package com.example.khoitriso.utils

sealed  class  MyResponse<T>(
    val messageCode: T? = null,
    val message: String? = null
){
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: String,data: T?= null):Resource<T>(data,message)
    class Loading<T>:Resource<T>()

}