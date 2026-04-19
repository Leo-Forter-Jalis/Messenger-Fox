package com.lfj.messfox.server

import java.util.Optional

inline fun <T, R> Optional<T>.ifPresentOrElseWithResult(crossinline action: (T) -> R, crossinline emptyAction: () -> R): R{
        return if(isPresent) action(get())
        else emptyAction()
}