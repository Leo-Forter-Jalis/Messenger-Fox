package com.lfj.messfox.server.dao

import java.util.*

inline fun <T, R> Optional<T>.ifPresentOrElseWithResult(crossinline action: (T) -> R, crossinline emptyAction: () -> R): R{
        return if(isPresent) action(get())
        else emptyAction()
 }