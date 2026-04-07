package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_detail


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.bookreadanddownloadforfree.bookfree.app.Route
import com.example.bookreadanddownloadforfree.bookfree.book.data.repository.BookRepository
import com.example.bookreadanddownloadforfree.bookfree.core.domian.map
import com.example.bookreadanddownloadforfree.bookfree.core.domian.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class BookDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository,

) : ViewModel()
{
    private val bookId = savedStateHandle.toRoute<Route.BookDetail>().id

    private val _state = MutableStateFlow(BookDetailState())
    val state = _state

        .onStart {
            observeFavoriteStatus()
            fetchBookDescription()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )











     fun onAction(action: BookDetailAction){
        when(action){
            is BookDetailAction.OnSelectedBookChange ->{
                _state.update { it.copy(book=action.book.value ) }
            }


            is BookDetailAction.OnFavoriteClickDetail ->{
                viewModelScope.launch {
                    val currentBook = state.value.book
                    val language = currentBook?.languages?.firstOrNull() ?: ""

                    if(state.value.isFavorite){
                        bookRepository.deleteFavoriteBook(
                            bookId,
                            language =language
                        )
                    }
                    else{
                        state.value.book?.let { book ->
                            bookRepository.markAsFavorite(book)
                        }
                    }
                }
            }


            is BookDetailAction.OnTabSelected -> {
                _state.update {
                    it.copy(selectedTabIndex = action.index)
                }
            }

            else -> Unit
        }
    }




















    // No BookDetailViewModel, ajuste para observar baseado no livro atual
    private fun observeFavoriteStatus() {
        viewModelScope.launch {
            // Observamos as mudanças no estado do livro
            state.map { it.book }.distinctUntilChanged().collect { book ->
                val lang = book?.languages?.firstOrNull() ?: ""

                // Aqui você precisaria de uma função no repository que aceite ID e Language
                // Ou simplesmente filtrar a lista de favoritos completa
                bookRepository.getFavoriteBookAll().collect { favorites ->
                    val isFavorite = favorites.any { it.id == bookId && it.languages.contains(lang) }
                    _state.update { it.copy(isFavorite = isFavorite) }
                }
            }
        }
    }




private fun fetchBookDescription(){
    viewModelScope.launch {
        bookRepository.getBookDescription(bookId)
            .onSuccess { description ->
                _state.update{it.copy(
                    book =it.book?.copy(
                        description = description
                    ),
                    isLanding = false
                )}
            }



    }
}














}
/*FATAL EXCEPTION: main (Explain with AI)
                                                                                                    Process: com.example.bookreadanddownloadforfree, PID: 22981
                                                                                                    java.lang.IllegalArgumentException: Key "OL257943W" was already used. If you are using LazyColumn/Row please make sure you provide a unique key for each item.
                                                                                                    	at androidx.compose.ui.internal.InlineClassHelperKt.throwIllegalArgumentException(InlineClassHelper.kt:36)
                                                                                                    	at androidx.compose.ui.layout.LayoutNodeSubcompositionsState.subcompose(SubcomposeLayout.kt:1055)
                                                                                                    	at androidx.compose.ui.layout.LayoutNodeSubcompositionsState$Scope.subcompose(SubcomposeLayout.kt:926)
                                                                                                    	at androidx.compose.foundation.lazy.layout.LazyLayoutMeasureScopeImpl.measure-0kLqBqw(LazyLayoutMeasureScope.kt:124)
                                                                                                    	at androidx.compose.foundation.lazy.LazyListMeasuredItemProvider.getAndMeasure-0kLqBqw(LazyListMeasuredItemProvider.kt:55)
                                                                                                    	at androidx.compose.foundation.lazy.LazyListMeasuredItemProvider.getAndMeasure-0kLqBqw$default(LazyListMeasuredItemProvider.kt:49)
                                                                                                    	at androidx.compose.foundation.lazy.LazyListMeasureKt.measureLazyList-LCrQqZ4(LazyListMeasure.kt:222)
                                                                                                    	at androidx.compose.foundation.lazy.LazyListKt$rememberLazyListMeasurePolicy$1$1.invoke-0kLqBqw(LazyList.kt:352)
                                                                                                    	at androidx.compose.foundation.lazy.LazyListKt$rememberLazyListMeasurePolicy$1$1.invoke(LazyList.kt:200)
                                                                                                    	at androidx.compose.foundation.lazy.layout.LazyLayoutKt$LazyLayout$1$2$1.invoke-0kLqBqw(LazyLayout.kt:78)
                                                                                                    	at androidx.compose.foundation.lazy.layout.LazyLayoutKt$LazyLayout$1$2$1.invoke(LazyLayout.kt:76)
                                                                                                    	at androidx.compose.ui.layout.LayoutNodeSubcompositionsState$createMeasurePolicy$1.measure-3p2s80s(SubcomposeLayout.kt:754)
                                                                                                    	at androidx.compose.ui.node.InnerNodeCoordinator.measure-BRTryo0(InnerNodeCoordinator.kt:128)
                                                                                                    	at androidx.compose.ui.graphics.SimpleGraphicsLayerModifier.measure-3p2s80s(GraphicsLayerModifier.kt:642)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
                                                                                                    	at androidx.compose.foundation.lazy.layout.LazyLayoutBeyondBoundsModifierNode.measure-3p2s80s(LazyLayoutBeyondBoundsModifierLocal.kt:118)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate$performMeasureBlock$1.invoke(MeasurePassDelegate.kt:171)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate$performMeasureBlock$1.invoke(MeasurePassDelegate.kt:170)
                                                                                                    	at androidx.compose.runtime.snapshots.Snapshot$Companion.observe(Snapshot.kt:502)
                                                                                                    	at androidx.compose.runtime.snapshots.SnapshotStateObserver$ObservedScopeMap.observe(SnapshotStateObserver.kt:464)
                                                                                                    	at androidx.compose.runtime.snapshots.SnapshotStateObserver.observeReads(SnapshotStateObserver.kt:248)
                                                                                                    	at androidx.compose.ui.node.OwnerSnapshotObserver.observeReads$ui_release(OwnerSnapshotObserver.kt:124)
                                                                                                    	at androidx.compose.ui.node.OwnerSnapshotObserver.observeMeasureSnapshotReads$ui_release(OwnerSnapshotObserver.kt:107)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate.performMeasure-BRTryo0$ui_release(MeasurePassDelegate.kt:424)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate.remeasure-BRTryo0(MeasurePassDelegate.kt:472)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate.measure-BRTryo0(MeasurePassDelegate.kt:452)
                                                                                                    	at androidx.compose.foundation.layout.RowColumnMeasurePolicyKt.measure(RowColumnMeasurePolicy.kt:208)
                                                                                                    	at androidx.compose.foundation.layout.RowColumnMeasurePolicyKt.measure$default(RowColumnMeasurePolicy.kt:77)
                                                                                                    	at androidx.compose.foundation.layout.ColumnMeasurePolicy.measure-3p2s80s(Column.kt:208)
                                                                                                    	at androidx.compose.ui.node.InnerNodeCoordinator.measure-BRTryo0(InnerNodeCoordinator.kt:128)
                                                                                                    	at androidx.compose.foundation.layout.PaddingNode.measure-3p2s80s(Padding.kt:401)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
                                                                                                    	at androidx.compose.foundation.layout.FillNode.measure-3p2s80s(Size.kt:721)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
2026-01-24 21:20:24.744 22981-22981 AndroidRuntime          com...le.bookreadanddownloadforfree  E  	at androidx.compose.foundation.layout.FillNode.measure-3p2s80s(Size.kt:721) (Explain with AI)
                                                                                                    	at androidx.compose.ui.node.LayoutModifierNodeCoordinator.measure-BRTryo0(LayoutModifierNodeCoordinator.kt:190)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate$performMeasureBlock$1.invoke(MeasurePassDelegate.kt:171)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate$performMeasureBlock$1.invoke(MeasurePassDelegate.kt:170)
                                                                                                    	at androidx.compose.runtime.snapshots.Snapshot$Companion.observe(Snapshot.kt:2495)
                                                                                                    	at androidx.compose.runtime.snapshots.SnapshotStateObserver$ObservedScopeMap.observe(SnapshotStateObserver.kt:464)
                                                                                                    	at androidx.compose.runtime.snapshots.SnapshotStateObserver.observeReads(SnapshotStateObserver.kt:248)
                                                                                                    	at androidx.compose.ui.node.OwnerSnapshotObserver.observeReads$ui_release(OwnerSnapshotObserver.kt:124)
                                                                                                    	at androidx.compose.ui.node.OwnerSnapshotObserver.observeMeasureSnapshotReads$ui_release(OwnerSnapshotObserver.kt:107)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate.performMeasure-BRTryo0$ui_release(MeasurePassDelegate.kt:424)
                                                                                                    	at androidx.compose.ui.node.MeasurePassDelegate.remeasure-BRTryo0(MeasurePassDelegate.kt:472)
                                                                                                    	at androidx.compose.ui.node.LayoutNode.remeasure-_Sx5XlM$ui_release(LayoutNode.kt:1212)
                                                                                                    	at androidx.compose.ui.node.LayoutNode.remeasure-_Sx5XlM$ui_release$default(LayoutNode.kt:1205)
                                                                                                    	at androidx.compose.ui.node.MeasureAndLayoutDelegate.doRemeasure-sdFAvZA(MeasureAndLayoutDelegate.kt:369)
                                                                                                    	at androidx.compose.ui.node.MeasureAndLayoutDelegate.remeasureAndRelayoutIfNeeded(MeasureAndLayoutDelegate.kt:569)
                                                                                                    	at androidx.compose.ui.node.MeasureAndLayoutDelegate.remeasureAndRelayoutIfNeeded$default(MeasureAndLayoutDelegate.kt:535)
                                                                                                    	at androidx.compose.ui.node.MeasureAndLayoutDelegate.measureAndLayout(MeasureAndLayoutDelegate.kt:390)
                                                                                                    	at androidx.compose.ui.platform.AndroidComposeView.measureAndLayout(AndroidComposeView.android.kt:1541)
                                                                                                    	at androidx.compose.ui.node.Owner.measureAndLayout$default(Owner.kt:252)
                                                                                                    	at androidx.compose.ui.platform.AndroidComposeView.dispatchDraw(AndroidComposeView.android.kt:1889)
                                                                                                    	at android.view.View.draw(View.java:21578)
                                                                                                    	at android.view.View.updateDisplayListIfDirty(View.java:20414)
                                                                                                    	at android.view.ViewGroup.recreateChildDisplayList(ViewGroup.java:4457)
                                                                                                    	at android.view.ViewGroup.dispatchGetDisplayList(ViewGroup.java:4430)
                                                                                                    	at android.view.View.updateDisplayListIfDirty(View.java:20365)
                                                                                                    	at android.view.ViewGroup.recreateChildDisplayList(ViewGroup.java:4457)
                                                                                                    	at android.view.ViewGroup.dispatchGetDisplayList(ViewGroup.java:4430)
                                                                                                    	at android.view.View.updateDisplayListIfDirty(View.java:20365)
                                                                                                    	at android.view.ViewGroup.recreateChildDisplayList(ViewGroup.java:4457)
                                                                                                    	at android.view.ViewGroup.dispatchGetDisplayList(ViewGroup.java:4430)
                                                                                                    	at android.view.View.updateDisplayListIfDirty(View.java:20365)
                                                                                                    	at android.view.ViewGroup.recreateChildDisplayList(ViewGroup.java:4457)
                                                                                                    	at android.view.ViewGroup.dispatchGetDisplayList(ViewGroup.java:4430)
                                                                                                    	at android.view.View.updateDisplayListIfDirty(View.java:20365)
                                                                                                    	at android.view.ThreadedRenderer.updateViewTreeDisplayList(ThreadedRenderer.java:575)
                                                                                                    	at android.view.ThreadedRenderer.updateRootDisplayList(ThreadedRenderer.java:581)
                                                                                                    	at android.view.ThreadedRenderer.draw(ThreadedRenderer.java:654)
                                                                                                    	at android.view.ViewRootImpl.draw(ViewRootImpl.java:3803)
                                                                                                    	at android.view.ViewRootImpl.performDraw(ViewRootImpl.java:3607)
                                                                                                    	at android.view.ViewRootImpl.performTraversals(ViewRootImpl.java:2927)
                                                                                                    	at android.view.ViewRootImpl.doTraversal(ViewRootImpl.java:1840)
                                                                                                    	at android.view.ViewRootImpl$TraversalRunnable.run(ViewRootImpl.java:7937)
                                                                                                    	at android.view.Choreographer$CallbackRecord.run(Choreographer.java:980)
                                                                                                    	at android.view.Choreographer.doCallbacks(Choreographer.java:804)
                                                                                                    	at android.view.Choreographer.doFrame(Choreographer.java:739)
                                                                                                    	at android.view.Choreographer$FrameDisplayEventReceiver.run(Choreographer.java:965)
                                                                                                    	at android.os.Handler.handleCallback(Handler.java:883)
                                                                                                    	at android.os.Handler.dispatchMessage(Handler.java:100)
                                                                                                    	at android.os.Looper.loop(Looper.java:264)
2026-01-24 21:20:24.745 22981-22981 AndroidRuntime          com...le.bookreadanddownloadforfree  E  	at android.app.ActivityThread.main(ActivityThread.java:7663) (Explain with AI)
                                                                                                    	at java.lang.reflect.Method.invoke(Native Method)
                                                                                                    	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:492)
                                                                                                    	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:980)
2026-01-24 21:20:24.931 22981-22994 downloadforfre          com...le.bookreadanddownloadforfree  I  Background concurrent copying GC freed 307132(11MB) AllocSpace objects, 2(40KB) LOS objects, 61% free, 7777KB/19MB, paused 129us total 222.828ms
2026-01-24 21:20:25.133 22981-23409 System.out              com...le.bookreadanddownloadforfree  I  [socket]:check permission begin!
2026-01-24 21:20:25.141 22981-23407 System.out              com...le.bookreadanddownloadforfree  I  [socket]:check permission begin!
2026-01-24 21:20:28.802 22981-23409 System.out              com...le.bookreadanddownloadforfree  I  [socket]:check permission begin!
2026-01-24 21:20:29.758 22981-23407 System.out              com...le.bookreadanddownloadforfree  I  [socket]:check permission begin!
2026-01-24 21:20:32.338  1056-1634  InputDispatcher         system_server                        E  Window handle Window{b13ebd2 u0 Application Error: com.example.bookreadanddownloadforfree} has no registered input channel
2026-01-24 21:20:32.367  1056-1634  InputDispatcher         system_server                        E  Window handle Window{b13ebd2 u0 Application Error: com.example.bookreadanddownloadforfree} has no registered input channel
2026-01-24 21:20:32.383  1056-1634  InputDispatcher         system_server                        E  Window handle Window{b13ebd2 u0 Application Error: com.example.bookreadanddownloadforfree} has no registered input channel
2026-01-24 21:20:32.401  1056-1634  InputDispatcher         system_server                        E  Window handle Window{b13ebd2 u0 Application Error: com.example.bookreadanddownloadforfree} has no registered input channel

Skipped 37 frames!  The application may be doing too much work on its main thread.
 */