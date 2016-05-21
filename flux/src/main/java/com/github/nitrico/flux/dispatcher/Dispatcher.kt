package com.github.nitrico.flux.dispatcher

import android.util.Log
import com.github.nitrico.flux.Flux
import com.github.nitrico.flux.action.Action
import com.github.nitrico.flux.action.ErrorAction
import com.github.nitrico.flux.store.StoreChange
import rx.Subscription
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject
import rx.subjects.Subject

internal object Dispatcher {

    private const val TAG = "Flux"

    private val bus: Subject<Any, Any> = SerializedSubject(PublishSubject.create())
    private val actions = mutableMapOf<StoreDispatch, Subscription>()
    private val changes = mutableMapOf<ViewDispatch, Subscription>()
    private val errors = mutableMapOf<ViewDispatch, Subscription>()

    internal fun post(action: Action) = bus.onNext(action)
    internal fun post(storeChange: StoreChange) = bus.onNext(storeChange)

    internal fun registerStore(store: StoreDispatch) {
        subscribeToActions(store)
    }

    internal fun registerView(view: ViewDispatch) {
        subscribeToChanges(view)
        subscribeToErrors(view)
    }

    internal fun unregisterStore(store: StoreDispatch) {
        unsubscribeFromActions(store)
    }

    internal fun unregisterView(view: ViewDispatch) {
        unsubscribeFromChanges(view)
        unsubscribeFromErrors(view)
    }

    @Synchronized internal fun unregisterAll() {
        for (subscription in actions.values) subscription.unsubscribe()
        for (subscription in changes.values) subscription.unsubscribe()
        for (subscription in errors.values) subscription.unsubscribe()
        actions.clear()
        changes.clear()
        errors.clear()
    }

    private fun subscribeToActions(store: StoreDispatch) {
        val subscription = actions[store]
        if (subscription == null || subscription.isUnsubscribed) {
            if (Flux.log) Log.d(TAG, "${store.javaClass.simpleName} registered")
            actions.put(store, bus
                    .filter { it is Action }
                    .subscribe {
                        it as Action
                        if (Flux.log) Log.d(TAG, "${store.javaClass.simpleName} <- $it")
                        store.onAction(it)
                    })
        }
    }

    private fun subscribeToErrors(view: ViewDispatch) {
        val subscription = errors[view]
        if (subscription == null || subscription.isUnsubscribed) {
            errors.put(view, bus
                    .filter { it is ErrorAction }
                    .subscribe {
                        it as ErrorAction
                        if (Flux.log) Log.d(TAG, "${view.javaClass.simpleName} <- ${it.action}")
                        view.onError(it)
                    })
        }
    }

    private fun subscribeToChanges(view: ViewDispatch) {
        val subscription = changes[view]
        if (subscription == null || subscription.isUnsubscribed) {
            if (Flux.log) Log.d(TAG, "${view.javaClass.simpleName} registered")
            changes.put(view, bus
                    .filter { it is StoreChange }
                    .subscribe {
                        it as StoreChange
                        if (Flux.log) Log.d(TAG, "${view.javaClass.simpleName} <- $it")
                        view.onStoreChanged(it)
                    })
        }
    }

    private fun unsubscribeFromActions(store: StoreDispatch) {
        val subscription = actions[store]
        if (subscription != null && !subscription.isUnsubscribed) {
            subscription.unsubscribe()
            actions.remove(store)
            if (Flux.log) Log.d(TAG, "${store.javaClass.simpleName} unregistered")
        }
    }

    private fun unsubscribeFromErrors(view: ViewDispatch) {
        val subscription = errors[view]
        if (subscription != null && !subscription.isUnsubscribed) {
            subscription.unsubscribe()
            errors.remove(view)
        }
    }

    private fun unsubscribeFromChanges(view: ViewDispatch) {
        val subscription = changes[view]
        if (subscription != null && !subscription.isUnsubscribed) {
            subscription.unsubscribe()
            changes.remove(view)
            if (Flux.log) Log.d(TAG, "${view.javaClass.simpleName} unregistered")
        }
    }

}
