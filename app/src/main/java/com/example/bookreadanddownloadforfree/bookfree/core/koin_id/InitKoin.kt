package com.example.bookreadanddownloadforfree.bookfree.core.koin_id

import com.example.bookreadanddownloadforfree.bookfree.book.presentation.layoutconfig.LayoutConfig
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration


fun initKoin(config: KoinAppDeclaration? = null){

    startKoin {
        config?.invoke(this)
        modules(sharedModule,databaseModule
            )
    }
}