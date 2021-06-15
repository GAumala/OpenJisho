package com.gaumala.openjisho.frontend.my_lists

sealed class MyListsSideEffect {
   object Load: MyListsSideEffect()
   data class Delete(val names: List<String>): MyListsSideEffect()
}