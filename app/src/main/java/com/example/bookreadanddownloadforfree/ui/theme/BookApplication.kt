package com.example.bookreadanddownloadforfree.ui.theme

import android.app.Application
import com.example.bookreadanddownloadforfree.bookfree.core.koin_id.initKoin
import com.example.bookreadanddownloadforfree.bookfree.core.koin_id.sharedModule
import org.koin.android.ext.koin.androidContext


class BookApplication: Application() {
    override fun onCreate(){
        super.onCreate()
        initKoin{
            androidContext(this@BookApplication)
            modules(listOf(sharedModule))
        }
    }
}