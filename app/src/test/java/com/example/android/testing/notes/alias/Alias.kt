package com.example.android.testing.notes.alias

import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

/**
 * Created by Fabiomsr on 23/7/16.
 */
fun  <T> _is(matcher: Matcher<T> ): Matcher<T> {
    return `is`(matcher)
}

fun  <T> _when(methodCall: T ): OngoingStubbing<T> {
    return Mockito.`when`(methodCall)
}